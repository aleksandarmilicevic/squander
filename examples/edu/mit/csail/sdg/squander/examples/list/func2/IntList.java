/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list.func2;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Functional list implementation.
 * 
 * @author Aleksandar Milicevic
 */
@SpecField({
    "size : one int",
    "keys : int -> int"
})
public abstract class IntList {

    @Returns("this.size")
    public final int size() { return Squander.exe(this); } 
    
    @Returns("this.keys[idx]")
    public final int get(int idx) { return Squander.exe(this, idx); }
    
    @Ensures("return.keys[0] = this.keys[startIdx] && return.size = this.size - startIdx")
    public final IntList sublist(int startIdx) { return Squander.exe(this, startIdx); }
    
    public static void main(String[] args) {
        IntList lst = new Nil();
        int[] elems = new int[] {7, 2, 4, 5, 8, 4};
        for (int k : elems) {
            lst = new Cons(k, lst);
        }
        System.out.println(lst);
        System.out.println(lst.size());
    }    
    
}
/*! @} */
