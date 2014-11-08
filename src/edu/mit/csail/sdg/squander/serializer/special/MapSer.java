/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.special;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * Map serializer. 
 * 
 * @author Aleksandar Milicevic
 */
public class MapSer implements IObjSer {

    public static final String DATA = ArraySer.ELEMS;
    public static final String LENGTH = "length";
    
    @Override
    public boolean accepts(Class<?> clz) {
        return Map.class.isAssignableFrom(clz);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map newInstance(Class<?> cls) {
        return new HashMap();
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        Map<?, ?> map = (Map<?, ?>) obj;
        ClassSpec cls = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();

        JField dataFld = cls.findField(DATA);
        if (dataFld != null) {
            FieldValue fvElems = new FieldValue(dataFld, 3);
            for (Map.Entry e : map.entrySet()) {
                fvElems.addTuple(new ObjTuple(obj, e.getKey(), e.getValue()));
            }
            result.add(fvElems);
        }
        
        JField lenField = cls.findField(LENGTH);
        if (lenField != null) {
            FieldValue fvLen = new FieldValue(lenField, 2);
            fvLen.addTuple(new ObjTuple(obj, map.size()));
            result.add(fvLen);
        }
        
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
            throw new RuntimeException("Unknown field name for Java Map: " + fldName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object restoreElems(Object obj, FieldValue fieldValue) {
        ObjTupleSet value = fieldValue.tupleSet();
        assert value.arity() == 3;
        Map map = (Map) obj;
        map.clear();
        for (ObjTuple ot : value) 
            map.put(ot.get(1), ot.get(2));
        return map;
    }

}
/*! @} */
