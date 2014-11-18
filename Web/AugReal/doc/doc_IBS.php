<html >
<header>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="theme.css">
</header>
<body>

<?php
require "Functions.php";
require "../config.inc.php";
$server = "http://".$_SERVER['SERVER_NAME'];
$path = $server."/AugReal/ARExporters/api_Metaio_v2/IBS/";
?>

<h1>Image based channel (IBS)</h1><br />

<h2 class="title">Description</h2>
   <p id="desc">
   This URL provides the xml for the images based channel of Live+Gov. The xml contains text information, the urls of the images to download, and the url of the zipped 3d objects. The zip contains the necessary data to construct a 3d object, namely the obj, the mtl and the textures images. In addition, a Tracking.zip file is provided for the images to track and the instructions about how to place 3d objects above images.
   </p>
<h2 class="title">URL</h2>
	<div id="apiURLContainer">
		<?php echo "<span class='apiURL'><a href='".$path."index.php?'>".$path."index.php</a></span>"; ?>
	</div>
<h2 class="title">Parameters (Optional)</h2>
	<span class="method">GET</span>
	<br><br>
	<table id="deftable">
	<tr><th>Field</th><th>Type</th><th>Description</th><th>Default</th></tr>
	<tr><td class="parName">idapp<td>String</td></td><td>Select application framework by binary switching. The id of the application context as bytes. For example to download information only for YUCAT use 1000. BISCAYTIK is 0100. MATTERSOFT is 0010. TETRAGON is 0001. If you wish to download data for multiple applications use combinations, e.g. 1100 downloads data for YUCAT and BISCAYTIK simultaneously.</td><td>1110</td></tr> 
	<tr><td class="parName">no3d</td><td>String</td><td>If set to <b>false</b> then XML does not contain any 3d model information (Optional).</td><td>true</td></tr>
	<tr><td class="parName">lang</td><td>String</td><td>Language (Optional), either "en" (English, Default) or "es" (Spanish)</td><td>en</td></tr>
	</table>
<h2 class="title">Response</h2>
	<p id="desc">
	- The XML configuration file is returned.<br>
	- "Permission denial code 1". Superuser can not authenticate any other user.<br>
	- "Permission denial code 2". User can not access IBS channel.
	</p>

<h2 class="title">Diagnostics</h2>
<?php $response = postToURL($path."index.php",'0100',SCUSER,SCPASS,SCUSER,SCPASS);
			print_response($response, $formattype='xml')?>
</body>
</html>