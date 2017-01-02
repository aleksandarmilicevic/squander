<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Squander -- Home Page</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>
  <div id="container">
    <?php
      include 'header.html';
      $tab = 'idx';
      include 'menu.php';
    ?>
    
		
    <div id="main">
      <div id="content">
<?php /* <h1>welcome to the <span class="sq">Squander</span> home page</h1> */ ?>
        <ul id="big">
	  <li><em>unified</em> execution of <em>imperative</em>
	  and <em>declarative</em> code</li>
	  <li>executable <a href="http://alloy.mit.edu">Alloy</a>-like
	  specifications for <a href="http://www.java.com">Java</a>
	  programs</li>
	  <li>first order relational logic with transitive closure</li>
	  <li>support for Java arrays and collections</li>
	  <li>easy to specify and solve constraint problems</li>
	  <div class="code">
<font color="#3f5fbf">/**</font><br />
<font color="#3f5fbf">&nbsp;* Graph k-Coloring problem: assign up to k different colors to graph nodes</font><br/>
<font color="#3f5fbf">&nbsp;* such that no two adjacent nodes have the same color.</font><br />
<font color="#ffffff">&nbsp;</font><font color="#3f5fbf">*/</font><br />
<font color="#646464">@Ensures</font><font color="#000000">({</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;return.keys&nbsp;=&nbsp;this.nodes.elts&#34;</font><font color="#000000">,&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all&nbsp;c&nbsp;:&nbsp;return.vals&nbsp;|&nbsp;c&nbsp;&gt;&nbsp;0&nbsp;&amp;&amp;&nbsp;c&nbsp;&lt;=&nbsp;k&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all e : this.edges.elts | return.elts[e.src] != return.elts[e.dst]&#34;</font><br/>
<font color="#000000">})</font><br />
<font color="#646464">@Modifies</font><font color="#000000">(</font><font color="#2a00ff">&#34;return.elts&#34;</font><font color="#000000">)</font><br />
<font color="#646464">@Options</font><font color="#000000">(</font><font color="#000000">ensureAllInts=</font><font color="#7f0055"><b>true</b></font><font color="#000000">)</font><br />
<font color="#646464">@FreshObjects</font><font color="#000000">(</font><font color="#000000">cls&nbsp;=&nbsp;Map.class,&nbsp;typeParams=</font><font color="#000000">{</font><font color="#000000">Node.class,&nbsp;Integer.</font><font color="#7f0055"><b>class</b></font><font color="#000000">}</font><font color="#000000">,&nbsp;num&nbsp;=&nbsp;</font><font color="#990000">1</font><font color="#000000">)</font><br />
<font color="#7f0055"><b>public&nbsp;</b></font><font color="#000000">Map&lt;Node,&nbsp;Integer&gt;&nbsp;color</font><font color="#000000">(</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">k</font><font color="#000000">)&nbsp;{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>return&nbsp;</b></font><font color="#000000">Squander.exe</font><font color="#000000">(</font><font color="#000000">this,&nbsp;k</font><font color="#000000">)</font><font color="#000000">;</font><br />
<font color="#000000">}</font><br/>
<br/>
<font color="#7f0055"><b>public&nbsp;static&nbsp;</b></font><font color="#7f0055"><b>void&nbsp;</b></font><font color="#000000">main</font><font color="#000000">(</font><font color="#000000">String</font><font color="#000000">[]&nbsp;</font><font color="#000000">args</font><font color="#000000">)&nbsp;{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">Graph&nbsp;g&nbsp;=&nbsp;readGraph</font><font color="#000000">(</font><font color="#000000">args</font><font color="#000000">[</font><font color="#990000">0</font><font color="#000000">])</font><font color="#000000">;&nbsp;</font><font color="#3f7f5f">//&nbsp;reads graph from file</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">k&nbsp;=&nbsp;Integer.parseInt</font><font color="#000000">(</font><font color="#000000">args</font><font color="#000000">[</font><font color="#990000">1</font><font color="#000000">])</font><font color="#000000">;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">Map&lt;Node,&nbsp;Integer&gt;&nbsp;colors&nbsp;=&nbsp;g.color</font><font color="#000000">(</font><font color="#000000">k</font><font color="#000000">)</font><font color="#000000">;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">System.out.println</font><font color="#000000">(</font><font color="#000000">colors</font><font color="#000000">)</font><font color="#000000">;</font><br />
<font color="#000000">}</font>
	  </div>
	</ul>
      </div>
      <div id="menu-v">
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
