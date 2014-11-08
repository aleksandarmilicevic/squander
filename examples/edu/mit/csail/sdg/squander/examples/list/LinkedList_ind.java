/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list;

import java.util.Arrays;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Linked list implementation with inductive definitions. 
 * 
 * @author Aleksandar Milicevic
 */
@SpecField("nodes : set Node | this.nodes = this.header.succ")
public class LinkedList_ind {

    @Invariant("this !in this.^next")
    @SpecField("succ : set Node | this.next == null ? this.succ = this : this.succ = this + this.next.succ - null")
    public static class Node {
        private int value; 
        private Node next;
        
        public Node(int value) {
            this.value = value;
        }

        public int getValue()           { return value; }
        public void setValue(int value) { this.value = value; }
        public Node getNext()           { return next; }
        public void setNext(Node next)  { this.next = next; } 
        
        @Returns("this.succ")
        public Node[] succ() {
            return Squander.exe(this);
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

    }
    
    private Node header;

    @Requires("n.succ !in this.nodes")
    @Ensures("this.nodes = @old(this.nodes) + n.succ")
    @Modifies({"this.header", "this.nodes", "Node.succ", "Node.next [{n : Node | n in this.nodes && n.next == null}]"})
    public void addNode(Node n) { Squander.exe(this, new Class[] {Node.class}, new Node[] {n}); }
    
    @Ensures("return[int] = this.nodes && return.length=#this.nodes")
    @Modifies({"return.elts", "return.length"})
    @FreshObjects(cls = Node[].class, num = 1)
    public Node[] nodes() { return Squander.exe(this); }
    
    public Node header() { return header; }
    
    @Override
    public String toString() {
        return Arrays.toString(nodes());
    }

    public static void main(String[] args) {
        LinkedList_ind lst = new LinkedList_ind();
//        lst.addNode(new Node(1));
//        System.out.println(lst);
        lst.header = new Node(1);
        Node n2 = new Node(2);
        n2.setNext(new Node(3));
        lst.addNode(n2);
//        System.out.println(lst);
    }
    
}
/*! @} */
