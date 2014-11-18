<?php 

// Schedule this file to be executed every 24 hours 
require 'KeepLogFunctions.php';
post_LogFile(SCUSER,SCPASS,'WEB',SCCUSTOMERID,19,'',date('Y-m-d H:i'));
?>