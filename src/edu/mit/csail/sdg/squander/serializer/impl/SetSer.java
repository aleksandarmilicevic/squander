/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * Set serializer. 
 * 
 * @author Aleksandar Milicevic
 */
public class SetSer implements IObjSer {

    public static final String DATA = ArraySer.ELEMS;
    public static final String LENGTH = "length";
    
    @Override
    public boolean accepts(Class<?> clz) {
        return Set.class.isAssignableFrom(clz);
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Set newInstance(Class<?> cls) {
        return new HashSet();
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        ClassSpec cls = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();
        Set set = (Set) obj;
        
        JField dataField = cls.findField(DATA);
        if (dataField != null) {
            FieldValue fvElems = new FieldValue(dataField, 2);
            for (Object elem : set) {
                fvElems.addTuple(new ObjTuple(obj, elem));
            }
            result.add(fvElems);
        }
        
        JField lenField = cls.findField(LENGTH);
        if (lenField != null) {
            FieldValue fvLen = new FieldValue(lenField, 2);
            fvLen.addTuple(new ObjTuple(obj, set.size()));
            result.add(fvLen);
        }
        return result;
    }
    
    @Override
    public Object concrFunc(Object obj, FieldValue fieldValue) {
        String fldName = fieldValue.jfield().name();
        if (DATA.equals(fldName))
            return restoreElems(obj, fieldValue);
        else if (LENGTH.equals(fldName))
            return restoreLength(obj, fieldValue);
        else
            throw new RuntimeException("Unknown field name for Java Set: " + fldName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object restoreElems(Object obj, FieldValue fieldValue) {
        ObjTupleSet value = fieldValue.tupleSet();
        assert value.arity() == 2;
        Set set = (Set) obj;
        set.clear();
        for (ObjTuple ot : value) 
            set.add(ot.get(1));
        return set;
    }

    private Object restoreLength(Object obj, FieldValue fldVal) {
        return obj;
    }
}
/*! @} */
