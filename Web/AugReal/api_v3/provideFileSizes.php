<?php 
// ============ Db library ==================
require '../DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler();
print $dbHandle->getFilesSizesForAppId($_GET['AppId']); // AppId = 2 for Biscaytik


?>