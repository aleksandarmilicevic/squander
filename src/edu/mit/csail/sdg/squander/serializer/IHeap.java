/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer;

import java.lang.reflect.Field;

import edu.mit.csail.sdg.squander.utils.Predicate;


/**
 * An abstract representation of the heap (at least the part that we care about)
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public interface IHeap {

    /**
     * Serializes whole heap (all object on the heap). The given listener gets
     * notified about all visited objects and their fields.
     * 
     * @param listener
     *            : gets informed about traversed objects/fields
     * @param pred
     *            : tell which fields to follow during the serialization
     */
    public void serialize(HeapListener listener, Predicate<Field> pred);

}
/*! @} */
