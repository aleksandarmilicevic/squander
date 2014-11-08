/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import edu.mit.csail.sdg.squander.serializer.AbstractHeap;


/**
 * Concrete representation of the heap. The heap in this case consists of the
 * caller object (<code>callerObj</code>) and method arguments (
 * <code>methodArgs</code>).
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public class Heap extends AbstractHeap {

    /**
     * Maps <code>obj.hashCode -> obj</code>.  For all objects except for strings, 
     * <code>System.identityHashCode</code> is used, whereas for strings 
     * <code>String.hashCode</code> is used.  
     */
    private final LinkedHashMap<Integer, Object> heapObjects;

    public Heap(Object[] objects) {
        heapObjects = new LinkedHashMap<Integer, Object>();
        for (Object obj : objects) 
            if (obj != null) 
                addObject(obj);
    }

    public void addObjects(Iterable<Object> objects) {
        for (Object obj : objects)
            if (obj != null) 
                addObject(obj);
    }
    
    public void addObject(Object obj) {
        if (obj == null)
            return;
        if (obj instanceof String)
            heapObjects.put(obj.hashCode(), obj);
        else
            heapObjects.put(System.identityHashCode(obj), obj);
    }
    
    @Override
    protected List<Object> getHeapObjects() {
        return Collections.unmodifiableList(new ArrayList<Object>(heapObjects.values()));
    }
}
/*! @} */
