/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kodkod.util.collections.IdentityHashSet;
import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.serializer.impl.ObjSerFactory;
import edu.mit.csail.sdg.squander.spec.ForgeScene;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.utils.Utils;
import edu.mit.csail.sdg.util.collections.Iterators;
import forge.program.ForgeDomain;
import forge.program.ForgeLiteral;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeType.Unary;
import forge.program.GlobalVariable;
import forge.program.LocalVariable;

/** 
 * Represents the result of an analysis
 * 
 *  @author Aleksandar Milicevic
 */
public final class SquanderResult implements ISquanderResult {
    
    private final ForgeConverter fconv;
    private final Collection<GlobalVariable> modifiable;
    private IEvaluator eval;
    
    public SquanderResult(IEvaluator eval, ForgeConverter fconv, Collection<GlobalVariable> mod) {
        this.eval = eval;
        this.fconv = fconv;
        this.modifiable = mod;
    }
    
    public ForgeProgram program()      { return fconv.forgeScene().program(); }
    public LocalVariable self()        { return fconv.forgeScene().thisVar(); }
    public LocalVariable returnVar()   { return fconv.forgeScene().returnVar(); }

    public boolean hasSolution()       { return eval.hasSolution(); }
    public String getTrace()           { return eval.trace(); }
    public String getStats()           { return eval.stats(); }

    public String unsatCore()          { return eval.unsatCore(); }
    
    public boolean findNext() {
        if (!eval.hasSolution())
            return false;
        eval = eval.nextSolution();
        if (!eval.hasSolution())
            return false;
        restoreJavaHeap();
        return true;
    }

    @SuppressWarnings("unchecked")
    public <R> R getReturnValue() {
        if (returnVar() == null) {
            return null;
        } else {
            Class<?> retType = fconv.javaScene().method().returnType().clazz();
            assert retType != null;
            ObjTupleSet returnVal = eval.evaluate(returnVar());
            assert returnVal.tuples().size() == 1 : "return value should not be a set: " + returnVal;
            ForgeDomain arrDom = Utils.getArrayDomain(returnVar()); //TODO: don't use these Util methods
            if (arrDom != null) {
                GlobalVariable elems = fconv.forgeScene().global(arrDom + "__elts");
                returnVal = returnVal.join(eval.evaluate(elems));
                return (R) convertForgeSetToJavaArray(returnVal, retType);
            }
            return (R) returnVal.tuples().iterator().next().get(0); 
        }
    }
    
    public void restoreJavaHeap() {
        ForgeScene forgeScene = fconv.forgeScene();
        for (GlobalVariable mod : modifiable) {
            ObjTupleSet fldVal = eval.evaluate(mod);
            IdentityHashSet<Object> visited = new IdentityHashSet<Object>();
            if (fldVal.isEmpty()) {
                restoreEmpty(mod);
                continue;
            }
            for (ObjTuple ot : fldVal.tuples()) {
                Object obj = ot.get(0);
                if (obj == null)
                    continue;
                if (!visited.add(obj))
                    continue;
                JField jf = getJFieldForVar(forgeScene, mod);
                FieldValue fv = new FieldValue(jf, mod.arity());
                ObjTupleSet objFldVal = ObjTupleSet.filter(fldVal, 0, obj);
                fv.addAllTuples(objFldVal);
                ObjSerFactory.factory.getSerForObj(obj).concrFunc(obj, fv);
            }
        }
    }
    
    @Override
    public void exportToAlloyVizInst(PrintStream ps, ExtraSkolems skolems, Comparator<? super Object> comparator) {
        assert hasSolution() : "can't export since no solution was found";
        new AlloyVizExporter(fconv, eval).export(ps, skolems, comparator);
    }

    @Deprecated
    public Iterator<Object> getSpecField(String specFieldName) {
        // self represents caller
        ObjTupleSet self = eval.evaluate(self());
        GlobalVariable v = fconv.forgeScene().global(specFieldName);
        if (v == null) throw new NullPointerException("couldn't find forge global variable " + specFieldName);
        ObjTupleSet evaluate = eval.evaluate(v);
        System.out.println("evaluate: " + evaluate);
        ObjTupleSet thisSpecFieldValue = ObjTupleSet.join(self, evaluate);
        Set<ObjTuple> tuples = thisSpecFieldValue.tuples();

        if (tuples.size() == 0) {
            return Iterators.empty();
        } else if (thisSpecFieldValue.arity() == 0) {
            // empty
            return Iterators.empty();

        } else if (thisSpecFieldValue.arity() == 1) {
            final Iterator<ObjTuple> it = tuples.iterator();
            // set
            return new Iterator<Object>() {
                @Override public boolean hasNext() { return it.hasNext(); }
                @Override public Object next()    { return it.next().get(0); }
                @Override public void remove() {
                    throw new UnsupportedOperationException();
                }
                
            };
        
        } else if (thisSpecFieldValue.arity() == 2) {
            // sequence (we hope)
            // radix sort
            List<Object> seq = new ArrayList<Object>(tuples.size());
            for (int i = 0; i < tuples.size(); i++) {
                seq.add(null);
            }
            for (ObjTuple t : tuples) {
                int index = (Integer) t.get(0);
                Object javaObject = t.get(1);
                assert seq.get(index) == null : "seq already has something at index " + index + " " + seq.get(index);
                seq.set(index, javaObject);
            }
            // return the sorted result
            return seq.iterator();
        } else {
            // dunno ...
            throw new UnsupportedOperationException("spec field has a weird arity: " + thisSpecFieldValue.arity() + " " + v);
        }
    }
    
    // ======================================================================
    // ============================= private ================================
    // ======================================================================
    
    private JField getJFieldForVar(ForgeScene forgeScene, GlobalVariable mod) {
        return forgeScene.fields(mod).iterator().next();
    }
    
    private void restoreEmpty(GlobalVariable mod) {
        ForgeType.Unary dom = (Unary) mod.type().projectType(0);
        for (ForgeLiteral lit : fconv.findLiteralsForType(dom)) {
            Object obj = fconv.lit2obj(lit);
            JField jf = getJFieldForVar(fconv.forgeScene(), mod);
            FieldValue fv = new FieldValue(jf, mod.arity());
            ObjSerFactory.factory.getSerForObj(obj).concrFunc(obj, fv);
        }
    }
    
    private Object convertForgeSetToJavaArray(ObjTupleSet ots, Class<?> arrCls) {
        int n = ots.size();
        Object arrObj = Array.newInstance(arrCls.getComponentType(), n);
        int idx = 0;
        for (ObjTuple tuple : ots.tuples()) {
            if (tuple.arity() == 1) {
                Array.set(arrObj, idx++, tuple.get(0));
            } else {
                Integer index = (Integer) tuple.get(0);
                Array.set(arrObj, index, tuple.get(1));
            }
        }
        // convert to right array type
        return arrObj;
    }

}
/*! @} */
