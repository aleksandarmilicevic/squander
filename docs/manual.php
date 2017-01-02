<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Squander -- User Manual</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
	<script type="text/javascript" src="scripts.js"></script>
</head>

<body>
  <div id="container">
    <?php
      include 'header.html';
      $tab = 'mnl';
      include 'menu.php';
    ?>
    
		
    <div id="main">
      <div id="content">
	<h1 id="spec">specification language</h1>
	<p>
	  <a href="http://sdg.csail.mit.edu/forge/plugin.html">JFSL</a>
          (JForge Specification Language) is a formal lightweight
          specification language for Java which supports first-order
          relational logic with transitive closure.  It provides
          relational and set algebra, as well as common Java
          operators.  It also supports specification fields which can
          be particularly useful for specifying abstract data types.
	</p>
	
	<p>
	  JFSL specifications are written as Java annotations.  The
	  most commonly used annotations are:
	  <ul>
	    <li><tt>@Invariant("&lt;expr&gt;")</tt> - defines a class invariant</li>
	    <li><tt>@Requires("&lt;expr&gt;")</tt> - defines a method precondition</li>
	    <li><tt>@Ensures("&lt;expr&gt;")</tt> - defines a method postcondition</li>
	    <li><tt>@Modifies("f [s][u]")</tt> - defines a frame condition for a method</li>
	    <li><tt>@SpecField("&lt;fld_decl&gt; | &lt;abs_func&gt;")</tt> - defines a specification field</li>
	  </ul>
	  Section 2.2.2 of
	  this <a href="doc/milicevic-ms.pdf">thesis</a> better
	  explains how these annotations are interpreted
	  by <span class="sq">Squander</span>. </p>

	  <p> The semantics and the syntax of JFSL expressions are
	    fully
	    explained <a href="http://sdg.csail.mit.edu/pubs/theses/kuat-meng-thesis.pdf">here</a>
	    (Chapter 3).  It is important to know that everything is a
	    relation in JFSL.  Classes are represented as unary
	    relations, whereas Java fields are represented as binary
	    relations (where the domain is the declaring class and the
	    range is the field type).  The dot operator (<tt>.</tt>)
	    is actually a plain relational join, and not the field
	    dereferencing operator as in Java.  However, because of
	    the inherent relational nature of object oriented
	    programs, in most cases the relational join operator
	    behaves exactly the same as field dereferencing.  For
	    example, let <tt>node</tt> be a variable of
	    type <tt>Node</tt>, and let <tt>key</tt> be an integer
	    field in class <tt>Node</tt>.  In JFSL, <tt>node</tt> is
	    represented as a singleton unary relation (a relation
	    containing one atom), and a binary relation (of
	    type <tt>Node -> Integer</tt>) is used to represent the
	    field <tt>key</tt>.  Joining these two relations evaluates
	    to a singleton unary relation, containing the value of
	    the <tt>key</tt> field of the <tt>node</tt> instance.
	  </p>

	  <h1 id="globalopts">global options</h1>
	  <p>
	    There are several global options that the user may want to
	    set before running <span class="sq">Squander</span>.  They
	    are listed in
	    class <a href="doc/html/classedu_1_1mit_1_1csail_1_1sdg_1_1squander_1_1options_1_1GlobalOptions.html">GlobalOptions</a>.
	    Here are a couple of important remarks:
	    <ul>
	      <li><b>log_level</b> - by default set to <tt>NONE</tt>,
	      so you won't see too much information about the
	      execution.  By setting this parameter
	      to <tt>DEBUG</tt>, <span class="sq">Squander</span> will
	      produce verbose output, saying exactly what bounds and
	      what formula are given to Kodkod for solving.</li>
	      <li><b>engine</b> - by default set to <tt>Kodkod</tt>,
	      which should be suitable for most
	      cases; <tt>Kodkod2</tt> is experimental and probably
	      shouldn't be used; <tt>Forge</tt>
	      uses <a href="http://sdg.csail.mit.edu/forge">Forge</a>
	      as the back-end, and it typically performs worse
	      than <tt>Kodkod</tt>; <tt>KodkodPart</tt>
	      and <tt>KodkodPart2</tt> both implement the KodkodPart
	      algorithm
	      (explained <a href="doc/milicevic-ms.pdf">here</a>).
	      Differences between them are technical, and in
	      practise <tt>KodkodPart2</tt> should always be a better
	      choice.</li>
	      <li><b>sat_solver</b> - defaults to <tt>SAT4J</tt> which
	      is a pure Java solver.  Setting this parameter
	      to <tt>MiniSat</tt> usually results in better
	      performance, but it also requires native libraries.</li>
	      <li><b>unsat_core</b> - whether or not the solver should
	      compute the unsat core in case when a solution cannot be
	      found.  Setting this option to <tt>true</tt> helps
	      debugging your specifications but typically degrades
	      solving performance.</li>
	      <li>other parameters don't need to be changed for most
	      of the cases.</li>
	    </ul>
	  </p>

	  <h1 id="specopts">@Options annotation</h1>
	  <p>
	    The <a href="doc/html/interfaceedu_1_1mit_1_1csail_1_1sdg_1_1squander_1_1annotations_1_1Options.html"><tt>@Options</tt></a>
	    annotation lets you specify certain options at the method
	    specification level.  Currently, there are only a few
	    options available through this annotation:
	    <ul>
	      <li><b>bitwidth</b> - allows the user to explicitly
	      tell <span class="sq">Squander</span> what bitwidth to
	      use to represent integers.  Specifying this manually is
	      rarely needed, since <span class="sq">Squander</span>
	      automatically computes the minimal bitwidth necessary to
	      represent the content of the heap.  If an integer
	      greater than the maximal integer on the heap is expected
	      to be used (e.g. returned by the method), than it is
	      necessary to manually set the bitwidth to a constant
	      value.</li>
	      <li><b>ensureAllInts</b> - whether all integers (within
	      the bitwidth) should be used, or only those found on the
	      heap.  <span class="sq">Squander</span> actually has a
	      heuristic to arrive at the set of integers to be
	      used: 
		<ul>
		  <li>if any arithmetic operation is used, all ints are used</li>
		  <li>all integers found on the heap are added to the set of used integers, as well as array lengths, collection sizes etc.</li>
		  <li>all integer literals found in the specifications are added to the set of used integers</li>
		  <li>when the set cardinality operator (<tt>#</tt>) is seen in the spec, all integers from 0 to max size of the relation under the "#" operator are added to the set of used integers</li>
		</ul>
	      The <tt>ensureAllInts</tt> option overrides this heuristic.
	      </li>
	      <li><b>solve_all</b> - whether the solver should be
	      capable of enumerating all solutions.  This feature is
	      still experimental.  Please contact the author if you
	      want to use it.</li>
	    </ul>
	  </p>

	  <h1 id="javacol">java collections</h1>
	  <p>
	    <span class="sq">Squander</span> lets you write
	    specifications that talk about third party classes,
	    i.e. classes for which you don't have the source code so
	    you can't manually annotate them with specs.  Out of the
	    box, <span class="sq">Squander</span> supports:
	    <ul>
	      <li><a href="javascript:toggleLayer('arrjfs');">arrays</a></li>
	      <div id="arrjfs" class="code" style="display:none;">
<font color="#7f0055"><b>interface&nbsp;</b></font><font color="#000000">Object</font><font color="#000000">[]</font><font color="#000000">&lt;E&gt;&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;elems&nbsp;&nbsp;:&nbsp;int&nbsp;-&gt;&nbsp;E&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;length:&nbsp;one&nbsp;int&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@Invariant</font><font color="#000000">({</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;this.length&nbsp;&gt;=&nbsp;0&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all&nbsp;i&nbsp;:&nbsp;int&nbsp;|&nbsp;(i&nbsp;&lt;&nbsp;this.length&nbsp;&amp;&amp;&nbsp;i&nbsp;&gt;=&nbsp;0)&nbsp;?&nbsp;one&nbsp;this.elems[i]&nbsp;:&nbsp;no&nbsp;this.elems[i]&#34;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">})&nbsp;&nbsp;&nbsp;&nbsp;</font><br />
<font color="#000000">}</font>
	      </div>

	      <li><a href="javascript:toggleLayer('setjfs');"><tt>java.util.Set</tt></a></li>
	      <div id="setjfs" class="code" style="display:none;">
<font color="#7f0055"><b>interface&nbsp;</b></font><font color="#000000">Set&lt;K&gt;&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;elts&nbsp;&nbsp;&nbsp;:&nbsp;set&nbsp;K&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;length&nbsp;:&nbsp;one&nbsp;int&nbsp;|&nbsp;this.length&nbsp;=&nbsp;#this.elts&#34;</font><font color="#000000">)&nbsp;&nbsp;&nbsp;</font><br />
<font color="#000000">}</font>
	      </div>

	      <li><a href="javascript:toggleLayer('listjfs');"><tt>java.util.List</tt></a></li>
	      <div id="listjfs" class="code" style="display:none;">
<font color="#7f0055"><b>interface&nbsp;</b></font><font color="#000000">List&lt;E&gt;&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;elts&nbsp;&nbsp;:&nbsp;int&nbsp;-&gt;&nbsp;E&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;length:&nbsp;one&nbsp;int&nbsp;|&nbsp;this.length&nbsp;=&nbsp;#this.elts&#34;</font><font color="#000000">)&nbsp;&nbsp;&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;prev&nbsp;:&nbsp;E&nbsp;-&gt;&nbsp;E&nbsp;|&nbsp;this.prev&nbsp;=&nbsp;(~this.elts)&nbsp;.&nbsp;DEC&nbsp;.&nbsp;(this.elts)&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;next&nbsp;:&nbsp;E&nbsp;-&gt;&nbsp;E&nbsp;|&nbsp;this.next&nbsp;=&nbsp;(~this.elts)&nbsp;.&nbsp;INC&nbsp;.&nbsp;(this.elts)&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@Invariant</font><font color="#000000">({</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;this.length&nbsp;&gt;=&nbsp;0&#34;</font><font color="#000000">,</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all&nbsp;i&nbsp;:&nbsp;int&nbsp;|&nbsp;(i&nbsp;&lt;&nbsp;this.length&nbsp;&amp;&amp;&nbsp;i&nbsp;&gt;=&nbsp;0)&nbsp;?&nbsp;one&nbsp;this.elts[i]&nbsp;:&nbsp;no&nbsp;this.elts[i]&#34;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">})&nbsp;&nbsp;&nbsp;</font><br />
<font color="#000000">}</font>
	      </div>

	      <li><a href="javascript:toggleLayer('mapjfs');"><tt>java.util.Map</tt></a></li>
	      <div id="mapjfs" class="code" style="display:none;">
