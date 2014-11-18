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
$path = $server."/AugReal/ARExporters/api_Metaio_v2/LBS/";
?>

<h1>Location based channel (LBS)</h1><br />
   
<h2 class="title">Description</h2>
   <p id="desc">
   This URL provides the xml for the location based channel of Live+Gov. 
   The xml contains text information, the urls of the images to download, 
   and the url of the zipped 3d objects. The zip contains the necessary data
   to construct a 3d object, namely the obj, the mtl and the textures images.
   </p>
<h2 class="title">URL</h2>
    <div id="apiURLContainer">
    <?php echo "<span class='apiURL'><a href='".$path."index.php?'>".$path."index.php</a></span>"; ?>
    </div>
<h2 class="title">Parameters (Optional)</h2>
	<span class="method">GET</span>
	<br>
	<br>
	<table id="deftable">
	<tr><th>Field</th><th>Type</th><th>Description</th><th>Default</th></tr>
	<tr><td class="parName">idapp</td><td>String</td><td>Select the application framework by binary switching. For example to download information only for YUCAT use <b>1000</b>. 
                BISCAYTIK is <b>0100</b>. MATTERSOFT is <b>0010</b>. TETRAGON is <b>0001</b>. 
                If you wish to download data for multiple 
                applications use combinations, e.g. <b>1100</b> downloads data for YUCAT and BISCAYTIK simultaneously.</td><td>1110</tr> 
	<tr><td class="parName">SW3d</td><td>String</td><td>If set to <b>false</b> then XML does not contain any 3d model information.</td><td>true</td></tr> 
	<tr><td class="parName">m</td><td>String</td><td>Limit of entities to download</td><td>no limit</td></tr>

	<tr><td class="parName">l</td><td>String</td><td>User position [latitude:float],[longitude:float],[altitude:float]</td><td>-</td></tr>
	<tr><td class="parName">lang</td><td>String</td><td>Language (Optional), either "en" (English, Default) or "es" (Spanish)</td><td>en</td></tr>
	<tr><td class="parName">distthres</td><td>String</td><td>Distance in meters from user position to filter entities to download from server</td><td>10000</td></tr>
	<tr><td class="parName">debug</td><td>String</td><td>If <b>true</b> then return a predifined xml (used for debugging purposes).</td><td>false</td></tr>
	</table>
<h2 class="title">Response</h2>
	<p id="desc">
	- The XML configuration file is returned.<br>
	- "Permission denial code 1". Superuser can not authenticate any other user.<br>
	- "Permission denial code 2". User can not access LBS channel.
	</p>

<h2 class="title">Diagnostics</h2>
	<?php $response = postToURL($path."index.php",'0100',SCUSER,SCPASS,SCUSER,SCPASS);
			print_response($response, $formattype='xml')?>
</body> 
</html>