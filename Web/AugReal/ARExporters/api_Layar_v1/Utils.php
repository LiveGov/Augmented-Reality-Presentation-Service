<?php 
/*** Specific Custom Functions ***/


// Retrieve parameter values using $_GET and put them in $value array with
// parameter name as key. 
function getRequestParams($keys) {

  $paramsArray = array();
  try {
    foreach( $keys as $key ) {
      if ( isset($_GET[$key]) ){
        $paramsArray[$key] = $_GET[$key]; 
      }else 
        throw new Exception($key .' parameter is not passed in GetPOI request.');
    }
    return $paramsArray;
  }
  catch(Exception $e) {
    echo 'Message: ' .$e->getMessage();
  }
}//getRequestParams

// Connect to the database, configuration information is stored in
// config.inc.php file
function connectDb() {
	try {
		$dbconn = 'mysql:host=' . DBHOST . ';dbname=' . DBDATA ;
		$db = new PDO($dbconn , DBUSER , DBPASS , array(PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8'));
		// set the error mode to exceptions
		$db->setAttribute(PDO::ATTR_ERRMODE , PDO::ERRMODE_EXCEPTION);
		return $db;
	}// try
	catch(PDOException $e) {
		error_log('message:' . $e->getMessage());
	}// catch
}// connectDb

// Change a string value to float
function changetoFloat($string) {
	if (strlen(trim($string)) != 0)
		return (float)$string;
	return NULL;
}
?>