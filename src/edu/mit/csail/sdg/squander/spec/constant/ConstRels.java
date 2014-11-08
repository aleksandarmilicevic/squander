/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.constant;

import java.util.HashMap;
import java.util.Map;

public class ConstRels {

    private static Map<String, ConstRel> enums; 
    
    static {
        enums = new HashMap<String, ConstRel>();
        for (ConstRel ec : ConstRel.class.getEnumConstants()) {
            enums.put(ec.name(), ec);
        }
    }
    
    public static ConstRel findRel(String text) {
        return enums.get(text);
    }

}
/*! @} */
