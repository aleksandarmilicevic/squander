/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.partitioning;

import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.engine.ISquanderResult;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

@Invariant({
    /* no nulls */
    "null !in this.parts.elts + this.doms.elts + this.lits.elts",

    /* edges are only within this graph */
    "all p : this.parts.elts | p.doms.elts in this.doms.elts",
    "all d : this.doms.elts  | d.insts.elts in this.lits.elts",
    
    /* every domain is in some partition */
    "all d : this.doms.elts | d in this.parts.elts.doms.elts",
    
    /* no two partitions have the same set of domains */
    "all p1 : this.parts.elts| all p2 : this.parts.elts | p1 != p2 => p1.doms.elts != p2.doms.elts",
    
    /* every literal belongs to exactly one Domain */
    "all lit : this.lits.elts | one d : this.doms.elts | lit in d.insts.elts"
})
public class Graph {
    
    private static final int n = 4;
    
    private final Set<Partition> parts = new HashSet<Partition>();
    private final Set<Domain> doms = new HashSet<Domain>();
    private final Set<Literal> lits = new HashSet<Literal>();

    @Ensures({
        "some this.parts.elts",
        "some this.doms.elts", 
        "some this.lits.elts",
        "#this.parts.elts > #this.doms.elts",
        "#this.doms.elts < #this.lits.elts"
    })
    @Modifies({
        "this.parts.elts", 
        "this.doms.elts", 
        "this.lits.elts",
        "Partition.doms.elts", 
        "Domain.insts.elts"
    })
    @Fresh({
        @FreshObjects(cls=Partition.class, num = n),
        @FreshObjects(cls=Domain.class, num = n),
        @FreshObjects(cls=Literal.class, num = n)
    })
    @Options(solveAll = true)
    public void gen() {
        Squander.exe(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Partitions: %s\n", parts.toString()));
        sb.append(String.format("Domains:    %s\n", doms.toString()));
        sb.append(String.format("Literals:   %s\n", lits.toString()));
        sb.append("-----------------------------------------------\n");
        sb.append(String.format("partition -> {domain}:\n"));
        for (Partition p : parts) {
            sb.append(String.format("  %s -> %s\n", p, p.getDoms()));
        }
        sb.append("\n");
        sb.append(String.format("domain -> {literal}:\n"));
        for (Domain d : doms) {
            sb.append(String.format("  %s -> %s\n", d, d.getInsts()));
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        Graph g = new Graph();
        SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;
        g.gen();
        while (true) {
//            if (cnt == 100)
//                break;
//            System.out.println(cnt++);
            try {
                ISquanderResult r = Squander.getLastResult();
                if (r.hasSolution()) {
//                    System.out.println(g);
                    r.findNext();
                } else {
                    break;
                }
            } catch (Throwable t) {
                System.out.println("no more solutions");
                t.printStackTrace();
                break;
            }
        }
    }
}
/*! @} */
