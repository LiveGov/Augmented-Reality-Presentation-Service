<?php 
//----------- authorization libraries ----------
$hs = getallheaders();

require "../../auth/include/SC_auth.php";
$authinfo = Authenticate(null, null, $_POST['username'],$_POST['password'],$hs['Authorization']);

if ($authinfo==null){// superuser not authorized to do authorization of registered users
	print "Permission denial code 1";
	die();
}

if ($authinfo['permissions']==''){ // no permissions at all 
	print "Permission denial code 2";
	die();
} else { // check some permissions
	if ($action=='IBS'){
		$HasAccessToIBS = chPerm('|arserver_api_IBS|',$authinfo['permissions']);
		if (!$HasAccessToIBS){
			print "Permission denial code 2";
			die();
		}
	} else if ($action=='LBS'){
		$HasAccessToLBS = chPerm('|arserver_api_LBS|',$authinfo['permissions']);

		if (!$HasAccessToLBS){
			print "Permission denial code 2";
			die();
		}
	}
}


?>