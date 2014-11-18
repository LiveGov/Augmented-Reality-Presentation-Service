<?php

function postToURL($url,$idapp,$su_username,$su_password,$username,$password){

	$header = array("Content-Type: application/x-www-form-urlencoded",
			"Authorization: Basic " . base64_encode($su_username.":".$su_password));
	$postWhat = array('idapp' => $idapp,'username' => $username, 'password' => $password);
	$data = http_build_query($postWhat, '', '&');
	$params = array(
			'http'=>array(
					'method'=>'POST',
					'header'=> implode("\r\n", $header),
					'content'=> $data
			)
	);
	$context = stream_context_create($params);
	return trim(file_get_contents($url,false,$context));
}


function printfilecontents($filename){
	$handle = fopen($filename, "r");
	$contents = nl2br(fread($handle, filesize($filename)));
	print $contents;
	fclose($handle);
}

// --- Print API response ------
function print_response($response, $formattype='xml'){

	if (substr($response,0,5)!='<?xml')
		$formattype='json';

	print "<p style='margin-left:50px'>";
	if ($response){
		print "Status: <font color='green'><strong>ok</strong></font> <br/><br/>" ;


		if ($formattype=='xml'){
			$dom = new DOMDocument;
			$dom->preserveWhiteSpace = FALSE;
			$dom->loadXML($response);
			$dom->formatOutput = TRUE;
			$response = $dom->saveXml();
		}
			
		print "Response:<br/>";
		echo "<textarea name='comments' style='overflow:auto;' cols='95' rows='".(substr_count( $response, "\n" )*1.08)."'>";
		echo $response;
		echo "</textarea>";

	}else {
		print "Status: not responding";
	}
	print "</p>";
}

//--------- Dirsize --------
function dirSize($directory) {
	$size = 0;
	foreach(new RecursiveIteratorIterator(new RecursiveDirectoryIterator($directory)) as $file){
		$size+=$file->getSize();
	}
	return $size;
}

// -------- Server to Client speed ----------------------
function InternetSpeedTest(){
	$kb=512;
	echo "<!-";
	flush();
	$time = explode(" ",microtime());
	$start = $time[0] + $time[1];
	for($x=0;$x<$kb;$x++){
		echo str_pad('', 1024, '.');
		flush();
	}
	echo "--!>";
	$time = explode(" ",microtime());
	$finish = $time[0] + $time[1];
	$deltat = $finish - $start;
	//echo "-> Test finished in $deltat seconds. Your speed is ". round($kb / $deltat, 3)."Kb/s";

	return round($kb / $deltat, 0)." Kb/s";
}


//-------------------- DB size --------------------
function DBSize(){
    
	require_once("../config.inc.php");
	if(!$link=mysql_connect("localhost",DBUSER,DBPASS))
		die("error");

	mysql_set_charset('utf8',$link);
	mysql_select_db("AugReal");

	$q = mysql_query("SHOW TABLE STATUS");
	$size = 0;
	while($row = mysql_fetch_array($q)) {
		$size += $row["Data_length"] + $row["Index_length"];
	}

	$decimals = 2;
	$mbytes = number_format($size/(1024*1024),$decimals);

	return $mbytes;
}

//-------------- Convert to human readable form ------------
function prefixBytes($bytes){

	$bytes = explode(" ",$bytes);

	$plusOrder = 0;

	if (count($bytes)>1)
		if ($bytes[1] == "kB")
		$plusOrder = 1;

	$bytes = $bytes[0];

	$si_prefix = array( 'B', 'KB', 'MB', 'GB', 'TB', 'EB', 'ZB', 'YB' );
	$base = 1024;


	$class = min((int)log($bytes , $base) , count($si_prefix) - 1);
	//		echo $bytes . '<br />';
	//		echo sprintf('%1.2f' , $bytes / pow($base,$class)) . ' ' . $si_prefix[$class] . '<br />';

	return sprintf('%1.2f' ,$bytes / pow($base,$class)) . ' ' . $si_prefix[$class+$plusOrder];
}


//------------ Memory RAM ----------
function getSystemMemInfo()
{
	$data = explode("\n", file_get_contents("/proc/meminfo"));
	$meminfo = array();
	foreach ($data as $line) {
		list($key, $val) = (strstr($line, ':') ? explode(':', $line) : array($line, ''));
		if (strlen($val)>0)
			$meminfo[$key] = trim($val);
	}
	return $meminfo;
}

