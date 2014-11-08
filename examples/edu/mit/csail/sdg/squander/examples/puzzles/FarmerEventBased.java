package edu.mit.csail.sdg.squander.examples.puzzles;

import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Macros;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.eventbased.Final;
import edu.mit.csail.sdg.squander.eventbased.Initial;
import edu.mit.csail.sdg.squander.eventbased.State;
import edu.mit.csail.sdg.squander.eventbased.StateFrame;
import edu.mit.csail.sdg.squander.eventbased.StateOptions;
import edu.mit.csail.sdg.squander.eventbased.Transition;

@Macros({
    @Macro(var = "FARMER",  expr = "0"),
    @Macro(var = "FOX",     expr = "1"),
    @Macro(var = "CHICKEN", expr = "2"),
    @Macro(var = "GRAIN",   expr = "3")
})
@Invariant({
    "(this.objs[$FOX] = this.objs[$CHICKEN]) => (this.objs[$CHICKEN] = this.objs[$FARMER])",
    "(this.objs[$GRAIN] = this.objs[$CHICKEN]) => (this.objs[$CHICKEN] = this.objs[$FARMER])",  
})
@Initial("all b in this.objs.vals | b")
@Transition({
    "this'.objs[$FARMER] != this.objs[$FARMER]",
    "no x, y : {0 ... 3} @- {$FARMER} | x != y && this'.objs[x] != this.objs[x]" +
    "                                          && this'.objs[y] != this.objs[y]",
})
@Final("all b in this.objs.vals | !b")
@StateFrame("FarmerEventBased.objs.elts")
@StateOptions(@Options(ensureAllInts = false))
public class FarmerEventBased extends State {
    static final int FARMER = 0;
    static final int FOX = 1;
    static final int CHICKEN = 2;
    static final int GRAIN = 3;
    
    boolean[] objs = new boolean[4];  
    
    public boolean farmer()  { return objs[FARMER]; }
    public boolean fox()     { return objs[FOX]; }
    public boolean chicken() { return objs[CHICKEN]; }
    public boolean grain()   { return objs[GRAIN]; }
    
    private static String b(boolean f) { return f ? "X" : "O"; }    
    private static String bn(boolean f) { return b(!f); }
    
    public static FarmerEventBased[] findTrace(int n) {
        return Squander.findTrace(FarmerEventBased.class, n);
    }
    
    public static void main(String[] args) {
        //TODO: why does this not work with noOverflow??? (especially because the other Farmer example does work with noOverflow)
        FarmerEventBased[] steps = findTrace(7);
        for (FarmerEventBased s : steps) {
            System.out.println("  Fa  Ch  Fo  Gr  ");
            System.out.println(String.format("  %s   %s   %s   %s   ", b(s.farmer()), b(s.chicken()), b(s.fox()), b(s.grain())));
            System.out.println(String.format("  %s   %s   %s   %s   ", bn(s.farmer()), bn(s.chicken()), bn(s.fox()), bn(s.grain())));
            System.out.println("------------------------");
        }        
    }
    
    
}
