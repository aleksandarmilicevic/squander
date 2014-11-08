/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Purely declarative implementation
 *  
 * @author Aleksandar Milicevic
 */
public class DeclarativeSqRoot implements ISqRoot {

    protected DeclarativeSqRoot() {}
    
    public DeclarativeSqRoot(int myS) {
        init(myS);
    }
    
    @Requires("myS >= 0")
    @Ensures({"this.s = myS", "this.r = 0", "this.q = (myS = 0 ? 1 : myS)"})
    @Modifies({"this.s", "this.r", "this.q"})
    private void init(int myS) {
        Squander.exe(this, new Class[] { int.class }, new Object[] { myS });
    }

    @Override public int getS() { return Squander.exe(this); }    
    @Override public int getR() { return Squander.exe(this); }    
    @Override public int getQ() { return Squander.exe(this); }
    
    @Ensures("this.r = r")
    @Modifies("this.r")
    protected void setR(int r) { Squander.exe(this, new Class[] {int.class}, new Object[] {r}); }    
    
    @Ensures("this.q = q")
    @Modifies("this.q")
    protected void setQ(int q) { Squander.exe(this, new Class[] {int.class}, new Object[] {q}); }
    
    @Override
    public int sqRoot() {
        return Squander.exe(this);
    }

}
/*! @} */
