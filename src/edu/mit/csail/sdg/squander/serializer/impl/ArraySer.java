/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.impl;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JavaScene;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;

/**
 * Array serializer.  
 * 
 * @author Aleksandar Milicevic
 */
public class ArraySer implements IObjSer {

    public static final String ELEMS = "elts";
    public static final String LENGTH = "length";
    
    @Override
    public boolean accepts(Class<?> clz) {
        return clz.isArray();
    }

    @Override
    public Object newInstance(Class<?> cls) {
        return ReflectionUtils.createNewArray(cls.getComponentType(), 0);
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        ClassSpec cls = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();
        FieldValue fvElems = new FieldValue(cls.findField(ELEMS), 3);
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            fvElems.addTuple(new ObjTuple(obj, i, Array.get(obj, i)));
        }
        result.add(fvElems);
        
        FieldValue fvLen = new FieldValue(cls.findField(LENGTH), 2);
        fvLen.addTuple(new ObjTuple(obj, len));
        result.add(fvLen);
        
        return result;
    }
    
    @Override
    public Object concrFunc(Object obj, FieldValue fieldValue) {
        String fldName = fieldValue.jfield().name();
        assert obj.getClass().isArray();
        if (ELEMS.equals(fldName))
            return restoreElems(obj, fieldValue);
        else if (LENGTH.equals(fldName))
            return restoreLength(obj, fieldValue);
        else if (!fieldValue.jfield().isPureAbstract())
            return obj;
        else
            throw new RuntimeException("Unknown field name for Java array: " + fldName);
    }

    private Object restoreElems(Object obj, FieldValue fieldValue) {
        ObjTupleSet value = fieldValue.tupleSet();
        assert value.arity() == 3;
        Object arr = obj; 
        int len = value.tuples().size();
        if (Array.getLength(obj) != len)
            arr = Array.newInstance(obj.getClass().getComponentType(), len);
        for (ObjTuple ot : value.tuples()) {
            int idx = (Integer) ot.get(1);
            Object elem = ot.get(2);
            Array.set(arr, idx, elem);
        }
        return arr;
    }

    private Object restoreLength(Object obj, FieldValue fldVal) {
        return obj;
    }

}
/*! @} */
