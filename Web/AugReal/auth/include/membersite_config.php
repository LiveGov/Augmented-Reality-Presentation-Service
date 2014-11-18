<?PHP
//include constants
if(basename(getcwd())=='doc' || basename(getcwd())=='api_v3')
	require_once("../config.inc.php");
else 	
	require_once("config.inc.php");
	
require_once("fg_membersite.php");


// create the class
$fgmembersite = new FGMembersite();

//Provide your site name here
$fgmembersite->SetWebsiteName(SITENAME);
//Provide the email address where you want to get notifications
$fgmembersite->SetAdminEmail(ADMINEMAIL);
$fgmembersite->SetSU_username(SCUSER);
$fgmembersite->SetSU_password(SCPASS);
$fgmembersite->SetServiceCenterPath(SCURL);
$fgmembersite->SetRandomKey('qSRaVS6DrTzrPvx');
?>