/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.special;

import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * List serializer. 
 * 
 * @author Aleksandar Milicevic
 */
public class ListSer implements IObjSer {

    public static final String DATA = ArraySer.ELEMS;
    
    @Override
    public boolean accepts(Class<?> clz) {
        return List.class.isAssignableFrom(clz);
    }

    @Override
    public List<?> newInstance(Class<?> cls) {
        return new LinkedList<Object>();
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        List<?> lst = (List<?>) obj;
        ClassSpec cls = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();
        
        JField dataFld = cls.findField(DATA);
        if (dataFld != null) {
            FieldValue fvElems = new FieldValue(dataFld, 3);
            int idx = 0;
            for (Object elem : lst) {
                fvElems.addTuple(new ObjTuple(obj, idx++, elem));
            }
            result.add(fvElems);
        }
        
//        JField lenFld = cls.findField(LENGTH);
//        if (lenFld != null) {
//            FieldValue fvLen = new FieldValue(lenFld, 2);
//            fvLen.addTuple(new ObjTuple(obj, lst.size()));
//            result.add(fvLen);
//        }
        
        return result;
    }
    
    @Override
    public Object concrFunc(Object obj, FieldValue fieldValue) {
        String fldName = fieldValue.jfield().name();
        if (DATA.equals(fldName))
            return restoreElems(obj, fieldValue);
//        else if (LENGTH.equals(fldName))
//            return restoreLength(obj, fieldValue);
        else if (!fieldValue.jfield().isPureAbstract())
            return obj;
        else 
            throw new RuntimeException("Unknown field name for Java List: " + fldName);
    }

    @SuppressWarnings("unchecked")
    private Object restoreElems(Object obj, FieldValue fieldValue) {
        ObjTupleSet value = fieldValue.tupleSet();
        assert value.arity() == 3;
        List<Object> lst = (List<Object>) obj;
        int len = value.tuples().size();
        lst.clear();
        Object[] lstArr = new Object[len];
        for (ObjTuple ot : value) 
            lstArr[(Integer) ot.get(1)] = ot.get(2);
        for (int i = 0; i < len; i++)
            lst.add(lstArr[i]);
        return lst;
    }

//    private Object restoreLength(Object obj, FieldValue fldVal) {
//        return obj;
//    }

}
/*! @} */