<font color="#7f0055"><b>interface&nbsp;</b></font><font color="#000000">Map&lt;K,V&gt;&nbsp;</font><font color="#000000">{</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;elts&nbsp;&nbsp;&nbsp;:&nbsp;K&nbsp;-&gt;&nbsp;V&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;length&nbsp;:&nbsp;one&nbsp;int&nbsp;|&nbsp;this.length&nbsp;=&nbsp;#this.elts&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;keys&nbsp;&nbsp;&nbsp;:&nbsp;set&nbsp;K&nbsp;&nbsp;&nbsp;|&nbsp;this.keys&nbsp;=&nbsp;this.elts.V&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@SpecField</font><font color="#000000">(</font><font color="#2a00ff">&#34;vals&nbsp;&nbsp;&nbsp;:&nbsp;set&nbsp;V&nbsp;&nbsp;&nbsp;|&nbsp;this.vals&nbsp;=&nbsp;this.elts[K]&#34;</font><font color="#000000">)</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#646464">@Invariant</font><font color="#000000">({</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#2a00ff">&#34;all&nbsp;k&nbsp;:&nbsp;K&nbsp;|&nbsp;k&nbsp;in&nbsp;this.elts.V&nbsp;=&gt;&nbsp;one&nbsp;this.elts[k]&#34;</font><br />
<font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">})&nbsp;&nbsp;&nbsp;</font><br />
<font color="#000000">}</font>
	      </div>
	    </ul>
	    If you click on the links above, you can see the
	    specification fields that are provided for each of those
	    classes.  Support for other third party classes can easily
	    be implemented (more details in Chapter 6 of
	    this <a href="doc/milicevic-ms.pdf">thesis</a>).
	  </p>

	  <h1 id="troublesh">troubleshooting</h1>
	  <p>
	    JFSL type checker will catch most of the spelling and type
	    errors in your specifications.  The most common type of
	    problems is when <span class="sq">Squander</span> finds no
	    solution and you know that there should be one.  In other
	    words, you made a mistake in your specification.  In some
	    cases, the error is in the logic of your specification,
	    i.e. the post-condition or some of the invariants are
	    logically incorrect.  A more subtle category of errors is
	    when your logic is correct, but still you are getting no
	    solution, due to some "technical" details.  If you believe
	    that your logic is correct, check the following:
	    <ul>
	      <li><b>frame condition</b> - did you list all fields
              that must be modifiable in order to satisfy the spec?
              For example, if your return type is an array, then you
              must explicitly say that the content of that array is
              modifiable, i.e. <tt>@Modifies("return.elems,
              return.length")</tt> (it is similar if the return type
              is a Java collection class).</li>
	      <li><b>integers</b> - is the bitwidth large enough to
	      represent all integers you care about?  do you need to
	      explicitly "ensureAllInts"?  If you set the log level
	      to <tt>DEBUG</tt> you'll see exactly what bitwidth and
	      what integers are used in the final formula.</li>
	      <li><b>reachable portion of the heap</b> - set the log
	      level to <tt>DEBUG</tt> to see whether all heap objects
	      you care about are added to the Kodkod bounds.
              <span class="sq">Squander</span> could simply encode the
	      entire portion of the heap that is reachable by
	      following <b>all</b> fields. Instead, <span class="sq">Squander</span> tries
	      to optimize the encoding and use only those fields that
	      are mentioned/used in the spec that is being executed.
	      So, if you realize that some objects were skipped during
	      the translation, simply and an invariant saying that the
	      field that leads to those objects is not null - this
	      invariant is trivially satisfiable, it typically won't
	      impose any performance overhead, but will
	      help <span class="sq">Squander</span> discover all
	      relevant objects on the heap.</li>
	      <li>for all other problems, please send an email
	      to <a href="mailto:aleks@csail.mit.edu">Aleksandar
	      Milicevic</a>.
	    </ul>
	  </p>
      </div>
      <div id="menu-v">
	<h1>navigate</h1>
	<ul>
	  <li><a href="#spec">spec language</a></li>
	  <li><a href="#globalopts">global options</a></li>
	  <li><a href="#specopts">@Options</a></li>
	  <li><a href="#javacol">java collections</a></li>
	  <li><a href="#troublesh">troubleshooting</a></li>
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


