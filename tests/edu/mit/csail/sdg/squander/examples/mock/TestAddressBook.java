/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public abstract class TestAddressBook extends MySquanderTestBase {

    protected AddressBook addressBook;
    
    @Before
    public abstract void setUp();
    
    @Test
    public void testBindIfPresent() {
        EmailMessage m = new EmailMessage("Daniel");
        Assert.assertTrue(m.bind(addressBook));
        Assert.assertEquals("dnj@mit.edu", m.emailAddress);
    }

    @Test
    public void testBindIfAbsent() {
        EmailMessage m = new EmailMessage("Robert");
        Assert.assertFalse(m.bind(addressBook));
    }
}
/*! @} */
