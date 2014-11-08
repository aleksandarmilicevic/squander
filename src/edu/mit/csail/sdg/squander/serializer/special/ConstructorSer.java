package edu.mit.csail.sdg.squander.serializer.special;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * Serializer for java.lang.reflect.Constructor
 * 
 * @author Aleksandar Milicevic
 */
public class ConstructorSer implements IObjSer {
    public static final String NAME = "name";
    
    @Override
    public boolean accepts(Class<?> clz) {
        return Constructor.class.isAssignableFrom(clz);
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        Constructor<?> constrObj = (Constructor<?>) obj;
        ClassSpec classSpec = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();
        {
            JField nameFld = classSpec.findField(NAME);
            if (nameFld != null) {
                FieldValue fv = new FieldValue(nameFld, 2);
                fv.addTuple(new ObjTuple(obj, constrObj.getName()));
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
