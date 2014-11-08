/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.serializer.impl.IObjSer;
import edu.mit.csail.sdg.squander.serializer.impl.ObjSerFactory;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JType;
import edu.mit.csail.sdg.squander.spec.JavaScene;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.utils.Utils;
import forge.program.ForgeVariable;
import kodkod.util.collections.IdentityHashSet;

// ==========================================================
// ------------------ class Heap2Lit ------------------------
// ==========================================================

/**
 * Used to convert the heap to bounds for fields, that are going
 * to be used later for solving.
 * 
 * @author Aleksandar Milicevic
 */
public class Heap2Bounds {
    
    /**
     * Wrapper class for a pair of type: <code>&lt;Object, JType.Unary[]&gt;</code> 
     */
    public static class Pair {
        Object obj; 
        JType.Unary[] actualTypeArgs;

        public Pair(Object obj) {
            init(obj);
        }
        
        public Pair(Object obj, Unary[] actualTypeArgs) {
            if (actualTypeArgs == null)
                init(obj);
            else
                init(obj, actualTypeArgs);
        }

        @Override
        public String toString() {
            return String.format("(%s, %s)", obj, Arrays.toString(actualTypeArgs));
        }

        private void init(Object obj) {
            this.obj = obj;
            this.actualTypeArgs = new Unary[obj.getClass().getTypeParameters().length];
            for (int i = 0; i < actualTypeArgs.length; i++)
                actualTypeArgs[i] = JType.Factory.instance.newJType(Object.class);   
        }

        private void init(Object obj, Unary[] actualTypeArgs) {
            assert actualTypeArgs.length == obj.getClass().getTypeParameters().length;
            this.obj = obj;
            this.actualTypeArgs = actualTypeArgs;
        }
    }

    private final JavaScene javaScene;
    private final Set<Object> visited = new IdentityHashSet<Object>();
    
    private Map<Class<?>, Set<Object>> world = new HashMap<Class<?>, Set<Object>>();
    private Map<String, ObjTupleSet> varName2Const = new HashMap<String, ObjTupleSet>();
    private int rbw = SquanderGlobalOptions.INSTANCE.min_bitwidth;
    
    public Heap2Bounds(JavaScene javaScene) {
        this.javaScene = javaScene;
    }
    
    /** Returns min required bit width, based on the content of the heap */
    public int minBW()                                    { return rbw; }
    /** Returns the exact bounds for all fields (field names, that is) */
    public Map<String, ObjTupleSet> bounds()              { return varName2Const; }
    /** Returns the exact bound for the field represented by the given Forge variable */
    public ObjTupleSet getBound(ForgeVariable var)        { return varName2Const.get(var.name()); }
    /** removes the bound for the given variable */
    public ObjTupleSet removeBoundFor(ForgeVariable var)  { return varName2Const.remove(var.name()); }

    /** Returns all reachable objects found on the heap */
    public Set<Object> reachableObjects()                 { return visited; }
    public Set<Object> objectsForClass(Class<?> cls)      { return Collections.unmodifiableSet(myObjectsForClass(cls)); } 
    private Set<Object> myObjectsForClass(Class<?> cls)   {
        Set<Object> ret = world.get(cls);
        if (ret == null) {
            ret = new IdentityHashSet<Object>();
            world.put(cls, ret); 
        }
        return ret;
    }

    /** Makes sure that the bitwidth is big enough to represent the given integer value */
    public void ensureAdequateIntBitWidth(int x) {
        int pos = x >= 0 ? x : Math.abs(x) - 1;
        int bw = 1 + (32 - Integer.numberOfLeadingZeros(pos));
        rbw = Math.max(rbw, bw);
    }
    
    /**
     * Traverses the heap starting from the given root objects (<code>rootObjects</code>).
     * It uses {@link IObjSer} to serialize objects (i.e. break them apart).  During
     * the traversal it collects the bounds for fields. 
     */
    public void traverse(List<Pair> rootObjects) {
        List<Pair> workList = new LinkedList<Pair>();
        for (Pair p : rootObjects)
            workList.add(new Pair(p.obj, p.actualTypeArgs));
        ObjSerFactory f = ObjSerFactory.factory;
        while (!workList.isEmpty()) {
            Pair p = workList.remove(0);
            if (!visited.add(p.obj))
                continue;
            newObject(p.obj, p.actualTypeArgs);
            IObjSer ser = f.getSerForObj(p.obj);
            List<FieldValue> vals = ser.absFunc(javaScene, p.obj);
            for (FieldValue val : vals) { 
                Unary[][] tparams = val.jfield().getTypeParams();
                assert tparams.length == val.jfield().type().arity();
                addBound(val.jfield().fullName(), val.tupleSet());
                for (ObjTuple ot : val.tupleSet()) {
                    for (int i = 0; i < ot.arity(); i++) {
                        Object atom = ot.get(i);
                        if (atom == null)
                            continue;
                        workList.add(new Pair(atom, i == 0 ? null : tparams[i-1]));
                    }
                }
            }
        }
    }
    
    void addBound(String varName, ObjTuple value) {
        ObjTupleSet ots = new ObjTupleSet(value.arity());
        ots.add(value);
        addBound(varName, ots);
    }
    
    void addBound(String varName, ObjTupleSet value) {
        ObjTupleSet curVal = varName2Const.get(varName);
        if (curVal == null) {
            curVal = new ObjTupleSet(value.arity());
            varName2Const.put(varName, curVal);
        } 
        for (ObjTuple ot : value)
            curVal.add(ot);
    }
    
    // ----------------------------------------------------------------------
    // ------------------------- private ------------------------------------
    // ----------------------------------------------------------------------
    
    private void newObject(Object object, Unary[] actualTypeParams) {
        if (object == null)
            return;
        classifyObj(object);
        ensureAdequateBitWidth(object);
        Class<?> cls = object.getClass();
        ClassSpec cs = javaScene.ensureClass(JType.Factory.instance.newJType(cls, actualTypeParams));
        javaScene.addObj2spec(object, cs);
        if (Utils.isPrimitive(cls))
            visitPrimitive(object);
    }

    private void classifyObj(Object object) {
        myObjectsForClass(object.getClass()).add(object);
    }

    private void visitPrimitive(Object obj) {
        if (obj.getClass() == Integer.class) {
            int intVal = (Integer) obj;
            ensureAdequateIntBitWidth(intVal);
        }
    }

    private void ensureAdequateBitWidth(final Object obj) {
        if (obj == null)
            return;
        final Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            // array, not regular object
            if (cls.getComponentType().isPrimitive()) {
                // array of primitives
                if (cls.getComponentType().equals(int.class)) {
                    int[] a = (int[]) obj;
                    for (int x : a) {
                        ensureAdequateBitWidth(x);
                    }
                }
            } else {
                // array of objects -- doesn't matter
            }
        } else {
            // regular object, only care about integers
            if (obj instanceof Integer || obj.getClass().equals(int.class)) {
                ensureAdequateIntBitWidth((Integer) obj);
            }
        }
    }

}
    
    
    
/*! @} */
