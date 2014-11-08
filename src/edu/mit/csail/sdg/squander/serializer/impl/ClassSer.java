package edu.mit.csail.sdg.squander.serializer.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.ast.java.JFunc;
import edu.mit.csail.sdg.squander.spec.ClassSpec;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JavaScene;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;

/**
 * Serializer for java.lang.Class
 * 
 * @author Aleksandar Milicevic
 */
public class ClassSer implements IObjSer {
    
    public static final String NAME = "name";
    public static final String SIMPLE_NAME = "simpleName";
    public static final String FIELDS = "fields";
    public static final String METHODS = "methods";
    
    @Override
    public boolean accepts(Class<?> clz) {
        return Class.class.isAssignableFrom(clz);
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        Class<?> clzObj = (Class<?>) obj;
        ClassSpec classSpec = javaScene.classSpecForObj(obj);
        List<FieldValue> result = new LinkedList<FieldValue>();
        {
            JField nameFld = classSpec.findField(NAME);
            if (nameFld != null) {
                FieldValue fv = new FieldValue(nameFld, 2);
                fv.addTuple(new ObjTuple(obj, clzObj.getName()));
                result.add(fv);
            }
        }
        {
            JField nameFld = classSpec.findField(SIMPLE_NAME);
            if (nameFld != null) {
                FieldValue fv = new FieldValue(nameFld, 2);
                fv.addTuple(new ObjTuple(obj, clzObj.getSimpleName()));
                result.add(fv);
            }
        }
        {
            JField fieldsFld = classSpec.findField(FIELDS);
            if (fieldsFld != null) {
                FieldValue fvElems = new FieldValue(fieldsFld, 2);
                for (Field fld : ReflectionUtils.getAllFields(clzObj)) {
                    fvElems.addTuple(new ObjTuple(obj, new edu.mit.csail.sdg.squander.ast.java.JField(fld)));
                }
                result.add(fvElems);
            }
        }        
        {
            JField methodFld = classSpec.findField(METHODS);
            if (methodFld != null) {
                FieldValue fvElems = new FieldValue(methodFld, 2);
                for (Method meth : ReflectionUtils.getAllMethods(clzObj)) {
                    fvElems.addTuple(new ObjTuple(obj, new JFunc(meth)));
                }
                for (Constructor<?> constr : ReflectionUtils.getAllConstructors(clzObj)) {
                    fvElems.addTuple(new ObjTuple(obj, new JFunc(constr)));
                }
                result.add(fvElems);
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
