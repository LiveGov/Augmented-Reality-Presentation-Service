<html>
<head>
<?php
	require_once("../auth/include/membersite_config.php");
	require_once("../KeepLogFunctions.php");
	$fgmembersite->CheckLogin(); // this will recall $_SESSION vars
	
  	$isAbleToView = chPerm('|arserver_api_viewDocumentation|',$_SESSION['permissions_of_user']);
  	
  	if (!$isAbleToView)
 		die();
?>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Documentation</title>
<link rel="stylesheet" type="text/css" href="theme.css" />
</head>


<frameset rows="6%,94%" frameborder="0">
<frame src="topbar.php" name="topFrame" ></frame>
<frameset cols="25%,75%" frameborder="5px" >
<frame src="sidebar.php" name="sidebarFrame"></frame>
<frame name="classFrame"></frame>
</frameset>

</frameset>

<body>
</body>
</html>
