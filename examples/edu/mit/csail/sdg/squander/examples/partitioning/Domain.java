/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.partitioning;

import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Invariant;

@Invariant("null !in this.insts.elts")
public class Domain {
    
    private static int cnt = 0; 
    private final int idx = cnt++;
    
    private final Set<Literal> insts = new HashSet<Literal>();
    
    
    public int getIdx()             { return idx; }
    public Set<Literal> getInsts() { return insts; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idx;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Domain other = (Domain) obj;
        if (idx != other.idx)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Dom" + idx;
    }


}
/*! @} */
