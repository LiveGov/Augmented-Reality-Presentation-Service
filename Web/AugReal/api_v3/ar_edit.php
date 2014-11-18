<?php
//header( 'Content-type: application/json; charset=utf-8' );

$action = 'edit';
require 'api_header.php';

//------ if no id of Entity is given then die
$id	= CheckSet('id',0);   // CheckSet is at My_Utils
if ($id==0){
	print "Error code 0"; die();
}

//-------- find author and coauthors of the past -------------
$res_e = $dbHandle->getAREntity($id);
$row = mysql_fetch_array($res_e);

$id_author   = $row['id_author'];
$id_coauthor = $row['id_coauthor'];


if(!$fgmembersite->CheckLogin()){
	die();
} else {
	$login_rname = $_SESSION['name_of_user'];
	$login_id = $_SESSION['id_of_user'];
	$login_permissions = $_SESSION['permissions_of_user'];
}


//--------- Check permissions ----------
$isAbleToEdit = chPerm('|ar_web_editAREntity|', $login_permissions);
$IsTheAuthor   = $login_id == $id_author;
$IsTheCoauthor = chCoauth($login_id, $id_coauthor);

if (($IsTheAuthor || $IsTheCoauthor) && $isAbleToEdit){
} else {
	die();
}

//--------------------------------------
$id_author   = CheckSet('id_author',$row['id_author']);

// From browser
if (isset($_REQUEST['cthrs']))
	$id_coauthor = implode($_REQUEST['cthrs'], ",");
else // From api
	$id_coauthor = CheckSet('id_coauthor',$row['id_coauthor']);

$id_external = CheckSet('id_external',$row['id_external']);
$id_VRec	 = CheckSet('id_VRec',$row['id_VRec']);
$id_app		 = CheckSet('id_app',$row['id_app']);

$androidapp		 = CheckSet('androidapp',$row['androidapp']);
$iphoneapp		 = CheckSet('iphoneapp',$row['iphoneapp']);

$title		 = CheckSet('title',$row['title']);
$titleB		 = CheckSet('titleB',$row['titleB']);
$titleC		 = CheckSet('titleC',$row['titleC']);
$titleD		 = CheckSet('titleD',$row['titleD']);
$description = CheckSet('description',$row['description']);
$descriptionB= CheckSet('descriptionB',$row['descriptionB']);
$descriptionC= CheckSet('descriptionC',$row['descriptionC']);
$descriptionD= CheckSet('descriptionD',$row['descriptionD']);

$ModelsPathAtServer= $_SERVER['DOCUMENT_ROOT'].'/AugReal/Models3D_DB/'.$id.'/';


$trackImRot = CheckSet('trackImRot', $row['trackImRot']);
$trackImScale = CheckSet('trackImScale', $row['trackImScale']);


$author	 = $login_rname;
$latitude   = CheckSet('latitude',$row['latitude']);
$longitude  = CheckSet('longitude',$row['longitude']);

//liaros
$alt = CheckSet('altitude',$row['altitude']);
$alt = floor($alt);
$alt += intval($id)/100000;
$altitude = $alt;
//end-liaros

if ($altitude==0)
	$altitude = 60;

$linkurl		   = CheckSet('linkurl',$row['linkurl']);
$streetnameaddress = CheckSet('streetnameaddress',$row['streetnameaddress']);
$postalcode		= CheckSet('postalcode',$row['postalcode']);
$numberaddress	 = CheckSet('numberaddress',$row['numberaddress']);
$country		   = CheckSet('country',$row['country']);


$Langs = explode(";",$row['Langs']);

$Lang1 = "";
$Lang2 = "";
$Lang3 = "";
$Lang4 = "";


$Langs[0] = CheckSet('Lang1',$Langs[0]);
$Langs[1] = CheckSet('Lang2',$Langs[1]);
$Langs[2] = CheckSet('Lang3',$Langs[2]);
$Langs[3] = CheckSet('Lang4',$Langs[3]);

$Langs = array_filter($Langs);

$Langs = implode(";",$Langs);



