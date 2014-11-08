/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list.func2;

import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({
    "size : one int    | this.size = 1 + this.rest.size",
    "keys : int -> int | #this.keys = this.size && " +
                         "(0 -> this.key) in this.keys && " +
                         "(all i: int | i < 0 ? no this.keys[i] : lone this.keys[i] && this.keys[i+1]=this.rest.keys[i])"
})
public class Cons extends IntList {

    private final int key; 
    private final IntList rest;

    public Cons(int key, IntList rest) {
        this.key = key;
        this.rest = rest;
    }

    public int getKey() {
        return key;
    }

    public IntList getRest() {
        return rest;
    }

    @Override
    public String toString() {
        return "Cons(" + key + ", " + rest + ")";
    }

}
/*! @} */
