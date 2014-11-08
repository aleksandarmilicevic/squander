/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;

public class TestUtils {

    public static void assertArraysEqualNoOrdering(Object[] expected, Object[] actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals("array lengths are different", expected.length, actual.length);
        for (Object obj : expected) {
            assertArrayContains(actual, obj);
        }
    }
    
    public static void assertArraysEqualNoOrdering(int[] expected, int[] actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals("array lengths are different", expected.length, actual.length);
        for (int obj : expected) {
            assertArrayContains(actual, obj);
        }
    }
    
    public static void assertArrayContains(Object[] array, Object obj) {
        assertNotNull(array);
        for (Object arrObj : array) {
            if (arrObj == obj)
                return;
        }
        fail("Array does not contain given object: " + Arrays.toString(array) + ", obj = " + obj);
    }
    
    public static void assertArrayContains(int[] array, int obj) {
        assertNotNull(array);
        for (int arrObj : array) {
            if (arrObj == obj)
                return;
        }
        fail("Array does not contain given int: " + Arrays.toString(array) + ", obj = " + obj);
    }
    
}
/*! @} */
