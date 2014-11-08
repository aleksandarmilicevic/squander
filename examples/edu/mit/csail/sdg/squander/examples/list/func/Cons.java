/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list.func;

import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({
    "next : one List | this.next = this.rest"
})
public class Cons extends IntList {

    final int key; 
    final IntList rest;
    
    public Cons(int key, IntList rest) {
        this.key = key;
        this.rest = rest;
    }

    @Returns("this.key")
    public int key() { return key; }
}

/*! @} */
