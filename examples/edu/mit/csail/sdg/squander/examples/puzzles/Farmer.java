package edu.mit.csail.sdg.squander.examples.puzzles;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Macros;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;

/**
 * The classic river crossing puzzle. A farmer is carrying a fox, a
 * chicken, and a sack of grain. He must cross a river using a boat
 * that can only hold the farmer and at most one other thing. If the
 * farmer leaves the fox alone with the chicken, the fox will eat the
 * chicken; and if he leaves the chicken alone with the grain, the
 * chicken will eat the grain. How can the farmer bring everything
 * to the far side of the river intact?
 *
 * @author Aleksandar Milicevic <aleks@csail.mit.edu>
 */
@Macros({
    @Macro(var = "FARMER",  expr = "0"),
    @Macro(var = "FOX",     expr = "1"),
    @Macro(var = "CHICKEN", expr = "2"),
    @Macro(var = "GRAIN",   expr = "3")
})
public class Farmer {

    public static final int N = 4;
    
    @Invariant({
        "(this.objs[$FOX] = this.objs[$CHICKEN]) => (this.objs[$CHICKEN] = this.objs[$FARMER])",
        "(this.objs[$GRAIN] = this.objs[$CHICKEN]) => (this.objs[$CHICKEN] = this.objs[$FARMER])",  
    })
    static class State {
        static final int FARMER = 0;
        static final int FOX = 1;
        static final int CHICKEN = 2;
        static final int GRAIN = 3;
    
        boolean[] objs = new boolean[4];

        public static State newNone() {
            return new State(false, false, false, false);
        }

        public static State newAll() {
            return new State(true, true, true, true);
        }

        public State(boolean farmer, boolean fox, boolean chicken, boolean grain) {
            this.objs = new boolean[] {farmer, fox, chicken, grain};
        }
        
        public boolean farmer() { return objs[FARMER]; }
        public boolean fox() { return objs[FOX]; }
        public boolean chicken() { return objs[CHICKEN]; }
        public boolean grain() { return objs[GRAIN]; }
    }

    @Options(ensureAllInts = false)
    @Ensures({
        "return > 1 && return <= steps.length",        
        // initial state
        "all b in steps.first.objs.vals | b",
        // transition
        "all i : {1 ... (return-1)} | " +
        "    steps[i].objs[$FARMER] = !steps[i-1].objs[$FARMER] &&" +
        "    (no x, y : {0 ... 3} @- {$FARMER} | x != y && steps[i].objs[x] != steps[i-1].objs[x] && " +
        "                                                  steps[i].objs[y] != steps[i-1].objs[y])",
        // final state
        "all b : steps[return-1].objs.vals | !b" })
    @Modifies("State.objs.elts")
    public static int solve(State[] steps) {
        return Squander.exe(null, new Object[] { steps });
    }
    
    private static String b(boolean f) { return f ? "X" : "O"; }    
    private static String bn(boolean f) { return b(!f); }
    
    public static void main(String[] args) {        
        int maxSteps = 8;
        State[] steps = new State[maxSteps];
        for (int i = 0; i < maxSteps; i++) {
            steps[i] = State.newAll();
        }
        
        int n = solve(steps);
        
        for (int i = 0; i < n; i++) {
            State s = steps[i];
            System.out.println("  Fa  Ch  Fo  Gr  ");
            System.out.println(String.format("  %s   %s   %s   %s   ", b(s.farmer()), b(s.chicken()), b(s.fox()), b(s.grain())));
            System.out.println(String.format("  %s   %s   %s   %s   ", bn(s.farmer()), bn(s.chicken()), bn(s.fox()), bn(s.grain())));
            System.out.println("------------------------");
        }        
    }

}
