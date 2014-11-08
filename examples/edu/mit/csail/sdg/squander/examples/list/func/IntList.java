/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list.func;

import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Functional list implementation.
 * 
 * @author Aleksandar Milicevic
 */
@SpecField({
    "next : one List", 
    "size : one int | this.next == null ? this.size = 1 : this.size = 1 + this.next.size"
})
public abstract class IntList {
    
    @Returns("this.next")
    public IntList next() { return Squander.exe(this); }
    
    @Returns("this.size")
    public final int size() { return Squander.exe(this); } 

//    @Ensures("all i: int | return[i] = this.keys[i]")
//    @Modifies("return.elts, return.length")
//    @FreshObjects(cls=int[].class, num=1)
//    public final int[] keys() { return Squander.magic(this); }
//    
//    @Ensures("startIdx == 0 ? return = this : this.")
//    public final List sublist(int startIdx) { 
//        return Squander.magic(this, new Class[] {int.class}, new Object[] {startIdx}); 
//    }
    
    public static void main(String[] args) {
        IntList lst = null;
        for (int k : new int[] {7, 3, 4 /*, 2, 4, 1, 4, 6, 2, 5 */}) {
            lst = new Cons(k, lst);
        }
        System.out.println(lst.size());
    }    
    
}

/*! @} */
