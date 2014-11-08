/*! \addtogroup AbstractState Abstract State 
 * This module is responsible for maintaining the abstract state of objects. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.absstate;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Used to store the abstract state (values of spec fields) of all heap objects. 
 * 
 * @author Aleksandar Milicevic
 */
public class AbstractState {
    
    /**
     * Keeps abstract states of different heap objects.
     */
    private IdentityHashMap<Object, ObjAbsState> obj2AbsState = new IdentityHashMap<Object, ObjAbsState>();

    public Collection<Object> getAllObjects() { return obj2AbsState.keySet(); }
    
    public ObjAbsState getObjState(Object obj) {
        return obj2AbsState.get(obj);
    }
    
    public ObjAbsState getOrAddObjState(Object obj) {
        ObjAbsState objState = getObjState(obj); 
        if (objState == null) {
            objState = new ObjAbsState(obj);
            setObjState(obj, objState);
        }
        return objState;
    }

    public void setObjState(Object obj, ObjAbsState objState) {
        obj2AbsState.put(obj, objState);
    }

    public void clear() {
        obj2AbsState.clear();
    }

    public Set<Entry<Object, ObjAbsState>> getEntries() {
        return obj2AbsState.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object obj : obj2AbsState.keySet()) {
            sb.append(obj2AbsState.get(obj)).append("\n");
        }
        return sb.toString();
    }

    public Collection<ObjAbsState> getAllObjStates() {
        return obj2AbsState.values();
    }
    
}
/*! @} */
