/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.bst;

import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * Balanced binary search tree. 
 * <p>
 * Adds an additional invariant to ensure that for every tree node 
 * <code>n</code>, the sizes of its left and right subtrees don't 
 * differ by more than 1. 
 * 
 * @author Aleksandar Milicevic
 */
@Invariant("all n: this.nodes | (#n.left.^(left+right) - #n.right.^(left+right)) in {-1, 0, 1}")
public class BalancedBST extends BST_noParent {

    @Override
    public boolean repOk() {
        boolean b = super.repOk();
        if (!b)
            return false;
        return check(root);
    }
    private boolean check(Node n) {
        if (n == null)
            return true;
        int lhsSize = 0; 
        if (n.left != null)
            lhsSize = n.left.size();
        int rhsSize = 0; 
        if (n.right != null)
            rhsSize = n.right.size();
        if (Math.abs(lhsSize - rhsSize) > 1)
            return false;
        if (!check(n.left))
            return false;
        if (!check(n.right))
            return false;
        return true;
    }

    @Options(ensureAllInts = true)
    @Override
    public void insertKey_squander(int k) {
        super.insertKey_squander(k);
    }
    
    @Options(ensureAllInts = true)
    @Override
    public void insertNode_slow_squander(Node z) {
        super.insertNode_slow_squander(z);
    }
    
    @Options(ensureAllInts = true)
    @Override
    public void removeNode_squander(Node nodeToRemove) {
        super.removeNode_squander(nodeToRemove);
    }

    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;
        //TODO: make it a junit test
        BST_noParent bst = new BalancedBST();
        bst.insertKey_squander(4);
        System.out.println(bst);
        assert bst.repOk();
        
        bst.insertKey_squander(6);
        System.out.println(bst);
        assert bst.repOk();
        
        bst.insertKey_squander(5);
        System.out.println(bst);
        assert bst.repOk();
        
        bst.insertKey_squander(7);
        System.out.println(bst);
        assert bst.repOk();
        
        bst.insertKey_squander(1);
        System.out.println(bst);
        assert bst.repOk();
        
        bst.insertKey_squander(8);
        System.out.println(bst);
        assert bst.repOk();
        
        bst.removeNode_squander(bst.findNode(5));
        System.out.println(bst);
        assert bst.repOk();
        
        bst.removeNode_squander(bst.findNode(1));
        System.out.println(bst);
        assert bst.repOk();
        
        bst.removeNode_squander(bst.findNode(7));
        System.out.println(bst);
        assert bst.repOk();
        
        bst.removeNode_squander(bst.findNode(4));
        System.out.println(bst);
        assert bst.repOk();
        
        bst.removeNode_squander(bst.findNode(6));
        System.out.println(bst);
        assert bst.repOk();
        
        bst.removeNode_squander(bst.findNode(8));
        System.out.println(bst);
        assert bst.repOk();
    }
    
}
/*! @} */