$resUpdate = $dbHandle->updateAREntityFull($id,$id_coauthor,$id_external,$id_VRec,$id_app,$title,$titleB,$titleC,$titleD,
		$description,$descriptionB,$descriptionC,$descriptionD,$author,$trackImRot,
		$latitude,$longitude,$altitude,$linkurl,$streetnameaddress,$postalcode,$numberaddress,
		$country,$Langs,$trackImScale,$androidapp,$iphoneapp);



if ($resUpdate)
	print "CODE 1";


// ------------------ UPLOAD FILE SCRIPT ----------------------
// obj --->  "application/x-tgif"
// 3ds --->  "image/x-3ds"				|| ($_FILES["obj"]["type"] == "image/x-3ds") || ($_FILES["obj"]["type"] == "application/octet-stream"
// md2 --->   "application/octet-stream"

MyMkdir($ModelsPathAtServer);

$iM = "1";
$pool=array("1","2","3");

foreach ($pool as $iM){
			
	// Check for format
	if ($_FILES["obj".$iM]["name"]!="") {
		if (strchr($_FILES["obj".$iM]["name"], '.')==".obj" && strcmp($_FILES["obj".$iM]["type"],"application/octet-stream")==0){
			if ($_FILES["obj".$iM]["error"] > 0){
				echo "Error: " . $_FILES["obj".$iM]["error"] . "<br>";
			} else {
				$ModelCertainPath = $ModelsPathAtServer.$iM."/";
				MyMkdir($ModelCertainPath);
				$ModelFileName = "AR_".$id."_".$iM.strrchr($_FILES["obj".$iM]["name"], '.'); // filename . extension

				// Upload
				move_uploaded_file($_FILES["obj".$iM]["tmp_name"], $ModelCertainPath . $ModelFileName);

				// Change mtl lib inside obj file so as to match new name, read the entire string
				$contents=implode("",file($ModelCertainPath.$ModelFileName));

				$fp=fopen($ModelCertainPath.$ModelFileName,'w');
				//replace something in the file string

				$StartI = strpos($contents, "mtllib");
				$EndI   = strpos($contents, '.mtl');
				$ReplSTR   = substr($contents, $StartI+7, $EndI-3-$StartI);
				$FNLength = strpos($ModelFileName, '.');
				$ReplWithFN = substr($ModelFileName, 0, $FNLength).".mtl";
				$new_contents = str_replace($ReplSTR, $ReplWithFN, $contents);

				//now, TOTALLY rewrite the file
				fwrite($fp,$new_contents,strlen($new_contents));
				fclose($fp);

				// Update also the DB models field
				$resUpdateModels = $dbHandle->updateAREntity($id, 'models',$iM);
			}
		} else{ // Wrong format
			echo "Object coordinates file was not updated because due to wrong format (".
					$_FILES["obj".$iM]["type"].",".
					$_FILES["obj".$iM]["name"].",".
					strchr($_FILES["obj".$iM]["name"],'.').")<br/>";
			echo "Allowed formats: obj";
		}
	}

	// -------------------- Material File --------------------
	if ($_FILES["mtl".$iM]["name"]!=""){
		if (strchr($_FILES["mtl".$iM]["name"], '.')==".mtl" && strcmp($_FILES["mtl".$iM]["type"],"application/octet-stream")==0){
			if ($_FILES["mtl".$iM]["error"] > 0){
				echo "Error: " . $_FILES["mtl".$iM]["error"] . "<br>";
			} else {
				$ModelCertainPath = $ModelsPathAtServer.$iM."/";
				MyMkdir($ModelCertainPath);

				$ModelMaterialFileName = "AR_".$id."_".$iM.strrchr($_FILES["mtl".$iM]["name"], '.');
				move_uploaded_file($_FILES["mtl".$iM]["tmp_name"], $ModelCertainPath.$ModelMaterialFileName);

				// Change '\' to '/' (linux) inside mtl file
				$contents=implode("",file($ModelCertainPath.$ModelMaterialFileName));

				$fp=fopen($ModelCertainPath.$ModelMaterialFileName,'w');
				//replace something in the file string
				$new_contents = str_replace('\\', '/', $contents);

				//now, TOTALLY rewrite the file
				fwrite($fp,$new_contents,strlen($new_contents));
				fclose($fp);
			}
		} else {
			echo "Model Texture file was not updated because due to wrong format (".
					$_FILES["mtl".$iM]["type"].",".
					$_FILES["mtl".$iM]["name"].",".
					strchr($_FILES["mtl".$iM]["name"],'.').")<br />";
			echo "Allowed formats: mtl";
		}
	}

	// -------------------- Texture File --------------------
	if ($_FILES["zip".$iM]["name"]!=""){
		if (($_FILES["zip".$iM]["type"] == "application/x-zip-compressed") && strchr($_FILES["zip".$iM]["name"], '.')==".zip"){
			if ($_FILES["zip".$iM]["error"] > 0) {
				echo "Error:".$_FILES["zip".$iM]["error"]."<br>";
			} else {
				$ModelCertainPath = $ModelsPathAtServer.$iM."/";
				MyMkdir($ModelCertainPath);
				$ModelTextureFileName = "AR_".$id."_".$iM. strrchr($_FILES["zip".$iM]["name"],'.');
				move_uploaded_file($_FILES["zip".$iM]["tmp_name"], $ModelCertainPath.$ModelTextureFileName);

				$zip = new ZipArchive;
				$res = $zip->open($ModelCertainPath.$ModelTextureFileName);
				if ($res === TRUE) {
					$zip->extractTo($ModelCertainPath);
					$zip->close();
					// unlink($ModelsPathAtServer.$ModelTextureFileName); // Do not delete the zip because it is needed by mobile
				} else {
					echo 'textures unzip folder:failed';
				}
			}
		} else {
			echo "Model Texture file was not updated because due to wrong format (".
					$_FILES["zip".$iM]["type"] .",".
					$_FILES["zip".$iM]["name"].",".
					strchr($_FILES["zip".$iM]["name"],'.').")<br>";
			echo "Allowed formats: zip";
		}
	}

	//------------ METAIO FORMAT: COMPRESS OBJ, MTL, FOLDER_TEXTURE to a single file ------------
	$OBJ =  "AR_".$id."_".$iM.".obj";
	
	if (file_exists($ModelsPathAtServer.$iM."/".$OBJ)){ // if  OBJ exists then start
		// OPEN ZIP
		$zipArchive = new ZipArchive();
		$zipFileName = "AR_".$id."_".$iM."junaio.zip";
		
		if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') 
			$DS = "\\";
		else 
			$DS = "/";
		
		$zipArchive->open($ModelsPathAtServer.$DS.$iM.$DS.$zipFileName, ZIPARCHIVE::OVERWRITE);

		
		
		// ADD OBJ
		$zipArchive->addFile($ModelsPathAtServer.$iM.$DS.$OBJ, $OBJ);
			
		// ADD MTL if EXISTS
		$MTL =  "AR_".$id."_".$iM.".mtl";
		if (file_exists($ModelsPathAtServer.$iM.$DS.$MTL))
			$zipArchive->addFile($ModelsPathAtServer.$iM.$DS.$MTL, $MTL);

		// ADD folder of TEXTURE images
		if (is_dir($ModelsPathAtServer.$iM.$DS."images")){
			$zipArchive->addEmptyDir('images');
			$files = new RecursiveIteratorIterator(
					new RecursiveDirectoryIterator($ModelsPathAtServer.$iM.$DS."images"),
					RecursiveIteratorIterator::SELF_FIRST);

			foreach ($files as $file){
				if( in_array(substr($file, strrpos($file, $DS)+1), array('.', '..')) )
					continue;
				$zipArchive->addFile($file, "images".$DS.basename($file) );
			}
		}
		$zipArchive->close();
		
		$zipfullpath = $ModelsPathAtServer.$iM.$DS.$zipFileName;
		
		$hashjunaio = hash_file('md5', $zipfullpath);
		$dbHandle->insertARHash($id, $iM, $zipFileName, $hashjunaio, date('Y-m-d H:i:s'), filesize(		$ModelsPathAtServer.$iM.$DS.$zipFileName) );
	}
	
	//------------- Layar 3D Convert format  ---------------------------------
// 	java -jar Layar3DModelConverter.jar [options] <inputfile.obj>
	
// 	-o : Output file. If omitted, the filename will be based on input filename.
// 	-d : Drop normals. Let the client calculate smooth normals.
// 	-f : Calculate face normals. Use normals per face causing hard edges in the model.
// 	-m : Optimize materials. Groups the faces by material to improve rendering performance.
// 	-w : Make diffuse color white. Used to solve dark textured models.
// 	-t : Load the given image file as texture on the material(s).
// 	-x : Rotate model 90 degrees around the X axis. Can be used to fix orientation of .obj file.
// 	-X : Rotate model -90 degrees around the X axis. Can be used to fix orientation of .obj file.
// 	-h : Show this help message.

	if (file_exists($ModelsPathAtServer.$iM."/".$OBJ)){ // if  OBJ exists then start
		$FILE_INPUT_JAR = $ModelsPathAtServer.$iM."/".$OBJ;
		
		$FILE_OUTPUT_JAR = str_replace('obj','l3d',$FILE_INPUT_JAR);
		
		$commandjava = "java -jar Layar3DModelConverterCLI.jar -o ".$FILE_OUTPUT_JAR." ".$FILE_INPUT_JAR." 2>&1";
		$res = exec($commandjava, $output, $return_var);
		
		$Layar_converter_message = "";
		if ($return_var>0)
			$Layar_converter_message .= "error 1: No arguments passed to java";
		if (count($output)>0){
			$Layar_converter_message .= "error 2: File not found";
			print_r($output);	
		}
	}

	// ------- MODEL SCREENSHOT ----
	if ($_FILES["Image".$iM]["name"]!=""){
		MyMkdir($ModelsPathAtServer.$iM."/");
		$destimage = $ModelsPathAtServer.$iM."/AR_".$id."_".$iM.strrchr($_FILES["Image".$iM]["name"],'.');
		move_uploaded_file($_FILES["Image".$iM]["tmp_name"], $destimage);
		
		$hashjpg = hash_file('md5', $destimage);
		$dbHandle->insertARHash($id, $iM."01", "AR_".$id."_".$iM.strrchr($_FILES["Image".$iM]["name"],'.'), $hashjpg, 
				date('Y-m-d H:i:s'), filesize($destimage));
	}
}

