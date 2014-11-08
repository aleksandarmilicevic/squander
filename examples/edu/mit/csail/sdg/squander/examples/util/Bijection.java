package edu.mit.csail.sdg.squander.examples.util;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant({
    "this.elts[K] = V",
    "this.elts.(V) = K"
})
public class Bijection<K, V> extends NNMap<K, V> {
    private static final long serialVersionUID = 6569603247546615874L;
}
