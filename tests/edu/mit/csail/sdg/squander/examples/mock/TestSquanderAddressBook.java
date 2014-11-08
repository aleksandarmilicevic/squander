/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public final class TestSquanderAddressBook extends TestAddressBook {

    @Override
    @Before
    public void setUp() {
        final SquanderAddressBook sqAddressBook = new SquanderAddressBook();
        sqAddressBook.setEmailAddress("Daniel", "dnj@mit.edu");
        System.out.println("concrete state: " + sqAddressBook);
        Assert.assertEquals("concrete state not changed", 1, sqAddressBook.entries.length);
        Assert.assertEquals("concrete state not changed", "Daniel", sqAddressBook.entries[0].entryName);
        Assert.assertEquals("concrete state not changed", "dnj@mit.edu", sqAddressBook.entries[0].entryEmail);
        this.addressBook = sqAddressBook;
    }

}
/*! @} */