//--------------- Geolocation from IP -------------------------------------
function geoFromIP($IP){
	$jsonOut = file_get_contents("http://freegeoip.net/json/".$IP);
	$out = json_decode($jsonOut,true);
	return $out["country_code"]." ".$out["latitude"]." ".$out["longitude"];
}


//---------------- Browser Engine Detection -------------
function detectBrowserEngine(){
	define("UNKNOWN", 0);
	define("TRIDENT", 1);
	define("GECKO", 2);
	define("PRESTO", 3);
	define("WEBKIT", 4);
	define("VALIDATOR", 5);
	define("ROBOTS", 6);

	if(!isset($_SESSION["info"]['browser'])) {
		$_SESSION["info"]['browser']['engine'] = UNKNOWN;
		$_SESSION["info"]['browser']['version'] = UNKNOWN;
		$_SESSION["info"]['browser']['platform'] = 'Unknown';

		$navigator_user_agent = ' ' . strtolower($_SERVER['HTTP_USER_AGENT']);

		if (strpos($navigator_user_agent, 'linux')) :
		$_SESSION["info"]['browser']['platform'] = 'Linux';
		elseif (strpos($navigator_user_agent, 'mac')) :
		$_SESSION["info"]['browser']['platform'] = 'Mac';
		elseif (strpos($navigator_user_agent, 'win')) :
		$_SESSION["info"]['browser']['platform'] = 'Windows';
		endif;

		if (strpos($navigator_user_agent, "trident")) {
			$_SESSION["info"]['browser']['engine'] = TRIDENT;
			$_SESSION["info"]['browser']['version'] = floatval(substr($navigator_user_agent, strpos($navigator_user_agent, "trident/") + 8, 3));
		}
		elseif (strpos($navigator_user_agent, "webkit")) {
			$_SESSION["info"]['browser']['engine'] = WEBKIT;
			$_SESSION["info"]['browser']['version'] = floatval(substr($navigator_user_agent, strpos($navigator_user_agent, "webkit/") + 7, 8));
		}
		elseif (strpos($navigator_user_agent, "presto")) {
			$_SESSION["info"]['browser']['engine'] = PRESTO;
			$_SESSION["info"]['browser']['version'] = floatval(substr($navigator_user_agent, strpos($navigator_user_agent, "presto/") + 6, 7));
		}
		elseif (strpos($navigator_user_agent, "gecko")) {
			$_SESSION["info"]['browser']['engine'] = GECKO;
			$_SESSION["info"]['browser']['version'] = floatval(substr($navigator_user_agent, strpos($navigator_user_agent, "gecko/") + 6, 9));
		}
		elseif (strpos($navigator_user_agent, "robot"))
		$_SESSION["info"]['browser']['engine'] = ROBOTS;
		elseif (strpos($navigator_user_agent, "spider"))
		$_SESSION["info"]['browser']['engine'] = ROBOTS;
		elseif (strpos($navigator_user_agent, "bot"))
		$_SESSION["info"]['browser']['engine'] = ROBOTS;
		elseif (strpos($navigator_user_agent, "crawl"))
		$_SESSION["info"]['browser']['engine'] = ROBOTS;
		elseif (strpos($navigator_user_agent, "search"))
		$_SESSION["info"]['browser']['engine'] = ROBOTS;
		elseif (strpos($navigator_user_agent, "w3c_validator"))
		$_SESSION["info"]['browser']['engine'] = VALIDATOR;
		elseif (strpos($navigator_user_agent, "jigsaw"))
		$_SESSION["info"]['browser']['engine'] = VALIDATOR;

		$browserEngine = "(unknown)";
		switch($_SESSION["info"]['browser']['engine']) {
			case TRIDENT: $browserEngine = "Trident";
			break;
			case GECKO: $browserEngine = "Gecko";
			break;
			case PRESTO: $browserEngine = "Presto";
			break;
			case WEBKIT: $browserEngine = "Webkit";
			break;
			case VALIDATOR: $browserEngine = "Validator";
			break;
			case ROBOTS: $browserEngine = "Robot";
		}


		//echo $_SESSION["info"]['browser']['engine']; //Engine detected:
		//echo $_SESSION["info"]['browser']['version']; //"\nEngine version: "
		//echo $_SESSION["info"]['browser']['platform']; //"\nPlatform detected: "

		return $browserEngine." ". $_SESSION["info"]['browser']['version']." ".$_SESSION["info"]['browser']['platform'];
	}
}







?>