<?php

require_once('config.inc.php'); // database configuration.

class DatabaseHandler{
 
    
	
	private $query = '';
	private $dblink;
	const _DBNAME = 'augreal';
	const _AREntities_TABLE = 'AugReal_ARMain';
	const _ARQuestions_TABLE = 'AugReal_ARQuestions';
	const _ARApps = 'AugReal_ARApp';
	const _IMAGE_TABLE = 'AugReal_ARImages';
	const _MODEL_TABLE = 'AugReal_ARModels';
	const _FILEHASHES_TABLE = 'AugReal_ARFileHashes';
	
	
	// Metaio does not want utf8 whereas web-browsers want
	function __construct($utf8SW=true)
	{
		$this->establishLink($utf8SW);
	}


	function establishLink($utf8SW)
	{
		$this->dblink = mysql_connect('localhost', DBUSER, DBPASS );
		if ($utf8SW)
			mysql_set_charset('utf8',$this->dblink);
		
		if(!$this->dblink)
		{
			die(mysql_error($this->dblink));
		}
		if(!mysql_select_db(self::_DBNAME,$this->dblink))
		{
			die(mysql_error($this->dblink));
		}
	}
	
	// ---------- DELETE AR Entity --------------
	function deleteAREntity($id){
		
		$this->query = "DELETE FROM ".self::_AREntities_TABLE." WHERE id=".$id;
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			return false;
		}
			
