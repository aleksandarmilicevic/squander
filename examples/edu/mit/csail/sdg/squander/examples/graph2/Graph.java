/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.graph2;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Abstract (mock) graph.
 * 
 * @author kuat
 */
@SpecField( { "nodes : set Node", 
              "edges : set Node -> Node" })
public class Graph {

    public static class Node {
        public int label;

        Node()          { this(0); }
        Node(int label) { this.label = label; }

        @Override
        public String toString() {
            return String.valueOf(label);
        }
    }

    @Ensures({"no this.nodes", "no this.edges"})
    public Graph() {}
    
    @Ensures( { "return !in @old(this.nodes)",
                "this.nodes = @old(this.nodes + return)", // why does this line cause trouble???
                "return.label = key" })
    @Modifies("this.nodes")
    @Fresh(@FreshObjects(cls = Node.class, num = 1))
    public Node newNode(int key) {
        return Squander.exe(this, new Class<?>[] { int.class }, new Object[] { key });
    }

    @Ensures( { "this.nodes = @old(this.nodes + n)" })
    @Modifies("this.nodes")
    public void addNode(Node n) {
        Squander.exe(this, new Class<?>[] { Node.class }, new Object[] { n });
    }

    @Requires("a + b in this.nodes")
    @Ensures("this.edges = @old(this.edges) + a -> b")
    @Modifies("this.edges")
    public void newEdge(Node a, Node b) {
        Squander.exe(this, new Class<?>[] { Node.class, Node.class }, new Object[] { a, b });
    }

    @Ensures("return[int] = this.nodes")
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))
    public Node[] nodes() {
        return Squander.exe(this);
    }

    @Returns("#this.edges") @Options(ensureAllInts=true) public int numEdges() { return Squander.exe(this); }
    @Returns("#this.nodes") @Options(ensureAllInts=true) public int numNodes() { return Squander.exe(this); }
    @Returns("#this.nodes") @Options(ensureAllInts=true) public int size() { return numNodes(); }
    
    @Ensures( {
        "return[int] = this.nodes",
        "return.length = #this.nodes",
        "all i : int | i >= 0 && i < return.length - 1 => return[i] -> return[i+1] in this.edges"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))
    public Node[] hamiltonian() {
        return Squander.exe(this);
    }

    @Ensures( {
        "return[int] = this.nodes",
        "return.length = #this.nodes",
        "all i : int | all j : int | 0 <= i && i < j && j < return.length => return[i] -> return[j] !in this.edges"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))
    public Node[] topsort() {
        return Squander.exe(this);
    }

    @Ensures({
        "return[int] in this.nodes",
        "all i : int | all j : int | 0 <= i && i < j && j < return.length => return[i] -> return [j] in this.(edges + ~ edges)",
        "#return[int] >= k"
    })
    @Modifies({"return.length", "return.elts"})
    @Fresh(@FreshObjects(cls = Node[].class, num = 1))        
    public Node[] maxClique(int k) {
        return Squander.exe(this, new Class<?>[]{int.class}, new Object[]{k});
    }

}
/*! @} */
