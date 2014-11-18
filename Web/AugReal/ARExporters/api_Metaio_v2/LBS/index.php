<?php 
header('Content-type: application/xml;');
/**
 * LBS:
 * Parameters:
 * 		lang: language ISO-639-1, en (English), es (Spanish), eu (Basque)
 *		debug: true or false (if true then send a certain test xml (very simple))
 *		idapp: select application framework from set 1,2,3,4 by binary switching, i.e.
 *			1000 = (1), 1100 = (1,2), 1101 = (1,2,4)
 *	 SW3d: Boolean switch for 3D models. Default: True.
 *		m:  maximum number of Entities
 *		l: location (latitude, longitude), e.g. l=40.567,22.999
 *		distthres: distance from user threshold (meters): Only objects closer to user below this threshold will be send
 *		device: String "android" or "iphone"
 */

//--------- authorization header
$action = 'LBS';
require '../../../My_Utils.php'; // MyMkdir, CheckSet,LogToFile
require '../../../KeepLogFunctions.php';// chCoauth

//-------- data libraries ------
require '../../../DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler(false);

$version_path = "/";

//--------- Lang ----------

if (isset($_GET["lang"])){
	// Gordexola native app
	$lang = $_GET["lang"];
} else {
	// Junaio 
	$headers = apache_request_headers();

	$lang = "en";
	if (isset($_REQUEST["lang"]))
		$lang = $_REQUEST["lang"];
	else {
		foreach ($headers as $header => $value)
			if ($header=='Accept-Language')
			$lang = substr($value,0,2);
	}
}
//----------------------------------------------
if(!empty($_GET["debug"])){
	if ($_GET["debug"]=="true"){
		$path = getcwd ()."/debugarel.xml";
		$str = file_get_contents($path);
		echo $str;
		die();
	}
}

//------------- Select by id   1000 = YUCAT, 0100= BIZ,  ..., 1100 = YUCAT and BIZ-----------------
if(isset($_REQUEST["idapp"])){
	$binlex=$_REQUEST["idapp"];
	$appsetArr = array();
	for($i = 0; $i < strlen($binlex); $i++)
		if($binlex[$i]==1)
		$appsetArr[]=($i+1);

	$appset = implode(",",$appsetArr);
	$arel = NULL;
	$upscale = 1;
	$xrot = 0;
} else {
	$appset = "2"; //1,2,3,4"; // From Junaio Browser
	//$arel = "http://".$_SERVER['SERVER_NAME']."/api_Metaio_v2/LBS/arel/index.html";
	$arel = NULL;
	$upscale = 300;
	$xrot = 90;
}
//--------------------------------------------------------------------------------------
$res = $dbHandle->getAREntitiesAllFields($appset);

while($e=mysql_fetch_assoc($res))
	$db_output[]=$e;

//------------ import AREL --------------
require_once '../ARELLibrary/arel_xmlhelper.class.php';  // The library

//--------- No 3D models ----------
$SW3d = true;
if (isset($_REQUEST["SW3d"]))
		$SW3d = false;

//--------- Max number of 2D Entities---------------
if (!empty($_GET["m"]))
	$maxE = $_GET["m"];

//-------------------------------------------------
$serverpath_data =  "http://".$_SERVER['SERVER_NAME']."/AugReal/Models3D_DB/";      // The path



ArelXMLHelper::start(NULL, $arel, ArelXMLHelper::TRACKING_GPS); //use the Arel Helper to start the output with arel

//--------------- if altitude is not zero then use its value as id to select a single entity -----
$mid = 0;

if(!empty($_GET['identity'])){
	$userposition = $_GET['identity'];
	$filter = intval($userposition);
	$mid = $filter;
} else if(!empty($_GET['l'])){
	$userposition = explode(",", $_GET['l']);
}

$NEntities = count($db_output);

if($mid>0){
	for ( $i=0; $i < $NEntities; $i++){
		$currEntity = $db_output[$i];
		if ($mid == $currEntity['id']){
			$Entity = $db_output[$i];
		}
	}

	$db_output = null;
	$db_output[] = $Entity;
}

// Distance thres
if(!empty($_GET['distthres']))
	$distthres = $_GET['distthres'];
else
	$distthres = 5000000;



