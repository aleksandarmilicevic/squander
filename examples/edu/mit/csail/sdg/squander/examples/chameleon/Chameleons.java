package edu.mit.csail.sdg.squander.examples.chameleon;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Macros;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.examples.util.NNMap;
import edu.mit.csail.sdg.squander.utils.AutoId;

@Invariant({
    "some this.chameleons.elts", 
    "some this.states.elts"
})
public class Chameleons {
    
    public static enum Kind { A, B, C }
    
    public static class Chameleon extends AutoId {}
    
    @Invariant({
        // must assign a kind (color) to all chameleons
        "Chameleon = this.kind.keys",
        // 'meets' is symmetric
        "this.meets.elts = ~(this.meets.elts)",
        // no chameleon meets itself
        "no (IDEN & this.meets.elts)",
        // some chameleons meet in every state
        "some this.meets.elts",
    })
    public static class State {
        public NNMap<Chameleon, Kind>      kind  = new NNMap<Chameleons.Chameleon, Chameleons.Kind>(); 
        public NNMap<Chameleon, Chameleon> meets = new NNMap<Chameleons.Chameleon, Chameleons.Chameleon>(); 
    }
    
    public Set<Chameleon> chameleons;
    public State[] states; 

    @Macros({
        @Macro(var = "s1",     expr = "this.states[i1]"),     // current state
        @Macro(var = "s2",     expr = "this.states[i1+1]"),   // next state
        @Macro(var = "ch2",    expr = "$s1.meets[ch]"),  // chameleon whom 'ch' met in the current state
        // chameleon either didn't meet anyone or met someone with the same color thus its kind didn't change
        @Macro(var = "same" ,  expr = "(no $ch2 || " +
        		                      "  $s1.kind[ch] = $s1.kind[$ch2]) && " +
                                      "$s2.kind[ch] = $s1.kind[ch]"),
        // chameleon met someone with a different color, thus its kind changed to a kind different from the two                                      
        @Macro(var = "change", expr = "(some $ch2) && " +
                                      "($s1.kind[ch] != $s1.kind[$ch2]) && " +
                                      "($s2.kind[ch] !in ($s1.kind[ch] + $s1.kind[$ch2]))")
    })
    @Ensures({
        // transition constraint
        "all i1 : {0 ... (this.states.length - 2)} | " +
        "  all ch : Chameleon | " +
        "    $same || $change"
    })
    @Modifies({ "State.kind.elts", "State.meets.elts" })
    @Options(ensureAllInts = true)
    public void solve(int nChameleons, int nStates) {
        // init stuff
        chameleons = new LinkedHashSet<Chameleons.Chameleon>();
        for (int i = 0; i < nChameleons; i++) chameleons.add(new Chameleon());
        states = new State[nStates];
        for (int i = 0; i < nStates; i++) states[i] = new State();
        
        // solve
        Squander.exe(this, nChameleons, nStates);
    }
    
    public String printChameleon(Chameleon ch, State state) {
        if (ch == null)
            return "null";
        return ch.toString() + ": " + state.kind.get(ch);
    }

    public void print() {
        System.out.println("Chameleons: ");
        String s = ""; 
        for (Chameleon ch : chameleons) {
            if (s.length() > 0) s += ", ";
            s += ch.toString();
        }
        System.out.println("  " + s);
        System.out.println();
        System.out.println("States: ");
        for (State state : states) {
            s = "";
            for (Chameleon ch : chameleons) {
                if (s.length() > 0) s += ", ";
                s += printChameleon(ch, state);
            }
            System.out.println("  colors: \n    " + s);
            System.out.println();
            System.out.println("  meets: ");
            for (Chameleon ch : chameleons) {
                Chameleon ch2 = state.meets.get(ch);
                if (ch2 != null) 
                    System.out.println("    " + printChameleon(ch, state) + " --meets--> " + printChameleon(ch2, state));
            }
            System.out.println("----------------------");
        }   
    }
    
}