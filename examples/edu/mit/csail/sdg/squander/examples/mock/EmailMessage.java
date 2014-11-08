/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.mock;

public class EmailMessage {
    String recipientName;
    String emailAddress;
    
    public EmailMessage(String recipient) {
        this.recipientName = recipient;
    }

    boolean bind(AddressBook abook) {
        // convert recipientName to emailAddress
        if (!abook.contains(recipientName)) {        
            return false;
        } else {
            this.emailAddress = abook.getEmailAddress(recipientName);
            return true;
        }
    }
}
/*! @} */
