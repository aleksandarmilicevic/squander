/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * A Linked List implementation.
 *  
 * @author Aleksandar Milicevic
 */
@SpecField("nodes : set Node | this.nodes = this.header.succ - null")
public class LinkedList implements Iterable<LinkedList.Node> {

    @Invariant("this !in this.^next")
    @SpecField("succ : set Node | this.succ = this.*next - null")
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

        public int size() {
            if (next == null)
                return 1;
            else 
                return 1 + next.size();
        }

    }
    
    private Node header;

    @Requires("n.succ !in this.nodes")
    @Ensures("this.nodes = @old(this.nodes) + n.succ")
    @Modifies({
        "this.header", 
        "Node.next [{nn : this.nodes | nn.next == null}]"
    })
    public void add(Node n) { Squander.exe(this, n); }
    
    public void add_man(Node node) {
        Node n = header;
        Node prev = null;
        while (n != null) {
            prev = n; 
            n = n.next;
        }
        if (prev == null)
            header = node;
        else 
            prev.next = node;
    }
    
    @Requires("n in this.nodes")
    @Ensures("this.nodes = @old(this.nodes) - n")
    @Modifies({
        "this.header", 
        "Node.next [{nn : this.nodes | nn.next == n}]"
    })
    public void remove(Node n) { Squander.exe(this, n); }
    
    
    
    @Ensures({"return.length=#this.nodes", 
              "all n1 : Node | n1 in this.nodes => n1 in return[int] && (all n2 : Node | n2 in n1.^next => return.elts.n1 < return.elts.n2)"})
    @Modifies({"return.elts", "return.length"})
    @FreshObjects(cls = Node[].class, num = 1)
    public Node[] nodes() { return Squander.exe(this); }
    
    public Node header() { return header; }
    
    @Override
    public Iterator<Node> iterator() {
        List<Node> nodes = new ArrayList<Node>();
        Node n = header; 
        while (n != null) {
            nodes.add(n);
            n = n.next;
        }
        return nodes.iterator();
    }

    @Override
    public String toString() {
        return Arrays.toString(nodes());
    }

    public static void main(String[] args) {
        LinkedList lst = new LinkedList();
        lst.add(new Node(1));
        lst.header = new Node(1);
        Node n2 = new Node(2);
        n2.setNext(new Node(3));
        lst.add(n2);
        lst.remove(lst.header);
        
        System.out.println(lst);
//        int sum = Utils.fold(lst, 0, SqFunc.mkFunc("return = @arg(0) + @arg(1).value"));
//        System.out.println(sum);
    }

    public int size() {
        if (header == null)
            return 0;
        return header.size();
    }

    public boolean repOk() {
        Set<Integer> visited = new HashSet<Integer>();
        Node n = header;
        while (n != null) {
            if (!visited.add(System.identityHashCode(n)))
                return false;
            n = n.next;
        }
        return true;
    }

    public int findNode(Integer key) {
        Node n = header;
        int cnt = 0; 
        while (n != null) {
            if (n.value == key)
                return cnt;
            cnt++; 
            n = n.next;
        }
        return -1;
    }

}
/*! @} */
