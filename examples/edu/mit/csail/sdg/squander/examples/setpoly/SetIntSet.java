/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.setpoly;

import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.SpecField;

@SpecField("elems: set int from this.mySet.elts | this.elems = this.mySet.elts")
public class SetIntSet extends IntSet {

    private Set<Integer> mySet = new HashSet<Integer>();

    @Override
    public Set<Integer> nodes() {
        return mySet;
    }

    @Override
    public String toString() {
        return mySet.toString();
    }
    
}
/*! @} */
