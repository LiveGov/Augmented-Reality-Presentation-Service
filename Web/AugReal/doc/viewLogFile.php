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
		
	$isAbleToViewLogFile = chPerm('|arserver_api_viewLogFiles|',$_SESSION['permissions_of_user']);
	
	if (!$isAbleToViewLogFile)
		die();
?>


<?php 
	require 'Functions.php';
	//$IP = $_SERVER['REMOTE_ADDR'];
	//$GeoIP = geoFromIP($IP);
	//$AGENT = detectBrowserEngine();
	//$arpout = exec('arp -a $ipAddress');
	
	
	//$succ = chmod($_SERVER['HTTP_HOST']."/alpha_version/doc/MyLog.txt",0755);
	//print_r( error_get_last() );
	//var_dump($succ);
	
	
	
	//$file = file_get_contents('../MyLog.txt', FILE_USE_INCLUDE_PATH);
	
	// get contents of a file into a string
	$filenameAPI = $_SERVER["DOCUMENT_ROOT"]."/AugReal/logAR_API.txt";
	$filenameWEB = $_SERVER["DOCUMENT_ROOT"]."/AugReal/logAR_WEB.txt";
	print $filenameAPI;
	print "<br>";
	printfilecontents($filenameAPI);
	
	print "<br>===========================================================================";
	print "<br>===========================================================================";
	print "<br>===========================================================================";
	print "<br>";
	print $filenameWEB;
	print "<br>";
	printfilecontents($filenameWEB);
	//-------- before ---------------------
	//print get_current_user ();
	//print_r(posix_getpwuid(fileowner('viewLogFile.php')));
	//echo substr(sprintf('%o', fileperms('viewLogFile.php')), -4);
	//print_r(posix_getpwuid(fileowner($filename)));
	//echo substr(sprintf('%o', fileperms($filename)), -4);
	//----------------
	//print_r( error_get_last() );
	//print "<br>";
// 	if (!chmod($filename, 511)){
// 		print "Not working";
// 	}
	//print_r( error_get_last() );
	
	//echo substr(sprintf('%o', fileperms($filename)), -4);
	
	
	
	
	
	
	
	
	//$succ = chmod('../MyLog.txt',760);
	//print nl2br($file);
?>

	
	

</body>
</html>
