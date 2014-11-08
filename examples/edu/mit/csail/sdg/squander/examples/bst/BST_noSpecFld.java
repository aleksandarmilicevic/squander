/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.bst;

import java.text.MessageFormat;
import java.util.HashSet;

import edu.mit.csail.sdg.annotations.Case;
import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Nullable;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Pure;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.Specification;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Binary search tree that doesn't make use of the spec field. 
 * 
 * @author Aleksandar Milicevic
 */
public class BST_noSpecFld {
    
    @Invariant( {
        /* left sorted  */  "all x : this.left.*(left + right) - null | x.key < this.key",
        /* right sorted */  "all x : this.right.*(left + right) - null | x.key > this.key"
        }) 
    public final static class Node {
        Node left;
        Node right;
        int key;
        
        Node() {}
        
        public Node(int key)    { this.key = key; }
        
        public Node getLeft()   { return left; }
        public Node getRight()  { return right; }
        public int getKey()     { return key; }
        
        public int size() {
            return 1 + (left != null ? left.size() : 0) + (right != null ? right.size() : 0);
        }
        
        boolean repOk(HashSet<Node> visited) {
            if (!visited.add(this))
                return false;
            if (left != null) {
                if (left.key >= key) return false;
                if (!left.repOk(visited)) return false;
            }
            if (right != null) {
                if (right.key <= key) return false;
                if (!right.repOk(visited)) return false;
            }
            return true;    
        }
        
        @Override
        public String toString() {
            return MessageFormat.format("{0}", key);
        }
    }
    
    @Nullable
    private Node root;

    public BST_noSpecFld() {
        root = null;
    }

    public Node getRoot() { return root; }
    public void setRoot(Node root) { this.root = root; }

    public int size() {
        if (root == null)
            return 0;
        return root.size();
    }
        
    public boolean repOk() {
        if (root == null)
            return true;
        return root.repOk(new HashSet<Node>());
    }
    
    public BST_noSpecFld insertNode(Node z) {
        if (root == null) {
            root = z;
            return this;
        }
        Node parent = null;
        Node current = root;
        while (true) {
            parent = current;
            if (z.key == current.key)
                throw new RuntimeException("key : " + z.key + " already exists");
            if (z.key > current.key) {
                current = current.right;
                if (current == null) {
                    parent.right = z;
                    break;
                }
            } else {
                current = current.left;
                if (current == null) {
                    parent.left = z;
                    break;
                }
            }
        }
        return this;
    }
    
    public Node findNode(int keyToFind) {
        Node currNode = root; 
        while (currNode != null) {
            if (currNode.key == keyToFind)
                return currNode;
            else if (keyToFind > currNode.key)
                currNode = currNode.right;
            else
                currNode = currNode.left;
        }
        return null;
    }

    public boolean findNode(Node n) {
        Node currNode = root; 
        while (currNode != null) {
            if (currNode == n)
                return true;
            if (currNode.key == n.key)
                return false;
            else if (n.key > currNode.key)
                currNode = currNode.right;
            else
                currNode = currNode.left;
        }
        return false;
    }
    
    public int min() {
        if (root == null)
            return -1;
        Node n = root;
        while (n.left != null)
            n = n.left;
        return n.key;
    }
    
    public int max() {
        if (root == null)
            return -1;
        Node n = root;
        while (n.right != null)
            n = n.right;
        return n.key;
    }
    
    @Returns("some (this.root.*(left+right) - null) ? {k in (this.root.*(left+right) - null).key | no y in (this.root.*(left+right) - null) | y.key > k} : -1")
    public int max_squander() {
        return Squander.exe(this);
    }
    
    @Returns("some (this.root.*(left+right) - null) ? {k in (this.root.*(left+right) - null).key | no y in (this.root.*(left+right) - null) | y.key < k} : -1")
    public int min_squander() {
        return Squander.exe(this);
    }
    
