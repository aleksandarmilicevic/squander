package edu.mit.csail.sdg.squander.examples.chameleon;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.squander.examples.util.NNMap;
import edu.mit.csail.sdg.squander.examples.util.NNSet;
import edu.mit.csail.sdg.squander.utils.AutoId;

@Invariant({
    "this.projections.elts.(color.keys + shape.keys + atom.keys) in this.allNodes.elts",
    "this.projections.elts.(source.keys + destination.keys) in this.allEdges.elts"  
})
public class Visual<P, A> {

    public static enum Color { Red, Blue, Green, Yellow }
    public static enum Shape { Box, Circle, Hexagon }
    
    public static class Node extends AutoId {}
    
    public static class Edge extends AutoId {}
    
    @Invariant({
        // all edges point to nodes in this projection
        "this.(source+destination).vals in this.atom.keys",
    })
    public static class Projection<P, A> {        
        public P projection;
                
        public NNMap<Node, Color> color   = new NNMap<Visual.Node, Visual.Color>();
        public NNMap<Node, Shape> shape   = new NNMap<Visual.Node, Visual.Shape>();
        public NNMap<Node, A> atom        = new NNMap<Visual.Node, A>();
        
        //public NNMap<Edge, Field> relation   = new NNMap<Visual.Edge, Field>();
        public NNMap<Edge, Node> source       = new NNMap<Visual.Edge, Visual.Node>();
        public NNMap<Edge, Node> destination  = new NNMap<Visual.Edge, Visual.Node>();
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("  nodes: \n");
            Collection<Node> nodes = color.keySet();
            for (Node n : nodes) {
                sb.append(String.format("    %s %s %s --> %s\n", n, color.get(n), shape.get(n), atom.get(n)));
            }
            sb.append("\n");
            sb.append("  edges: \n");
            Collection<Edge> edges = source.keySet();
            for (Edge e : edges) {
                sb.append(String.format("    %s: %s --> %s, relation: %s\n", e, source.get(e), destination.get(e)));
            }
            return "";
        }        
    }
    
    public NNSet<Node> allNodes = new NNSet<Visual.Node>();
    public NNSet<Edge> allEdges = new NNSet<Visual.Edge>();
    public Set<Projection<P, A>> projections = new HashSet<Visual.Projection<P, A>>();
    
}
