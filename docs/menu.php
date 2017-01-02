    <div id="menu-h">
      <ul>		
        <li><a href="index.php"         <? if ($tab == 'idx') echo 'class="active"' ?>>home</a></li>
	<li><a href="download.php"      <? if ($tab == 'dwn') echo 'class="active"' ?>>download</a></li>
	<li><a href="overview.php"      <? if ($tab == 'ovr') echo 'class="active"' ?>>overview</a></li>
	<li><a href="manual.php"        <? if ($tab == 'mnl') echo 'class="active"' ?>>manual</a></li> 
	<li><a href="example.php"       <? if ($tab == 'exm') echo 'class="active"' ?>>examples</a></li>
	<li><a href="documentation.php" <? if ($tab == 'doc') echo 'class="active"' ?>>documentation</a></li>
      </ul>
    </div>