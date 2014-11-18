<?php

//--------------------- INSTALL OF DATABASE ---------------------------------------------

require("config.inc.php"); // First modify this file. Put your mysql credentials

// Create connection
$conn = mysql_connect("localhost", DBUSER, DBPASS);
mysql_set_charset('utf8',$conn);

if (!$conn){
    die("Connection failed: " . mysqli_connect_error());
} else {
	echo "Connected successfully<br>";
	import_file("ARServiceTables.sql");
}

function import_file($filename){
    if ($file = file_get_contents($filename)){
        foreach(explode(";\r", $file) as $query){
            $query = trim($query);
            if (!empty($query) && $query != ";") {
                $result = mysql_query($query);
				if (!$result) {
					echo ('Invalid query: ' . $query . '<br><br><br>');
				} else {
					echo "query ok<br>";
				}
            }
        }
    }
}
//------------------------------ END OF DB INSTALLER ------------------------------

?>