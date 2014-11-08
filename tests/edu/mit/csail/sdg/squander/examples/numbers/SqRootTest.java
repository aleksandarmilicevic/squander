/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

/**
 * Unit test for our fully declarative, mixed, and fully imperative square root
 * implementation
 * 
 * @author Aleksandar Milicevic
 */
public class SqRootTest extends MySquanderTestBase {

    @Test
    public void test1() {
        Assert.assertEquals(0, new DeclarativeSqRoot(0).sqRoot());
        Assert.assertEquals(2, new DeclarativeSqRoot(6).sqRoot());
        Assert.assertEquals(1, new DeclarativeSqRoot(2).sqRoot());
        Assert.assertEquals(4, new DeclarativeSqRoot(16).sqRoot());
        Assert.assertEquals(5, new DeclarativeSqRoot(35).sqRoot());
        Assert.assertEquals(6, new DeclarativeSqRoot(36).sqRoot());
    }

    @Test
    public void test2() {
        Assert.assertEquals(0, new MixedSqRoot(0).sqRoot());
        Assert.assertEquals(2, new MixedSqRoot(6).sqRoot());
        Assert.assertEquals(1, new MixedSqRoot(2).sqRoot());
        Assert.assertEquals(4, new MixedSqRoot(16).sqRoot());
        Assert.assertEquals(5, new MixedSqRoot(35).sqRoot());
        Assert.assertEquals(6, new MixedSqRoot(36).sqRoot());
    }

    @Ignore("ImperativeSqRoot doesn't use Squander")
    @Test
    public void test3() {
        Assert.assertEquals(0, new ImperativeSqRoot(0).sqRoot());
        Assert.assertEquals(2, new ImperativeSqRoot(6).sqRoot());
        Assert.assertEquals(1, new ImperativeSqRoot(2).sqRoot());
        Assert.assertEquals(4, new ImperativeSqRoot(16).sqRoot());
        Assert.assertEquals(5, new ImperativeSqRoot(35).sqRoot());
        Assert.assertEquals(6, new ImperativeSqRoot(36).sqRoot());
    }
    
}
/*! @} */
