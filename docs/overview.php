<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Squander -- Overview</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>
  <div id="container">
    <?php
      include 'header.html';
      $tab = 'ovr';
      include 'menu.php';
    ?>
	
    <div id="main">
      <div id="content">
	<h1 id="intro">introduction</h1>
	<p>
	  <span class="sq">Squander</span> is a
	  framework that provides a unified environment for writing
	  declarative constraints and imperative statements in the
	  context of a single program. This is particularly useful for
	  implementing programs that involve computations that are
	  relatively easy to specify but hard to solve
	  algorithmically. In such cases, declarative constraints can
	  be a natural way to express the core computation, whereas
	  imperative code is a natural choice for reading the input
	  parameters, populating the working data structures, setting
	  up the problem, and presenting the solution back to the
	  user.
	</p>
	<p>
	  By being able to mix imperative code with executable
	  declarative specifications, the user can easily express
	  constraint problems in-place, i.e. in terms of the existing
	  data structures and objects on the heap. They can then run
	  our solver, which will find a solution to the given set of
	  constraints (if one exists) and automatically update the
	  heap to reflect the solution. Afterwards, the user can
	  continue to manipulate the program heap in the usual
	  imperative way. 
	</p>
	<p>
	  Without a technology like this one, the standard solution
	  would be to manually translate the problem into the language
	  of an external solver, run the solver, and then again,
	  manually translate the solution back to the native
	  programming language. This obviously requires more work, it
	  is cumbersome, and after all, it is more error-prone.
	</p>

	<h1 id="arch">architecture</h1>
	<img src="images/arch.png" width="630px" alt="arch"/>
	<ol>
	  <li>serialize the heap into relations</li>
	  <li>translate specs and heap relations into Kodkod</li>
	  <li>translate relational into boolean logic</li>
	  <li>(if a solution is found) restore relations from boolean assignments</li>
	  <li>(if a solution is found) restore field values from relations</li>
	  <li>(if a solution is found) restore the heap to reflect the solution</li>
	</ol>

	<h1 id="app">applications</h1>
	<ul>
	  <li><span class="emph">solving hard constraint
	  problems</span>; puzzles (sudoku, n-queens, knight's tour, ...), graph problems (k-coloring,
	  hamiltonian path, max clique, ...), schedulers, dependency mangers, ...</li>
	  <li><span class="emph">test input generation</span>;
	  e.g. generate data structure instances that satisfy complex
	  constraints</li>
	  <li><span class="emph">specification validation</span>;
          specifications can also contain errors, and the most
          intuitive way to test a specification would be to execute it
          on some concrete input and see if the result makes sense or
          not</li>
	  <li><span class="emph">runtime assertion checking</span>;
          check whether a given rich property holds at an arbitrary
          point during the execution of a program</li>
	</ul>
	
	<h1 id="lmt">limitations</h1>
	<ul>
	  <li><span class="emph">boundedness</span>; everything has to
          be bounded, i.e. <span class="sq">Squander</span> cannot
          generate an arbitrary number of new objects (which may be
          needed to satisfy a specification); instead, the exact
          number of new objects of each class must be specified by the
          user</li>
	  <li><span class="emph">small integers</span>; integers must
          also be bounded to a small bitwidth (to make the solving
          tractable), which can occasionally cause subtle integer
          overflow bugs, which are typically hard to find</li>
	  <li><span class="emph">equality issues</span>; referential
	  equality is used by default for all classes except
	  for <tt>String</tt>, so it is impossible to write a spec that 
          asserts that two objects are equal in the sense of Java <tt>equals</tt>
	  <li><span class="emph">lack of support for higher-order
          expressions</span>; it is not possible to write a
          specification that says “<i>find a path in the graph such
          that there is no other path in the graph longer than it</i>”
          and solve it with <span class="sq">Squander</span>; it is
          possible, however, to express and solve “<i>find a path in
          the graph with at least k nodes</i>”, which is
          computationally as hard as the previous problem, because a
          binary search can be used to efficiently find the maximum k
          for which a solution exists
</li>
	</ul>
      </div>

      <div id="menu-v">
	<h1>navigate</h1>
	<ul>
	  <li><a href="#intro">introduction</a></li>
	  <li><a href="#arch">architecture</a></li>
	  <li><a href="#app">applications</a></li>
	  <li><a href="#lmt">limitations</a></li>
	</ul>
	<?php 
          include 'links.html'; 
        ?>
      </div>
      
    </div>
    
  </div>
<div class="clear"></div>
<?php
  include 'footer.html';
?>
</body>

</html>
