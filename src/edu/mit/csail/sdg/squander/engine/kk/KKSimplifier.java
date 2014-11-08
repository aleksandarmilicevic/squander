package edu.mit.csail.sdg.squander.engine.kk;

import kodkod.ast.Formula;
import kodkod.instance.Bounds;
import kodkod.util.nodes.PrettyPrinter;

public class KKSimplifier {

    public Formula simplify(Formula f, Bounds b) {
        System.out.println(PrettyPrinter.print(f, 0));
        System.exit(23);
        return null;
    }
}
