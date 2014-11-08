/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.impl;

import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * String serializer. 
 * 
 * @author Aleksandar Milicevic
 */
public class StringSer implements IObjSer {

    @Override
    public boolean accepts(Class<?> clz) {
        return String.class == clz;
    }

    @Override
    public Object newInstance(Class<?> cls) {
        throw new RuntimeException("cannot create a new String");
    }

    @Override
    public Object concrFunc(Object obj, FieldValue fieldValue) {
        throw new RuntimeException("There should be no fields for Java String class");
    }

    @Override
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj) {
        return new LinkedList<FieldValue>();
    }

}
/*! @} */
