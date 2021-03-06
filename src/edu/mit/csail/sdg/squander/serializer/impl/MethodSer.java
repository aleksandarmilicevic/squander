package edu.mit.csail.sdg.squander.serializer.impl;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * Serializer for java.lang.reflect.Method
 * 
 * @author Aleksandar Milicevic
 */
public class MethodSer implements IObjSer {
    
    public static final String NAME = "name";
    
    @Override
    public boolean accepts(Class<?> clz) {
        return Method.class.isAssignableFrom(clz);
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        Method methObj = (Method) obj;
        ClassSpec classSpec = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();
        {
            JField nameFld = classSpec.findField(NAME);
            if (nameFld != null) {
                FieldValue fv = new FieldValue(nameFld, 2);
                fv.addTuple(new ObjTuple(obj, methObj.getName()));
            }
        }
        return result;
    }

    @Override
    public Object newInstance(Class<?> clz) {
        throw new IllegalStateException();
    }
    
    @Override
    public Object concrFunc(Object obj, FieldValue fieldValue) {
        throw new IllegalStateException();
    }


}
