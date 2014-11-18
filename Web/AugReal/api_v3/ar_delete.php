<?php
// 	-------------- Delete a record ----------------------------

$action = 'delete';
require 'api_header.php';

//------ if no id of Entity is given then die
$id	= CheckSet('id',0);   // CheckSet is at My_Utils
if ($id==0){
	print "ERROR CODE DELETE 0"; die();
}

//-------- find author and coauthors of the past -------------
$res_e = $dbHandle->getAREntity($id);
$row = mysql_fetch_array($res_e);

$id_author   = $row['id_author'];
$id_coauthor = $row['id_coauthor'];

if(!$fgmembersite->CheckLogin()){
	die("ERROR CODE DELETE 2");
} else {
	$login_rname = $_SESSION['name_of_user'];
	$login_id = $_SESSION['id_of_user'];
	$login_permissions = $_SESSION['permissions_of_user'];
}
//--------- Check permissions ----------
$isAbleToDelete = chPerm('|ar_web_deleteAREntity|', $login_permissions);
$IsTheAuthor   = $login_id == $id_author;

if ($IsTheAuthor && $isAbleToDelete){
	$resDelete = $dbHandle->deleteAREntity(round($_REQUEST['id']));
	if ($resDelete){
		Log2File("WEB",SCUSER,SCPASS,'2',$_SESSION['id_of_user'],date('Y-m-d H:i:s'),'','Deleted: '.round($_REQUEST['id']));
	} else {
		//Log2File("WEB; delete; SuccessLogin; No permission to delete; User:".$_SESSION['name_of_user']);
	}
} else {
	die("ERROR CODE DELETE 3");
} 

// REDIRECTOR to Human_AR_Edit.php (in case it is not called from api itself)
if ($hs['Authorization']==null)
	echo "<script type='text/javascript'>window.location = document.referrer;</script>";
?>