//--------- Images ------------
//-  BACKGROUND --- and icon marker create
if ($_FILES["Image0"]["name"]!=""){
	MyMkdir($ModelsPathAtServer);
	$image_final_pos = $ModelsPathAtServer."AR_".$id.strrchr($_FILES["Image0"]["name"],'.');
	move_uploaded_file($_FILES["Image0"]["tmp_name"], $image_final_pos);

	$resUpdateJPG = $dbHandle->updateAREntity($id, 'jpg','1');

	// Create marker icon
	$image0 = imagecreatefromjpeg($image_final_pos);
	$new_image_icon = imagecreatetruecolor(40, 40);
	imagecopyresampled($new_image_icon, $image0, 0, 0, 0, 0, 40, 40, imagesx($image0), imagesy($image0));
	$iconurl = str_replace("AR","markerAR",$image_final_pos);
	imagejpeg($new_image_icon, $iconurl, 95);
}

// ------- Image Tracking ----
if (isset($_FILES["ImageTracking"]["name"])){
	MyMkdir($ModelsPathAtServer."1/");
	move_uploaded_file($_FILES["ImageTracking"]["tmp_name"], $ModelsPathAtServer."1/".$id."_1.png");
}


Log2File($caller,SCUSER,SCPASS,SCCUSTOMERID,$_SESSION['id_of_user'],date('Y-m-d H:i:s'),'',$action.': '.round($_REQUEST['id']));

// REDIRECTOR to Human_AR_Edit.php (in case it is not called from api itself)
if (!isset($hs['Authorization']))
	echo "<script type='text/javascript'>window.location = document.referrer;</script>";


?>
