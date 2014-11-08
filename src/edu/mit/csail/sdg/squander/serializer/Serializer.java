/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;

import edu.mit.csail.sdg.squander.utils.Predicate;
import edu.mit.csail.sdg.squander.utils.Utils;
import edu.mit.csail.sdg.util.reflection.FieldPredicates;

public class Serializer {
    
    private final Map<Integer, Object> serializedObjects = new HashMap<Integer, Object>();

    private static enum Phase { Serialization, Mutation, Disposed }
    private Phase phase = Phase.Serialization;
    
    /**
     * Returns an unmodifiable list of the objects that have been serialized.
     * @return
     */
    public List<Object> serializedObjects() {
        return new ArrayList<Object>(serializedObjects.values());
    }
    
    /**
     * @param obj
     *            : the root object (to start the serialization from)
     * @param listener
     *            : gets informed about traversed object/fields
     * @param fieldPred
     *            : simple predicate function. Used during the heap
     *            serialization (traversal) to determine whether a field should
     *            be followed or not.
     */
    public void serialize(Object obj, HeapListener listener, Predicate<Field> fieldPred) {
        if (phase != Phase.Serialization) {
            throw new IllegalStateException("already moved on to phase " + phase);
        }
        
        if (obj == null) {
            return;
        }
        
        if (Utils.isPrimitive(obj.getClass())) {
            listener.visitPrimitive(obj);
            return;
        }

        // basic information
        //final Class<?> cls = obj.getClass();
        final int id = System.identityHashCode(obj);

        // check that we haven't serialized this object already
        if (serializedObjects.put(id, obj) != null)
            return;
        
        // start serializing
        listener.newObject(obj);
        
        if (obj.getClass().isArray()) {
            serializeArray(obj, listener, fieldPred);
        } else {
            serializeClass(obj, listener, fieldPred);
        }
    }


    /**
     * Goes through the elements of the given array, notifies the given listener 
     * about those array elements, and finally recursively calls serialize on
     * every array element (unless array is of primitive type)
     * 
     * @author Aleksandar Milicevic
     * @param fieldPred 
     */
    private void serializeArray(Object obj, HeapListener listener, Predicate<Field> fieldPred) {
        Class<?> cls = obj.getClass();
        Class<?> arrCls = cls.getComponentType();
        if (arrCls.isPrimitive()) {
            if (arrCls == int.class) {
                traverseIntArray(obj, listener);
            } else if (arrCls == boolean.class) {
                traverseBoolArray(obj, listener);
            } else {
                throw new RuntimeException("UNSUPPORTED PRIMITIVE TYPE: " + arrCls.getName());
            }
        } else {
            traverseObjArray(obj, listener, fieldPred);
        }
    }

    /**
     * Notifies the given listener about int array elements
     */
    private void traverseIntArray(Object obj, HeapListener listener) {
        int len = Array.getLength(obj);
        listener.visitArrayLength(obj, len);
        for (int i = 0; i < len; i++) {
            listener.visitArrayIntField(obj, i, Array.getInt(obj, i));
        }
    }

    private void traverseBoolArray(Object obj, HeapListener listener) {
        int len = Array.getLength(obj);
        listener.visitArrayLength(obj, len);
        for (int i = 0; i < len; i++) {
            listener.visitArrayBoolField(obj, i, Array.getBoolean(obj, i));
        }
    }
    
    /**
     * Notifies the given listener about reference array elements, and
     * after that recursively calls serialize for every one of them. 
     */
    private void traverseObjArray(Object obj, HeapListener listener, Predicate<Field> fieldPred) {
        List<Object> arrElems = new LinkedList<Object>();
        int len = Array.getLength(obj);
        listener.visitArrayLength(obj, len);
        for (int i = 0; i < len; i++) {
            Object value = Array.get(obj, i);
            listener.visitArrayRefField(obj, i, value);
            arrElems.add(value);
        }
        for (Object arrElem : arrElems) {
            serialize(arrElem, listener, fieldPred);
        }
    }


    private void serializeClass(Object obj, HeapListener listener, Predicate<Field> fieldPred) {
        if (obj.getClass() == String.class)
            return;
        if (obj instanceof Collection<?>) {
            listener.visitRefField(null, obj, obj);
            for (Object o : (Collection<?>) obj) {
                serialize(o, listener, fieldPred);
            }
            return;
        }
            
        if (obj instanceof Map<?, ?>) {
            listener.visitRefField(null, obj, obj);
            for (Entry<?, ?> e : ((Map<?, ?>) obj).entrySet()) {
                serialize(e.getKey(), listener, fieldPred);
                serialize(e.getValue(), listener, fieldPred);
            }
            return;
        }

        final SortedSet<Field> fields = FieldPredicates.satisfyingFields(
                FieldPredicates.AllNonTransientInstanceFields, obj.getClass(), Object.class);

        for (final Field field : fields) {
            try {
                if (!fieldPred.exe(field))
                    continue;
                field.setAccessible(true);
                if (field.getType().isPrimitive()) {
                    // primitive type
                    if (field.getType().equals(int.class)) {
                        final int value = field.getInt(obj);
                        listener.visitIntField(field, obj, value);
                    } else if (field.getType().equals(boolean.class)) {
                        final boolean value = field.getBoolean(obj);
                        listener.visitBooleanField(field, obj, value);
                    } else {
                        // TODO: enhance to serialize floats, etc
                        System.err.println("UNSUPPORTED PRIMITIVE TYPE: " + field);
                    }
                } else {
                    // reference type
                    final Object value = field.get(obj);
                    listener.visitRefField(field, obj, value);
                    serialize(value, listener, fieldPred);
                }
            } catch (final IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void mutate(final Field field, final int sourceId, final int targetId) {
        if (phase.compareTo(Phase.Mutation) > 0) {
            throw new IllegalStateException("already moved on to phase " + phase);
        }
        phase = Phase.Mutation;
        
        final Object source = serializedObjects.get(sourceId);
        final Object target = serializedObjects.get(targetId);
        field.setAccessible(true);
        try {
            field.set(source, target);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void dispose() {
        phase = Phase.Disposed;
        serializedObjects.clear();
    }

}
/*! @} */
