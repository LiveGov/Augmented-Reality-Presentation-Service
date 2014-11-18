<html>

<header>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="theme.css">
</header>
<body>

<?php
require "Functions.php";
$server = "";
$path = "";
$server = "http://".$_SERVER['SERVER_NAME'];
$path = $server."/AugReal/api_v3/";
?>

<h1>Get data from AR-Server</h1>

<h2 class="title">Description</h2>
	<p id="desc">This URL provides the interface to get AR-Entities from AR Server.</p>
<h2 class="title">URL</h2>
	<div id="apiURLContainer">
	<?php echo "<span class='apiURL'><a href='".$path."ar_get.php?'>".$path."ar_get.php</a></span>"; ?>
	</div>

<h2 class="title">Parameters</h2>
	<span class="method">POST</span><br><br>
	
	Parameters (Required): username, password<br>
	
	There are two ways to get data, either get an Entity solely by providing explicitly the id of the Entity, 
	or massively get multiple Entities by defining the 4 GPS points defining the rectangle to get Entities within.
	<ul>
	   <li>Solely by id: id</li>
	   <li>Massively by application id: id_app (1->YUCAT, 2->BISCAYTIK, 3->MATTERSOFT, 0->Unclassified)</li>
	   <li>Massively by defining a deployment geographical rectangle and id_app:  
        		x0down=23.1&x0up=23.3&y0down=40.1&y0up=40.5&id_app=1; where x is the longitude and y is the latitude.
         
  		<table border="1">
    	<tr><td>x0up  </td><td>Longitude upper limit  </td></tr> 
    	<tr><td>x0down</td><td>Longitude lower limit</td></tr> 
    	<tr><td>y0up  </td><td>Latitude upper limit</td></tr> 
    	<tr><td>y0down</td><td>Latitude lower limit</td></tr>
    	<tr><td>id_app</td><td>Id of the certain application</td></tr>  
  		</table> 
  
		</li>
	</ul>
	
	
<h2 class="title">Response</h2>
	<p id="desc">
	-The Entities in JSON format:<br>
	<textarea name='comments' cols='105' rows='14'>
	[
{
id: "41",
id_author: "8",
id_coauthor: " ",
id_external: "0",
id_app: "2",
id_VRec: "0",
title: "Town Square Improvement",
titleB: "Mejora de la iluminación en la Plaza Molinar",
titleC: "",
titleD: " ",
description: "An improvement of the lighting of the Town Square is being studied by the Local Council, who would like to know the opinion of the people of Gordexola.",
descriptionB: "Una mejora de la iluminación de la Plaza está siendo estudiada por el Ayuntamiento, para mejorar la eficiencia de la iluminación, disminuir la contaminación y lograr ahorro energético.",
descriptionC: "",
descriptionD: " ",
author: "Dimitrios Ververidis",
models: "2",
jpg: "1",
linkurl: " ",
latitude: "43.1800350",
longitude: "-3.0732450",
altitude: "60.00041",
date: "2013-11-12 17:28:06",
streetnameaddress: "Plaza de Molinar Plaza",
numberaddress: "10",
postalcode: "48192",
country: "Spain ",
Langs: "en;es;eu;dut",
name_VRec: ""
}
]
	</textarea>
	
	<br>
	-Permission denial code 1: The superuser is not authorized to do any authorization action on behalf of other users<br>
	-Permission denial code 2: The user is not authorized to do get action<br>
	-Error code 1: Not sufficient parameters provided to retrieve AR-Entities<br>
	</p>
	
<h2 class="title">Diagnostics</h2>
	<?php $response = postToURL($path."ar_get.php",'0100','','','','');
				print_response($response, $formattype='xml')?>
	
<h2 class="title">PHP Example</h2>
<textarea name='comments' cols='105' rows='14'>
$header = array("Authorization: Basic " . base64_encode("superuser_username1:superuser_password"));
$postWhat = array('id' => '41','username' => 'myusername', 'password' => 'mypassword');
$data = http_build_query($postWhat, '', '&');
$params = array(
		'http'=>array(
				'method'=>"POST",
				'header'=> implode("\r\n", $header),
				'content' => $data
				)
		);
$context = stream_context_create($params);
$data = file_get_contents($url,false,$context);
print $data;
</textarea>
	
	
	
</body>
</html>