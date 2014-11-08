/*! \addtogroup AbstractState Abstract State 
 * This module is responsible for maintaining the abstract state of objects. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.absstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A tuple of objects.  An array of objects is used as a representation.
 * 
 * @immutable
 * 
 * @author Aleksandar Milicevic
 */
public class ObjTuple implements Iterable<Object> {

    private final int arity;
    private final Object[] tuple;

    public ObjTuple(Object... tuple) {
        this.tuple = tuple;
        this.arity = tuple.length;
    }
    
    public ObjTuple(List<Object> tuple) {
        this(tuple.toArray());
    } 
    
    public int arity()      { return arity; }
    public Object[] atoms() { return tuple; }

    public ObjTuple projection(int startCol, int endCol) {
        Object[] a = new Object[endCol - startCol + 1];
        System.arraycopy(tuple, startCol, a, 0, a.length);
        return new ObjTuple(a);
    }
    
    public ObjTuple projection(Iterable<Integer> columns) {
        List<Object> newTuple = new ArrayList<Object>(10);
        for (Integer col : columns)
            newTuple.add(tuple[col]);
        return new ObjTuple(newTuple);
    }
    
    public Object get(int index) {
        return tuple[index];
    }
    
    public Object[] tuple() { return tuple; }
    
    @Override
    public Iterator<Object> iterator() {
        return Arrays.asList(tuple).iterator();
    }

    public ObjTuple product(ObjTuple t) {
        return product(this, t);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + arity;
        result = prime * result + Arrays.hashCode(tuple);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ObjTuple other = (ObjTuple) obj;
        if (arity != other.arity)
            return false;
        if (!arrayEquals(tuple, other.tuple))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(tuple);
    }

    public static ObjTuple product(ObjTuple tuple, Object atom) {
        return product(tuple, new ObjTuple(atom));
    }
    
    public static ObjTuple product(ObjTuple t1, ObjTuple t2) {
        int arity = t1.arity + t2.arity; 
        Object[] atoms = new Object[arity]; 
        System.arraycopy(t1.tuple, 0, atoms, 0, t1.arity);
        System.arraycopy(t2.tuple, 0, atoms, t1.arity, t2.arity);
        return new ObjTuple(atoms);
    }
    
    private static boolean arrayEquals(Object[] a1, Object[] a2) {
        if (a1 == null)
            return a2 == null;
        if (a2 == null)
            return false;
        if (a1.length != a2.length)
            return false;
        for (int i = 0; i < a1.length; i++) {
            if (!objLiteralsEquals(a1[i], a2[i]))
                return false;
        }
        return true;
    }

    private static boolean objLiteralsEquals(Object o1, Object o2) {
        // ideally, should be able to convert to ForgeLiterals and then just compare their names
        // but this class doesn't have access to ForgeScene
        if (o1 == null)
            return o2 == null;
        if (o1 instanceof String || o1 instanceof Integer || o1 instanceof Boolean)
            return o1.equals(o2);
        return o1 == o2;
    }

}
/*! @} */
