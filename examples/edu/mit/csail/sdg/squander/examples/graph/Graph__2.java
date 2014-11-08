/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * The same as {@link Graph} but with an extra spec field. 
 * 
 * @author Aleksandar Milicevic
 */
@SpecField("edgeSet : set Node -> Node | " +
        "(all e : this.edges.elts | e.src -> e.dst in this.edgeSet) &&" +
        "#this.edgeSet = #this.edges.elts")
public class Graph__2 implements Serializable {

    private static final long serialVersionUID = -6332798241722968023L;

    // ================================
    // ---------- Class Node ----------
    // ================================
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
    public static class Edge implements Serializable {
        private static final long serialVersionUID = -5337889501186870289L;
        
        public final Node src;
        public final Node dst;

        public Edge(Node src, Node dest) {
            this.src = src;
            this.dst = dest;
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
        
    }
    
    private Set<Node> nodes = new LinkedHashSet<Node>();
    private Set<Edge> edges = new LinkedHashSet<Edge>();

    public Graph__2() {}
    
    public Node newNode(int key) {
        Node n = new Node(key);
        addNode(n);
        return n;
    }

    public void addNode(Node n) { nodes.add(n); }
    public void addEdge(Edge e) { edges.add(e); }
    
    public Edge newEdge(Node a, Node b) {
        Edge e = new Edge(a, b);
        addEdge(e);
        return e;
    }

    public boolean containsEdge(Node n1, Node n2) { return edges.contains(new Edge(n1, n2)); }
    
    public Node[] nodes() { return nodes.toArray(new Node[0]); }
    public Edge[] edges() { return edges.toArray(new Edge[0]); }
    
    public int numEdges() { return edges.size(); }
    public int numNodes() { return nodes.size(); }
    public int size()     { return numNodes(); }

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
    
    @Ensures( {
            "return[int] = this.nodes.elts",
            "return.length = #this.nodes.elts", // this spec needed to bring the required integers into the scene
            "all i : int | i >= 0 && i < return.length - 1 => (return[i]->return[i+1]) in this.edgeSet"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))
    @Options(ensureAllInts = false)
    public Node[] hamiltonian() {
        return Squander.exe(this);
    }

    @Ensures( {
            "return[int] = this.nodes.elts",
            "return.length = #this.nodes.elts",
            "all i : int | all j : int | 0 <= i && i < j && j < return.length => (return[i]->return[j]) !in this.edgeSet"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))
    public Node[] topsort() {
        return Squander.exe(this);
    }

    @Ensures({
            "return[int] in this.nodes.elts",
            "all i : int | all j : int | 0 <= i && i < j && j < return.length => (return[i]->return[j]) in this.edgeSet + ~this.edgeSet",
            "#return[int] >= k"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))        
    public Node[] maxClique(int k) {
        return Squander.exe(this, new Class<?>[]{int.class}, new Object[]{k});
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

}
/*! @} */
