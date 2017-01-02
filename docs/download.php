<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Squander -- Download</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>
  <div id="container">
    <?php
      include 'header.html';
      $tab = 'dwn';
      include 'menu.php';
      $bin_path = 'dist/squander-bin.tgz';
      $src_path = 'dist/squander-src.tgz';
    ?>
    
		
    <div id="main">
      <div id="content">
	<h1 id="download">download</h1>
	<ul>
	  <li>binaries (dependencies included) [<a href="<?php echo $bin_path; ?>">tgz</a>]</li>
	  <li>source [<a href="<?php echo $src_path; ?>">tgz</a>]</li>
	</ul>

	<h1 id="comp">compilation instructions</h1>
	<span class="sq">Squander</span> source distribution comes
	with an ANT build script, so you will have to
	install <a href="http://ant.apache.org">ANT</a> first in order
	to run it.  In addition, you'll need
	an <a href="http://www.eclipse.org">Eclipse</a> compiler
	(namely, only <tt>org.eclipse.jdt.core_*.jar</tt> files from
	the Eclipse's "plugin" folder) because the Java compiler from Sun
	doesn't handle methods with generic return types properly.
	<ol>
	  <li>download the source tarball (e.g. <tt>wget <?php echo "http://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['REQUEST_URI']) . "/" . $src_path; ?></tt>)</li>
	  <li>unpack the archive (e.g. <tt>tar xzvf squander-src.tgz</tt>)</li>
	  <li>change to the unpacked folder (e.g. <tt>cd squander</tt>)</li>
	  <li>(needed the very first time only) set
	  the <tt>ECLIPSE_HOME</tt> environment variable to point to
	  your Eclipse installation and then run <tt>ant
	  init-eclipse-compiler</tt> (might require "root" privileges, e.g.
	  if your ANT installation folder in "/usr/share/")</li>
	  <li>to build from sources, execute <tt>ant build-eclipse-compiler</tt></li>
	  <li>to run tests, execute <tt>ant test</tt></li>
	  <li>to create a <tt>jar</tt> distribution (<tt>dist/squander.jar</tt>), execute <tt>ant jar</tt></li>
	</ol>

	<h1 id="run">using <span class="sq">Squander</span></h1> 

	<p>To use <span class="sq">Squander</span> simply add
	the <tt>squander.jar</tt> file (either from the binary
	distribution or the one built from sources), along with
	all other <tt>.jar</tt> files provided in the <tt>lib</tt>
	folder, to your <tt>CLASSPATH</tt>.  By default, SAT4J solver
	is used, which doesn't require any native libraries.  If you
	want to switch to MiniSAT, which should typically give better
	performance, you will have to add the native libraries for
	your platform (also provided in the <tt>lib</tt> folder) to
	your <tt>LD_LIBRARY_PATH</tt>.  For example:</p>

<pre>
export CLASSPATH=squander.jar 
export CLASSPATH=$CLASSPATH:`find lib -type f -name "*.jar" | xargs | sed 's/\ /:/g'`
export LD_LIBRARY_PATH="lib/amd64-linux"
java -ea edu.mit.csail.sdg.squander.examples.sudoku.Sudoku1
</pre>
	
      </div>
      <div id="menu-v">
	<h1>navigate</h1>
	<ul>
	  <li><a href="#download">download</a></li>
	  <li><a href="#comp">compiling</a></li>
	  <li><a href="#run">using</a></li>
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
