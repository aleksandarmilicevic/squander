<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Squander -- Documentation</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>
  <div id="container">
    <?php
      include 'header.html';
      $tab = 'doc';
      include 'menu.php';
    ?>
    
		
    <div id="main">
      <div id="content">
	<h1>documentation</h1>
	<ul>
	  <li>project documentation (powered by Doxygen) [<a href="doc/html">html</a>][<a href="doc/squander-doxy.pdf">pdf</a>]</li>
	</ul>
	<h2>publications</h2>
	<ul id="publist">
	  <li>Aleksandar Milicevic, Derek Rayside, Kuat Yessenov and Daniel Jackson. <br/>
             <span class="papertitle">Unifying Execution of Imperative and Declarative Code</span>. <br/> 
             <i>33rd International Conference on Software Engineering (ICSE), Waikiki, Honolulu, Hawaii</i>, May 2011.<br/>
             [<a href="doc/milicevic-icse11-squander.pdf">pdf</a>][<a href="doc/milicevic-icse11-squander-abstract.txt">abstract</a>][<a href="doc/milicevic-icse11-squander-bibtex.bib">bibtex</a>][<a href="doc/milicevic-icse11-squander-slides.pdf">slides</a>]
          </li>
	  <li>Aleksandar Milicevic.<br/> 
              <span class="papertitle">Executable Specifications for Java Programs</span>. <br/> 
              <i>MIT Masters Thesis</i>, 2010.<br/> 
              [<a href="doc/milicevic-ms.pdf">pdf</a>][<a href="doc/milicevic-ms-abstract.txt">abstract</a>][<a href="doc/milicevic-ms-bibtex.bib">bibtex</a>]
          </li>
	  <li>Derek Rayside, Aleksandar Milicevic, Kuat Yessenov, Greg Dennis and Daniel Jackson. <br/> 
             <span class="papertitle">Agile Specifications</span>.<br/>  
             <i>OOPSLA Onward! 2009 (short paper), Orlando, Florida, USA</i>.<br/> 
             [<a href="doc/rayside-onward09.pdf">pdf</a>][<a href="doc/rayside-onward09-abstract.txt">abstract</a>][<a href="doc/rayside-onward09-bibtex.bib">bibtex</a>]
          </li>
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
