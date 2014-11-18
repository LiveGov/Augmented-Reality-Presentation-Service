<?php require_once("auth/include/membersite_config.php");

require_once("KeepLogFunctions.php");
?>

<link rel="STYLESHEET" type="text/css" href="auth/style/fg_membersite.css" />
<script type='text/javascript' src='scripts/gen_validatorv31.js'></script>
<script type='text/javascript'>
// Home div open/close
function homebuttonopen(){
	document.getElementById('fg_membersite_content').style.display = 
		document.getElementById('fg_membersite_content').style.display!='none'?'none':'';
}
</script>
<?php
if ($_GET['logout']){

	$fgmembersite->CheckLogin();
	Log2File('WEB',SCUSER,SCPASS,SCCUSTOMERID,$_SESSION['id_of_user'],'',date('Y-m-d H:i:s'),"Logout");
	$fgmembersite->LogOut();
	$fgmembersite->RedirectToURL("index.php");
}

if($fgmembersite->CheckLogin()){
	echo "<button id='Btlogin' type='button' onclick='homebuttonopen()'>".$_SESSION['name_of_user']."</button>";
	echo "<div id='fg_membersite' style='display:none'></div>";
} else {
	$fgmembersite->RedirectToURL("index.php");
}
?>

<!-- After successfull-login -->
<div id='fg_membersite_content' style="display:none; padding-right:70px; margin-top:24px;">
	<a id='logout' href='<?php echo $fgmembersite->GetSelfScript()."?logout=true" ?>'>Logout</a>
</div>
