<?php 
$action = 'insert';
require 'api_header.php';

//----------------------------------
$SUCCESS_NEW = $dbHandle->insertAREntity($login_id,$login_rname,
										CheckSet($_REQUEST['latitude'],0),
										CheckSet($_REQUEST['longitude'],0));
$lastid = mysql_insert_id();

$altitude_with_id = 60.0 + $lastid/100000;
$dbHandle->updateAREntity($lastid,'altitude',$altitude_with_id);

if ($SUCCESS_NEW){
	print "CODE 1, ".$lastid;
	Log2File('API',SCUSER,SCPASS,SCCUSTOMERID,$login_id,date('Y-m-d H:i:s'),'',$action);
}
?>