<?php
/*
	  <ul>
	    <li><tt>@Invariant("&lt;expr&gt;")</tt> - attached to
	      classes and used to define conditions that must always
	      (before and after every execution of a method) hold true
	      for the given class.</li>
	    <li><tt>@Requires("&lt;expr&gt;")</tt> - attached to
	      methods and used to specify constraints on the state
	      before method invocation. The method is expected to
	      execute correctly only if the precondition is satisfied
	      immediately before invocation. Class invariants are
	      implicitly added to method preconditions.</li>
	    <li><tt>@Ensures("&lt;expr&gt;")</tt> - attached to methods and used to
	      specify constraints on the state after method
	      invocation. In other words, it captures all effect the
	      method is expected to produce. Class invariants are
	      implicitly added to method postconditions.</li>
	    <li><tt>@Modifies("f [s][l][u]")</tt> - attached to
	      methods and used to specify frame condition.  The frame
	      condition annotation holds up to 4 different pieces of
	      specification. The first, and the only mandatory piece,
	      is the name of the modifiable field, <tt>f</tt>. It is
	      optionally followed by the
	      <i>instance selector</i> (<tt>s</tt>), the <i>lower
	      bound</i> and the <i>upper bound</i>.  The instance
	      selector specifies instances for which the field may be
	      modified (assumed “all” if not specified). The lower
	      bound (assumed “empty” if not specified) contains
	      concrete field values for some objects in the
	      post-state.  The upper bound of the modification
	      (assumed the extent of the field’s type if not
	      specified) holds all possible fields values in the
	      post-state.</li>
	    <li><tt>@SpecField</tt> - attached to classes and used to
	      define specification fields.  Definition of a
	      specification field consists of a type declaration, and
	      (optionally) an abstraction function. The abstraction
	      function defines how the field value is computed in
	      terms of other fields. For example, <tt>@SpecField("x:
	      one int | x = this.y - this.z")</tt> defines a singleton
	      integer field x, the value of which must be equal to the
	      difference of y and z. Specifications fields are
	      inherited from super-types and sub-types can override
	      the abstraction function (by simply redefining it).</li>
	  </ul>
*/
?>
