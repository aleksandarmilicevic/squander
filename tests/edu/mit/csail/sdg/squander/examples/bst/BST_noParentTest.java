/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.bst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.examples.bst.BST_noParent.Node;
import edu.mit.csail.sdg.squander.util.test.TestUtils;


public class BST_noParentTest extends MySquanderTestBase {

    private BST_noParent bst;
    private int[] keys;
    private Node[] nodes;

    @Before
    public void before() {
        keys = new int[] { 3, -4, -2, 2, 6 };
        initBinarySearchTree();
    }

    private void initBinarySearchTree() {
        nodes = new Node[keys.length];
        for (int i = 0; i < keys.length; i++)
            nodes[i] = new Node(keys[i]);
        bst = new BST_noParent();
        for (Node n : nodes)
            bst.insertNode(n);
    }

    @Test
    public void testMin_squander() {
        String bstStr = bst.toString();
        assertEquals("Wrong return value for the min_squander method", -4, bst.min_squander());
        assertTrue("repOK failed after calling max method", bst.repOk());
        assertEquals("BST changed!", bstStr, bst.toString());
        BST_noParent emptyBst = new BST_noParent();
        assertEquals("expected to get -1 as min on empty BST", -1, emptyBst.min_squander());
        assertTrue("repOK on empty BST failed after calling max method", emptyBst.repOk());
        assertEquals("BST changed!", bstStr, bst.toString());
    }

    @Test
    public void testMax_squander() {
        String bstStr = bst.toString();
        assertEquals("Wrong return value for the max_squander method", 6, bst.max_squander());
        assertTrue("repOK failed after calling min method", bst.repOk());
        assertEquals("BST changed!", bstStr, bst.toString());
        BST_noParent emptyBst = new BST_noParent();
        assertEquals("expected to get -1 as max on empty BST", -1, emptyBst.max_squander());
        assertTrue("repOK on empty BST failed after calling min method", emptyBst.repOk());
        assertEquals("BST changed!", bstStr, bst.toString());
    }

    @Test
    public void testFindNode_squander() {
        String bstStr = bst.toString();
        for (Node n : nodes) {
            assertEquals("Wrong node found using findNode", n, bst.findNode_squander(n.getKey()));
            assertTrue("repOK failed after calling findNode for key " + n.getKey(), bst.repOk());
            assertEquals("BST modified!", bstStr, bst.toString());
        }
        int nonExistentKey = 0;
        assertEquals("Expected to get null for the non-existend node", null, bst.findNode_squander(nonExistentKey));
        assertEquals("BST modified!", bstStr, bst.toString());
        
        assertEquals("expected to get null for empty bst", null, new BST_noParent().findNode(nonExistentKey));
    }

    @Test
    public void testRemoveNode_squander() {
        for (int i = 0; i < nodes.length; i++) {
            Node n = nodes[i];
            bst.removeNode_squander(n);
            assertTrue("repOK failed after calling removeNode for key " + n.getKey(), bst.repOk());
            assertEquals(nodes.length - i - 1, bst.size());
            assertTrue("nodeToRemove is still there", bst.findNode(n.getKey()) == null);
        }
    }

    @Test
    public void testInsertNode_squander() {
        BST_noParent bst = new BST_noParent();
        BST_noParent bstSq = new BST_noParent();
        for (int i = 0; i < keys.length; i++) {
            bst.insertNode(new Node(keys[i]));
            Node n = new Node(keys[i]);
            bstSq.insertNode_squander(n);
            assertTrue("repOK failed after calling insertNode for key " + n.getKey(), bstSq.repOk());
            assertEquals(i + 1, bstSq.size());
            assertTrue("nodeToInsert is not there", bstSq.findNode(n));
            assertEquals("didn't exect it to shuffle the tree", bst.toString(), bstSq.toString());
        }
    }

    @Test
    public void testInsertKey_squander() {
        BST_noParent bst = new BST_noParent();
        for (int i = 0; i < keys.length; i++) {
            bst.insertKey_squander(i);
            assertTrue("repOK failed after calling insertNode for key " + i, bst.repOk());
            assertEquals(i + 1, bst.size());
            assertTrue("nodeToInsert is not there", bst.findNode(i) != null);
        }
    }

    @Test
    public void testGetAllNodes_squander() {
        Node[] nodes = bst.getAllNodes_squander();
        Assert.assertNotNull(nodes);
        int[] keys = new int[nodes.length];
        int idx = 0;
        for (Node node : nodes) {
            keys[idx++] = node.key;
        }
        TestUtils.assertArraysEqualNoOrdering(new int[] { 2, -2, -4, 3, 6 }, keys);
    }

    @Test
    public void testInsertMix() {
        BST_noParent bst = new BST_noParent();
        for (int i = 0; i < keys.length; i++) {
            Node n = new Node(i);
            if (i % 2 == 0)
                bst.insertNode_squander(n);
            else 
                bst.insertNode(n);
            assertTrue("repOK failed after calling insertNode for key " + n.getKey(), bst.repOk());
            assertEquals(i + 1, bst.size());
            assertTrue("nodeToInsert is not there", bst.findNode(n.getKey()) != null);
        }
    }
    
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(BST_noParentTest.class.getName());
    }

}
/*! @} */
