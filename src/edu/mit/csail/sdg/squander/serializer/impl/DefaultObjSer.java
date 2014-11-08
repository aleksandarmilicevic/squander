/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.impl;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.AbstractState;
import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjAbsState;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;

/**
 * Default object serializer that works for all classes.  It serializes objects
 * simply by reading their fields using Java reflection. 
 * 
 * @author Aleksandar Milicevic
 */
public class DefaultObjSer implements IObjSer {
    
    private static final AbstractState abstractState = new AbstractState();

    @Override
    public boolean accepts(Class<?> clz) {
        return true;
    }

    @Override
    public Object newInstance(Class<?> cls) {
        return ReflectionUtils.createObjectDefaultConstructor(cls);
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        try {
            ClassSpec cls = javaScene.classSpecForObj(obj);
            assert cls != null : "can't find class spec for: " + obj.getClass().getName();
            List<FieldValue> result = new LinkedList<FieldValue>();
            for (JField jf : cls.usedFieldsAll()) {
                if (!jf.isSpec()) {
                    // java field
                    Field field = jf.getJavaField();
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (jf.isStatic()) {
                        FieldValue fv = new FieldValue(jf, 1);
                        fv.addTuple(new ObjTuple(value));
                        result.add(fv);
                    } else {
                        FieldValue fv = new FieldValue(jf, 2);
                        fv.addTuple(new ObjTuple(obj, value));
                        result.add(fv);
                    }
                } else {
                    // spec fields
                    ObjAbsState objState = abstractState.getObjState(obj);
                    if (objState == null)
                        continue;
                    FieldValue fv = objState.getSpecField(jf.name());
                    if (fv != null && fv.jfield().isPureAbstract())
                        result.add(fv);
                }                
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object concrFunc(Object obj, FieldValue fldVal) {
        Field javaField = fldVal.jfield().getJavaField();
        ObjTupleSet value = fldVal.tupleSet();
        if (javaField != null) {
            assert value.arity() == 2 : "can't set non-unary value for Java field " + javaField + ": " + value;
            assert value.tuples().size() == 1 : "can't set non-tuple value for Java field " + javaField + ": " + value;
            ReflectionUtils.setFieldValue(obj, javaField, value.iterator().next().get(1));
        } else { // must be a spec field
            ObjAbsState objState = abstractState.getOrAddObjState(obj);
            objState.add(fldVal); 
        }
        return obj;
    }

}
/*! @} */
