/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.impl;

import java.util.List;

import edu.mit.csail.sdg.squander.absstate.FieldValue;
import edu.mit.csail.sdg.squander.spec.JavaScene;

/**
 * Interface for object serialization/deserialization 
 * 
 * @author Aleksandar Milicevic
 */
public interface IObjSer {

    /** returns whether this serializer supports (can serialize) the given class */
    public boolean accepts(Class<?> clz);
    
    /** creates a new instance of the given class */
    public Object newInstance(Class<?> cls);
    
    /** abstraction function */
    public List<FieldValue> absFunc(JavaScene javaScene, Object obj);
    
    /** concretization function */
    public Object concrFunc(Object obj, FieldValue fieldValue);

}
/*! @} */
