package edu.mit.csail.sdg.squander.examples.overflow;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;

public class OverflowWithFreeVars {
    public int a, b, c;

    /**
     * Check that there can't exist values for variables a, b, c such that both
     * "a + b" is not greater than "c" and "a + b" is not less than or equal to
     * "c".
     * 
     * Here the idea is to ensure that the overflow circuit doesn't get negated.
     */
    @Ensures("!(this.a + this.b > this.c) && !(this.a + this.b <= this.c)")
    @Modifies("this.a, this.b, this.c")
    public void noSol1() {
        Squander.exe(this);
    }

    /**
     * Check that there can't exist values for variables a, b, c such that both
     * "a + b" is greater than "c" and "a + b" is less than or equal to "c".
     */
    @Ensures("this.a + this.b > this.c && this.a + this.b <= this.c")
    @Modifies("this.a, this.b, this.c")
    public void noSol1dual() {
        Squander.exe(this);
    }
    
    /**
     * Checks that there can't exist values for variables a, b, such that both
     * of them are the same, both greater than 0, and their sum is not 2, given
     * that the int bitwidth is 3 (ints: [-4, ..., 3]).
     * 
     * Here the idea is to ensure that something like "2 + 2 != 2" cannot be
     * true, because "2 + 2" overflows, and if that is negated to check the
     * inequality, then the result is TRUE.
     */
    @Ensures("this.a > 0 && this.a = this.b && this.a + this.b != 2")
    @Modifies("this.a, this.b")
    @Options(bitwidth = 3)
    public void noSol2() {
        Squander.exe(this);
    }
    
}