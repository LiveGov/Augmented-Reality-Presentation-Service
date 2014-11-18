<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="theme.css">
<title>Object Visual Recognition Service</title>
</head>

<body>

<h1> Object Visual Recognition Service</h1>
<h2 class="title">Description</h2>
<p id="desc">Performs the visual recognition task using an input image. The client (e.g. mobile application) sends a picture to the server and the server returns a concept and a prediction score </p>

<h2 class="title">URL</h2>
<div id="apiURLContainer">
<span class="apiURL"> <?php echo "http://".$_SERVER['SERVER_NAME'].'/AugReal/api_v3/recognizer.php' ?></span>
</div>
<h2 class="title">Parameters</h2>
<span class="method"> POST</span>
<br /><br />
<table id="deftable">
<tr><th>Field</th><th>Type</th><th>Description</th><th>Default</th></tr>
<tr><td class="parName">app_id</td><td>String</td><td>Search for the models associated with the given application framework. <ul> <li> -1 : all models</li><li> 1 : YUCAT</li><li> 2 : BISCAYTIK</li> <li> 3 : MATTERSOFT </li> <li> 4 : Tetragon</li> <li> 5 : a set of predefined models (for debugging purposes)</li></ul></td><td>Required</td></tr>
<tr><td class="parName">upload</td><td>Data</td><td>The data of the image to recognize (jpeg representation). 
The image ratio size should be one of the following 4:3, 16:9, 3:4, or 9:16. 
<!--  Error codes:
   <ol style="list-style-type: none;">
   		<li>1 : keypoints < 100</li>
   		<li>2 : img</li> 
    	<li>3 : models</li>
 		<li>4 : no image specified</li>
		<li>5 : image not found</li>
 		<li>6 : image ratio error</li>
 		<li>7 : codebook not found</li>
   </ol>
    -->

 </td><td>Required</td></tr>
</table>
<h2 class="title">Response</h2>
<p id="desc"> 
On Success : &lt;model name&gt;;&lt;prediction-score&gt;;&lt;model id &gt;<br />
On Failure : (Empty String)
</p>

<h2 class="title">Diagnostics</h2>
<table id="indexTable">
<tr><th>Tag</th><th>Value</th></tr>

	<?php

	$response = file_get_contents("http://".$_SERVER['SERVER_NAME'].'/AugReal/api_v3/recognizer.php?diag');
	$r_arr = explode(";",$response);
	$N = count($r_arr);

	if ($N > 2){
		print "<tr><td>Status</td><td> <strong><font color='green'>OK</font></strong><br/></td></tr>";
	} else {
		print "<tr><td>Status</td><td> <strong><font color='red'>ERROR</font></strong><br/></td></tr>";
	}
	
	if ($N > 0)
		print "<tr><td>Test Image</td><td>".$r_arr[0]."</td></tr>";
	if ($N > 1)
		print "<tr><td>Result</td><td>".$r_arr[1] ."</td></tr>";
	if ($N > 2)
		print "<tr><td>Score</td><td>".$r_arr[2] ."</td></tr>";
	if ($N > 3)
		print "<tr><td>Entity Id</td><td>".$r_arr[3] ."</td></tr>";
?>
</table>

</body>
</html>
