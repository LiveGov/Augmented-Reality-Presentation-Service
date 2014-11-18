<?php
header('Content-type: application/xml; charset=utf8'); 
/** 
 * IBS channel  
 * Parameters:
 * 		lang: language ISO-639-1, en (English), es (Spanish), eu (Basque)
 * 		idapp: select application framework from set 1,2,3,4 by binary switching, i.e.
 *           1000 = (1), 1100 = (1,2), 1101 = (1,2,4)
 *		device: String "android" or "iphone"
 */

$serv    = $_SERVER['SERVER_NAME'];
$path    = pathinfo( $_SERVER['PHP_SELF'] );
$urlpath = $path['dirname'];

//--------- authorization header ----------
$action = 'IBS';
require '../../../My_Utils.php'; // MyMkdir, CheckSet,LogToFile
require '../../../KeepLogFunctions.php'; // chCoauth

//-------- data libraries ------
require '../../../DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler(false);

//-------------------------------------
$serverpath_data =  "http://".$serv."/AugReal/Models3D_DB/";

$rootpath = $_SERVER['DOCUMENT_ROOT'];
$version_path = "/";

//--------- Lang ----------
$headers = apache_request_headers();

$lang = "en";
if (isset($_REQUEST["lang"])){
 	$lang = $_REQUEST["lang"];
 	
//  	if ($lang!='en' || $lang!='es' || $lang!='eu')
//  		$lang = 'en';
}else {
 	foreach ($headers as $header => $value)
 		if ($header=='Accept-Language')
 			$lang = substr($value,0,2);
}

//------------- Select by id   1000 = YUCAT, 0100= BIZ,  ..., 1100 = YUCAT and BIZ-----------------
if(isset($_REQUEST["idapp"])){
	$binlex=$_REQUEST["idapp"];
	$appsetArr = array();
	for($i = 0; $i < strlen($binlex); $i++)
		if($binlex[$i]==1)
			$appsetArr[]=($i+1);

	$appset = implode(",",$appsetArr);
	$upscale = 1;
} else {
	$appset = "2"; // From Junaio Browser
	$upscale = 10;
}

//--------------------------------------------------------------------------------------
$res = $dbHandle->getAREntitiesAllFields($appset);

while($e=mysql_fetch_assoc($res))
	$db_output[]=$e;

//--------- Load AREL library ----------
require_once '../ARELLibrary/arel_xmlhelper.class.php';  // The library

// ids of availabe entitities
$ids = array();
foreach ($db_output as $Entity)
	array_push($ids, $Entity['id']);

// construct TrackingXML.zip
$filename    = GenerateTrackingXML($ids,$rootpath);
$zipfilename = ZipTrackXMLwithPatterns($filename,$ids,$rootpath);
trackingZipDBupd($appset,$rootpath,$zipfilename,$dbHandle);

// Start ARELXMLHelper
$trackingXML="http://".$serv.dirname($_SERVER['PHP_SELF'])."/".basename($zipfilename);
ArelXMLHelper::start(NULL, NULL, $trackingXML);

//------------ Loop all entities and check if tracking pattern is available  ---------------------
$count = 0;
foreach ($db_output as $Entity){

	$id = $Entity['id'];

	//- Check if tracking image exists
	if (is_file($rootpath.'/AugReal/Models3D_DB/'.$id.'/1/'.$id.'_1.png')){

		$trackImRot = CheckNull($Entity['trackImRot'],0);
		$trackImScale = CheckNull($Entity['trackImScale'],30);
		
		$upscale_processed = $upscale * (30 / $trackImScale );
		
		$count++;
		$obj = ArelXMLHelper::createGLUEModel3D($count,
				$serverpath_data.$id."/1/AR_".$id."_1junaio.zip",
				null, array(0,0,0), array($upscale_processed, $upscale_processed, $upscale_processed),
				new ArelRotation(ArelRotation::ROTATION_EULERDEG, array(0,$trackImRot,0)), $count);
		
		if ($lang=="es" || $lang=="eu"){ 
			$title = CheckNull($Entity['titleB'],"N/A"); 
			$descr = CheckNull($Entity['descriptionB'],"N/A");
			$downloadCaption = "Descargue la app oficial";
		} else {
			$title = CheckNull($Entity['title'],"N/A");
			$descr = CheckNull($Entity['description'] ,"N/A");
			$downloadCaption = "Download the official app";
		}

		$obj->setTitle($title);
		$obj->addParameter("type","IBS");
		$obj->addParameter("idEntity",$id);
		$obj->setThumbnail($serverpath_data.$id."/1/AR_".$id."_1.jpg");
		
		//------ Number of 3d models for this case --------
		$iM=1;
		for ($i=2; $i <= 3; $i++)
			if (httpfileexists($serverpath_data.$id."/".$i."/AR_".$id."_".$i."junaio.zip"))
					$iM = $i;

		$obj->addParameter("NModels3D",$iM);

		//	------ Popup information ---------
		$obj_pop = new ArelPopup();
		$obj_pop->setDescription($descr);

		$nativeappbutton = null;
		
		if (isset($_GET['device'])){
		
			if ($_GET['device']=='iphone' || $_GET['device']=='ipad'){
				if ($Entity['iphoneapp'])
					$nativeappbutton = "itms-apps://itunes.apple.com/app/".$Entity['iphoneapp'];
			} else {
				if ($Entity['androidapp'])
						$nativeappbutton = "market://details?id=".$Entity['androidapp'];
			}
		} else {
		}

		if ($nativeappbutton)
			$obj_pop->addButton(array($downloadCaption,"opinionButton",$nativeappbutton));	
				
		$obj->setPopup($obj_pop);

		// 	--- Add object to list -----
		ArelXMLHelper::outputObject($obj);
	}
}

