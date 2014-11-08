package edu.mit.csail.sdg.squander.examples.util;

import java.util.HashMap;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant("null !in this.elts[K+null]")
public class NNMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 6761635068892241697L;
}
