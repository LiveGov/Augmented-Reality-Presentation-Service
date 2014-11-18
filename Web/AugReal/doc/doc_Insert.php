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

<h1>Insert data to AR-Server</h1>

<h2 class="title">Description</h2>
	<p id="desc">This URL provides the interface for inserting an AR-Entity to AR-Server</p>
	<h2 class="title">URL</h2>
	<div id="apiURLContainer">
		<?php echo "<span class='apiURL'><a href='".$path."ar_insert.php?'>".$path."ar_insert.php</a></span>"; ?>
	</div>
<h2 class="title">Parameters</h2>
	<span class="method">POST</span>
	<br>
	<br> Parameters required: username, password
	<br />
	
<h2 class="title">Response</h2>
	<p id="desc">
	CODE 1, id: An AR-Entity identified by the id number was created.<br>
	Permission denial code 1: The superuser is not authorized to do any authorization action on behalf of other users<br>
	Permission denial code 2: The user is not authorized to do insert action
	</p>

<h2 class="title">Diagnostics</h2>
	 
	
	<?php $response = postToURL($path."ar_insert.php",'0100','','','','');
				print_response($response, $formattype='xml')?>
	

<h2 class="title">PHP Example</h2>
<textarea name='comments' cols='105' rows='14'>
$header = array("Authorization: Basic " . base64_encode("superuser_username1:superuser_password"));
$postWhat = array('username' => 'myusername', 'password' => 'mypassword');
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