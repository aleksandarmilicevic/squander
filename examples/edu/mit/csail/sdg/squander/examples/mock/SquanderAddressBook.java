/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.mock;

import java.util.Arrays;

import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;


/** 
 * Concrete squander mock object. This one comes with concrete data structures.
 * <p>
 * this needs a concrete representation and a way of saying that change to the abstract state
 * can automatically trigger changes to the concrete state that the abstract state depends on.
 * basically the idea of a data group.
 * @author kuat
 *
 */
@SpecField("data : String -> String from this.entries | this.data = {x in String, y : String | some e : this.entries[int] | e.entryName = x && e.entryEmail = y}") 
public class SquanderAddressBook implements AddressBook {
    
    @Invariant("this.entryName != null && this.entryEmail != null")    
    public static class Entry {
        String entryName = "";
        String entryEmail = "";
        
        @Override
        public String toString() {
            return "entry: " + entryName + " = " + entryEmail;
        }
    }    

    @Invariant("null !in this.entries[int]")
    public Entry[] entries;
    
    @Fresh({@FreshObjects(cls=Entry.class, num=1), @FreshObjects(cls=Entry[].class, num=1)})
    @Override
    public void setEmailAddress(String name, String email) {
        Squander.exe(this, new Class<?>[]{String.class, String.class}, new Object[]{name, email});
    }

    @Override
    public String getEmailAddress(String name) {
        return Squander.exe(this, new Class<?>[]{String.class}, new Object[]{name});
    }

    @Override
    public boolean contains(String name) {
        return Squander.exe(this, new Class<?>[]{String.class}, new Object[]{name});
    }        
    
    @Override
    public String toString() {
        return this.entries == null ? null : Arrays.toString(this.entries);
    }
}
/*! @} */
