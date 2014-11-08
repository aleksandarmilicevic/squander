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
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.annotations.Specification;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Binary search tree, whose nodes don't have the "parent" pointer, like in
 * {@link BinarySearchTree}. 
 * 
 * @author Aleksandar Milicevic
 */
@SpecField("nodes : set Node | this.nodes = this.root.*(left+right) - null")
//@Invariant({"this.root != null => one root.(this.root)"})
public class BST_noParent {

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
    protected Node root;

    @Ensures("no this.nodes")
    public BST_noParent() {
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
    
    @Requires( {"z.key !in this.nodes.key", 
                "z.(left + right) = null", 
                "no root.z" })
    @Ensures("this.nodes = @old(this.nodes + z)")
    @Modifies("Node.left, Node.right, this.root, this.nodes")
    public BST_noParent insertNode(Node z) {
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
    
    @Returns("keyToFind in this.nodes.key ? {n in this.nodes | n.key == keyToFind} : null")
    @Pure
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

    @Returns("some this.nodes ? {k in this.nodes.key | no y in this.nodes | y.key < k} : -1")
    @Pure
    public int min() {
        if (root == null)
            return -1;
        Node n = root;
        while (n.left != null)
            n = n.left;
        return n.key;
    }
    
    @Returns("some this.nodes ? {k in this.nodes.key | no y in this.nodes | y.key > k} : -1")
    @Pure
    public int max() {
        if (root == null)
            return -1;
        Node n = root;
        while (n.right != null)
            n = n.right;
        return n.key;
    }
    
    @Returns("some this.nodes ? {k in this.nodes.key | no y in this.nodes | y.key > k} : -1")
    public int max_squander() {
        return Squander.exe(this);
    }
    
    @Returns("some this.nodes ? {k in this.nodes.key | no y in this.nodes | y.key < k} : -1")
    public int min_squander() {
        return Squander.exe(this);
    }
    
    @Returns("keyToFind in this.nodes.key ? {n in this.nodes | n.key == keyToFind} : null")
    public Node findNode_squander(int keyToFind) {
        return Squander.exe(this, new Class<?>[] {int.class}, new Object[] {keyToFind});
    }
    
//    @Specification({
//        @Case(requires = "this.root != nodeToRemove && nodeToRemove in this.nodes", 
//              ensures  = "this.nodes = @old(this.nodes) - nodeToRemove",
//              modifies = "Node.left, Node.right, this.nodes"),
//        @Case(requires = "this.root == nodeToRemove && nodeToRemove in this.nodes", 
//              ensures  = "this.nodes = @old(this.nodes) - nodeToRemove",
//              modifies = "Node.left, Node.right, this.nodes, this.root")
//    })
    @Ensures("this.nodes = @old(this.nodes) - nodeToRemove")
    @Modifies("this.root, Node.left, Node.right")
    public void removeNode_squander(Node nodeToRemove) {
        Squander.exe(this, new Class<?>[] {Node.class}, new Object[] {nodeToRemove});
    }
    
    @Specification({
        @Case(requires = {"this.root != null",
                          "z.key !in this.nodes.key", 
                          "z.(left + right) = null"}, 
              ensures  = "this.nodes = @old(this.nodes + z)",
              modifies = {"this.nodes.left | _<1> = null", 
                          "this.nodes.right| _<1> = null"
                          }),
        @Case(requires = {"this.root == null", 
                          "z.(left + right) = null"}, 
              ensures  = "this.root = z",
              modifies = "this.root")
    })
    public void insertNode_squander(Node z) {
        Squander.exe(this, z);
    }
    
    @Ensures("this.nodes = @old(this.nodes) + z")
    @Modifies("this.root, Node.left, Node.right")
    public void insertNode_slow_squander(Node z) {
        Squander.exe(this, z);
    }
    
    
    @Requires( {"k !in this.nodes.key"})
    @Ensures("@old(this.nodes) in this.nodes && @old(this.nodes.key) in this.nodes.key && k in this.nodes.key")
    @Modifies("Node.left, Node.right, Node.key, this.root")
    @Fresh({@FreshObjects(cls=Node.class, num=1)})
    public void insertKey_squander(int k) {
        Squander.exe(this, k);
    }
    
    @Ensures("return[int] = this.nodes && return.length = #this.nodes") 
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
        return String.format("{%d(%s, %s)}", node.key, printNode(node.left), printNode(node.right));
    }

    
    private static final int NUM_NODES = 20; 
    
    @Fresh({
        @FreshObjects(cls=BinarySearchTree.class, num=1),
        @FreshObjects(cls=Node.class, num=NUM_NODES)
    })
    @Ensures({"#this.nodes = " + NUM_NODES})
    @Modifies("this.nodes, this.root, Node.left, Node.right, Node.keys")
    @Options(solveAll = true)
    public void genBST() {
        Squander.exe(this);
    }
    
    
    public static void main(String[] args) {
        BST_noParent bst = new BST_noParent();
        Node node1 = new Node(4);
        Node node2 = new Node(6);
        Node node3 = new Node(5);
        bst.insertNode(node1).insertNode(node2).insertNode(node3);
        // System.out.println("running max_squander ...");
        // System.out.println("max = " + bst.max_squander());
        // System.out.println("running min_squander ...");
        // System.out.println("min = " + bst.min_squander());
        // System.out.println("running findNode_squander ...");
        System.out.println(bst);
        bst.insertNode_squander(new Node(0));
        System.out.println(bst);
        assert bst.repOk();
        assert bst.findNode(0) != null;
        assert bst.size() == 4;
    }

// GENERATE BSTs.
//    public static void main(String[] args) {
//        BinarySearchTree bst = new BinarySearchTree();
////        Squander sq = new Squander();
////        Squander.setDefaultOptions(5);
//        Iterator<SquanderResult> it = Squander.magic(bst);
//        long t1 = System.currentTimeMillis(); 
//        bst.genBST();
//        int cnt = 0; 
//        HashSet<String> solutions = new HashSet<String>();
//        int okCnt = 0; 
//        while (true) {
//            String bstStr = bst.toString();
//            if (!solutions.add(bstStr)) {
//                cnt++; 
//                if (cnt == 10)
//                    break;
//                if (!it.hasNext()) 
//                    break;
//                SquanderResult soln = it.next();
//                continue;
//            }
//            System.out.println("## " + bstStr);
//            okCnt++;
//            cnt = 0; 
//            if (okCnt > 100) break;
//            if (!it.hasNext()) 
//                break;
//            SquanderResult soln = it.next();
//        }
//        System.out.println("solutions: " + okCnt);
//        System.out.println("time: " + Long.toString(System.currentTimeMillis() - t1));
//        
//    }

//    /**
//     * Runs Squander for min and max methods
//     */
//    public static void main(String[] args) {
//        BinarySearchTree bst = new BinarySearchTree();
//        Node node1 = new Node(3);
//        Node node2 = new Node(-4);
//        Node node3 = new Node(-2);
//        Node node4 = new Node(2);
//        Node node5 = new Node(6);
//        bst.insert(node1).insert(node2).insert(node3).insert(node4).insert(node5);
////        System.out.println("running max_squander ...");
////        System.out.println("max = " + bst.max_squander());
////        System.out.println("running min_squander ...");
////        System.out.println("min = " + bst.min_squander());
////        System.out.println("running findNode_squander ...");
//        bst.removeNode_squander(node3);
//        System.out.println(bst);
//    }

}
/*! @} */
