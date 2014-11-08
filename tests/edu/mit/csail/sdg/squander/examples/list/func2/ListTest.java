/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list.func2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;


public class ListTest extends MySquanderTestBase {

    private IntList lst;
    private int[] elems;

    @Before
    public void setUp() {
        lst = new Nil();
        elems = new int[] {7, 2, 4, 5, 8, 4};
        for (int k : elems) {
            lst = new Cons(k, lst);
        }
    }
    
    @Test
    public void testSize() {
        Assert.assertEquals(elems.length, lst.size());
    }
    
    @Test
    public void testSublist() {
        IntList sub3 = lst.sublist(3);
        Assert.assertEquals("Cons(4, Cons(2, Cons(7, Nil)))", sub3.toString());
        IntList sub5 = lst.sublist(5);
        Assert.assertEquals("Cons(7, Nil)", sub5.toString());
    }
    
}
/*! @} */