		return true;
	}

	//-------- GET File Sizes for all id of AREntities belong to a certain appid --------------
	function getFilesSizesForAppId($AppId){
		$this->query = "SELECT id FROM ".self::_AREntities_TABLE." WHERE id_app IN (".$AppId.")";
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		} else {
			$totalSize = $this->getFileSizeTracking($AppId); // Tracking.zip size
			
			while($arEntity = mysql_fetch_assoc($result)){
				$res2 =  $this->getFilesSizes($arEntity['id'].'.%', $AppId);
				while($fsize = mysql_fetch_assoc($res2)){
					$totalSize += $fsize['size'];
				}
			}
			print round($totalSize/1000);
		}
	
	}

	//-------- GET File Sizes --------------
	// AR Entities
	function getFilesSizes($ids, $AppId=0){
		$trackingSTR = ".tracking";
		$this->query = "SELECT size FROM ".self::_FILEHASHES_TABLE." WHERE id LIKE '".$ids."'";
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		} else {
			return $result;
		}
	}
	
	// Tracking.zip
	function getFileSizeTracking($AppId){
		$trackingSTR = $AppId.".tracking";
		$this->query = "SELECT size FROM ".self::_FILEHASHES_TABLE." WHERE id LIKE '".$trackingSTR."'";
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		} else {
			$fsize = mysql_fetch_assoc($result);
			return $fsize['size'];
		}
	}
	
	
	//--------- GET HASH --------
	function getARHash($filename=null, $id=null){
		
		if ($filename)
			$this->query = "SELECT hash FROM ".self::_FILEHASHES_TABLE." WHERE filename='".$filename."'";
		else if ($id)
			$this->query = "SELECT hash FROM ".self::_FILEHASHES_TABLE." WHERE id='".$id."'";
	
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		} else {
			$a = mysql_fetch_assoc($result);
			return $a['hash'];
		}
	}
	
	//--------- INSERT HASH --------
	function insertARHash($id, $idm, $filename, $hashstr, $date, $size){
	
		$this->query = "INSERT INTO ".self::_FILEHASHES_TABLE." (id,filename,hash,date,size) VALUES ('"
						.$id.".".$idm."','".$filename."','".$hashstr."','".$date."','".$size."') ON DUPLICATE KEY UPDATE ".
		"hash='".$hashstr."',date='".$date."',size='".$size."'";
		
		
		if(!mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return false;
		}
	
		return true;
	}
	
	// ---------- GET AR APPS --------------
	function getARApps(){
	
		$this->query = "SELECT * FROM ".self::_ARApps;
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
		
		return $result;
	}
	
	//----------- INSERT ENTITY -----------
	function insertAREntity($login_id, $login_rname,$lat,$lon){
		
		$this->query = "INSERT INTO ".self::_AREntities_TABLE.
		"(title,id_author,description,author,latitude,longitude,id_app) VALUES ('MyTitle','"
				.$login_id."','My Description','".$login_rname."','".$lat."','".$lon."','0')";
		if(!mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return false;
		}
		
		return true;
	}
	
	
	//----------- INSERT QUESTION -----------
	function insertARQuestion($question,$answer1,$answer2,$answer3,$answer4,$answer5,$answer6,$answer7,$answer8){
		
		$this->query = "INSERT INTO ".self::_ARQuestions_TABLE.
		"(question,answer1,answer2,answer3,answer4,answer5,answer6,answer7,answer8) VALUES ('"
				.$question."','".$answer1."','".$answer2."','".$answer3."','".$answer4."','"
								.$answer5."','".$answer6."','".$answer7."','".$answer8."')";
		if(!mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return false;
		}
		
		return true;
	}
	
	//-------------- Edit Question ------
	function updateARQuestion($id_q,$question,$answer1,$answer2,$answer3,$answer4,$answer5,$answer6,$answer7,$answer8){
	
		$this->query = "UPDATE ".self::_ARQuestions_TABLE.
		"SET "."(question,answer1,answer2,answer3,answer4,answer5,answer6,answer7,answer8) VALUES ('"
				.$question."','".$answer1."','".$answer2."','".$answer3."','".$answer4."','"
						.$answer5."','".$answer6."','".$answer7."','".$answer8."') WHERE id_q=".$id_q;
		
		if(!mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return false;
		}
	
		return true;
	}
	
	//----------- Update an ENTITY field -----------
	function updateAREntity($id, $field,$field_value){
	
		$this->query = "UPDATE ".self::_AREntities_TABLE." SET ".$field."=".$field_value." WHERE id=".$id;
		if(!mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return false;
		}
		return true;
	}
	
	//-------- Update multiple fields of an Entity --------------
	function updateAREntityFull($id,$id_coauthor,$id_external,$id_VRec,$id_app,$title,$titleB,$titleC,$titleD,
			$description,$descriptionB,$descriptionC,$descriptionD,$author,$trackImRot,
			$latitude,$longitude,$altitude,$linkurl,$streetnameaddress,$postalcode,$numberaddress,
			$country,$Langs,$trackImScale,$androidapp,$iphoneapp){   // author is the realname
	
		$this->query = "UPDATE ".self::_AREntities_TABLE." SET id_coauthor='$id_coauthor
		',id_external='$id_external',id_VRec='$id_VRec',id_app='$id_app
		',title='$title',titleB='$titleB',titleC='$titleC',titleD='$titleD
		',description='$description',descriptionB='$descriptionB',descriptionC='$descriptionC',descriptionD='$descriptionD
		',author='$author',trackImRot='$trackImRot',latitude='$latitude',longitude='$longitude',  altitude='".strval($altitude)."',linkurl='$linkurl
		',streetnameaddress='$streetnameaddress',postalcode='$postalcode',numberaddress='$numberaddress',country='$country
		',Langs='$Langs',trackImScale='$trackImScale',androidapp='$androidapp',iphoneapp='$iphoneapp' WHERE id=".$id;
	
		if(!mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return false;
		}
	
		return true;
	}
	
	//----------- GET ALL AR ENTITIES ---------------
	function getAREntities($STR_APPS){
		$this->query = "SELECT id, id_author, id_coauthor, title, description, author, jpg, latitude, longitude, altitude, date FROM ".
					self::_AREntities_TABLE." WHERE id_app IN (".$STR_APPS.") ORDER BY id DESC";
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	//----------- GET AR ENTITIES IBS fields - 
	function getAREntitiesIBSFields($STR_APPS){
		$this->query = "SELECT id,trackImRot,trackImScale FROM ".self::_AREntities_TABLE." WHERE id_app IN (".$STR_APPS.") ORDER BY id ASC";
	
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	//----------- GET ALL AR ENTITIES - (used also for IBS)--------------
	function getAREntitiesAllFields($STR_APPS){
		$this->query = "SELECT * FROM ".self::_AREntities_TABLE." WHERE id_app IN (".$STR_APPS.") ORDER BY id ASC";
		
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	//----------- GET ALL AR ENTITIES - (used also for IBS)--------------
	function getAREntitiesFields4Wikitude($STR_APPS){
		$this->query = "SELECT id,title,titleB,titleC,". 
						"description,descriptionB,". 
						"longitude,latitude,androidapp,iphoneapp FROM ".
						self::_AREntities_TABLE." WHERE id_app IN (".$STR_APPS.") ORDER BY id DESC";
		
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}

		return $result;
	}


	//----------- GET A Single AR ENTITY ---------------
	function getAREntity($id){
		$this->query = "SELECT id,id_author,id_coauthor,id_external,id_app,id_VRec,title,titleB,titleC,titleD,description,descriptionB,".
						"descriptionC,descriptionD,author,models,jpg,trackImRot,linkurl,latitude,longitude,
						altitude,date,streetnameaddress,numberaddress,postalcode,country,Langs,trackImScale,androidapp,iphoneapp FROM ".
						self::_AREntities_TABLE." WHERE id=".$id;
		
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	

	//---------- Get Entities in a Rectangle -------
	function getAREntitiesRect($y0down,$y0up,$x0down,$x0up,$id_app=null){
		
		$this->query = "SELECT * FROM AugReal_ARMain WHERE latitude>".$y0down." AND latitude <".$y0up." AND longitude>".$x0down.
				" AND longitude<".$x0up;
				
		if ($id_app!=null && $id_app!=0)
			$this->query .= " AND id_app=".$id_app;
		
		
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
		
		return $result;
	}
	
	//---- get Questions by questions ids ---------------------
	function getQuestionsByIds($id_q){
		
		$this->query = "SELECT * FROM AugReal_ARQuestions";
		if($id_q!=null)
			$this->query .= " WHERE id_q IN (".$id_q.")";
	
		
		
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	
	//-------------- get Question by Entity id -----------------------------
	function getQuestions($id_e){
	
		$this->query = "SELECT id_q, id_e, lang, question, answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8".
				" FROM AugReal_ARQuestions WHERE id_e=".$id_e;
	
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	//---------- Get Models ------------------------
	function getModelsAsString()
	{
	
		$this->query = "SELECT * FROM ".self::_MODEL_TABLE." ORDER BY name";
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	//---------- Get Model by id ------------------------
	function getVisRecModel($id)
	{
	
		$this->query = "SELECT * FROM ".self::_MODEL_TABLE." WHERE id=".$id;
		if(!$result=mysql_query($this->query,$this->dblink)){
			die(mysql_error($this->dblink));
			print '<script type="text/javascript">alert("FAIL!")</script>';
			return null;
		}
	
		return $result;
	}
	
	// ------------- INSERT CONCEPT -------------------
	function insertConcept($conceptName, $imgCount)	{
		$this->query = "INSERT INTO ".self::_IMAGE_TABLE." VALUES(NULL , 'ReconEngine/content/".
							$conceptName."' , '".$conceptName."' , NOW() , ".$imgCount." , 0, 0)";
		if(!mysql_query($this->query,$this->dblink))
			die(mysql_error($this->dblink));
	}
	
	// ------------- INSERT MODEL -------------------
	function insertModel($modelName, $classesPositive, $classesNegative)
	{
		$buildPositiveStr = "";
		$buildNegativeStr = "";
		foreach($classesPositive as $name){
			$buildPositiveStr .= $name;
			$buildPositiveStr .= ';';
		}
		if(empty($classesNegative)){
			$buildNegativeStr = ';';
		}
		else{
			foreach($classesNegative as $name){
				$buildNegativeStr .= $name;
				$buildNegativeStr .= ';';
			}
		}
		$this->query = "INSERT INTO ".self::_MODEL_TABLE." VALUES( '".$modelName."' , 'ReconEngine/models/".$modelName.".bin' , '".$buildPositiveStr."' , '".$buildNegativeStr."' , NOW() , NULL, 0, 0)";
		//echo $this->query;
		if(!mysql_query($this->query,$this->dblink))
		{
			//die(mysql_error($this->dblink));
			die($this->query);
		}
	}
	

	
	//---------- Get Models ------------------------
	function getModels()
	{
		$models = array();
		$q = "SELECT * FROM ".self::_MODEL_TABLE." ORDER BY name";
		$result = mysql_query($q) or die(mysql_error());
		while($row = mysql_fetch_array($result))
			array_push($models,$row);
		
		return $models;
	}
	
	//----------- Get Concepts ----------------------
	function getConcepts()
	{
		$concepts = array();
		
		$q = "SELECT * FROM ".self::_IMAGE_TABLE." ORDER BY name";
		$result = mysql_query($q) or die(mysql_error());
		while($row = mysql_fetch_array($result))
				array_push($concepts,$row);
		
		if(empty($concepts))
			return NULL;
		else
			return $concepts;
	}
	
	//---------------------
	function conceptExistsInDb($name)
	{
		$q = "SELECT * FROM ".self::_IMAGE_TABLE." WHERE name = '".$name."'";
		$result = mysql_query($q) or die(mysql_error());
		if(mysql_num_rows($result) == 0)
			return false;
		return true;
	}
	
	function modelExistsInDb($name)
	{
		$q = "SELECT * FROM ".self::_MODEL_TABLE." WHERE name = '".$name."'";
		$result = mysql_query($q) or die(mysql_error());
		if(mysql_num_rows($result) == 0)
			return false;
		return true;
	}
	
	function getConcept($name){
		$q = "SELECT * FROM ".self::_IMAGE_TABLE." WHERE name = '".$name."' ORDER BY name";
		$result = mysql_query($q) or die (mysql_error());
		$row = mysql_fetch_array($result);
		return $row;
	}
	
	//--------------------------------------------------------------------
	
	function modelView($name)
	{
		$q = "SELECT * FROM ".self::_MODEL_TABLE." WHERE name = '".$name."' ORDER BY name";
		$result = mysql_query($q) or die (mysql_error());
		$row_visrec_models = mysql_fetch_array($result);
		$positiveClasses = explode(';',$row_visrec_models["classes_positive"]);
		$negativeClasses = explode(';',$row_visrec_models["classes_negative"]);
		echo '<table width="50%" border="1">';
		echo '<tr><th>Name</th><th>Date created</th><th>Threshold</th></tr>';
		echo '<tr><td>'.$row_visrec_models["name"].'</td><td>'.$row_visrec_models["date"].'</td><td>'.$row_visrec_models["threshold"].'</tr>';
		echo '<th>Positive Classes</th><th>Negative Classes</th><th>Associated AR Entities</th>';
		echo '<tr><td>';
			foreach($positiveClasses as $class)
				if(strlen($class)>1)
					echo $class.",";
		echo '</td><td>';
			foreach($negativeClasses as $class)
				if(strlen($class)>1)
					echo $class.',';
		echo '</td><td>';
			$this->printEntitiesAssociated($name);
		echo '</td>';
		echo '</tr></table>';
	}

	
	function getEntitiesAssociated($name)
	{
		$query = 'SELECT title,AugReal_ARMain.id FROM AugReal_ARModels INNER JOIN AugReal_ARMain ON AugReal_ARMain.id_VRec = AugReal_ARModels.id WHERE name="'.$name.'"';
		$result = mysql_query($query) or die(mysql_error());
		$assoc = array();
		while($row_ar_models = mysql_fetch_assoc($result))
			$assoc[]= '<a href=../Human_AR_Edit.php?id='.$row_ar_models["id"].'>'.$row_ar_models["title"].'</a>';
		
		return $assoc;
	}
	
	
	function deleteConcept($name, $verbose=true)
	{
		if($this->conceptIsLocked($name)){
			if($verbose)
				echo 'Concept is locked and cannot be deleted';
			return;
		}
		if($this->conceptExistsInDb($name))
		{
			$q = "SELECT * FROM ".self::_IMAGE_TABLE." WHERE name='".$name."'";
			$result = mysql_query($q) or die(mysql_error());
			$row_concept = mysql_fetch_array($result);
			$conceptFolder = getcwd().DIRECTORY_SEPARATOR.$row_concept["path"];
			
			//- delete images 
			foreach (scandir($conceptFolder) as $item) {
				if ($item == '.' || $item == '..') continue;
				$succUnlink = unlink($conceptFolder.DIRECTORY_SEPARATOR.$item);
			}
			
			//- remove directory
			$succRmdir = rmdir($conceptFolder);
// 			if (!$succRmdir){
// 				if($verbose)
// 					echo $succRmdir.': Can not remove directory '.$conceptFolder.' of concept';
// 			} else {
				$q = "DELETE FROM ".self::_IMAGE_TABLE." WHERE name='".$name."'";
				if(!mysql_query($q, $this->dblink))
					die(mysql_error($this->dblink));

				if($verbose)
					echo 'Concept '.$name.' deleted.';
//			}
			
		}
	}
	
	function conceptIsLocked($name)
	{
		$q = "SELECT locked FROM AugReal_ARImages WHERE name='".$name."'";
		$result = mysql_query($q) or die(mysql_error());
		$row_concept = mysql_fetch_array($result);
		if($row_concept["locked"]==1)
			return true;
		else
			return false;
	}
	
	function deleteModel($name)
	{
		if($this->modelExistsInDb($name))
		{
			$q = "SELECT * FROM ".self::_MODEL_TABLE." WHERE name='".$name."' AND locked=0";
			$result = mysql_query($q) or die(mysql_error());
			if(mysql_num_rows($result) == 0)
			{
				echo 'Model is locked and cannot be deleted';
				return;
			}
			$row_model = mysql_fetch_array($result);
			unlink($row_model["path"]);
			
			$q = "DELETE FROM ".self::_MODEL_TABLE." WHERE name='".$name."'";
			//echo $q;
			if(!mysql_query($q, $this->dblink))
			{
				die(mysql_error($this->dblink));
			}
			echo 'Model '.$name.' deleted.';
		}
	}
	
	function deleteImage($name, $path)
	{
		//UPDATE AugReal_ARImages SET image_count = image_count - 1 WHERE name='helicopter'
		$q = "UPDATE ".self::_IMAGE_TABLE." SET image_count = image_count - 1 WHERE name='".$name."'";
		if(!mysql_query($q,$this->dblink))
			die(mysql_error($this->dblink));
		
		unlink('ReconEngine/content/'.$path);
		if($this->featuresAvailable($name)==1){
			$this->setFeaturesAvailableStatus($name,0);
			if(file_exists('ReconEngine/content/'.$path.'.bin'))
				unlink('ReconEngine/content/'.$path.'.bin');
		}
	}
	function deleteDuplicates()
	{
		$this->query = 'CREATE TABLE temp AS SELECT DISTINCT * FROM '. self::_TABLE;
		if(!mysql_query($this->query,$this->dblink))
		{
			//echo 'test1';
			die(mysql_error($this->dblink));
		}
		$this->query = 'DROP TABLE '.self::_TABLE;
		if(!mysql_query($this->query,$this->dblink))
		{
			//echo 'test2';
			die(mysql_error($this->dblink));
		}
		$this->query = 'ALTER TABLE temp RENAME TO '.self::_TABLE;
		if(!mysql_query($this->query,$this->dblink));
		{
			//die(mysql_error($this->dblink));
		}
		//echo 'test4';
	}
	function getDescriptionForModelId($id)
	{
		$query = 'SELECT description FROM AugReal_ARMain WHERE id='.$id;
		$result = mysql_query($query, $this->dblink) or die(mysql_error());
		$row_model = mysql_fetch_assoc($result);
		return $row_model["description"];
	}
	
	function getAllModelNames()
	{
		$query = 'SELECT name FROM AugReal_ARModels';
		$result = mysql_query($query,$this->dblink) or die(mysql_error());
		$modelNames = array();
		while($row_model = mysql_fetch_assoc($result))
			array_push($modelNames,$row_model["name"]);
	
		return $modelNames;
	}
	function getModelsBasedOnApplicationId($id_app)
	{
		if($id_app==5)
		{
			$query = "SELECT name FROM AugReal_ARModels WHERE name='building' OR name ='car' OR name='person' OR name='desk' OR name='tree'";
			$result = mysql_query($query,$this->dblink) or die(mysql_error());
			$modelNames = array();
			while($row_model = mysql_fetch_assoc($result))
				array_push($modelNames,$row_model["name"]);
			
			return $modelNames;
		}
		$query = 'SELECT name FROM AugReal_ARMain INNER JOIN AugReal_ARModels ON AugReal_ARMain.id_VRec = AugReal_ARModels.id WHERE AugReal_ARMain.id_app = '.$id_app;
		$result = mysql_query($query,$this->dblink) or die(mysql_error());
		$modelNames = array();
		while($row_ar_main = mysql_fetch_assoc($result))
			array_push($modelNames,$row_ar_main["name"]);
		
		return $modelNames;
	}
	
	function getModelsAssoc()
	{
		$query = 	"SELECT AugReal_ARMain.title, AugReal_ARMain.id
					FROM AugReal_ARModels INNER JOIN AugReal_ARMain
					ON AugReal_ARModels.id = AugReal_ARMain.id_VRec;";
		$result = mysql_query($query,$this->dblink) or die(mysql_error());
		$names = array();
		$idxes = array();
		while($row_ar_main = mysql_fetch_assoc($result))
		{
			array_push($names,$row_ar_main["title"]);
			array_push($idxes,$row_ar_main["id"]);
		}
		$models_assoc = array_combine($idxes,$names);
		return $models_assoc;
	}
	
	function getModelIdForName($name)
	{
		$query = 'SELECT AugReal_ARMain.id FROM AugReal_ARMain INNER JOIN AugReal_ARModels ON AugReal_ARMain.id_VRec = AugReal_ARModels.id WHERE AugReal_ARModels.name = "'.$name.'"';
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
		$row_ar_main = mysql_fetch_assoc($result);
		return $row_ar_main["id"];
	}
	
	function getModelNameForId($id)
	{
		$query = 'SELECT title FROM AugReal_ARMain WHERE id='.$id;
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
		$row_ar_main = mysql_fetch_assoc($result);
		return $row_ar_main["title"];
	}
	
	function getThresholdForModelWithName($name)
	{
		$query = 'SELECT threshold FROM AugReal_ARModels WHERE name="'.$name.'"';
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
		$row_ar_models = mysql_fetch_assoc($result);
		return $row_ar_models["threshold"];
	}
	
	function featuresAvailable($name)
	{
		$query = 'SELECT features_available FROM AugReal_ARImages WHERE name="'.$name.'"';
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
		$row_ar_images = mysql_fetch_assoc($result);
		return $row_ar_images["features_available"];
	}
	function getNumberOfConceptsCurrentlyTraining()
	{
		$query = 'SELECT id FROM AugReal_ARImages WHERE features_available=2';
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
		return mysql_num_rows($result);
	}
	
	function setFeaturesAvailableStatus($name, $status)
	{
		$query = 'UPDATE AugReal_ARImages SET features_available='.$status.' WHERE name="'.$name.'"';
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
	}
	
	function updateImagesNo($name, $number)
	{
		$query = 'UPDATE AugReal_ARImages SET image_count='.$number.' WHERE name="'.$name.'"';
		$result = mysql_query($query,$this->dblink) or die (mysql_error());
	}
	function closeDb()
	{
		mysql_close($this->dblink);
	}		
	

	
	/*
	function printQuery($sql)
	{
		$result = mysql_query($sql) or die(mysql_error());
		echo '<table border="1"><th>Profile image</th><th>Username</th><th>User ID</th><th>Time</th><th>Tweet ID</th><th>Tweet</th>';
		while($row_sql = mysql_fetch_array($result)){
			echo '<tr><td><img src="'.$row_sql['Image_URL'].'" width="60" height="60" /></td><td>'.$row_sql['Username'].'</td><td>'.$row_sql['User_ID'].'</td><td>'.$row_sql['Time'].'</td><td>'.$row_sql['Tweet_ID'].'</td><td>'.$row_sql['Text'].'</tr>';
		}
		echo '</table>';
	}
	*/
}
