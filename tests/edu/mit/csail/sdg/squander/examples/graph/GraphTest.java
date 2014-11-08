/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.examples.graph.Graph.Edge;
import edu.mit.csail.sdg.squander.examples.graph.Graph.Node;
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

        Node[] hampathExpected = new Node[] { a, b, c, e, d };

        Node[] hampath = graph.hamiltonian();
        Assert.assertArrayEquals(hampathExpected, hampath);

        Edge[] hampath2 = graph.hamiltonian2();
        for (int i = 0; i < hampath2.length; i++)
            Assert.assertEquals(hampathExpected[i], hampath2[i].src);
        Assert.assertEquals(hampathExpected[hampathExpected.length - 1], hampath2[hampath2.length - 1].dst);
        
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
        Set<Node> set = new HashSet<Node>(Arrays.asList(maxClique));
        Set<Node> clique1 = new HashSet<Node>(Arrays.asList((new Node[] { a, c, d, e })));
        Set<Node> clique2 = new HashSet<Node>(Arrays.asList((new Node[] { a, b, c, d })));
        Assert.assertTrue(set.equals(clique1) || set.equals(clique2));

    }

    @Test
    public void testColor() {
        Graph g = new Graph();
        Node a = g.newNode(1);
        Node b = g.newNode(2);
        Node c = g.newNode(3);
        Node d = g.newNode(4);
        Node e = g.newNode(5);
        
        g.newUndirectedEdge(a, b);
        g.newUndirectedEdge(b, c);
        g.newUndirectedEdge(c, d);
        g.newUndirectedEdge(d, e);
        
        Map<Node, Integer> colors = g.color(2);
        Assert.assertTrue("invalid colors", checkColorsOk(g, colors));
        
        g.newEdge(e, a);
        try {
            g.color(2);
            Assert.fail("this graph cannot be colored with only 2 colors");
        } catch (Exception t) {
            colors = g.color(3);
            Assert.assertTrue("invalid colors", checkColorsOk(g, colors));
        }
    }
    
    @Test
    public void testTSP() {
        Graph g = new Graph();

        Node a = g.newNode(1);
        Node b = g.newNode(2);
        Node c = g.newNode(3);
        Node d = g.newNode(4);
        Node e = g.newNode(5);

        g.newUndirectedEdge(a, b, 1);
        g.newUndirectedEdge(a, c, 1);
        g.newUndirectedEdge(b, c, 1);
        g.newUndirectedEdge(d, c, 1);
        g.newUndirectedEdge(c, e, 1);
        g.newUndirectedEdge(a, e, 1);
        g.newUndirectedEdge(e, d, 1);
        g.newUndirectedEdge(d, a, 1);

        int cost = 5;
        Edge[] hamcycle = g.tsp(a, cost);
        Assert.assertTrue(Graph.checkTSP(g, hamcycle, cost));
        try {
            g.tsp(a, cost - 1);
            Assert.fail("there can't be a path with cost less than " + cost);
        } catch (Exception t) {
        }
    }

    
    private boolean checkColorsOk(Graph g, Map<Node, Integer> colors) {
        if (colors.size() != g.size())
            return false;
        for (Node n : colors.keySet()) {
            for (Node m : g.getNeighbors(n)) {
                if (colors.get(n) == colors.get(m))
                    return false;
            }
        }
        return true;
    }
    
}
/*! @} */
