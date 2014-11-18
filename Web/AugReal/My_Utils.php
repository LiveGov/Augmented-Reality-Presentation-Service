<?php


//------------- Make Directory function -------
function MyMkdir($pathToCreate){
	if (!is_dir($pathToCreate)){

		$resmkdir = mkdir($pathToCreate,0755,true);
		$res_chmod = chmod($pathToCreate, 0755);

		if (!$resmkdir){
			print "<br>fail to make dir";
			print_r ( error_get_last() );
		}else{
			if ($res_chmod)
				print "";
			else {
				print "<br> Directory Permissions not changed, I can not receive your files<br>";
				print_r ( error_get_last() );
			}
		}
	}
}

//------ Delete all files in current working directory ending with $extension -------
function deleteGWDAllfiles($cwd,$extension){
	
 	$files = glob($cwd.'/*.'.$extension); // get all file names
 	foreach($files as $file) // iterate files
 		if(is_file($file))
 			unlink($file);   // delete file
}


// function check var is null
function CheckNull($var, $altern){
	
	if (!is_null($var))
		return $var;
	else
		return $altern;
}


//------- Check if http file exists  ---------------------------------------
function httpfileexists($f){
	$exists = true;
	$file_headers = @get_headers($f);
	if($file_headers[0] == 'HTTP/1.1 404 Not Found')
		$exists = false;

	return $exists;
}

// function check var is exists
function CheckSet($var, $altern){
	
	if (isset($_REQUEST[$var]))
		return mysql_real_escape_string($_REQUEST[$var]);
	else
		return $altern;
}




//Check if 3 is included in 3,5,7,10 
function chCoauth($id='',$idall=''){
	
	$idall_ARR = explode(',',$idall);
	
	foreach ($idall_ARR as $idsole){
		if ($id == $idsole)
			return true;
	}
	
	return false;
}


// Subtract time stamps
function showTime($Time){
		$delta_time = time() - strtotime($Time);
	    $days  = floor($delta_time / (3600 * 24));
		$delta_time %= (3600*24);
		$hours = floor($delta_time / 3600);

		if ($hours < 0)
			$hours = 0;

		$delta_time %= 3600;
		$minutes = floor($delta_time / 60);

		if ($minutes < 0)
			$minutes = 0;

		if ($days < 0){
			$days =  0;
		}
		
		return " ".$days." days";
		
}



// Used by VisRec
function startsWith($haystack, $needle)
{
	return !strncmp($haystack, $needle, strlen($needle));
}

function endsWith($haystack, $needle)
{
	$length = strlen($needle);
	if ($length == 0) {
		return true;
	}
	return (substr($haystack, -$length) === $needle);
}

/*
 *     Delete a directory
*/
class Deleter{
	public static function deleteDir($dirPath) {
		if (! is_dir($dirPath)) {
			throw new InvalidArgumentException("$dirPath must be a directory");
		}
		if (substr($dirPath, strlen($dirPath) - 1, 1) != '/') {
			$dirPath .= '/';
		}
		$files = glob($dirPath . '*', GLOB_MARK);
		foreach ($files as $file) {
			if (is_dir($file)) {
				self::deleteDir($file);
			}
			else {
				unlink($file);
			}
		}
		$succRmdir = rmdir($dirPath);
		//echo 'Delete:'.$dirPath.': '.var_dump($succRmdir);
	}
}

/*
 *    Find if Features are estimated
*/
function featuresAvailable($positives,$negatives,$dbHandle2)
{
	foreach ($positives as $name)
	{
		if(!$dbHandle2->featuresAvailable($name))
			return false;
	}

	foreach ($negatives as $name)
	{
		if(!$dbHandle2->featuresAvailable($name))
			return false;
	}
	return true;
}

?>