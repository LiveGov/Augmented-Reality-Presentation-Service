<?php 
//----------- authorization libraries ----------
$hs = getallheaders();

require "../auth/include/SC_auth.php";
$authinfo = Authenticate(null, null, $_POST['username'],$_POST['password'],$hs['Authorization']);

if ($authinfo==null){ // superuser not authorized to do authorization of registered users
	print "Permission denial code 1";
	die();
}

if ($authinfo['permissions']==''){ // no permissions at all 
	print "Permission denial code 2";
	die();
} else { // check some permissions
	if ($action=='Documentation'){
		$HasAccessToDoc = chPerm('|arserver_api_viewDocumentation|',$authinfo['permissions']);
		if (!$HasAccessToDoc){
			print "Permission denial code 2";
			die();
		}
	} else if ($action=='Diagnostics'){
		$HasAccessToDiags = chPerm('|arserver_api_performDiagnostics|',$authinfo['permissions']);

		if (!$HasAccessToDiags){
			print "Permission denial code 2";
			die();
		}
	} else if ($action=='viewLogFiles'){
		$HasAccessToDiags = chPerm('|arserver_api_viewLogFiles|',$authinfo['permissions']);
		
		if (!$HasAccessToDiags){
			print "Permission denial code 2";
			die();
		}
	}
	
}


?>