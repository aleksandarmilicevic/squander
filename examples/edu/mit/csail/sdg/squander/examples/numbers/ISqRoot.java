/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({"s : int", "r : int", "q : int"})
@Invariant({
    "this.r > 0 => this.r <= this.s/this.r",  
    "this.s / this.q < this.q",
    "this.r >= 0",
    "this.s >= 0",
    "this.q > 0"
})
public interface ISqRoot {
    
    @Returns("this.s") public int getS();
    @Returns("this.r") public int getR();
    @Returns("this.q") public int getQ();
    
    @Ensures({"this.s < (this.r + 1) * (this.r + 1)"})
    @Returns("this.r")
    @Modifies({"this.r", "this.q"})
    public int sqRoot();

}
/*! @} */
