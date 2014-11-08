/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public class ArithmeticTest extends MySquanderTestBase {

    @Test
    public void test1() {
        Assert.assertEquals("gcd(3,5) = 1", 1, Arithmetic.gcd(3,5));
        Assert.assertEquals("gcd(2,4) = 2", 2, Arithmetic.gcd(2,4));
        Assert.assertEquals("gcd(6,4) = 2", 2, Arithmetic.gcd(6,4));
        Assert.assertEquals("gcd(3,3) = 3", 3, Arithmetic.gcd(3,3));
        Assert.assertEquals("gcd(12,15) = 3", 3, Arithmetic.gcd(12,15));
    }
    
    @Test
    public void test2() {
        Assert.assertEquals("sqrt(0) = 0", 0, Arithmetic.square_root(0));
        Assert.assertEquals("sqrt(1) = 1", 1, Arithmetic.square_root(1));
        Assert.assertEquals("sqrt(2) = 1", 1, Arithmetic.square_root(2));
        Assert.assertEquals("sqrt(4) = 2", 2, Arithmetic.square_root(4));
        Assert.assertEquals("sqrt(7) = 2", 2, Arithmetic.square_root(7));
        Assert.assertEquals("sqrt(10) = 3", 3, Arithmetic.square_root(10));
        Assert.assertEquals("sqrt(15) = 3", 3, Arithmetic.square_root(15));
    }
    
    @Test
    public void test3() {
        Assert.assertEquals("sqrt(1) = 1", 1, Arithmetic.square_root_mixed(1));
        Assert.assertEquals("sqrt(2) = 1", 1, Arithmetic.square_root_mixed(2));
        Assert.assertEquals("sqrt(4) = 2", 2, Arithmetic.square_root_mixed(4));
        Assert.assertEquals("sqrt(7) = 2", 2, Arithmetic.square_root_mixed(7));
        Assert.assertEquals("sqrt(9) = 3", 3, Arithmetic.square_root_mixed(9));
        Assert.assertEquals("sqrt(10) = 3", 3, Arithmetic.square_root_mixed(10));
        Assert.assertEquals("sqrt(15) = 3", 3, Arithmetic.square_root_mixed(15));
    }
    
    @Test
    public void test4() {
        Assert.assertArrayEquals(new int[] {-1, -1, 0, 2, 2, 5, 6}, Arithmetic.sort(new int[] {2, 5, -1, 0, -1, 2, 6}));
        Assert.assertArrayEquals(new int[] {-1, -1, 0, 2, 2, 5, 6}, Arithmetic.sort(new int[] {2, 6, 0, 2, -1, -1, 5}));
        Assert.assertArrayEquals(new int[] {-1, -1, 0, 2, 2, 5, 6}, Arithmetic.sort(new int[] {-1, -1, 0, 2, 2, 5, 6}));
        Assert.assertArrayEquals(new int[] {-1, -1, 0, 2, 2, 5, 6}, Arithmetic.sort(new int[] {6, 5, 2, 2, 0, -1, -1}));
    }
    
}
/*! @} */
