<?php
//moduleid: 4, healthcheckid: 8, name: "augmented_reality_api_healthcheck"
//moduleid: 6, healthcheckid: 9, name: "augmented_reality_web_healthcheck",
function setHealthCheck($su_username,$su_password){

	//------------- AUTH ------------------------
	require("auth/include/membersite_config.php");
	// Login
	$isLoggedIn = $fgmembersite->Login(trim($su_username),trim($su_password),trim($su_username),trim($su_password));

	$fgmembersite->CheckLogin(); // retrice SESSION vars
	require("My_Utils.php");

	//Check permissions
	if (!chPerm('|sc_api_diagnostics_sethealthcheck|',$_SESSION['permissions_of_user']))
		die("Not allowed");
	//--------------------------------------------
	$postWhat = array('status' => '0', 'date' => date('Y-m-d H:i:s').'Z'); //0:OK//1: WARNING//2:CRITICAL//3:UNKNOWN
	$data = http_build_query($postWhat, '', '&');


	//report header (authorization at service center)
	$header = implode("\r\n", array("Content-Type: application/x-www-form-urlencoded",
			"Authorization: Basic " . base64_encode($su_username.":".$su_password),
			"Content-Length: ".strlen($data)));

	$context_options = array(
			'http'=>array(
					'method'=> 'PUT',
					'header'=> $header,
					'content' => $data
			)
	);

	// 	if ($type=='API')
		// 		$hid = '8';
		// 	else if ($type=='WEB')
			// 		$hid = '9';

	//-------- API -------
	$url = SCURLDIAG."/healthcheck/8";
	$context = stream_context_create($context_options);
	$data = file_get_contents($url,false,$context);
	var_dump( $data );

	//-------- WEB -----------
	$url = SCURLDIAG."/healthcheck/9";
	$context = stream_context_create($context_options);
	$data = file_get_contents($url,false,$context);
	var_dump( $data );


	//-----------------------------------------

	// now Log out
	$fgmembersite->Logout();
}
//
function getHealthCheck($su_username,$su_password, $type){
	header('Content-type: text/plain');

	if ($type=='WEB')
		$module = '6';
	else if ($type=='API')
		$module = '4';

	//------------- AUTH ------------------------
	require("auth/include/membersite_config.php");
	// Login
	$isLoggedIn = $fgmembersite->Login(trim($su_username),trim($su_password),trim($su_username),trim($su_password));
	$fgmembersite->CheckLogin(); // retrieve SESSION vars
	require("My_Utils.php");

	// Check permissions
	if (!chPerm('|sc_api_diagnostics_gethealthchecksformodule|',$_SESSION['permissions_of_user']))
		die();

	//--------------------------------------------
	$header = array("Content-Type: multipart/form-data","Authorization: Basic " . base64_encode($su_username.":".$su_password));
	$p_or_g = "GET";

	$opts = array(
			'http'=>array(
					'method'=>$p_or_g,
					'header'=> implode("\r\n", $header),
					'content' => null
			)
	);

	$context = stream_context_create($opts);

	$url = SCURLDIAG."/healthcheck/module/".$module;

	$data = file_get_contents($url,false,$context);

	//$data = get_headers_with_stream_context($url,$context);

	//print_r(error_get_last());

	print_r( $data );
	//-----------------------------------------
	// now Log out
	$fgmembersite->Logout();
}
//--------  Add to Log file ----------
// TOdo : only one date (the current one)
function Log2File($type,$su_username,$su_password,$customerid,$registereduserid,$start_timestamp="",$end_timestamp="",$What){

		$filename = $_SERVER['DOCUMENT_ROOT']."/AugReal/"."logAR_".$type.".txt";
		$fp = fopen($filename,"a");

		$content = "";
		if (date("dmy")!=date("dmy", filemtime($filename)))
			$content = $content."\nNewDay";

		$content = $content."\ncustomerid:".$customerid.",registereduserid:".$registereduserid.", T:".$start_timestamp.$end_timestamp.", Details:".$What;

		fwrite($fp, $content);
		fclose($fp);
}


//---------- POST LOG FILE -------
function post_LogFile($su_username,$su_password,$type,$customerid,$registereduserid,$startdate,$enddate){

	header('Content-type: text/plain');
	//------------- AUTH ------------------------
	set_include_path( dirname(dirname(__FILE__)).'/AugReal/'); // now $base contains "app"
	
	require_once("auth/include/membersite_config.php");
	$isLoggedIn = $fgmembersite->Login(trim($su_username),trim($su_password),trim($su_username),trim($su_password));
	$fgmembersite->CheckLogin(); // retrieve SESSION vars
	require_once("My_Utils.php");
	$isAbleToPostLog = chPerm('|sc_api_diagnostics_postlogfile|',$_SESSION['permissions_of_user']); 	// Check permissions
	if (!$isAbleToPostLog)
		die();
	//--------------------------------------------
	if ($type=='WEB')
		$module=4;
	else if ($type=='API')
		$module=6;

	$filename = $base.'logAR_'.$type.'.txt';
	
	//------- Read log file ------------
	$fileHandle = fopen($filename, "rb", true);
	
	$fc = stream_get_contents($fileHandle);
	fclose($fileHandle);

	$lastindexNeDay = strripos($fc,'NewDay');
	$fc = substr($fc, $lastindexNeDay); 
	
	
	
	$eol = "\r\n";
	$data = '';
	$mime_boundary='-----------------------------LIVEANDGOV----';
	$data .= '--' . $mime_boundary . $eol;
	$data .= 'Content-Type: application/json' . $eol. $eol;
	$data .= '{"filename":"'.$filename.'","customerid":"'.$customerid.
	'","registereduserid":"'.$registereduserid.'","startdate":"'.$startdate.'","enddate":"'.$enddate.'"}' . $eol;
	$data .= '--' . $mime_boundary . $eol;
	$data .= 'Content-Type: application/text' . $eol. $eol;
	$data .= $fc.$eol;
	$data .= "--" . $mime_boundary . "--" . $eol . $eol;

	$params = array('http' => array(
			'method' => 'POST',
			'header' => 'Content-Type: multipart/form-data; boundary=' . $mime_boundary.$eol.
			'Authorization: Basic '.base64_encode($su_username.":".$su_password).' '.$eol
			.'Content-Length:'.strlen($data)
			,'content' => $data
	));


	$ctx = stream_context_create($params);

	$url = SCURLDIAG."/log/".$module;
	$response = get_headers_with_stream_context($url, $ctx);
	
	print_r($response);

	// now Log out
	$fgmembersite->Logout();
}

//Check if $small is a substring of $big
// print CheckPerm('|aaba|', '|aaa|bbbbb|ccccc|');
function chPerm($small='', $big=''){
	if (strpos($big,$small) !== false)
		return true;
	return false;
}


function get_headers_with_stream_context($url, $context, $assoc = 0) {
	$fp = fopen($url, 'r', null, $context);

	$metaData = stream_get_meta_data($fp);
	//var_dump($fp);

	$headerLines = $metaData['wrapper_data'];

	if(!$assoc) return $headerLines;

	$headers = array();
	foreach($headerLines as $line) {
		if(strpos($line, 'HTTP') === 0) {
			$headers[0] = $line;
			continue;
		}

		list($key, $value) = explode(': ', $line);
		$headers[$key] = $value;
	}

	return $headers;
}
?>