/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.rbt;

import edu.mit.csail.sdg.annotations.Case;
import edu.mit.csail.sdg.annotations.Effects;
import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Nullable;
import edu.mit.csail.sdg.annotations.Pure;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.annotations.Specification;
import edu.mit.csail.sdg.annotations.Throws;
import edu.mit.csail.sdg.annotations.Case.Type;
import edu.mit.csail.sdg.squander.Squander;

/** 
 * Case study of Red-Black trees with integer keys.
 * Stripped down and simplified. 
 * 
 * @author Kuat Yessenov
 */

/**
 * A tree with integer keys.
 * 
 * @author Emina Torlak
 */
@SpecField("nodes : set Node | this.nodes = this.root.*(left+right) - null")
public final class RedBlackTree {
    
    static final boolean BLACK = true;
    static final boolean RED = false;

    @Invariant( {
        /* root */ "this.root.parent in null",
        /* distinct */ "this.root != null => one root.(this.root)"})
    @Nullable
    Node root;

    /**
     * Creates an empty IntTree. 
     */
    @Ensures("no this.nodes")
    RedBlackTree() {
    }
    
    /**
     * Discards all elements from this tree. 
     */
    @Ensures("no this.nodes")
    @Modifies("this.root")
    void clear() {
        Squander.exe(this);
    }

    /**
     * Returns the node with the given key, or null no such node exists. return
     */
    @Ensures("return - null = this.nodes & (Node@key.k)")
    @Pure
    Node search(int k) {
        return Squander.exe(this, k);
    }

    /**
     * Returns the node whose key is the ceiling of <tt>k</tt> in this tree, or
     * null if no such node exists. 
     */
    @Ensures("return - null =" +
        "{n : this.nodes | n.key >= k && (no m in this.nodes | m.key >= k && m.key < n.key)}")
    @Pure
    Node searchGTE(int k) {
        return Squander.exe(this, new Class[] { int.class }, new Integer[] { k });
    }

    /**
     * Returns the node whose key is the floor of <tt>k</tt> in this tree, or
     * null if no such node exists. return {n: this.nodes | n.key <= k && no n':
     * this.nodes - n | n'.key <= k && n'.key > n.key }
     */
    @Ensures( { "(some node in this.nodes | node.key <= k) => return != null", 
            "return != null => return.key <= k",
            "return != null => (no node in this.nodes | node.key <= k && node.key > return.key)",
            "return in this.nodes + null" })
    @Pure
    Node searchLTE(int k) {
        return Squander.exe(this, new Class[] { int.class }, new Integer[] { k });
    }

    /**
     * Implementation of the tree-predecessor algorithm from CLR. Returns the
     * given node's predecessor, if it exists. Otherwise returns null. return
     * the given node's predecessor throws NullPointerException - node = null
     */
    @Specification( {
            @Case(requires = { "(some x:this.nodes | x.key < node.key)", "node in this.nodes" }, 
                  ensures = {
                    "return in {x in this.nodes | x.key < node.key}",
                    "no {x : this.nodes | x.key < node.key && x.key > return.key}" }),
            @Case(requires = { "no x in this.nodes | x.key < node.key", "node in this.nodes" }, ensures = "return = null"),
            @Case(requires = "node = null", ensures = "throw in NullPointerException", type = Type.EXCEPTIONAL) })
    @Pure
    Node predecessor(Node node) {
        return Squander.exe(this, new Class[] { Node.class }, new Node[] { node });
    }

    /**
     * Implementation of the tree-successor algorithm from CLR. Returns the
     * given node's successor, if it exists. Otherwise returns null. 
     */
    @Requires("node in this.nodes")
    @Ensures( { 
        "(some x:this.nodes | x.key > node.key) => return != null",
        "return != null => return.key > node.key && (no x:this.nodes | x.key > node.key && x.key < return.key)",
        "return in this.nodes + null"
        })
    @Throws("NullPointerException : node = null")
    @Pure
    Node successor(Node node) {
        return Squander.exe(this, new Class[] { Node.class }, new Node[] { node });
    }

    /**
     * Returns the node with the smallest key. return key.(min(this.nodes.key))
     */
    @Returns("some this.nodes ? {x in this.nodes | no y in this.nodes | y.key < x.key} : null")
    @Pure
    Node minAll() {
        return Squander.exe(this);
    }

    /**
     * Returns the node with the largest key. return key.(max(this.nodes.key))
     */
    @Returns("some this.nodes ? {x in this.nodes | no y in this.nodes | y.key > x.key} : null")
    @Pure
    Node maxAll() {
        return Squander.exe(this);
    }

    /**
     * Replaces the old node, o, with the given new node, n, in this tree.
     * 
     */
    @Requires( {"no tree : IntTree | tree.root = n", 
        "o in this.nodes", 
        "n.(left + right + parent) = null", 
        "o = o.parent.left => n.key < o.parent.key",
        "o = o.parent.right => n.key > o.parent.key", 
        "o.left != null => n.key > o.left.key",
        "o.right != null => n.key < o.right.key", "o != n" })
    @Ensures( { "this.nodes = @old(this.nodes) - o + n", 
        "o.parent + o.left + o.right = null" })
    @Modifies("Node.left, Node.right, Node.color, Node.parent, this.root, this.nodes")
    void replace(Node o, Node n) {
        Squander.exe(this, new Class[] { Node.class, Node.class }, new Node[] { o, n });
    }

    /**
     * Implementation of the CLR insertion algorithm.
     *  
     */
    @Requires( {"z.key !in this.nodes.key", 
        "z.(parent + left + right) = null", 
        "no root.z" })
    @Ensures("this.nodes = @old(this.nodes + z)")
    @Modifies("Node.left, Node.right, Node.color, Node.parent, this.root, this.nodes")
    void insert(Node z) {
        Squander.exe(this, new Class[] { Node.class }, new Node[] { z });
    }

    /**
     * A slightly modified implementation of the CLR deletion algorithm.
     * requires z in this.nodes effects this.nodes' = this.nodes - z
     */
    @Requires("z in this.nodes")
    @Effects("this.nodes = @old(this.nodes - z)")
    @Modifies("this.nodes, this.root, Node.left, Node.right, Node.color, Node.parent")
    void delete(Node z) {
        Squander.exe(this, new Class[] { Node.class }, new Node[] { z });
    }

    @Invariant( {
    /* parent left */"this.left != null => this.left.parent = this",
    /* parent right */"this.right != null => this.right.parent = this",
    /* parent */"this.parent != null => this in this.parent.(left + right)",
    /* form a tree */"this !in this.^parent",
    /* left sorted */"all x : this.left.^(left + right) + this.left - null | x.key < this.key",
    /* right sorted */"all x : this.right.^(left + right) + this.right - null | x.key > this.key",
    /* no red node has a red parent */" this.color = false && this.parent != null => this.parent.color = true"}) 
    public static class Node {
        public final Node parent, left, right;
        public final boolean color;
        public final int key;

        Node(int key) {
            this.parent = this.left = this.right = null;
            this.color = BLACK;
            this.key = key;
        }
    }
}
/*! @} */