//--------------------- Print result ---------------------------------------
$i= 0;
foreach ($db_output as $Entity){

	if (isset($maxE))
		if ($i < $maxE)
			$i ++;
	else
		break;

	$id = $Entity['id'];

	// title, description
	$LangsRegistry = explode(';',$Entity['Langs']);
	for ($i=0; $i< count($LangsRegistry); $i++){
		if ($LangsRegistry[$i] == $lang){
			$let = '';
			if ($i==1) $let='B';
			if ($i==2) $let='C';
			if ($i==3) $let='D';
			$title = CheckNull($Entity['title'.$let],"N/A");
			$descr = CheckNull($Entity['description'.$let],"N/A");
		}
	}
	
	
	if ($lang=="es" || $lang=="eu"){
		$downloadCaption = "Descargue la app oficial";
	} else {
		$downloadCaption = "Download the official app";
	} 

	// GPS
	$lat = CheckNull($Entity['latitude'],40.5678);
	$long = CheckNull($Entity['longitude'],23.0012);
	$alt = CheckNull($Entity['altitude'],0);

	// distance
 	if (isset($userposition[0]))
 		$distance2user = 100000 * sqrt(($lat - $userposition[0])*($lat - $userposition[0])
 				+ ($long - $userposition[1])*($long - $userposition[1]));

	// icon
	$icon = $serverpath_data.$Entity['id']."/1/AR_".$Entity['id']."_1.jpg";
	
	
	if (!httpfileexists($icon))
		$icon = null;

	
	
	//---------- 3D models CHECK IF OBJJUNAIO.zip FILE EXISTs ------
	if ($SW3d){
		
		$file = $serverpath_data.$Entity['id']."/1/AR_".$Entity['id']."_1junaio.zip";

		if (httpfileexists($file)){
			if (isset($distance2user))
				if ($distance2user > $distthres) // if it is too far do not send 3d models
					continue;
			 
			
			//3D static obj
			$oObject = ArelXMLHelper::createLocationBasedModel3D(
					"3D".$id, $title, $file, //model all in zip
					NULL, //texture
					array($lat,$long,$alt), array($upscale,$upscale,$upscale), //scale
					new ArelRotation(ArelRotation::ROTATION_EULERDEG, array($xrot,0,0)), //rotation
				$icon);  // icon for the map

			$oObject->addParameter("type","LBS 3D");
			$oObject->addParameter("idEntity",$id);

			//------ Number of 3d models for this case --------
			$iM = 1;
			for ($i=2; $i <= 3; $i++)
				if (httpfileexists($serverpath_data.$id."/".$i."/AR_".$id."_".$i."junaio.zip"))
						$iM = $i;
			
			
			
			$oObject->addParameter("NModels3D",$iM);
			//-------------------------------------------------

			ArelXMLHelper::outputObject($oObject);
		}
	}// ------------ end of 3d ----------------------------

	//----------------------- POI -------------------------------------------
	if(!empty($_GET["debug"]))
		if ($_GET["debug"]=="true")
			$icon = null;

	$nativeappbutton = null;
	if (isset($_GET['device'])){
		if ($_GET['device']=='iphone' || $_GET['device']=='ipad'){
			if ($Entity['iphoneapp'])
				$nativeappbutton = array($downloadCaption, "url","itms-apps://itunes.apple.com/app/".$Entity['iphoneapp']);
		} else {
			if ($Entity['androidapp'])
				$nativeappbutton = array($downloadCaption, "url","market://details?id=".$Entity['androidapp']);
		}
	} else {
		$nativeappbutton = null;
	}
	
	
	if ($nativeappbutton)
		$oObject = ArelXMLHelper::createLocationBasedPOI(
			$id, $title, array($lat,$long,$alt), $icon,  //thumb for the popup//
			$icon,  //icon  for the billboard //
			$descr, array($nativeappbutton));
	else 
		$oObject = ArelXMLHelper::createLocationBasedPOI(
				$id, $title, array($lat,$long,$alt), $icon,  //thumb for the popup//
				$icon,  //icon  for the billboard //
				$descr);
		

	$oObject->addParameter("type","LBS BILLBOARD");
	$oObject->addParameter("idEntity",$id);
	$oObject->addParameter("NModels3D",0);
	
	// Visibility distance
	// $oObject->setMaxDistance(20000000);
	ArelXMLHelper::outputObject($oObject);     //output the object
}

ArelXMLHelper::end();  //end the output
?>