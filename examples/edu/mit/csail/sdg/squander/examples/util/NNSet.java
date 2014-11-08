package edu.mit.csail.sdg.squander.examples.util;

import java.util.HashSet;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant("null !in this.elts")
public class NNSet<E> extends HashSet<E> {
    private static final long serialVersionUID = 4872953667395556975L;
}
