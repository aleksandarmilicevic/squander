/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.partitioning;

public class Literal {
    
    private static int cnt = 0; 
    private final int idx = cnt++;

    
    public int getIdx() { return idx; }

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
        Literal other = (Literal) obj;
        if (idx != other.idx)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Lit" + idx;
    }

}
/*! @} */
