/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sort;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;


public class SortTest extends MySquanderTestBase {

    @Test
    public void test1() {
        int[] a = new int[] {2, 5, -1, 0, -1, 2, 6};
        Sort s1 = new Sort(a);
        Arrays.sort(a);
        System.out.println(s1);
        s1.sort();
        System.out.println(s1);
        Assert.assertArrayEquals(a, s1.getA());
    }
    
    @Test
    public void test2() {
        int[] a = new int[] {1, 2, 4, 5, 7};
        Sort s1 = new Sort(a);
        Arrays.sort(a);
        s1.sort();
        Assert.assertArrayEquals(a, s1.getA());
    }
    
    @Test
    public void test3() {
        int[] a = new int[] {6, 4, 2, 1, -4, -6, -6};
        Sort s1 = new Sort(a);
        Arrays.sort(a);
        s1.sort();
        Assert.assertArrayEquals(a, s1.getA());
    }
    
}
/*! @} */
