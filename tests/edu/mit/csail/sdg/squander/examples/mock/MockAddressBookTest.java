/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.mock;

import org.junit.Before;


public final class MockAddressBookTest extends TestAddressBook {
    
    @Override
    @Before
    public void setUp() {
        addressBook = new MockAddressBook();
        addressBook.setEmailAddress("Daniel", "dnj@mit.edu");
    }

}
/*! @} */
