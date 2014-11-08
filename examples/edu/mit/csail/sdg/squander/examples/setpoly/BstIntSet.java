/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.setpoly;

import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.SpecField;

@SpecField({
    "elems: set int from this.nodes | this.elts = this.nodes.key",
    "nodes: set Node from this.root, Node.left, Node.right | this.nodes = this.root.*(left+right) - null"
})
public class BstIntSet extends IntSet {
    
    @Invariant( {
        /* left sorted  */  "all x : this.left.*(left + right) - null | x.key < this.key",
        /* right sorted */  "all x : this.right.*(left + right) - null | x.key > this.key"
    }) 
    public static class Node {
        public int key;
        public Node left; 
        public Node right; 
        
        public Node()        { this(-1); }
        public Node(int key) { this.key = key; } 
    }

    private Node root = null;
    
    @Override
    @Modifies("Node.key")
    @FreshObjects(cls = Node.class, num = 1)
    public boolean add(int e) {
        return super.add(e);
    }

    @Override
    public String toString() {
        return printNode(root);
    }

    private String printNode(Node n) {
        if (n == null) {
            return "-";
        }
        if (n.left == null && n.right == null)
            return Integer.toString(n.key);
        return String.format("%s (%s, %s)", n.key, printNode(n.left), printNode(n.right));
    }

}
/*! @} */
