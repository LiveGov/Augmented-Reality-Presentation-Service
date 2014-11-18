<?php 

$lang = "en";
if (isset($_REQUEST["lang"]))
	$lang = $_REQUEST["lang"];


$appset = "2";

//-------- data libraries ------
require '../../../DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler(false);

$res = $dbHandle->getAREntitiesIBSFields($appset);

while($e=mysql_fetch_assoc($res))
	$db_output[]=$e;


$db_output_final = array();
$rootpath = $_SERVER['DOCUMENT_ROOT'];

foreach ($db_output as $Entity){

	$id = $Entity['id'];
	
	
	//- Check if tracking image exists
	if (is_file($rootpath.'/AugReal/Models3D_DB/'.$id.'/1/'.$id.'_1.png')){
		
		$upscale = 300 / $Entity['trackImScale'] ;
		
		$Entity['trackImScale'] = $upscale; 
		
		array_push($db_output_final, $Entity); 
	}
}

print json_encode($db_output_final);
?>