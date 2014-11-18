<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<script src="http://code.jquery.com/jquery-1.8.3.min.js" type="text/javascript"></script>
		<script src="http://dev.junaio.com/arel/js/arel.js" type="text/javascript"></script>
		<script src="http://augreal.mklab.iti.gr/api_Metaio_v2/IBS/arel/logic.js" type="text/javascript"></script>
		<link href="http://augreal.mklab.iti.gr/api_Metaio_v2/IBS/arel/styles.css" type="text/css" rel="stylesheet" />
		<title>Rotation of 3D object with AREL</title>
	</head>
	<body>
	
	<?php
	  // switches between different styles for the buttons:
	  // on iPhone they should be smaller...
	
	  $agent = strtolower($_SERVER['HTTP_USER_AGENT']);
	
	  if(strpos($agent, "ipad") !== FALSE) {
		  echo "<div id=\"buttons\" class=\"ipad\">";
	  } else {
		  echo "<div id=\"buttons\" class=\"iphone\">";
	  }
	?>
	
		<!-- the buttons for the controls - style is defined in the css -->
		<div id="spacer"></div>
		<div id="top"></div>
		<div id="left"></div>
		<div id="down"></div>
		<div id="right"></div>
		</div>
	</body>
</html>