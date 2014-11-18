<?php 
// ============ Db library ==================
require '../DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler();


if ($_GET['filename'])
	print $dbHandle->getARHash($_GET['filename'], null);
else if ($_GET['id'])
	print $dbHandle->getARHash(null, $_GET['id']);
?>