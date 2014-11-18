<?php 
header('Content-type: application/json;');

require $_SERVER['DOCUMENT_ROOT'].'/AugReal/DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler(true);
$appset = "2";

$res = $dbHandle->getAREntitiesFields4Wikitude($appset);

while($e=mysql_fetch_assoc($res)){

	$strurlImage = "http://".$_SERVER['HTTP_HOST']."/AugReal/Models3D_DB/".$e['id']."/1/AR_".$e['id']."_1.jpg";
	if (urlExists($strurlImage) !== false )
		$e['markerImage'] = $strurlImage; 

	$db_output[]=$e;
}


print json_encode($db_output);


function urlExists($file){
	
	$file_headers = @get_headers($file);
	if($file_headers[0] == 'HTTP/1.1 404 Not Found') {
		$exists = false;
	}
	else {
		$exists = true;
	}
	
	return $exists;
}
?>