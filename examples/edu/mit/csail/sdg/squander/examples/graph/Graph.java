/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;

/**
 * An implementation of the Graph data structure, along with several 
 * graph algorithms. 
 * 
 * @author Aleksandar Milicevic
 */
public class Graph implements Serializable {

    private static final long serialVersionUID = -6332798241722968023L;

    // ================================
    // ---------- Class Node ----------
    // ================================
    /**
     * Graph node
     */
    public static class Node implements Serializable {
        private static final long serialVersionUID = 8914980217070845458L;
        
        public int label;

        public Node(int label) { this.label = label; }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + label;
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Node other = (Node) obj;
            if (label != other.label)
                return false;
            return true;
        }
        
        @Override
        public String toString() {
            return "(" + String.valueOf(label) + ")";
        }
    }
    
    // ================================
    // ---------- Class Edge ----------
    // ================================
    /**
     * Directed graph edge.
     */
    public static class Edge implements Serializable {
        private static final long serialVersionUID = -5337889501186870289L;
        
        public final Node src;
        public final Node dst;
        public final int cost;

        public Edge(Node src, Node dest, int cost) {
            this.src = src;
            this.dst = dest;
            this.cost = cost;
        }
        
        public Edge(Node src, Node dest) {
            this(src, dest, 0);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((dst == null) ? 0 : dst.hashCode());
            result = prime * result + ((src == null) ? 0 : src.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Edge other = (Edge) obj;
            if (dst == null) {
                if (other.dst != null)
                    return false;
            } else if (!dst.equals(other.dst))
                return false;
            if (src == null) {
                if (other.src != null)
                    return false;
            } else if (!src.equals(other.src))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("%s --%s--> %s", src, cost, dst);
        } 
        
    }
    
    private Set<Node> nodes = new LinkedHashSet<Node>();
    private Set<Edge> edges = new LinkedHashSet<Edge>();

    private Node auxNode;
    public Node getAuxNode() { return auxNode; }
    public void setAuxNode(Node aux) { this.auxNode = aux; }

    private int auxInt;
    public int getAuxInt() { return auxInt; }
    public void setAuxInt(int auxInt) { this.auxInt = auxInt; }
    
    public Graph() {}
    
    public Node newNode(int key) {
        Node n = new Node(key);
        addNode(n);
        return n;
    }

    public void addNode(Node n) { nodes.add(n); }
    public void addEdge(Edge e) { edges.add(e); }
    
    public void newUndirectedEdge(Node a, Node b) {
        newEdge(a, b);
        newEdge(b, a);
    }

    public void newUndirectedEdge(Node a, Node b, int cost) {
        newEdge(a, b, cost); 
        newEdge(b, a, cost);
    }
    
    public Edge newEdge(Node a, Node b) {
        Edge e = new Edge(a, b);
        addEdge(e);
        return e;        
    }
    
    public Edge newEdge(Node a, Node b, int cost) {
        Edge e = new Edge(a, b, cost);
        addEdge(e);
        return e;
    }

    public boolean containsEdge(Node n1, Node n2) { return edges.contains(new Edge(n1, n2)); }
    public Node findNode(int label) { 
        for (Node n : nodes)
            if (n.label == label)
                return n;
        return null;
    }
    
    public Node[] nodes() { return nodes.toArray(new Node[0]); }
    public Edge[] edges() { return edges.toArray(new Edge[0]); }
    
    public int numEdges() { return edges.size(); }
    public int numNodes() { return nodes.size(); }
    public int size()     { return numNodes(); }

    public Set<Node> getNeighbors(Node src) {
        Set<Node> neighbrs = new HashSet<Node>();
        for (Edge e : edges) 
            if (e.src == src)
                neighbrs.add(e.dst);
        return neighbrs;
    }
    
    public void removeAllIncomingEdges(Node n) {
        for (Edge e : new ArrayList<Edge>(edges)) {
            if (e.dst.equals(n))
                edges.remove(e);
        }
    }

    public void removeAllOutgoingEdges(Node n) {
        for (Edge e : new ArrayList<Edge>(edges)) {
            if (e.src.equals(n))
                edges.remove(e);
        }
    }
    
    /**
     * Spec for the Hamiltonian path problem
     */
    @Ensures( {
        "return[int] = this.nodes.elts",
        "return.length = #this.nodes.elts",
        "all i : int | i >= 0 && i < return.length - 1 => (exists e : this.edges.elts | e.src = return[i] && e.dst = return[i+1])"
    })
    @Modifies({"return.length", "return.elts"})
    @FreshObjects(cls = Node[].class, num = 1)
    @Options(ensureAllInts = false)
    public Node[] hamiltonian() {
        return Squander.exe(this);
    }

    /**
     * A different spec for the Hamiltonian path problem
     */
    @Ensures( {
        "return[int] in this.edges.elts", 
        "return[int].(src + dst) = this.nodes.elts",
        "return.length = #this.nodes.elts - 1",
        "all i : int | i >= 0 && i < return.length - 1 => return[i].dst = return[i+1].src"
    })
    @Modifies({"return.length", "return.elts"})
    @FreshObjects(cls = Edge[].class, num = 1)
    @Options(ensureAllInts = false)
    public Edge[] hamiltonian2() {
        return Squander.exe(this);
    }
    
    /**
     * Traveling Salesman Problem 
     */
    @Ensures( {
        "return[int] in this.edges.elts",
        "return[int].(src + dst) = this.nodes.elts",
        "return.length = #this.nodes.elts",
        "all i : int | i >= 0 && i < return.length - 1 => return[i].dst = return[i+1].src",
        "startNode = return[0].src && startNode = return[return.length - 1].dst", 
        "(sum i : int | return[i].cost) <= maxCost"
    })
    @Modifies( { "return.length", "return.elts" })
    @FreshObjects(cls = Edge[].class, num = 1)
    @Options(ensureAllInts = true)
    public Edge[] tsp(Node startNode, int maxCost) {
        return Squander.exe(this, startNode, maxCost);
    }

    /**
     * Topological sort
     */
    @Ensures( {
        "return[int] = this.nodes.elts",
        "return.length = #this.nodes.elts",
        "all i : int | all j : int | 0 <= i && i < j && j < return.length => (no e : this.edges.elts | e.src = return[i] && e.dst = return[j])"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))
    public Node[] topsort() {
        return Squander.exe(this);
    }

    /**
     * Max Clique 
     */
    @Ensures({
        "return[int] in this.nodes.elts",
        "all i : int | all j : int | 0 <= i && i < j && j < return.length => (exists e : this.edges.elts | (e.src = return[i] && e.dst = return [j]) || (e.dst = return[i] && e.src = return[j]))",
        "#return[int] >= k"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))        
    public Node[] maxClique(int k) {
        return Squander.exe(this, new Class<?>[]{int.class}, new Object[]{k});
    }

    /**
     * Graph k-Coloring problem 
     */
    @Ensures({
        "return.keys = this.nodes.elts", 
        "all c : return.vals | c > 0 && c <= k",
        "all e : this.edges.elts | return.elts[e.src] != return[e.dst]"
    })
    @Modifies("return.elts")
    @Options(ensureAllInts=true)
    @FreshObjects(cls = Map.class, typeParams={Node.class, Integer.class}, num = 1)
    public Map<Node, Integer> color(int k) {
        return Squander.exe(this, k);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Nodes (%s): %s\n", nodes.size(), nodes.toString()));
        sb.append(String.format("Edges (%s):\n", edges.size()));
        for (Edge e : edges) {
            sb.append(String.format("  %s -> %s", e.src, e.dst));
        }
        return sb.toString();
    }

    public static boolean checkTSP(Graph g, Edge[] hamcycle, int maxCost) {
        Edge prev = null;
        int cost = 0;
        Set<Node> visited = new HashSet<Node>();
        for (Edge curr : hamcycle) {
            if (prev != null) {
                if (prev.dst != curr.src)
                    return false;
            }
            if (!visited.add(curr.src))
                return false;
            cost += curr.cost;
            prev = curr;
        }
        Set<Node> graphNodes = new HashSet<Node>(Arrays.asList(g.nodes()));
        if (!visited.equals(graphNodes))
            return false;
        if (cost > maxCost)
            return false;
        return true;
    }

}
/*! @} */
