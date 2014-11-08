package edu.mit.csail.sdg.squander.examples.bst;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

public class BalancedBSTTest extends MySquanderTestBase {

	@Test
	public void testAll() {
		SquanderGlobalOptions.INSTANCE.log_level = Log.Level.NONE;
		
		BST_noParent bst = new BalancedBST();
        bst.insertKey_squander(4);
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(1, bst.size());
        
        bst.insertKey_squander(6);
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(2, bst.size());
        
        bst.insertKey_squander(5);
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(3, bst.size());
        
        bst.insertKey_squander(7);
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(4, bst.size());
        
        bst.insertKey_squander(1);
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(5, bst.size());
        
        bst.insertKey_squander(8);
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(6, bst.size());
        
        bst.removeNode_squander(bst.findNode(5));
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(5, bst.size());
        
        bst.removeNode_squander(bst.findNode(1));
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(4, bst.size());
        
        bst.removeNode_squander(bst.findNode(7));
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(3, bst.size());
        
        bst.removeNode_squander(bst.findNode(4));
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(2, bst.size());
        
        bst.removeNode_squander(bst.findNode(6));
        System.out.println(bst);
        Assert.assertTrue(bst.repOk());
        Assert.assertEquals(1, bst.size());
        
        bst.removeNode_squander(bst.findNode(8));
        System.out.println(bst);
        Assert.assertTrue(bst.repOk()); 
        Assert.assertEquals(0, bst.size());
	}
	
}
