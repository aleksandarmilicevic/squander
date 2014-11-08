package edu.mit.csail.sdg.squander.examples.trans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import kodkod.engine.satlab.SATFactory;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Macros;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

public class TransOrdering {
    
    static class Pair {
        final Trans before; 
        final Trans after;
        Pair(Trans before, Trans after) {
            this.before = before;
            this.after = after;
        }        
    }
    
    @Macros({
        @Macro(var="stalled", expr="{t: Trans | !t.isBlocked}"),
        @Macro(var="blocked", expr="{t: Trans | t.isBlocked}")
    })
    @Ensures({
        // the modification should be a permutation
        "trans.vals = @old(trans.vals)",
        // stalled transaction partial ordering
        "all ts1: $stalled, ts2: $stalled - ts1 | " +
        "  ts1.timestamp < ts2.timestamp <=> trans.elems.ts1 < trans.elts.ts2",
        // blocked after stalled
        "all tb: $blocked, ts: $stalled | " +
        "  ts.timestamp < tb.timestamp => trans.elems.ts < trans.elts.tb",
        // extra (user-defined) orderings
        "all p: extras.elts | " +
        "  trans.elems.(p.before) < trans.elts.(p.after)"
    })
    @Modifies("trans.elts")
    @Options(solveAll=true)
    public void order(Trans[] trans, Set<Pair> extras) {
        Squander.exe(this, new Object[] { trans, extras });
    }

    public void mmain() {
        SquanderGlobalOptions.INSTANCE.log_level = Level.WARN;
        Trans b1 = new Trans(1, true);
        Trans s2 = new Trans(2, false);
        Trans b3 = new Trans(3, true);
        Trans s4 = new Trans(4, false);
        
        // put them in any order here
        Trans[] trans = new Trans[] { s4, s2, b3, b1 };
        
        // define extra orderings here
        Set<Pair> extraOrderings = new HashSet<TransOrdering.Pair>();
        extraOrderings.add(new Pair(b1, b3));
        
        // solve
        order(trans, extraOrderings); // may throw exception if there is no solution at the beginning
        System.out.println(Arrays.toString(trans));
        
        // keep asking for different solutions
        while (Squander.getLastResult().findNext()) {
            System.out.println(Arrays.toString(trans));
        }
    }
    
    public void mmain2() {
        SquanderGlobalOptions.INSTANCE.log_level = Level.WARN;
        SquanderGlobalOptions.INSTANCE.sat_solver = SATFactory.MiniSat;
        
        Integer[] blocked = new Integer[] { 1, 5, 9 };
        Integer[] stalled = new Integer[] { 3, 4, 7, 11, 15, 17, 23, 27, 29 };
        Trans[] trans = new Trans[blocked.length + stalled.length];
        int index = 0;
        for (int i : blocked) { trans[index++] = new Trans(i, true); }
        for (int i : stalled) { trans[index++] = new Trans(i, false); }

        // define extra orderings here
        Set<Pair> extraOrderings = new HashSet<TransOrdering.Pair>();
        extraOrderings.add(new Pair(trans[0], trans[1]));
        extraOrderings.add(new Pair(trans[1], trans[2]));
        
        long t1 = System.currentTimeMillis();
        
        // solve
        order(trans, extraOrderings); // may throw exception if there is no solution at the beginning
        System.out.println(Arrays.toString(trans));
        
        int cnt = 1;
        
        // keep asking for different solutions
        while (Squander.getLastResult().findNext()) {
            System.out.println(Arrays.toString(trans));
            cnt++;
        }
        
        long t2 = System.currentTimeMillis(); 
        
        System.out.println((t2 - t1) / 1000.0f);
        System.out.println(cnt);
    }
    
    public static void main(String[] args) {
        new TransOrdering().mmain2();
    }
    

}
