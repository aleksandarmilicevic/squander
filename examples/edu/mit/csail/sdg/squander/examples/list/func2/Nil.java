/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list.func2;

import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({
    "size : one int | this.size = 0",
    "keys : int -> int | no this.keys"
})
public class Nil extends IntList {

    @Override
    public String toString() {
        return "Nil";
    }

}
/*! @} */
