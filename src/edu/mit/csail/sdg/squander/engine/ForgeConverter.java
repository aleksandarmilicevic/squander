/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kodkod.util.collections.IdentityHashSet;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.Heap2Bounds.Pair;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.serializer.AbstractHeapListener;
import edu.mit.csail.sdg.squander.serializer.impl.ObjSerFactory;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.ForgeScene;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JMethod;
import edu.mit.csail.sdg.squander.spec.JType;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.JavaScene;
import edu.mit.csail.sdg.squander.spec.MethodSpec;
import edu.mit.csail.sdg.squander.spec.MethodSpec.CaseSource;
import edu.mit.csail.sdg.squander.spec.Spec;
import edu.mit.csail.sdg.squander.spec.Tr;
import edu.mit.csail.sdg.squander.spec.constant.ConstRel;
import edu.mit.csail.sdg.squander.spec.constant.ConstRel2Bound;
import edu.mit.csail.sdg.squander.utils.Utils;
import forge.program.BooleanDomain;
import forge.program.BooleanLiteral;
import forge.program.ForgeDomain;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeProcedure;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeType.Tuple;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.InstanceDomain;
import forge.program.InstanceLiteral;
import forge.program.IntegerDomain;
import forge.program.IntegerLiteral;
import forge.program.LocalDecls;
import forge.program.LocalVariable;
import forge.solve.BooleanAtom;
import forge.solve.ForgeAtom;
import forge.solve.ForgeBounds;
import forge.solve.ForgeConstant;
import forge.solve.IntegerAtom;
import forge.solve.SolveOptions;
import forge.solve.SolveOptions.SatSolver;

/**
 * This class handles conversion of the heap (<code>heap</code>) and specs
 * (embodied inside <code>javaScene</code>) to Forge domains, literals, and 
 * bounds (embodied inside <code>forgeScene</code>). 
 * 
 * @author Aleksandar Milicevic
 */
public class ForgeConverter {
   
    private final SquanderReporter reporter;
    private final Heap heap;
    private final JavaScene javaScene;
    private ForgeScene forgeScene;

    private Heap2Bounds heap2lit;
    private ForgeBounds forgeBounds;

    private boolean finished = false;
    
    /**
     * @param heapRootObjects heap root objects
     */
    public ForgeConverter(SquanderReporter reporter,  Object ... heapRootObjects) {
        this.reporter = reporter;
        this.heap = new Heap(heapRootObjects);
        this.javaScene = new JavaScene();
    }

    /**
     * Use this method to set the specification of a method to be executed
     */
    public void setCallContext(Object caller, JMethod m, Object[] methodArgs) {
        assert !finished : "don't add stuff after calling finish()";
        createFreshObjects(m.spec());
        initJavaScene();
        javaScene.setCaller(caller);
        javaScene.setArgs(methodArgs);
        for (int i = 0; i < methodArgs.length; i++) {
            obj2typeparams.put(methodArgs[i], m.paramTypes().get(i).typeParams());
        }
        javaScene.setMethod(m);
        javaScene.finish();
        forgeScene = new ForgeScene(javaScene);
        
        finish();
        translateSpec();
    }
    
    public void translateSpec() {
        Tr.teribleHack2 = this;
        reporter.translatingSpecs();
        javaScene.translateSpecs(forgeScene);
    }
    
    /**
     * Call this method after executing the precondition spec and before executing the 
     * postcondition spec to bound the values of spec fields in the pre-state. 
     */
    public void boundSpecFields(IEvaluator eval) {
        for (JField fld : javaScene.specFields()) {
            GlobalVariable mod = forgeScene.global(fld);
            ObjTupleSet val = eval.evaluate(mod);
            heap2lit.addBound(fld.fullName(), val);
            if (forgeBounds != null) {
                ForgeConstant fc = conv2fc(val, forgeBounds); 
                forgeBounds.boundInitial(mod, fc, fc);
            }
        }
    }
    
