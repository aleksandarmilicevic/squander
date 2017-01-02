<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Squander -- Examples</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>
  <div id="container">
    <?php
      include 'header.html';
      $tab = 'exm';
      include 'menu.php';
    ?>
		
    <div id="main">
      <div id="content">

	<p>
	  This page provides a detailed explanation for several
	  interesting examples.  The research papers listed on
	  the <a href="documentation.php">documentation</a> page
	  present several other examples.  Finally, the source code
	  for many more examples can be found inside
	  the <a href="doc/html/group__Examples.html">examples</a>
	  module under the <a href="doc/html">project
	  documentation</a> section.
	</p>

	<h1 id="kcolor">graph k-coloring problem</h1>
	
	<p><a href="http://en.wikipedia.org/wiki/Graph_coloring">Graph
	coloring</a> is a well-known problem in graph theory.  For
	this particular example, the goal is to find a way of coloring
	the nodes of a graph, using at most <tt>k</tt> different
	colors, such that no two adjacent nodes share the same
	color.</p>

	<p>To represent graphs is Java, we'll use a set of nodes and a
	set of edges.  Nodes and edges are represented as separate
	classes.  A node can be assigned an arbitrary integer label,
	whereas an edge simply holds pointers to source and
	destination nodes and a cost.</p>

	<p>To solve this problem, we simply define an instance method
	(namely <tt>color</tt>) that takes the number of
	colors <tt>k</tt> and annotate it with a specification which
	declaratively and formally states what the effects of the method
	should be, i.e. <i>what</i> the heap should look like in the
	post-state, and <i>not how</i> to arrive at that heap.</p>

	<p>In order to assign colors to nodes, we could have used
	the <tt>Node.label</tt> field.  Instead, however, we decided
	to explicitly return the mapping between nodes and colors by
	returning a new instance of the Java <tt>Map</tt> class, just
	to illustrate the usage of Java maps and how the creation of
	new objects can be specified.</p>

	<p>For each supported Java collection class, we have defined
	several specification fields to model its abstract state.  For
	instance, for Java sets, spec field named <tt>elts</tt> refers
	to elements of the set.  Similarly, <tt>elts</tt> for maps
	refers to map entries (which are of type <tt>K -> V</tt>).
	For maps, two extra fields are defined, <tt>keys</tt>
	and </tt>values</tt>, that respectively resolve to map keys
	and map values.  A fuller description of specification fields
	for Java collections can be found on
	this <a href="documentation.php">page</a>.</p>

	<p>The <tt>@FreshObjects</tt> annotation is used to specify
	that one instance of <tt>java.util.Map&lt;Node,
	Integer&gt;</tt> should be created and added to the heap.
	Since that instance is going to be the only instance of that
	type, which is the same as the return type of the method, the
	solver will have to assign that instance to the method's
	return value.  We want the solver to compute the content of
	that map, so we must explicitly state that the content of the
	map (<tt>return.elts</tt>) is modifiable by using
	the <tt>@Modifies</tt> annotation.  To constrain the content
	of the map, we use the <tt>@Ensures</tt> annotation to specify
	the post-condition. In this case, we say that the set of keys
	of the return map (<tt>return.keys</tt>) must be exactly equal
	to the set of nodes of this graph (because we want to assign a
	color to every node).  The keys of the return map must all be
	between <tt>1</tt> and <tt>k</tt>, which is asserted in the
	second line of the post-condition.  To prevent the adjacent
	nodes from having the same color, we specify that for all
	edges, the end nodes must be assigned different colors.
	Finally, since <span class="sq">Squander</span> by default
	encodes only integers that found on the heap, we have to
	explicitly tell <span class="sq">Squander</span> to use all
	integers, because we want all integers from <tt>1</tt>
	to <tt>k</tt> to be included. </p>

	<div class="code">
