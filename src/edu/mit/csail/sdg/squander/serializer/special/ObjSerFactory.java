/*! \addtogroup Serialization Serialization 
 * This module is in charge of serializing and deserializing objects (i.e. it provides abstraction and concretization functions for several types of classes). 
 * @{ 
 */
package edu.mit.csail.sdg.squander.serializer.special;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Factory class responsible for returning the right serializer for a give class. 
 * 
 * @author Aleksandar Milicevic
 */
public class ObjSerFactory {

    public static final ObjSerFactory factory = new ObjSerFactory();
    
    private Stack<IObjSer> serializers = new Stack<IObjSer>();
    private Map<Class<?>, IObjSer> cache = new HashMap<Class<?>, IObjSer>();

    /**
     * Returns the right serializer for the given object
     */
    public IObjSer getSerForObj(Object obj) {
        return getSerForCls(obj.getClass());
    }
    
    /**
     * Returns the right serializer for the given class 
     */
    public IObjSer getSerForCls(Class<?> clz) {
        IObjSer result = cache.get(clz);
        if (result == null) {
            for (IObjSer ser : serializers) {
                if (ser.accepts(clz)) {
                    result = ser;
                    break;
                }
            }
            cache.put(clz, result);
        }
        return result;
    }
    
    private ObjSerFactory() {
        serializers.push(new ArraySer());
        serializers.push(new ListSer());
        serializers.push(new SetSer());
        serializers.push(new MapSer());
        serializers.push(new StringSer());
        serializers.push(new ClassSer());
        serializers.push(new DefaultObjSer());
    }

}
/*! @} */