    public ClassSpec ensureClass(Class<?> cls) {
        assert !finished : "";
        return javaScene.ensureClass(cls);
    }

    public JavaScene javaScene()     { assert finished : "call finish() first"; return javaScene; }
    public ForgeScene forgeScene()   { assert finished : "call finish() first"; return forgeScene; }
    public Heap2Bounds heap2Lit()    { assert finished : "call finish() first"; return heap2lit; }
    public ForgeBounds forgeBounds() { 
        assert finished : "call finish() first"; 
        if (forgeBounds == null) 
            forgeBounds = makeForgeBounds(heap2lit);
        return forgeBounds;
    }
    
    public int bw() {
        Options opts = javaScene.methodSpec().options();
        int bw = -1;
        if (opts != null) bw = opts.bitwidth();
        if (bw == -1) bw = heap2lit.minBW();
        return bw;
    }
    
    private void finish() {
        assert !finished : "don't call finish() twice";
        
        // traverse the heap to create forge literals and bounds
        reporter.traversingHeap();
        heap2lit = new Heap2Bounds(javaScene);
        List<Object> heapObjects = heap.getHeapObjects();
        List<Pair> rootObjects = new ArrayList<Pair>(heapObjects.size());
        for (Object o : heapObjects)
            rootObjects.add(new Pair(o, obj2typeparams.get(o)));
        heap2lit.traverse(rootObjects);
        for (Object o : heap2lit.reachableObjects()) {
            if (!Utils.isPrimitive(o.getClass()) && !o.getClass().isEnum())
                forgeScene.createLiteral(o);
            else if (o instanceof Integer)
                forgeScene.ensureInt((Integer) o);
        }
        for (Class<? extends Enum<?>> e : javaScene.enums()) {
            for (Enum<?> x : e.getEnumConstants()) {
                forgeScene.createLiteral(x, x.name());
            }
        }
            
        forgeScene.createLocalsForMethod(javaScene.method());

        // process options 
        Options opts = javaScene.methodSpec().options();
        if (opts != null) forgeScene.ensureAllInts(opts.ensureAllInts());
        
        // ensure ints for "num types" (types whose cardinality is mentioned in the specs)
        for (JType ft : javaScene.numTypes()) {
            int max = getMaxAtomsForType(forgeScene.convertToForgeType(ft, false));
            for (int i = 0; i <= max; i++) {
                heap2lit.ensureAdequateIntBitWidth(i);
                forgeScene.ensureInt(i);
            }
        }
        
        finished = true;        
        
        boundConsts(); 
        boundLocals();
        Log.log(printUniverseStats());
    }
    
    private Map<Object, JType.Unary[]> obj2typeparams = new IdentityHashMap<Object, Unary[]>();
    
    private void createFreshObjects(MethodSpec ms) {
        reporter.creatingFreshObjects();
        if (ms != null) {
            for (Entry<JType.Unary, Integer> e : ms.freshObjects().entrySet()) {
                Integer num = e.getValue();
                Class<?> cls = e.getKey().clazz();
                for (int i = 0; i < num; i++) {
                    Object obj = ObjSerFactory.factory.getSerForCls(cls).newInstance(cls);
                    heap.addObject(obj);
                    obj2typeparams.put(obj, e.getKey().typeParams());
                }
            }
        }
    }

    private Map<ForgeType, Integer> maxAtomsCache = new HashMap<ForgeType, Integer>();
    
    private int getMaxAtomsForType(ForgeType ft) {
        Integer result = maxAtomsCache.get(ft);
        if (result == null) {
            int max = -1;
            for (Tuple t : ft.tupleTypes()) {
//                int tupleSize = 1; 
//                for (ForgeDomain d : t.domains()) {
//                    tupleSize *= maxSize(d);
//                }
            	int tupleSize = maxSize(t.domain()) + 1; // "+1" for length
                if (tupleSize > max)
                    max = tupleSize;
            }
            result = max;
            maxAtomsCache.put(ft, result);
        }
        return result;
    }

