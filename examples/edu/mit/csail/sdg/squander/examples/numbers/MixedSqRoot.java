/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Mixed implementation that follows the approach from Carroll Morgan's book
 * 
 * @author Aleksandar Milicevic
 */
public class MixedSqRoot extends DeclarativeSqRoot {

    protected MixedSqRoot() {}
    
    @Requires("myS >= 0")
    public MixedSqRoot(int myS) {
        super(myS);
    }

    @Override
    public int sqRoot() {
        final int s = getS();
        while (getR() + 1 < getQ()) {
            final int p = findP();
            // BUG FOUND: overflow in p * p
            if (s / p < p) {
                findQ(p);
            } else {
                findR(p);
            }
        }
        return getR();
    }

    @Requires("this.r + 1 < this.q")
    @Ensures("return > this.r && return < this.q")
    protected int findP() {
        return Squander.exe(this);
    }

    @Requires({"p > 0", "this.s/p < p", "p < this.q"})
    @Modifies("this.q")
    @Ensures("this.q < @old(this.q)")
    @Returns("this.q")
    protected int findQ(int p) {
        return Squander.exe(this, new Class[] { int.class }, new Object[] { p });
    }
    
    @Requires({"p > 0", "this.s/p >= p", "this.r < p"})
    @Modifies("this.r")
    @Ensures("this.r > @old(this.r)")
    @Returns("this.r")
    protected int findR(int p) {
        return Squander.exe(this, new Class[] { int.class }, new Object[] { p });
    }

}
/*! @} */
