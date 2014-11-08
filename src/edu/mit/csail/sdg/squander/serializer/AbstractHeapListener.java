/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer;

import java.lang.reflect.Field;

/**
 * Provides no-ops for every required method.
 * 
 * @author drayside
 *
 */

public abstract class AbstractHeapListener implements HeapListener {

    @Override public void newObject(Object obj) {}
    @Override public void visitPrimitive(Object obj) {}
    @Override public void visitArrayIntField(Object sourceArray, int index, int value) {}
    @Override public void visitArrayBoolField(Object sourceArray, int index, boolean value) {}
    @Override public void visitArrayLength(Object sourceArray, int length) {}
    @Override public void visitArrayRefField(Object sourceArray, int index, Object value) {}
    @Override public void visitBooleanField(Field field, Object obj, boolean value) {}
    @Override public void visitIntField(Field field, Object source, int value) {}
    @Override public void visitRefField(Field field, Object source, Object value) {}

}
/*! @} */
