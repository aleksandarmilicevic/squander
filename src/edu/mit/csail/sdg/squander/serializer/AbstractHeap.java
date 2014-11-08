/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer;

import java.lang.reflect.Field;
import java.util.List;

import edu.mit.csail.sdg.squander.utils.Predicate;



/**
 * Common abstract base class for all (or some) concrete heap classes.
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public abstract class AbstractHeap implements IHeap {

    public void serialize(HeapListener listener) {
        serialize(listener, new Predicate.TruePred<Field>());
    }
    
    @Override
    public void serialize(HeapListener listener, Predicate<Field> pred) {
        final Serializer s = new Serializer();
        for (Object obj : getHeapObjects()) {
            s.serialize(obj, listener, pred);
        }
    }
    
    /**
     * Returns all object on this heap.
     */
    protected abstract List<Object> getHeapObjects();

}
/*! @} */
