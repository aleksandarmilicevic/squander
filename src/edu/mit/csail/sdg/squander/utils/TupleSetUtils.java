package edu.mit.csail.sdg.squander.utils;

import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

public class TupleSetUtils {

	public static TupleSet join(TupleSet ts1, TupleSet ts2, TupleFactory f) {
	    int arity = ts1.arity() + ts2.arity() - 2;
	    TupleSet result = f.noneOf(arity);
	    for (Tuple t1 : ts1)
	        for (Tuple t2 : ts2) 
	            if (t1.atom(t1.arity() - 1) == t2.atom(0)) {
	                Object[] atoms = new Object[arity]; 
	                for (int i = 0; i < t1.arity() - 1; i++) atoms[i] = t1.atom(i);
	                for (int i = 1; i < t2.arity(); i++) atoms[t1.arity() + i - 2] = t2.atom(i);
	                result.add(f.tuple(atoms));
	            }
	    return result;
	}

}