    private int maxSize(ForgeDomain d) {
        if (d instanceof BooleanDomain)
            return 2;
        if (d instanceof IntegerDomain) {
            if (!forgeScene.isEnsureAllInts())
                return forgeScene.ints().size() - 1;
            else 
                return 1; // can't return the number of all ints, because it is too big
        }
        InstanceDomain id = (InstanceDomain) d;
        int num = forgeScene.numLiteralsFor(id);
        return num;
    }

    public String printBounds() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ObjTupleSet> e : heap2lit.bounds().entrySet()) {
            sb.append(e.getKey() + ": " + printObjSet(e.getValue())).append("\n");
        }
        return sb.toString();
    }
    
    private String printObjSet(ObjTupleSet objSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int c = 0;
        for (ObjTuple t : objSet.tuples()) {
            if (c != 0)
                sb.append("; ");
            sb.append("[");
            for (int i = 0; i < t.arity(); i++) {
                if (i != 0)
                    sb.append(", ");
                sb.append(forgeScene.forgeLitForObj(t.get(i)));
            }
            sb.append("]");
            c++;
        }
        sb.append("]");
        return sb.toString();
    }

    public ForgeProcedure proc() {
        assert finished : "call finish() first"; 
        String name = "forge_proc";
        if (javaScene.methodSpec() != null)
            name = javaScene.method().name();
        ForgeProgram prog = forgeScene.program();
        LocalDecls ins = prog.emptyDecls();
        LocalDecls outs = prog.emptyDecls();
        if (forgeScene.thisVar() != null)
            ins = ins.and(forgeScene.thisVar());
        for (LocalVariable var : forgeScene.args())
            ins = ins.and(var);
        if (forgeScene.returnVar() != null) 
            outs = outs.and(forgeScene.returnVar());
        return prog.newProcedure(name, ins, outs);
    }
    
    public Spec getSpec() { 
        Spec spec = new Spec(javaScene, forgeScene);
        for (CaseSource cs : javaScene.methodSpec().cases()) {
            spec.addCase(cs.pre(), cs.post(), cs.frame(), javaScene.methodSpec().isHelper());
        }
        return spec;
    }

    public SolveOptions forgeOptions() {
        assert finished : "call finish() first"; 
        SatSolver satSolver = SatSolver.valueOf("SAT4J");
        return new SolveOptions.Builder()
            .reporter(reporter)
            .satSolver(satSolver)
            .build();
    }
    
    private Set<Integer> allInts = null;
    public Set<Integer> allInts() {
        if (allInts != null)
            return allInts;
        allInts = new HashSet<Integer>();
        int minInt = minInt();
        int maxInt = maxInt();
        if (forgeScene.isEnsureAllInts()) {
            for (int i = minInt; i <= maxInt; i++)
                allInts.add(i);
        } else {
            for (int i : forgeScene.ints())
                if (i >= minInt && i <= maxInt)
                    allInts.add(i);
        }
        return allInts;
    }

    private Map<ForgeType.Unary, List<InstanceLiteral>> instLitsCache = new HashMap<ForgeType.Unary, List<InstanceLiteral>>();
    public List<InstanceLiteral> findInstLiteralsForType(ForgeType.Unary colType) {
        List<InstanceLiteral> res = instLitsCache.get(colType);
        if (res == null) {
            ForgeProgram program = forgeScene.program();
            List<InstanceLiteral> lits = new LinkedList<InstanceLiteral>();
            for (InstanceLiteral l : program.instanceLiterals()) {
                if (l.type().subtypeOf(colType))
                    lits.add(l);
            }
            res = Collections.unmodifiableList(lits);
            if (finished)
                instLitsCache.put(colType, res);
        }
        return res;
    }
    
    private Map<ForgeType.Unary, List<ForgeLiteral>> litsCache = new HashMap<ForgeType.Unary, List<ForgeLiteral>>();
    /**Returns all Forge literals of the given unary Forge type */
    public List<ForgeLiteral> findLiteralsForType(ForgeType.Unary colType) {
        List<ForgeLiteral> res = litsCache.get(colType);
        if (res == null) {
            ForgeProgram program = forgeScene.program();
            LinkedList<ForgeLiteral> lits = new LinkedList<ForgeLiteral>();
            lits.addAll(findInstLiteralsForType(colType));
            if (program.booleanDomain().subtypeOf(colType)) {
                lits.add(program.trueLiteral());
                lits.add(program.falseLiteral());
            }
            if (program.integerDomain().subtypeOf(colType)) {
                for (int i : allInts())
                    lits.add(program.integerLiteral(i));
            }
            res = Collections.unmodifiableList(lits);
            if (finished)
                litsCache.put(colType, res);
        }
        return res;
    }
    
    public ObjTupleSet extent(ForgeType t) {
        // TODO: bound arrays tighter etc.
        ObjTupleSet result = null;
        for (int i = 0; i < t.arity(); i++) {
            ForgeType.Unary colType = (ForgeType.Unary) t.projectType(i);
            ObjTupleSet ots = new ObjTupleSet(1);
            for (ForgeLiteral lit : findLiteralsForType(colType)) {
                ots.add(new ObjTuple(lit2obj(lit)));
            }
            if (result == null)
                result = ots; 
            else 
                result = result.product(ots);
        }
        if (result == null)
            result = new ObjTupleSet(t.arity());
        return result;
    }
    
    public Collection<Object> univ() {
        Set<InstanceLiteral> instLits = forgeScene().program().instanceLiterals();
        Set<Integer> ints = allInts();
        Collection<Object> univ = new ArrayList<Object>(instLits.size() + ints.size() + 5);
        for (InstanceLiteral x : instLits) {
            univ.add(lit2obj(x));
        }
        univ.addAll(ints);
        univ.add(true);
        univ.add(false);
        univ.add(null);
        return univ;
    }
    
    // ----------------------------------------------------------
    
    /**
     * Converts the given Forge atom back to Java object
     */
    public Object atom2obj(ForgeAtom atom) {
        if (atom instanceof IntegerAtom) {
            return mapToInt(atom);
        } else if (atom instanceof BooleanAtom) {
            return mapToBoolean(atom);
        } else {
            Object obj = forgeScene.objForLit(atom.name());
            assert obj != null || "null".equals(atom.name()) : "cannot find object for atom " + atom.name();
            return obj;
        }
    }
    
    /**
     * Converts the given Forge literal back to Java object
     */
    public Object lit2obj(ForgeLiteral lit) {
        if (lit instanceof IntegerLiteral)
            return ((IntegerLiteral) lit).value();
        if (lit instanceof BooleanLiteral)
            return ((BooleanLiteral) lit).value();
        else {
            Object obj = forgeScene.strForLit(lit.name());
            if (obj == null)
                obj = forgeScene.objForLit(lit.name());
            assert obj != null || "null".equals(lit.name()) : "cannot find object for literal " + lit.name();
            return obj;
        }
    }
    
    public static Object mapToBoolean(final ForgeAtom atom) {
        return ((BooleanAtom) atom).value();
    }

    public static int mapToInt(final ForgeAtom atom) {
        return ((IntegerAtom) atom).value();
    }
    
    /** Returns the min int within the bounds */
    public int minInt() {
        assert finished : "Must call finish() first";
        return -(int)Math.pow(2, bw() - 1);
    }
    
    /** Returns the min int within the bounds */
    public int maxInt() {
        assert finished : "Must call finish() first";
        return (int)Math.pow(2, bw() - 1) - 1;
    }
    
    /**
     * Converts a given tuple set to Forge constant
     */
    //TODO: slow
    public ForgeConstant conv2fc(ObjTupleSet objTupleSet, ForgeBounds bounds) {
        ForgeConstant result = bounds.empty(objTupleSet.arity());
        for (ObjTuple t : objTupleSet) {
            ForgeConstant fc = obj2atom(t.get(0), bounds);
            assert fc != null : "could not convert obj " + t.get(0) + " to Forge constant";
            for (int i = 1; i < t.arity(); i++) 
                fc = fc.product(obj2atom(t.get(i), bounds));
            result = result.union(fc);
        }
        return result;
    }

    public ForgeExpression conv2fe(ObjTupleSet objTupleSet) {
        ForgeExpression result = null;
        for (ObjTuple t : objTupleSet) {
            ForgeExpression fe = conv2fe(t);
            if (result == null)
                result = fe;
            else
                result = result.union(fe);
        }
        return result;
    }
    
    public ForgeExpression conv2fe(ObjTuple t) {
        ForgeExpression fe = obj2lit(t.get(0));
        assert fe != null : "could not convert obj " + t.get(0) + " to ForgeExpression";
        for (int i = 1; i < t.arity(); i++) 
            fe = fe.product(obj2lit(t.get(i)));
        return fe;
    }

    /**
     * Returns Forge atom corresponding to the given Java object
     */
    public ForgeAtom obj2atom(Object obj, ForgeBounds bounds) {
        if (obj == null) 
            return bounds.instanceAtom(forgeScene.nullLit());
        if (obj instanceof Integer)
            return bounds.intAtom((Integer) obj);
        if (obj instanceof Boolean) 
            return bounds.boolAtom((Boolean) obj);
        return bounds.instanceAtom(forgeScene.instLitForObj(obj));
    }

    /**
     * Returns ForgeLiteral corresponding to the given Java object
     */
    public ForgeLiteral obj2lit(Object obj) {
        if (obj == null) 
            return forgeScene.nullLit();
        if (obj instanceof Integer)
            return forgeScene.program().integerLiteral((Integer) obj);
        if (obj instanceof Boolean) 
            if ((Boolean) obj)
                return forgeScene.program().trueLiteral();
            else
                return forgeScene.program().falseLiteral();
        return forgeScene.instLitForObj(obj);
    }

    // -----------------------------------------------------------
    // --------------------- private -----------------------------
    // -----------------------------------------------------------
    
    @SuppressWarnings("rawtypes")
    private void initJavaScene() {
        reporter.loadingJavaScene();
        final Set<Object> visitedObjects = new IdentityHashSet<Object>();
        final Map<Object, Unary[]> typeParams = new IdentityHashMap<Object, Unary[]>();
        final Map<Object, Field> flds = new IdentityHashMap<Object, Field>();
        // TODO: use SerObj infrastructure!
        heap.serialize(new AbstractHeapListener() {
            @Override public void newObject(Object obj) {
                visitedObjects.add(obj);
            }
            @Override public void visitRefField(Field field, Object source, Object value) {
                if (field == null) {
                    if (source instanceof Collection)
                        visitCollection((Collection) source);
                    else if (source instanceof Map)
                        visitMap((Map) source);
                    else
                        assert false;
                } else {
                    LinkedHashMap<TypeVariable, Unary> typeVarBindings = 
                            ClassSpec.getTypeVarBindings(source.getClass(), typeParams.get(source));
                    Unary[] tp = JField.newJavaField(field, typeVarBindings).getTypeParams()[0];
                    typeParams.put(value, tp);
                    flds.put(value, field);
                }
            }
            @SuppressWarnings("unchecked")
            private void visitMap(Map source) {
                Unary[] tp = typeParams.get(source);
                if (tp == null) return;
                assert tp.length == 2;
                Unary[] subTp1 = tp[0].typeParams();
                Unary[] subTp2 = tp[1].typeParams();
                for (Map.Entry e : (Set<Map.Entry>) source.entrySet()) {
                    if (subTp1 != null) typeParams.put(e.getKey(), subTp1);
                    if (subTp2 != null) typeParams.put(e.getValue(), subTp2);
                }
            }
            private void visitCollection(Collection source) {
                Unary[] tp = typeParams.get(source);
                if (tp == null) return;
                assert tp.length == 1;
                Unary[] subTp = tp[0].typeParams();
                if (subTp == null) return;
                for (Object obj : source) {
                    typeParams.put(obj, subTp);
                }
            }
        });
        for (Object obj : visitedObjects) {
            Unary[] tp = typeParams.get(obj);
            if (obj instanceof Collection<?> && tp == null)
                continue;
            if (tp == null)
                javaScene.ensureClass(obj.getClass());
            else
                javaScene.ensureClass(JType.Factory.instance.newJType(obj.getClass(), tp));
        }
    }

    private void boundLocals() {
        // bound this (if not static method)
        LocalVariable thisVar = forgeScene.thisVar();
        if (thisVar != null) {
            ObjTuple callerObjAtom = new ObjTuple(javaScene.caller());
            heap2lit.addBound(thisVar.name(), callerObjAtom);
        }
        
        // bound method arguments
        LocalVariable[] params = forgeScene.args();
        Object[] methodArgs = javaScene.methodArgs();
        assert methodArgs.length == params.length;
        for (int i = 0; i < params.length; i++) {
            ObjTuple argAtom = new ObjTuple(methodArgs[i]);
            heap2lit.addBound(params[i].name(), argAtom);
        }
    }
    
    private void boundConsts() {
        // bound constant global vars
        for (ConstRel rel : javaScene.constRels()) {
            GlobalVariable var = forgeScene.ensureConst(rel.name());
            ObjTupleSet bound = rel.accept(new ConstRel2Bound(this));
            heap2lit.addBound(var.name(), bound);
        }
    }

    private ForgeBounds makeForgeBounds(Heap2Bounds heap2lit) {
        reporter.creatingBounds();
        // the order in which bounds are applied is important
        HashMap<InstanceDomain, Integer> scopes = new HashMap<InstanceDomain, Integer>();

        // set all instance domains to default bound 
        // this bound tells how many more literals Forge should create for the given 
        // instance domain, so for Squander all these should be set to 0 (zero)
        for (InstanceDomain idom : forgeScene.domains()) {
            scopes.put(idom, 0);
        }

        ForgeBounds bounds = new ForgeBounds(forgeScene.program(), bw(), scopes);
        
        // bound global variables
        for (Entry<String, ObjTupleSet> e : heap2lit.bounds().entrySet()) {
            ForgeVariable var = forgeScene.findVar(e.getKey());
            if (var == null) continue; // var not used, so skip it
            ForgeConstant fc = conv2fc(e.getValue(), bounds); 
            bounds.boundInitial(var, fc, fc);
        }
        
        return bounds;
    }

    private String printUniverseStats() {
        Map<InstanceDomain, Integer> nums = new HashMap<InstanceDomain, Integer>();
        for (InstanceLiteral lit : forgeScene.program().instanceLiterals()) {
            Integer cnt = nums.get(lit.type());
            if (cnt == null)
                cnt = 0; 
            nums.put(lit.type(), cnt + 1);
        }
        List<Entry<InstanceDomain, Integer>> cnts = new ArrayList<Entry<InstanceDomain, Integer>>(nums.entrySet());
        Collections.sort(cnts, new Comparator<Entry<InstanceDomain, Integer>>() {
            @Override
            public int compare(Entry<InstanceDomain, Integer> o1, Entry<InstanceDomain, Integer> o2) {
                if (o1.getValue() == o2.getValue())
                    return 0; 
                if (o1.getValue() > o2.getValue())
                    return -1; 
                else 
                    return 1;
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append("Universe stats: \n");
        sb.append("-----------------------------------------------------\n");
        for (Entry<InstanceDomain, Integer> e : cnts) {
            sb.append(String.format("%40s : %s\n", e.getKey(), e.getValue()));
        }
        sb.append("=====================================================\n");
        sb.append(String.format("%40s : %s\n", "Total", forgeScene.program().instanceLiterals().size()));
        return sb.toString();
    }

}
/*! @} */