ArelXMLHelper::end();

//------------- Finish loop -------------
// Make the XML for tracking with snippet method
function GenerateTrackingXML($ids,$rootpath){

	// First delete the old ones
	deleteGWDAllfiles(getcwd(), 'zip');	

	$filename = getcwd().'/tracking.xml'; // .(time().rand()). !! new metaio does not want big names !
	$f = fopen($filename,'w');

	$Total =
	"<?xml version='1.0' encoding='UTF-8'?>\n\t<TrackingData>
	<Sensors>
		<Sensor type='FeatureBasedSensorSource' Subtype='FAST'>
			<SensorID>FeatureBasedSensorSource_0</SensorID>
			<Parameters>
				<FeatureDescriptorAlignment>regular</FeatureDescriptorAlignment>
				<MaxObjectsToDetectPerFrame>5</MaxObjectsToDetectPerFrame>
				<MaxObjectsToTrackInParallel>1</MaxObjectsToTrackInParallel>
				<SimilarityThreshold>0.7</SimilarityThreshold>
			</Parameters>";
	
	//--------- Loop define images -----
	foreach ($ids as $i){
		if (is_file($rootpath.'/AugReal/Models3D_DB/'.$i.'/1/'.$i.'_1.png')){
			$Total  = $Total.
			"\n\t\t\t<SensorCOS>
				<SensorCosID>".$i."</SensorCosID>
				<Parameters>
					<ReferenceImage widthMM='400' heightMM='400'>".$i."_1.png</ReferenceImage>
				</Parameters>
			</SensorCOS>";
		}
	}
	//---------------------------------	
	$Total   = $Total."\n\t\t</Sensor>\n\t</Sensors>";
	$Total   = $Total."\n\t<Connections>";
	
	//-------- Loop Define Coordinate Systems -----------
	$count = 0;
	for ($COS_ID = 1; $COS_ID < count($ids)+1; $COS_ID++){
		$id = $ids[$COS_ID-1];
		
		if (is_file($rootpath.'/AugReal/Models3D_DB/'.$id.'/1/'.$id.'_1.png')){
			$count ++ ;
			$Total = $Total."\n\t\t<COS>\n\t\t\t<Name>COS_".$count."</Name>
			<Fuser type='SmoothingFuser'>
				<Parameters>
					<KeepPoseForNumberOfFrames>2</KeepPoseForNumberOfFrames>
					<GravityAssistance></GravityAssistance>
					<AlphaTranslation>0.8</AlphaTranslation>
					<GammaTranslation>0.8</GammaTranslation>
					<AlphaRotation>0.5</AlphaRotation>
					<GammaRotation>0.5</GammaRotation>
					<ContinueLostTrackingWithOrientationSensor>false</ContinueLostTrackingWithOrientationSensor>
				</Parameters>
			</Fuser>
			<SensorSource>
				<SensorID>FeatureBasedSensorSource_0</SensorID>
				<SensorCosID>".$id."</SensorCosID>
				<HandEyeCalibration>
					<TranslationOffset>
						<x>0</x>
						<y>0</y>
						<z>0</z>
					</TranslationOffset>
					<RotationOffset>
						<x>0</x>
						<y>0</y>
						<z>0</z>
						<w>1</w>
					</RotationOffset>
				</HandEyeCalibration>
				<COSOffset>
					<TranslationOffset>
						<x>0</x>
						<y>0</y>
						<z>0</z>
					</TranslationOffset>
					<RotationOffset>
						<x>0</x>
						<y>0</y>
						<z>0</z>
						<w>1</w>
					</RotationOffset>
				</COSOffset>
			</SensorSource>
		</COS>";
		}
	}
	//-----------------------------------------------------------
		
	$Total   = $Total."\n\t</Connections>\n</TrackingData>";
	
	//----------- Strip garbage ---------------
	$Total = str_replace(array("\r",""),"",$Total); // remove newlines
	$out = fwrite($f,$Total);
	fclose($f);
	
	// Touch the file and change its creation date so as to generate the same zip file
	if (!touch($filename, 0)) {
		echo "Can not change date of Tracking.xml";
	} 
	
	return $filename;
}


//--------- zip the xml + patterns -------------------- 
function ZipTrackXMLwithPatterns($filename,$ids,$rootpath){
	
	$zip = new ZipArchive();
	$zipfilename = getcwd().'/'.basename($filename, ".xml");
	$zipfilename = $zipfilename.'.zip';
	
	if ($zip->open($zipfilename, ZipArchive::CREATE) === TRUE) {
		
		$zip->addFile($filename, 'TrackingXML.xml');
		
 		foreach ($ids as &$id)
 			if (is_file($rootpath.'/AugReal/Models3D_DB/'.$id.'/1/'.$id.'_1.png'))
 				$zip->addFile($rootpath.'/AugReal/Models3D_DB/'.$id.'/1/'.$id.'_1.png', $id.'_1.png');
 		
		$zip->close();
				
		if(is_file($filename) == TRUE){
			chmod($filename,0777);
			//chown($filename,"root");
			
			if (!@unlink($filename)){
				//print "xml NOT removed";
			}
		}
		return $zipfilename;
	} else {
// 		echo '<br>failed to make TrackingXML.zip<br>';
// 		print_r( error_get_last() );
	}
}

function trackingZipDBupd($appid, $rootpath,$filename,$dbHandle){
	$hashstr = hash_file('md5', $filename);
	$dbHandle->insertARHash($appid, 'tracking', $filename, $hashstr, date('Y-m-d H:i:s'), filesize($filename));
}

?>