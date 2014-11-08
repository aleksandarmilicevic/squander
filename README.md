squander
========

Website: http://people.csail.mit.edu/aleks/squander/

Executable specifications for Java programs

  * unified execution of imperative and declarative code
  * executable Alloy-like specifications for Java programs
  * first order relational logic with transitive closure
  * support for Java arrays and collections
  * easy to specify and solve constraint problems
  
```java
/**
 * Graph k-Coloring problem: assign up to k different colors to graph nodes
 * such that no two adjacent nodes have the same color.
 */
@Ensures({
    "return.keys = this.nodes.elts", 
    "all c : return.vals | c > 0 && c <= k",
    "all e : this.edges.elts | return.elts[e.src] != return.elts[e.dst]"
})
@Modifies("return.elts")
@Options(ensureAllInts=true)
@FreshObjects(cls = Map.class, typeParams={Node.class, Integer.class}, num = 1)
public Map<Node, Integer> color(int k) {
    return Squander.exe(this, k);
}
    
public static void main(String[] args) {
    Graph g = readGraph(args[0]); // reads graph from file
    int k = Integer.parseInt(args[1]);
    Map<Node, Integer> colors = g.color(k);
    System.out.println(colors);
}  
```  
