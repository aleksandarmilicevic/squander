/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
/**
 * 
 */
package edu.mit.csail.sdg.squander.serializer;

import java.lang.reflect.Field;

/**
 * Just prints out things to the console, as told to it by a Serializer.
 * 
 * @see Serializer
 * @see HeapListener
 * @author drayside
 *
 */
public class ConsoleHeapListener implements HeapListener {

    @Override
    public void newObject(final Object obj) {
        System.out.println(obj.getClass().getCanonicalName() + " " + System.identityHashCode(obj));
    }
    
    @Override
    public void visitPrimitive(Object obj) {
        newObject(obj);
    }

    @Override
    public void visitRefField(final Field field, final Object source, final Object value) {
        if (field == null)
            return;
        assert !field.getType().isPrimitive();
        int sourceId = System.identityHashCode(source);
        int targetId = System.identityHashCode(value);
        System.out.println(field.getName() + " " + sourceId + " " + targetId);
    }

    @Override
    public void visitIntField(final Field field, final Object source, final int value) {
        assert field.getType().equals(int.class);
        int sourceId = System.identityHashCode(source);
        System.out.println(field.getName() + " " + sourceId + " int" + value);
    }

    @Override
    public void visitBooleanField(final Field field, final Object source, final boolean value) {
        assert field.getType().equals(boolean.class);
        int sourceId = System.identityHashCode(source);
        System.out.println(field.getName() + " " + sourceId + " " + value);
    }

    @Override
    public void visitArrayIntField(Object sourceArray, int index, int value) {
        assert sourceArray.getClass().getComponentType() != null;
        assert sourceArray.getClass().getComponentType().equals(int.class);
        int sourceId = System.identityHashCode(sourceArray);
        System.out.println(sourceId + "[" + index + "] = (int)" + value);
    }

    @Override
    public void visitArrayBoolField(Object sourceArray, int index, boolean value) {
        assert sourceArray.getClass().getComponentType() != null;
        assert sourceArray.getClass().getComponentType().equals(boolean.class);
        int sourceId = System.identityHashCode(sourceArray);
        System.out.println(sourceId + "[" + index + "] = (boolean)" + value);
    }

    @Override
    public void visitArrayRefField(Object sourceArray, int index, Object value) {
        assert sourceArray.getClass().getComponentType() != null;
        assert !sourceArray.getClass().getComponentType().isPrimitive();
        int sourceId = System.identityHashCode(sourceArray);
        System.out.println(sourceId + "[" + index + "] = (Object)" + value);
    }

    @Override
    public void visitArrayLength(Object sourceArray, final int length) {
        assert sourceArray.getClass().getComponentType() != null;
        int sourceId = System.identityHashCode(sourceArray);
        System.out.println(sourceId + ".length = " + length);
    }
    
}
/*! @} */
