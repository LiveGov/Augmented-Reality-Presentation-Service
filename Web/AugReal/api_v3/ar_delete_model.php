<?php
$action = 'edit';
require 'api_header.php';
$pathdir = 'AugReal';

//------ if no id of Entity is given then die
$id	= CheckSet('id',0);   // CheckSet is at My_Utils
if ($id==0){
	print "Error code 0"; die();
}

//------ if no iM of Model of Entity is given then die
$iM	= CheckSet('iM',0);   // CheckSet is at My_Utils
if ($id==0){
	print "Error code 0"; die();
}

//-------- find author and coauthors of the past -------------
$res_e = $dbHandle->getAREntity($id);
$row = mysql_fetch_array($res_e);

$id_author   = $row['id_author'];
$id_coauthor = $row['id_coauthor'];

if(!$fgmembersite->CheckLogin()){
	die();
} else {
	$login_rname = $_SESSION['name_of_user'];
	$login_id = $_SESSION['id_of_user'];
	$login_permissions = $_SESSION['permissions_of_user'];
}

//--------- Check permissions ----------
$isAbleToEdit = chPerm('|ar_web_editAREntity|', $login_permissions);
$IsTheAuthor   = $login_id == $id_author;
$IsTheCoauthor = chCoauth($login_id, $id_coauthor);

if (($IsTheAuthor || $IsTheCoauthor) && $isAbleToEdit){
} else {
	die();
}

//---------- Update database  --------------
$resUpdate = $dbHandle->updateAREntity($id,'models',$iM-1);

//------------ Clear Folder ----------------
$deleter = new Deleter(); // My_Utils.php
$path =    $_SERVER['DOCUMENT_ROOT'].'/'.$pathdir.'/Models3D_DB/'.$id.'/'.$iM;
$deleter->deleteDir($path);

//-----------------------------------------
if ($resUpdate)
	print "CODE 1";

Log2File($caller,SCUSER,SCPASS,SCCUSTOMERID,$_SESSION['id_of_user'],date('Y-m-d H:i:s'),'','Delete Model '.$iM.' of AR Entity '.round($_REQUEST['id']));

// REDIRECTOR to Human_AR_Edit.php (in case it is not called from api itself)
if ($hs['Authorization']==null)
	echo "<script type='text/javascript'>window.location = document.referrer;</script>";
?>
