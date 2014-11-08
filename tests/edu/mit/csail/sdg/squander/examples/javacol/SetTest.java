/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.javacol;

import static edu.mit.csail.sdg.squander.examples.javacol.Collections.add;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.contains;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.remove;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public class SetTest extends MySquanderTestBase {
    
    private static final String[] keys = new String[] { "str1", "str2", "str3" };
    
    private Set<String> set; 
    private Set<String> sqSet;
    
    @Before
    public void setUp() {
        set = new HashSet<String>();
        sqSet = new HashSet<String>();
        for (int i = 0; i < keys.length; i++) {
            set.add(keys[i]);
            sqSet.add(keys[i]);
        }
    }
    
    @Test
    public void testContains() {
//        for (String key : keys) {
//            boolean b1 = set.contains(key);
//            boolean b2 = contains(sqSet, key);
//            Assert.assertEquals(b1, b2);
//            Assert.assertEquals(set.toString(), sqSet.toString());
//        }
//        Assert.assertFalse(contains(sqSet, "asdfa"));
        Assert.assertFalse(contains(sqSet, null));
    }
    
    @Test
    public void testAdd() {
        set = new HashSet<String>();
        sqSet = new HashSet<String>();
        for (String key : keys) {
            boolean b1 = set.add(key);
            boolean b2 = add(sqSet, key);
            Assert.assertEquals(b1, b2);
            Assert.assertEquals(set.toString(), sqSet.toString());
        }
        for (String key : keys) {
            boolean b1 = set.add(key);
            boolean b2 = add(sqSet, key);
            Assert.assertEquals(b1, b2);
            Assert.assertEquals(set.toString(), sqSet.toString());
        }
    }
    
    @Test
    public void testRemove() {
        Assert.assertFalse(remove(sqSet, null));
        Assert.assertEquals(set.toString(), sqSet.toString());
        Assert.assertFalse(remove(sqSet, "asdfa"));
        Assert.assertEquals(set.toString(), sqSet.toString());
        for (String key : keys) {
            boolean b1 = set.remove(key);
            boolean b2 = remove(sqSet, key);
            Assert.assertEquals(b1, b2);
            Assert.assertEquals(set.toString(), sqSet.toString());
        }
        Assert.assertFalse(remove(sqSet, null));
        Assert.assertEquals(set.toString(), sqSet.toString());
        Assert.assertFalse(remove(sqSet, "asdfa"));
        Assert.assertEquals(set.toString(), sqSet.toString());
    }
    
}
/*! @} */
