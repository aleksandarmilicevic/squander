function toggleLayer (whichLayer)
{
    var elem, vis;
    if (document.getElementById) // this is the way the standards work
	elem = document.getElementById (whichLayer);
    else if (document.all) // this is the way old msie versions work
	elem = document.all[whichLayer];
    else if (document.layers) // this is the way nn4 works
	elem = document.layers[whichLayer];
    vis = elem.style;
    // if the style.display value is blank we try to figure it out here
    vis.display = (vis.display=='' || vis.display=='none') ? 'block' : 'none';
}