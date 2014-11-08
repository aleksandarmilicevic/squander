/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.mock;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;

// Unfortunately, Java byte code does not support argument names in interfaces and abstract classes. Meh...

@SpecField("data : String -> String")
@Invariant("all x:String | lone this.data[x]")
public interface AddressBook {
    
    @Requires("@arg(0) != null && @arg(1) != null")
    @Ensures("this.data = @old(this.data) ++ @arg(0) -> @arg(1)")
    @Modifies("this.data")
    void setEmailAddress(String name, String email);
    
    @Ensures("return - null = this.data[@arg(0)]")
    String getEmailAddress(String name);
    
    @Returns("some this.data[@arg(0)]")
    boolean contains(String name);
}
/*! @} */
