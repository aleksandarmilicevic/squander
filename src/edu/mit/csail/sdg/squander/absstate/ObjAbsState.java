/*! \addtogroup AbstractState Abstract State 
 * This module is responsible for maintaining the abstract state of objects. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.absstate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.mit.csail.sdg.squander.spec.JField;


/**
 * The abstract state of an object consists of <code>FieldValue</code>s.
 * 
 * @author Aleksandar Milicevic
 */
public class ObjAbsState implements Iterable<FieldValue> {

    private final Object obj;

    /** Values of spec fields */
    private Map<String, FieldValue> specFields = new HashMap<String, FieldValue>();

    public ObjAbsState(Object obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public Iterator<FieldValue> iterator() {
        return specFields.values().iterator();
    }

    public void add(FieldValue e) {
        specFields.put(e.jfield().name(), e);
    }

    public FieldValue getSpecField(String name) {
        return specFields.get(name);
    }
    
    public FieldValue getOrAddSpecField(JField jf, int arity) {
        String name = jf.name();
        FieldValue sf = getSpecField(name);
        if (sf == null) {
            sf = new FieldValue(jf, arity);
            specFields.put(name, sf);
        }
        return sf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("obj: " + obj + "\n");
        for (String name : specFields.keySet()) {
            sb.append(specFields.get(name)).append("\n");
        }
        return sb.toString();
    }

}
/*! @} */