    @Returns("keyToFind in (this.root.*(left+right) - null).key ? {n in (this.root.*(left+right) - null) | n.key == keyToFind} : null")
    public Node findNode_squander(int keyToFind) {
        return Squander.exe(this, new Class<?>[] {int.class}, new Object[] {keyToFind});
    }
    
    @Specification({
        @Case(requires = "this.root != nodeToRemove", 
              ensures  = "(this.root.*(left+right) - null) = @old((this.root.*(left+right) - null) - nodeToRemove)",
              modifies = "Node.left, Node.right"),
        @Case(requires = "this.root == nodeToRemove", 
              ensures  = "(this.root.*(left+right) - null) = @old((this.root.*(left+right) - null) - nodeToRemove)",
              modifies = "Node.left, Node.right, this.root")
    })
    public void removeNode_squander(Node nodeToRemove) {
        Squander.exe(this, new Class<?>[] {Node.class}, new Object[] {nodeToRemove});
    }
    
    @Specification({
        @Case(requires = {"this.root != null",
                          "z.key !in (this.root.*(left+right) - null).key", 
                          "z.(left + right) = null"}, 
              ensures  = "(this.root.*(left+right) - null) = @old((this.root.*(left+right) - null) + z)",
              modifies = {"Node.left  [{n : Node | n in (this.root.*(left+right) - null) && n.left == null}]", 
                          "Node.right [{n : Node | n in (this.root.*(left+right) - null) && n.right == null}]"}),
        @Case(requires = {"this.root == null", 
                          "z.(left + right) = null"}, 
              ensures  = "this.root = z",
              modifies = "this.root")
    })
    public void insertNode_squander(Node z) {
        Squander.exe(this, new Class<?>[] {Node.class}, new Object[] {z});
    }
    
    @Requires( {"k !in (this.root.*(left+right) - null).key"})
    @Ensures("@old(this.root.*(left+right) - null) in (this.root.*(left+right) - null) && @old((this.root.*(left+right) - null).key) in (this.root.*(left+right) - null).key && k in (this.root.*(left+right) - null).key")
    @Modifies("Node.left, Node.right, Node.key, this.root")
    @Fresh({@FreshObjects(cls=Node.class, num=1)})
    public void insertKey_squander(int k) {
        Squander.exe(this, new Class<?>[] {int.class}, new Object[] {k});
    }
    
    @Ensures("return[int] = (this.root.*(left+right) - null) && return.length = #(this.root.*(left+right) - null)") 
    @Modifies({"return.length", "return.elts"})
    @Fresh({@FreshObjects(cls = Node[].class, num = 1)})
    public Node[] getAllNodes_squander() {
        return Squander.exe(this);
    }
    
    @Pure
    @Override
    public String toString() {
        if (root == null)
            return "null";
        return printNode(root);
    }

    private String printNode(Node node) {
        if (node == null)
            return "null";
        return String.format("{%d(left=%s, right=%s)}", node.key, printNode(node.left), printNode(node.right));
    }

    
    private static final int NUM_NODES = 20; 
    
    @Fresh({
        @FreshObjects(cls=BinarySearchTree.class, num=1),
        @FreshObjects(cls=Node.class, num=NUM_NODES)
    })
    @Ensures({"#(this.root.*(left+right) - null) = " + NUM_NODES})
    @Modifies("this.root, Node.left, Node.right, Node.keys")
    @Options(solveAll = true)
    public void genBST() {
        Squander.exe(this);
    }
    
    
    public static void main(String[] args) {
        BST_noSpecFld bst = new BST_noSpecFld();
        Node node1 = new Node(3);
        Node node2 = new Node(-4);
        Node node3 = new Node(-2);
        Node node4 = new Node(2);
        Node node5 = new Node(6);
        bst.insertNode(node1).insertNode(node2).insertNode(node3).insertNode(node4).insertNode(node5);
        // System.out.println("running max_squander ...");
        // System.out.println("max = " + bst.max_squander());
        // System.out.println("running min_squander ...");
        // System.out.println("min = " + bst.min_squander());
        // System.out.println("running findNode_squander ...");
        bst.insertNode_squander(new Node(1));
    }


}
/*! @} */
