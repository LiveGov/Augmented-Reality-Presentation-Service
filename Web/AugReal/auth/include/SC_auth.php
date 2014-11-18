<?php 
error_reporting(0);

//-------- get all anonymous users ----------------------------
function getAnonymousUsers($path,$su_username,$su_password,$username,$password){
	
	$superuserE64 = "Basic ".base64_encode($su_username.":".$su_password);
	
	return json_decode(SC_wrapper_API($path,"GetAnonymousUsers","2",null,$superuserE64),true);
}

//-------------- get all registered users ----------------------
function getRegisteredUsers($path,$su_username,$su_password,$customerid){
	
	$superuserE64 = "Basic ".base64_encode($su_username.":".$su_password);
	
	$all_users_info = json_decode(SC_wrapper_API($path,"GetRegisteredUsers",$customerid,"1",$superuserE64),true);
	
	$output= [];
	foreach ($all_users_info as $u){
		$output[] = array($u['accountinfo']['registereduserid'],  $u['userinfo']['firstname']." ".$u['userinfo']['lastname']);
	}

	return $output;
}



//--------- Authenticate and getPermissions for user by superuser --------------------- 
function Authenticate($path,$su_username,$su_password,$username,$password,$superuserE64=null){  // new $superuser (all base64 encoded)
	
	if (!$superuserE64)
		$superuserE64 = "Basic ".base64_encode($su_username.":".$su_password);

	$response = SC_wrapper_API($path,"AuthenticateRegisteredUser",$username,$password,$superuserE64);
	$UserInfo = json_decode($response,true);

	if ($UserInfo['accountinfo']['registereduserid'] > 0){
		
		$STRPPP = SC_wrapper_API($path,"GetPermissions",$username,$password,$superuserE64);
		$permArr = json_decode($STRPPP,true);

		$permsSTR = "";
		for ($i=0; $i<count($permArr); $i++)
			$permsSTR .= "|".$permArr[$i]['name']."|";

		$res = array(
				"id" => $UserInfo['accountinfo']['registereduserid'],
				"type" => $UserInfo['accountinfo']['type'],
				"username" => $UserInfo['accountinfo']['username'],
				"firstname" => $UserInfo['userinfo']['firstname'],
				"lastname" => $UserInfo['userinfo']['lastname'],
				"permissions" => $permsSTR
		);

		return $res;
	}else 
		return null;
}

/**
 * Wrapper to call Service Center API
 * 
 * @param string $function: GetCustomers, GetCustomer, GetRegisteredUsers, GetRegisteredUser ...
 * @param string $data1
 * @param string $data2
 * @return unknown
 */
function SC_wrapper_API($path, $function="GetCustomers", $data1="", $data2="", $superuser=""){

	
	
	$header = array("Content-Type: application/x-www-form-urlencoded","Authorization: " . $superuser);
	$p_or_g = "GET";
	$data = "";
	
	//	-------- GetCustomers -----------------
	if (strcmp($function,"GetCustomers")==0){
		$url = 'organization/customer/?customercode='.$data1; //mbh004
		
	//---------  GetCustomer ----------
	} else if (strcmp($function,"GetCustomer")==0){
		$url = 'organization/customer/'.$data1; // 2
	
	//---------- GetRegisteredUsers -----------
	}else if (strcmp($function,"GetRegisteredUsers")==0){
		$url = 'user/registered/customer/'.$data1.'/?type='.$data2; // 2   1
	
	//-------- GetAnonymousUsers -------------
	} else if (strcmp($function,"GetAnonymousUsers")==0){
			$url = "user/anonymous/customer/".$data1; //{customerid}	
		
	//------    GetRegisteredUser --------
	}else if (strcmp($function,"GetRegisteredUser")==0){
		$url = "user/registered/".$data1; // 8
		
	// --  AuthenticateRegisteredUser
	}else if (strcmp($function,"AuthenticateRegisteredUser")==0){
		$url = "account/authenticate";
		$p_or_g = "POST";

		$postWhat = array('username' => $data1,'password' => $data2);
		$data = http_build_query($postWhat, '', '&');
	
	// -------- 6. HasPermission ----------
	}else if (strcmp($function,"HasPermission")==0){
		$url = "account/haspermission/".$data1; //ar_web";
	// -------- 7. HasPermissionForUser -------
	}else if (strcmp($function,"HasPermissionForUser")==0){
		$url = "account/haspermission/".$data1."/".$data2; // 2/ar_web";
		
	// --------- 8. GetPermissions ------------------
	}else if (strcmp($function,"GetPermissions")==0){
		$url = "account/permission";
		
	// -------- 9. GetPermissionsForUser ------------
	}else if (strcmp($function,"GetPermissionsForUser")==0){
		$url = "account/permission/".$data1; //8 
	}
	//	------------- Caller ----------------
	$url = $path . $url;
	
	
	
	$params = array(
		'http'=>array(
				'method'=>$p_or_g,
				'header'=> implode("\r\n", $header),
				'content' => $data
				)
		);

	$context = stream_context_create($params);
		
	$data = file_get_contents($url,false,$context);
	
	
	
	return $data;
}

?>