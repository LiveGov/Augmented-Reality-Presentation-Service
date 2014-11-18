<?php 


require_once("../auth/include/membersite_config.php");
require '../My_Utils.php'; // MyMkdir, CheckSet,LogToFile
require '../KeepLogFunctions.php'; // chCoauth

//------ if not logged then check if the call is from API --------
$fgmembersite->CheckLogin(); // Sets Session vars;


if ($_SESSION['id_of_user']==""){ // Not Web Browser
	$hs = getallheaders();
	
	$authinfo = null;
	if (isset($hs['Authorization'])){
		require_once("../auth/include/SC_auth.php");
		$authinfo = Authenticate($SC_path,$arserver_username,$arserver_password, $_POST['username'],$_POST['password'],$hs['Authorization']);
	}

	if ($authinfo==null){
		print "Permission denial code 1"; 
		die();
	}

	// Check if the super user has access to api, and if can authenticate another register user
	$SU_HasAccessToAPI = chPerm('|ar_api|',$authinfo['permissions']);
	$SU_AuthentRegUser = chPerm('|sc_api_account_authenticateregistereduser|',$authinfo['permissions']);

	if (!$SU_HasAccessToAPI || !$SU_AuthentRegUser){
		print "Permission denial code 1";
		die();
	}

	$login_rname =  $authinfo['firstname']." ".$authinfo['lastname'];
	$login_id = $authinfo['id'];
	$login_permissions =$authinfo['permissions'];
	$caller = "API";
	
	$isAbleToAccessAPI  = chPerm('|ar_api|', $login_permissions);

	if (strcmp($action,"insert"))
		$isAbleForAction 		= chPerm('|arserver_api_insertEntity|', $login_permissions);
	else if (strcmp($action,"edit"))
		$isAbleForAction 		= chPerm('|arserver_api_editEntity|', $login_permissions);
	else if (strcmp($action,"get"))
		$isAbleForAction 		= chPerm('|arserver_api_getEntity|', $login_permissions);

	
	
	$isAbleToAccessAPI = true; 
	$isAbleForAction = true;
	
	if ($isAbleToAccessAPI && $isAbleForAction){
		require '../DatabaseHandler.class.php';  // Connect with with DB
		$dbHandle = new DatabaseHandler();
	}else {
		print "Permission denial code 2";
		die();
	}

} else {
	require '../DatabaseHandler.class.php';  // Connect with with DB
	$dbHandle = new DatabaseHandler();
	$caller = "WEB";
}

?>