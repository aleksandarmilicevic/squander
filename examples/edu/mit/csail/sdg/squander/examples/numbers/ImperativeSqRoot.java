/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({
    "s : int from this.s1 | this.s = this.s1",
    "r : int from this.r1 | this.r = this.r1",
    "q : int from this.q1 | this.q = this.q1"    
})

public class ImperativeSqRoot extends MixedSqRoot {
    
    // JFORGE ARTIFACT: bug in byte code loader, need to forcefully bring it to the scene
//    @SuppressWarnings("unused")
//    private Squander squander = new Squander();
    
    private final int s1; 
    private int r1; 
    private int q1; 
    
    public ImperativeSqRoot(int s) {
        super();
        this.s1 = s;
        this.r1 = 0; 
        this.q1 = s; 
    }
    
    @Override public int getS() { return s1; }
    @Override public int getQ() { return q1; }
    @Override public int getR() { return r1; }

    @Override protected void setQ(int q) { this.q1 = q; }
    @Override protected void setR(int r) { this.r1 = r; }

    @Override
    protected int findP() {
        // BUG: >>> 1 instead of / 2
        return (r1 + q1) >>> 1;
    }

    @Override
    protected int findQ(int p) {
        q1 = p; 
        return q1;
    }

    @Override
    protected int findR(int p) {
        r1 = p;
        return r1;
    }

}
/*! @} */
