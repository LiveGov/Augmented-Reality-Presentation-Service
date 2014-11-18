<?PHP
require_once("auth/include/membersite_config.php");
require_once("KeepLogFunctions.php");

if(isset($_POST['submitted']))
{
	// super user and typed user credentials
	$isLoggedIn = $fgmembersite->Login(	trim($_POST['username']),
										trim($_POST['password']));

	if ($isLoggedIn)
	{
		if(chPerm('|ar_web|',$_SESSION['permissions_of_user'])){ // 2 is for UPlanning
			Log2File("WEB",SCUSER,SCPASS,SCCUSTOMERID,$_SESSION['id_of_user'],date('Y-m-d H:i:s'),'','Logged in');
			$fgmembersite->RedirectToURL("AR_Main.php");
		}else{
			$ErrorPerm = "You don't have permissions to enter AR-Server, although you are a registered user.";
		}
	} 
}

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-US" lang="en-US">
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>
	<title>Login</title>
	<link rel="STYLESHEET" type="text/css" href="auth/style/fg_membersite.css" />
	<script type='text/javascript' src='auth/scripts/gen_validatorv31.js'></script>
</head>
<body>

<!-- Form Code Start -->
<div id='fg_membersite'>

<form id='login' action='<?php echo $fgmembersite->GetSelfScript(); ?>' method='post' accept-charset='UTF-8'>
<fieldset >
<legend>Login</legend>

<img src='images/livegov-logo3.jpg' width='100px' style='vertical-align:middle;margin:10px'> - Augmented Reality</img>

<input type='hidden' name='submitted' id='submitted' value='1'/>



<div><span class='error'><?php echo $fgmembersite->GetErrorMessage(); ?></span></div>
<div><span class='error'><?php echo nl2br(htmlentities($ErrorPerm)); ?></span></div>

<div class='container'>
    <label for='username' >UserName:</label><br/>
    <input type='text' name='username' id='username' value='<?php echo $fgmembersite->SafeDisplay('username') ?>' maxlength="50" /><br/>
    <span id='login_username_errorloc' class='error'></span>
</div>
<div class='container'>
    <label for='password' >Password:</label><br/>
    <input type='password' name='password' id='password' maxlength="50" /><br/>
    <span id='login_password_errorloc' class='error'></span>
</div>

<div class='container'>
    <input type='submit' name='Submit' value='Submit' />
</div>
<!-- 
<div class='short_explanation'><a href='reset-pwd-req.php'>Forgot Password?</a></div>
 -->
</fieldset>
</form>

<!-- client-side Form Validations:
Uses the excellent form validation script from JavaScript-coder.com-->
<script type='text/javascript'>
    var frmvalidator  = new Validator("login");
    frmvalidator.EnableOnPageErrorDisplay();
    frmvalidator.EnableMsgsTogether();
    frmvalidator.addValidation("username","req","Please provide your username");
    frmvalidator.addValidation("password","req","Please provide the password");
</script>
</div>


</body>
</html>