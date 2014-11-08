/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.graph2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.examples.graph2.Graph.Node;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;


/**
 * Testing various graph algorithms.
 * 
 * @author kuat
 */
public class GraphTest extends MySquanderTestBase {

    @Test
    public void testGraph1() {
        Graph graph = new Graph();

        Node a = new Node(1);
        Node b = new Node(2);
        Node c = new Node(3);
        Node d = new Node(4);
        Node e = new Node(5);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addNode(d);
        graph.addNode(e);

        Assert.assertEquals(5, graph.numNodes());

        graph.newEdge(a, b);
        graph.newEdge(a, c);
        graph.newEdge(b, c);
        graph.newEdge(d, c);
        graph.newEdge(c, e);
        graph.newEdge(a, e);
        graph.newEdge(e, d);

        Assert.assertEquals(7, graph.numEdges());

        Node[] hampath = graph.hamiltonian();
        System.out.println(Arrays.toString(hampath));
        Assert.assertArrayEquals(new Node[] { a, b, c, e, d }, hampath);

        try {
            graph.topsort();
            Assert.fail("there should be no solution in this case");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGraph2() {
        SquanderGlobalOptions.INSTANCE.min_bitwidth = 5;
        Graph graph = new Graph();
        Node a = new Node(1);
        Node b = new Node(2);
        Node c = new Node(3);
        Node d = new Node(4);
        Node e = new Node(5);
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addNode(d);
        graph.addNode(e);
        graph.newEdge(a, b);
        graph.newEdge(a, c);
        graph.newEdge(a, d);
        graph.newEdge(a, e);
        graph.newEdge(b, c);
        graph.newEdge(b, d);
        graph.newEdge(c, d);
        graph.newEdge(c, e);
        graph.newEdge(e, d);

        Assert.assertEquals(1, a.label);
        Assert.assertEquals(2, b.label);
        Assert.assertEquals(3, c.label);
        Assert.assertEquals(4, d.label);
        Assert.assertEquals(5, e.label);
        Assert.assertEquals(9, graph.numEdges());

        // Node[] hampath = graph.hamiltonian();
        // System.out.println(Arrays.toString(hampath));
        // Assert.assertArrayEquals(new Node[]{a,b,c,e,d}, hampath);
        //        
        // Node[] topsort = graph.topsort();
        // System.out.println(Arrays.toString(topsort));
        // Assert.assertArrayEquals(new Node[]{d, e, c, b, a}, topsort);
        //        
        Node[] maxClique = graph.maxClique(4);
        System.out.println(Arrays.toString(maxClique));
        Set<Node> set = new HashSet<Node>(Arrays.asList(maxClique));
        Set<Node> clique1 = new HashSet<Node>(Arrays.asList((new Node[] { a, c, d, e })));
        Set<Node> clique2 = new HashSet<Node>(Arrays.asList((new Node[] { a, b, c, d })));
        Assert.assertTrue(set.equals(clique1) || set.equals(clique2));

    }

}
/*! @} */
