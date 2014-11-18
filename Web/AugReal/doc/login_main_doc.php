<?php require_once("../auth/include/membersite_config.php"); ?>

<link rel="STYLESHEET" type="text/css" href="../auth/style/fg_membersite.css" />
<script type='text/javascript' src='../auth/scripts/gen_validatorv31.js'></script>
<script type='text/javascript'>
// Home div open/close
function homebuttonopen(){
	document.getElementById('fg_membersite_content').style.marginTop='0px';
	document.getElementById('fg_membersite_content').style.position = 'static' 
	document.getElementById('fg_membersite_content').style.display=document.getElementById('fg_membersite_content').style.display!='none'?'none':'';
	
}
</script>
<?php
 
if ($_GET['logout']){
	$fgmembersite->LogOut();
	$fgmembersite->RedirectToURL("index.php");
}

//<!-- After successfull-login -->
if($fgmembersite->CheckLogin()){
	echo "<button id='Btlogin' type='button' onclick='homebuttonopen()'>".$_SESSION['name_of_user']."</button>";
	echo "<div id='fg_membersite_content' style='display:none'>";
	echo "<a id='logout' href='".$fgmembersite->GetSelfScript()."?logout=true'>Logout</a>";
	echo "</div>";
	
} else {
	$fgmembersite->RedirectToURL("http://augreal.mklab.iti.gr/doc/index.php");
}
?>


