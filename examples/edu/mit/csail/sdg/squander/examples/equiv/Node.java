/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.equiv;

import java.util.Arrays;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * One way to define the equivalency of objects.
 * 
 * @author Aleksandar Milicevic
 */
@SpecField("equiv : set Object from Node.left, Node.right | " +
           " this.equiv = {x : Node | (this.value = x.value) && " +
           "                          (this.left  -> x.left  in equiv + null -> null) && " +
           "                          (this.right -> x.right in equiv + null -> null)" +
           "}")
public class Node {
    
    int value; 
    Node left; 
    Node right; 
    
    public Node(int value) {
        this.value = value;
    }

    @Ensures("return[int] = this.equiv && return.length = #this.equiv")  // TODO: does not ensure that the lengths are the same
    @Fresh({@FreshObjects(cls = Node[].class, num = 1)})
    public Node[] getEquiv(Node n1, Node n2) {
        return Squander.exe(this, new Class[] {Node.class, Node.class}, new Object[] {n1, n2});
    }

    @Override
    public String toString() {
        return String.format("%d: (v=%d, l=%d, r=%d)", System.identityHashCode(this), value, 
                System.identityHashCode(left), System.identityHashCode(right));
    }
    
    public static void main(String[] args) {
        Node n11 = new Node(2);
        Node n12 = new Node(1); 
        Node n13 = new Node(3);
        n11.left = n12; 
        n11.right = n13;
        
        Node n21 = new Node(2);
        Node n22 = new Node(1); 
        Node n23 = new Node(3);
        n21.left = n22; 
        n21.right = n23;
        
        Node n31 = new Node(2);
        Node n32 = new Node(1); 
        Node n33 = new Node(3);
        n31.left = n32; 
        n32.left = n33;
        
        System.out.println(Arrays.toString(n11.getEquiv(n21, n31)));
        System.out.println(System.identityHashCode(n11));
        System.out.println(System.identityHashCode(n21));
        System.out.println(System.identityHashCode(n31));
    }
    
}
/*! @} */
