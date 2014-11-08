/*! \addtogroup AbstractState Abstract State 
 * This module is responsible for maintaining the abstract state of objects. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.absstate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import edu.mit.csail.sdg.squander.engine.ForgeConverter;
import forge.solve.ForgeAtom;
import forge.solve.ForgeConstant;
import forge.solve.ForgeConstant.Tuple;

/**
 * A set of tuples of objects ({@link ObjTuple})
 * 
 * @author Aleksandar Milicevic
 */
public class ObjTupleSet implements Iterable<ObjTuple> {
    
    private final int arity;
    private final Set<ObjTuple> tuples = new HashSet<ObjTuple>();

    public ObjTupleSet(int arity) {
        this.arity = arity;
    }

    public ObjTupleSet(ObjTuple val) {
        this(val.arity());
        tuples.add(val);
    }

    public int arity()       { return arity; }
    public boolean isEmpty() { return tuples.isEmpty(); }
    public boolean isTuple() { return tuples.size() == 1; }
    public boolean isUnary() { return arity == 1; }
    
    public boolean add(ObjTuple e) {
        assert arity == e.arity();
        return tuples.add(e);
    }
    
    public boolean contains(Object o) { return tuples.contains(o); }
    public boolean remove(Object o)   { return tuples.remove(o); }
    public int size()                 { return tuples.size(); }
    public Set<ObjTuple> tuples()     { return Collections.unmodifiableSet(tuples); }

    @Override public Iterator<ObjTuple> iterator() { return tuples.iterator(); }
    @Override public String toString() { return tuples.toString(); }

    /** Performs relational join and returns the result as a new tuple set */
    public ObjTupleSet join(ObjTupleSet ots)         { return join(this, ots); }
    /** Performs relational product and returns the result as a new tuple set */
    public ObjTupleSet product(ObjTupleSet ots)      { return product(this, ots); }
    /** Returns the union of this tuple set and the given tuple set */
    public ObjTupleSet union(ObjTupleSet ots)        { return union(this, ots); }
    /** Returns the union of this tuple set and the given tuple */
    public ObjTupleSet union(ObjTuple val)           { return union(new ObjTupleSet(val)); }
    /** Performs set difference and returns the result as a new tuple set */
    public ObjTupleSet diff(ObjTupleSet ots)         { return diff(this, ots); }
    /** Returns whether this tuple set is a subset of the given tuple set */
    public boolean subsetOf(ObjTupleSet rhs)         { return rhs.tuples.containsAll(tuples); }
    /** Performs set intersection and returns the result as a new tuple set */
    public ObjTupleSet intersection(ObjTupleSet ots) { return intersection(this, ots); }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + arity;
        result = prime * result + ((tuples == null) ? 0 : tuples.hashCode());
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
        ObjTupleSet other = (ObjTupleSet) obj;
        if (arity != other.arity)
            return false;
        if (tuples == null) {
            if (other.tuples != null)
                return false;
        } else if (!tuples.equals(other.tuples))
            return false;
        return true;
    }

    /**
     * Returns a new tuple set containing only the given tuple
     */
    public static ObjTupleSet singleTuple(Object... tuple) {
        ObjTupleSet ots = new ObjTupleSet(tuple.length);
        ots.add(new ObjTuple(tuple));
        return ots;
    }

    /**
     * Returns a new tuple set containing the tuples from <code>otset</code> which 
     * contain object <code>obj</code> at position <code>i</code>
     */
    public static ObjTupleSet filter(ObjTupleSet otset, int i, Object obj) {
        ObjTupleSet result = new ObjTupleSet(otset.arity);
        for (ObjTuple ot : otset) {
            if (ot.get(i) == obj)
                result.add(ot);
        }
        return result;
    }

    /**
     * Performs relational join of the given two tuple sets and returns the 
     * result as a new tuple set.
     */
    public static ObjTupleSet join(ObjTupleSet lhs, ObjTupleSet rhs) {
        assert lhs.arity > 0; 
        assert rhs.arity > 0; 
        assert lhs.arity + rhs.arity > 2;
        ObjTupleSet result = new ObjTupleSet(lhs.arity + rhs.arity - 2);
        for (ObjTuple l : lhs) {
            for (ObjTuple r : rhs) {
                if (l.get(l.arity() - 1) == r.get(0)) {
                    Object[] atoms = new Object[result.arity]; 
                    System.arraycopy(l.tuple(), 0, atoms, 0, l.arity() - 1);
                    System.arraycopy(r.tuple(), 1, atoms, l.arity() - 1, r.arity() - 1);
                    result.add(new ObjTuple(atoms));
                }
            }
        }
        return result;
    }
    
    /**
     * Performs relational product of the given two tuple sets and returns the 
     * result as a new tuple set.
     */
    public static ObjTupleSet product(ObjTupleSet lhs, ObjTupleSet rhs) {
        int arity = lhs.arity + rhs.arity;
        ObjTupleSet result = new ObjTupleSet(arity);
        for (ObjTuple t1 : lhs.tuples) 
            for (ObjTuple t2 : rhs.tuples)
                result.add(t1.product(t2));
        return result;
    }

    /**
     * Converts the given Forge constant to a tuple set
     */
    public static ObjTupleSet convertFrom(ForgeConstant val, ForgeConverter fconv) {
        ObjTupleSet result = new ObjTupleSet(val.arity());
        for (Tuple t : val.tuples()) {
            ObjTuple ot = convertToObjTuple(t, fconv);
            result.add(ot);
        }
        return result;
    }

    /**
     * Converts the given Forge tuple to <code>ObjTuple</code>
     */
    private static ObjTuple convertToObjTuple(Tuple t, ForgeConverter fconv) {
        Object[] res = new Object[t.arity()];
        int idx = 0; 
        for (ForgeAtom a : t.atoms()) {
            res[idx++] = fconv.atom2obj(a);
        }
        return new ObjTuple(res);
    }

    /**
     * Returns the set union of the two given tuple sets. 
     */
    public static ObjTupleSet union(ObjTupleSet lhs, ObjTupleSet rhs) {
        assert (lhs.arity == rhs.arity);
        ObjTupleSet result = new ObjTupleSet(lhs.arity);
        result.tuples.addAll(lhs.tuples); 
        result.tuples.addAll(rhs.tuples);
        return result;
    }
    
    /**
     * Returns the set difference of the two given tuple sets 
     */
    public static ObjTupleSet diff(ObjTupleSet lhs, ObjTupleSet rhs) {
        assert (lhs.arity == rhs.arity);
        ObjTupleSet result = new ObjTupleSet(lhs.arity);
        result.tuples.addAll(lhs.tuples); 
        result.tuples.removeAll(rhs.tuples);
        return result;
    }
    
    /**
     * Returns the set intersection of the two given tuple sets
     */
    public static ObjTupleSet intersection(ObjTupleSet lhs, ObjTupleSet rhs) {
        assert (lhs.arity == rhs.arity); 
        ObjTupleSet result = new ObjTupleSet(lhs.arity); 
        for (ObjTuple t : lhs)
            if (rhs.tuples.contains(t))
                result.add(t);
        return result;        
    }

    public ObjTupleSet projection(List<Integer> columns) {
        ObjTupleSet ots = new ObjTupleSet(columns.size());
        for (ObjTuple t : this.tuples()) {
            ots.add(t.projection(columns));
        }
        return ots;
    }

}
/*! @} */
