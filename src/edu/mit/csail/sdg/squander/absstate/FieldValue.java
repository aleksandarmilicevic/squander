/*! \addtogroup AbstractState Abstract State 
 * This module is responsible for maintaining the abstract state of objects. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.absstate;

import edu.mit.csail.sdg.squander.spec.JField;

/**
 * Used to represent the value of a spec field.
 * It consists of a field definition and its value (which is a set of tuples).
 * 
 * @author Aleksandar Milicevic
 */
public class FieldValue {

    /** Field definition */
    private final JField jfield;
    /** Field value (set of tuples of Java objects) */
    private final ObjTupleSet tupleSet;

    public FieldValue(JField jfield, int arity) {
        assert jfield != null;
        this.jfield = jfield;
        this.tupleSet = new ObjTupleSet(arity);
    }

    public JField jfield() { return jfield; }

    public ObjTupleSet tupleSet() {
        return tupleSet;
    }

    public void addTuple(ObjTuple tuple) {
        tupleSet.add(tuple);
    }

    public void addAllTuples(ObjTupleSet value) {
        for (ObjTuple ot : value)
            addTuple(ot);
    }
    
    @Override
    public String toString() {
        return String.format("  (%s) -> %s\n", jfield.name(), tupleSet);
    }

}
/*! @} */
