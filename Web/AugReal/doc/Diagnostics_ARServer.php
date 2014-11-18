<html>
<header>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="theme.css">
</header>
<body>
<?php	

    require_once("../auth/include/membersite_config.php");
	require_once("../KeepLogFunctions.php");
	$fgmembersite->CheckLogin(); // this will recall $_SESSION vars
		
  	$isAbleToDiagnose = chPerm('|arserver_api_performDiagnostics|',$_SESSION['permissions_of_user']);
  	
  	if (!$isAbleToDiagnose)
 		die();
	
	require 'Functions.php';
	$path= $_SERVER['DOCUMENT_ROOT'];
	?>

	<h1>AR-Server Framework</h1>
	<h2>Diagnostics</h2>

	<table id="indexTable">
		<tr><th>Tag</th><th>Value</th></tr>
		<?php 
		print "<tr><td>HTTP_HOST</td><td>".$_SERVER['HTTP_HOST']."</td></tr>";
		print "<tr><td>SERVER_SOFTWARE</td><td>".$_SERVER['SERVER_SOFTWARE']."</td></tr>";
		print "<tr><td>SERVER_ADDRESS</td><td>".$_SERVER['SERVER_ADDR']."</td></tr>";
		print "<tr><td>SERVER_PORT</td><td>".$_SERVER['SERVER_PORT']."</td></tr>";

		//--------------------------- RAM ----------
		$meminfo = getSystemMemInfo() ;
		print "<tr><td>RAM Total</td><td>".prefixBytes($meminfo['MemTotal'])."</td></tr>";
		//print "<tr><td>RAM Free</td><td>".prefixBytes($meminfo['Cached'])."</td></tr>";
		//--------- HD space available -------
		print "<tr><td>HD Free space</td><td>".prefixBytes( disk_free_space(".") )."</td></tr>";
		//---------- DB Size ------------------------------
		print "<tr><td>DB Size</td><td>".DBSize()." MB</td></tr>";
		//---------- 3D Models Size ------------------------------
		print "<tr><td>3D Models Size</td><td>".prefixBytes(dirSize($path."/Models3D_DB"))."</td></tr>";
		//---------- Images Size ------------------------------
		print "<tr><td>Images size</td><td>".
		          prefixBytes(dirSize($path."/VisRec/ReconEngine/content"))."</td></tr>";
		//--------- Speed test ------------
		print "<tr><td>Server to you connection speed</td><td>".InternetSpeedTest()."</td></tr>";
		?>
	</table>
</body>
</html>