<font color="#7f0055"><b>class&nbsp;</b></font><font color="#000000">Graph&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#7f0055"><b>class&nbsp;</b></font><font color="#000000">Node&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>public&nbsp;final&nbsp;</b></font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">label;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>public&nbsp;</b></font><font color="#000000">Node</font><font color="#000000">(</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">label</font><font color="#000000">)&nbsp;{&nbsp;</font><font color="#7f0055"><b>this</b></font><font color="#000000">.label&nbsp;=&nbsp;label;&nbsp;</font><font color="#000000">}</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#000000">}</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#7f0055"><b>class&nbsp;</b></font><font color="#000000">Edge&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>public&nbsp;final&nbsp;</b></font><font color="#000000">Node&nbsp;src,&nbsp;dest;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>public final int&nbsp;</b></font><font color="#000000">cost;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>public&nbsp;</b></font><font color="#000000">Edge</font><font color="#000000">(</font><font color="#000000">Node&nbsp;src,&nbsp;Node&nbsp;dest,&nbsp;</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">cost) {</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>this</b></font><font color="#000000">.src&nbsp;=&nbsp;src;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>this</b></font><font color="#000000">.dest&nbsp;=&nbsp;dest;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>this</b></font><font color="#000000">.cost&nbsp;=&nbsp;cost;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">}&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#000000">}</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#7f0055"><b>private&nbsp;</b></font><font color="#000000">Set&lt;Node&gt;&nbsp;nodes&nbsp;=&nbsp;</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">LinkedHashSet&lt;Node&gt;</font><font color="#000000">()</font><font color="#000000">;</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#7f0055"><b>private&nbsp;</b></font><font color="#000000">Set&lt;Edge&gt;&nbsp;edges&nbsp;=&nbsp;</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">LinkedHashSet&lt;Edge&gt;</font><font color="#000000">()</font><font color="#000000">;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#646464">@Ensures</font><font color="#000000">({</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;return.keys&nbsp;=&nbsp;this.nodes.elts&#34;</font><font color="#000000">,&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all&nbsp;c&nbsp;:&nbsp;return.vals&nbsp;|&nbsp;c&nbsp;&gt;&nbsp;0&nbsp;&amp;&amp;&nbsp;c&nbsp;&lt;=&nbsp;k&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all e : this.edges.elts | return.elts[e.src] != return.elts[e.dst]&#34;</font><br/>
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#000000">})</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#646464">@Modifies</font><font color="#000000">(</font><font color="#2a00ff">&#34;return.elts&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#646464">@Options</font><font color="#000000">(</font><font color="#000000">ensureAllInts=</font><font color="#7f0055"><b>true</b></font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#646464">@FreshObjects</font><font color="#000000">(</font><font color="#000000">cls&nbsp;=&nbsp;Map.class,&nbsp;typeParams=</font><font color="#000000">{</font><font color="#000000">Node.class,&nbsp;Integer.</font><font color="#7f0055"><b>class</b></font><font color="#000000">}</font><font color="#000000">,&nbsp;num&nbsp;=&nbsp;</font><font color="#990000">1</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#7f0055"><b>public&nbsp;</b></font><font color="#000000">Map&lt;Node,&nbsp;Integer&gt;&nbsp;color</font><font color="#000000">(</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">k</font><font color="#000000">)&nbsp;{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>return&nbsp;</b></font><font color="#000000">Squander.exe</font><font color="#000000">(</font><font color="#000000">this,&nbsp;k</font><font color="#000000">)</font><font color="#000000">;</font><br />
<font color="#ffffff">&nbsp;&nbsp;</font><font color="#000000">}</font><br />
<font color="#000000">}</font>	  
	</div>

	<h1 id="tsp">travelling salesman problem</h1>
	
	<p>In this version of
	the <a href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">
	Travelling Salesman Problem</a> (TSP), our goal is to find a
	path (sequence of edges) in the graph that starts from the
	given <tt>startNode</tt> node, visits all nodes in the graph
	and returns to the starting node, whose cost is less than the
	given <tt>maxCost</tt> number.  We'll use the same classes to
	represent graphs as in the previous example.</p>

        <p>One way to formally specify this problem is shown below.
        We decided that the <tt>tsp</tt> method returns a sequence of
        edges to represent the path (alternatively it could return a
        sequence of nodes).  The post-condition for the <tt>tsp</tt>
        method ensures the following:
	  <ul>
	    <li>the resulting edges are chosen only from the set of graph edges</li>
	    <li>the resulting edges cover all nodes</li>
	    <li>the number of the resulting edges is equal to the
	    number of nodes (because we start and finish in the same
	    node)</li>
	    <li>the resulting edges are connected</li>
	    <li>the first and the last node on the path is equal to the given start node</li>
	    <li>the cost is less than or equal to the given max cost</li>
	  </ul>
	</p>
	
	<div class="code">
	<font color="#646464">@Ensures</font><font color="#000000">(&nbsp;{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;return[int]&nbsp;in&nbsp;this.edges.elts&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;return[int].(src&nbsp;+&nbsp;dst)&nbsp;=&nbsp;this.nodes.elts&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;return.length&nbsp;=&nbsp;#this.nodes.elts&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all&nbsp;i&nbsp;:&nbsp;int&nbsp;|&nbsp;i&nbsp;&gt;=&nbsp;0&nbsp;&amp;&amp;&nbsp;i&nbsp;&lt;&nbsp;return.length&nbsp;-&nbsp;1&nbsp;=&gt;&nbsp;return[i].dst&nbsp;=&nbsp;return[i+1].src&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;startNode&nbsp;=&nbsp;return[0].src&nbsp;&amp;&amp;&nbsp;startNode&nbsp;=&nbsp;return[return.length&nbsp;-&nbsp;1].dst&#34;</font><font color="#000000">,&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;(sum&nbsp;i&nbsp;:&nbsp;int&nbsp;|&nbsp;return[i].cost)&nbsp;&lt;=&nbsp;maxCost&#34;</font><br />
<font color="#000000">})</font><br />
<font color="#646464">@Modifies</font><font color="#000000">(&nbsp;{&nbsp;</font><font color="#2a00ff">&#34;return.length&#34;</font><font color="#000000">,&nbsp;</font><font color="#2a00ff">&#34;return.elems&#34;&nbsp;</font><font color="#000000">})</font><br />
<font color="#646464">@FreshObjects</font><font color="#000000">(</font><font color="#000000">cls&nbsp;=&nbsp;Edge</font><font color="#000000">[]</font><font color="#000000">.class,&nbsp;num&nbsp;=&nbsp;</font><font color="#990000">1</font><font color="#000000">)</font><br />
<font color="#646464">@Options</font><font color="#000000">(</font><font color="#000000">ensureAllInts&nbsp;=&nbsp;</font><font color="#7f0055"><b>true</b></font><font color="#000000">)</font><br />
<font color="#7f0055"><b>public&nbsp;</b></font><font color="#000000">Edge</font><font color="#000000">[]&nbsp;</font><font color="#000000">tsp</font><font color="#000000">(</font><font color="#000000">Node&nbsp;startNode,&nbsp;</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">maxCost</font><font color="#000000">)&nbsp;{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>return&nbsp;</b></font><font color="#000000">Squander.exe</font><font color="#000000">(</font><font color="#000000">this,&nbsp;startNode,&nbsp;maxCost</font><font color="#000000">)</font><font color="#000000">;</font><br />
<font color="#000000">}</font>
	</div>
      </div>

  

      <div id="menu-v">
	<h1>navigate</h1>
	<ul>
	  <li><a href="#kcolor">k-coloring</a></li>
	  <li><a href="#tsp">travelling salesman</a></li>
	</ul>
	<?php 
          include 'links.html'; 
        ?>
      </div>
    </div>
    
  </div>

<div class="clear"></div>

<?php
  include 'footer.html'
?>
</body>

</html>
