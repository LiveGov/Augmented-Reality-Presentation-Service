<?php

$action = 'get';
require_once("../auth/include/membersite_config.php");
require 'api_header.php';

//----------- GET a single Entity BY ID ----------------------------
if(isset($_REQUEST['id']) ){

	$res = $dbHandle->getAREntity($_REQUEST['id']); 

} elseif (isset($_REQUEST['y0down']) && isset($_REQUEST['y0up']) && isset($_REQUEST['x0down']) && isset($_REQUEST['x0up'])) {

	//--------- GET multiple Entities in a RECTANGLE -------------------------------------
	$res = $dbHandle->getAREntitiesRect($_REQUEST['y0down'],$_REQUEST['y0up'],$_REQUEST['x0down'],$_REQUEST['x0up'],
			isset($_REQUEST['id_app'])?$_REQUEST['id_app']:null);

	// Get all entities of an Application id
} elseif (isset($_REQUEST['id_app'])){
	
	$res = $dbHandle->getAREntitiesAllFields($_REQUEST['id_app']);
	
} else {
	print "Error code 1";
	mysql_close();
	die();
}


while($row=mysql_fetch_assoc($res)){
	$res_VRec = $dbHandle->getVisRecModel($row['id_VRec']);
	$row_VRec=mysql_fetch_assoc($res_VRec);
	
	$newarr = array("name_VRec" => ($row_VRec['name']!=null?$row_VRec['name']:""));
	
	$newarr2 = array("id_VRec" => $row['id_VRec']);
	$output[]= array_merge($row , $newarr, $newarr2);
}

print(json_encode($output));

mysql_close();

Log2File('API',SCUSER,SCPASS,SCCUSTOMERID,$login_id,date('Y-m-d H:i:s'),'',$action);
?>