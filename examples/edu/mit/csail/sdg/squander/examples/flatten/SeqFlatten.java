package edu.mit.csail.sdg.squander.examples.flatten;

import java.util.Arrays;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

public class SeqFlatten {
    
    static class A {
        final int id; 
        B[] moreLinks;
        
        A(int id) { this.id = id; }     
        public String toString() { return "A" + id; }
    }

    static class B {
        final int id;

        B(int id) { this.id = id; }
        public String toString() { return "B" + id; }
    }
    
    A[] links; 
    
    @Options(ensureAllInts = true, solveAll = true)
    @Ensures({
        // no nulls
        "null !in return[int]",
        // the return list contains all Bs reachable from this.links exactly once
        "all b: this.links[int].moreLinks[int] | #(return.elts.b) = 1",
        // ensure ordering for this.links
        "all a1, a2: this.links[int] | (this.links.elems.a1 < this.links.elts.a2) => " +
        "  (all b1: a1.moreLinks[int] | all b2: a2.moreLinks[int] | return.elems.b1 < return.elts.b2)",
        // ensure ordering for moreLinks
        "all a: this.links[int] | " +
        "  all b1, b2: a.moreLinks[int] |" +
        "    a.moreLinks.elems.b1 < a.moreLinks.elems.b2 => return.elems.b1 < return.elts.b2" 
    })
    @Modifies({
        "return.elts  | _<0> !in (null + this.links[int].moreLinks)",
        "return.length | _<0> !in (null + this.links[int].moreLinks)"
    })
    @FreshObjects(cls = B[].class, num = 1)
    public B[] flatten() {
        return Squander.exe(this);
    }
    
    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Level.ERROR;
        SeqFlatten f = new SeqFlatten();
        
        A a1 = new A(1);
        A a2 = new A(2);
        B b1 = new B(1);
        B b2 = new B(2);
        B b3 = new B(3);
        B b4 = new B(4);
        a1.moreLinks = new B[] { b1, b2 };
        a2.moreLinks = new B[] { b3, b4 };
        f.links = new A[] { a1, a2 };
        
        B[] flattened = f.flatten();
        
        System.out.println(Arrays.toString(flattened));
        
        // keep asking for different solutions
        while (Squander.getLastResult().findNext()) {
            B[] ret = Squander.getLastResult().getReturnValue();
            System.out.println(Arrays.toString(ret));
        }
    }
}
