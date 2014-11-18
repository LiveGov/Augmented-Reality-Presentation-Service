<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Edit item</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8; no-cache">
	<link rel="stylesheet" type="text/css" href="css/theme_ar.css">

 	<!--  Map v3 -->
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3.13&amp;key=AIzaSyBL3jul31HDAelbR_k0slDO_XP0t0jIhJ0&amp;sensor=false"></script>
	<!-- 2. 3D Engine -->
	<script type="text/javascript" src="libs/jsc3d/jsc3d.js"></script>
	<script type="text/javascript" src="libs/jsc3d/jsc3d.console.js"></script>
	<script type="text/javascript" src="js/My_Utils.js"></script> <!--  UrlExists and toggleVisibility -->

	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
	<script type="text/javascript"> var idEntity = "<?php print($_REQUEST['id']);?>";</script>
	<script type="text/javascript" src="libs/colorbox-master/jquery.colorbox.js"></script>
	<script type="text/javascript" src="libs/colorbox-master/colorbox_header.js"></script>
	<script type="text/javascript">var DirModels3D = "Models3D_DB/";</script>
	<script type="text/javascript" src="js/tabsHandlers.js"></script>
	
	<script src="libs/knob/jquery.min.js"></script>
	<script src="libs/knob/jquery.knob.js"></script>
	<script src="libs/knob/ga.js"></script>
	<script src="libs/knob/main.js"></script>

</head>

<!-- PHP Inputs

STATUS: INITIAL 
$_GET['id']           : id of the AR Entity -> General

STATUS: SEE_CONCEPT
$_GET['nameconcept']  : Visual Recognition -> Classification -> Concepts -> Select Concept to see images 
$_GET['id']           : id of the AR Entity

STATUS: PAGE_SHIFT_CONCEPT (shift page of images for this concept)
$_GET['id']
$_GET['nameconcept']  : Visual Recognition -> Classification -> Concepts -> Select Concept to see images
$_GET['page']         : Page of Images
$_GET['ipp']          : Images per page

STATUS: CHANGE_IPP_CONCEPT (change images per page for concept)
$_GET['id']
$_GET['nameconcept']  : Visual Recognition -> Classification -> Concepts -> Select Concept to see images
$_GET['page']         : Page of Images
$_GET['ipp']          : Images per page

STATUS: ADD_CONCEPT
FLICKR case
$_POST['concept']   : name of concept 
$_POST['noimages']  : number of images 
!$zip_upload		: no zip to upload

ZIP case (not working yet)
$_FILES["zipImages"]
$_FILES["zipImages"]["name"]!=""


STATUS: DELETE_CONCEPT
$_GET['id']           : id of the AR Entity
$_GET['deleteconcept']: name of concept to delete


STATUS: CREATE_MODEL
$_POST['idEntity']  : id Entity
$_POST['modelname'] : the name of the model to create

STATUS: DELETE_MODEL
$_GET['deletemodel']
$_GET['id'] 



PHP global variables
$row :           the AR enitity data
$row_concept:   the Concept data
$row_model:     the Model data

-->

<?php



require "auth/login_main.php"; // Login module

require 'My_Utils.php'; // chCoauth
require_once("KeepLogFunctions.php");

if(!$fgmembersite->CheckLogin()){
	die();
} else {
	$login_rname = $_SESSION['name_of_user'];
	$login_id = $_SESSION['id_of_user'];
	$login_permissions = $_SESSION['permissions_of_user'];
}

// ============ Db library ==================
require 'DatabaseHandler.class.php';  // Connect with with DB
$dbHandle = new DatabaseHandler();


// ========= Server and paths ==================
$server = $_SERVER["HTTP_HOST"];
$DirModels3D = "Models3D_DB/";

//--  =========== MySQL Data ===================
$result= $dbHandle->getAREntity(round($_REQUEST['id']));
$row=mysql_fetch_array($result);

$Main_im = $DirModels3D.$row['id']."/AR_".$row['id'].".jpg"; 

// ========== Permissions ===================
$isAbleToView = chPerm('|ar_web|',$login_permissions);
if (!$isAbleToView)
	die();

$isAbleToEdit = chPerm('|ar_web_editAREntity|', $login_permissions);
$IsTheAuthor   = $login_id == $row['id_author'];
$IsTheCoauthor = chCoauth($login_id, $row['id_coauthor']);
$isAbleToCreateConcept = chPerm('|ar_web_createConceptFromFlickr|', $login_permissions) ||
			chPerm('|ar_web_createConceptFromZip|', $login_permissions);
$isAbleToCreateRecModel = chPerm('|ar_web_createRecognitionModel|', $login_permissions);
$isAbleToDeleteRecModel = chPerm('|ar_web_deleteRecognitionModel|', $login_permissions);

$MODIFY = false;
$MODIFY_RECOGNITION = false;
$dis = "disabled";
$dis_Recognition = "disabled";

if (($IsTheAuthor || $IsTheCoauthor) && $isAbleToEdit){
	$MODIFY = true;
	$dis = "";
	
	if ($isAbleToCreateConcept && $isAbleToCreateRecModel && $isAbleToDeleteRecModel){
		$MODIFY_RECOGNITION = true;
		$dis_Recognition = "";
	}
}
?>

<!--   ======= BODY STARTS HERE ======= -->
<body >
<a href='AR_Main.php' class='backtohome'>Back</a>

<?php if ($MODIFY)
		echo "<center><div class='edittitle'>Edit entity</div></center>";
	else 
		echo "<center><div class='edittitle'>View entity</div></center>";
?>

<form action='api_v3/ar_edit.php' method=post enctype="multipart/form-data" name="EditARForm" accept-charset='UTF-8' 
		style='background:#ddd;padding:5px'>

<!--  ==================== id ================ -->
<?php echo "<span style='float:left;font-size:15pt;font-weight:bold;display:table;margin:10px;'>#".htmlspecialchars($row['id'])."</span>"; ?>

<!--  ==================== Tab shifter ================ -->
<span id="t1h" onclick="javascript:activateMainTab('t1')">General</span>
<span id="t2h" onclick="javascript:activateMainTab('t2')">3D</span>
<span id="t3h" onclick="javascript:activateMainTab('t3')">Visual Recognition</span>
<!-- <span id="t4h" onclick="javascript:activateMainTab('t4')">Questions</span>  -->

<?php if ($MODIFY)
	echo "<input type='submit' class='submitEditFormA' name='submitA' value='Save'>"; 
?>
<div id="tabMainCtrl" style='padding-top:37px' >
<!--   ------------ GENERAL INFO --------------------------  -->
<div id="t1" >
<table><tr>
	<!--	  Text Information  -->
	<td width='59%'>
	<?php
	echo "<input type=hidden name=id value=".$row['id'].">";	 
	echo "<input type='textarea' style='display:none' name=id_author value='".$row['id_author']."'>";
	echo "<input type='textarea' style='display:none' name=author value='".$row['author']."'>";

	//================== Application =====================
	echo "<hr>";
	echo "<table id='appandsec'><tr><td>";
	$resApp = $dbHandle->getARApps();

	echo "Application<br><select name='id_app' ".$dis.">";
	echo '<option value=0 selected></option>';
	while($rowapp=mysql_fetch_assoc($resApp)){
		echo '<option value='.$rowapp['id_app'].' '.($rowapp['id_app']==$row['id_app']?' selected':'').'>'.
				$rowapp['description'].'</option>';
		$appnames[] = $rowapp['description'];
	}
	echo "</select>";
	?>
	<button type='button' class="toggler off" style="margin-right:0px;" id='btapplinking' name='btapplinking' onclick="toggle_visibility('appl','btapplinking')">Info</button>
	<br>
	<!--  View linking with External DB information  -->
	<div style='display:none;' id='appl'>
		The use case that this Entity is associated with.
		<table align="center">
			<tr><td>
				ID of the external database record:
				<?php echo "<input type=text size=3 name=id_external ".$dis." value=".$row['id_external'].">";?>
			</td></tr>
			<tr><td>
			URL<?php echo "<input name=linkurl size=50 ".$dis." value='".htmlspecialchars($row['linkurl'])."'>"; ?>
			</td></tr>
		</table>
	</div>
	<br/>
	
	<table><tr><td>Android app:</td><td> <?php echo "<input name=androidapp size=30 value='".htmlspecialchars($row['androidapp'])."'>"; ?></td>
			<tr><td>iPhone app:</td><td> <?php echo "<input name=iphoneapp size=15 value='".htmlspecialchars($row['iphoneapp'])."'>"; ?></td>
	</tr></table>
	<?php	
	echo "</td>";
	// ================= Coauthors =================
	echo "<td style='border-left: 2px solid #ddd; border-radius:0pt; padding:15px'>";
	
	echo "Permissions";
		
	$all_users = getRegisteredUsers($SC_path,$arserver_username,$arserver_password,$customerid); // look at auth/include/membersite_config.php and SC_auth.php

	
	echo "<table id='securityTable'>";
	echo "<tr><th>User</th><th>Edit</th><th>Delete</th></tr>";
	echo "<tr><td>".$row['author']." (author)</td><td><input type='checkbox' checked disabled></td><td><input type='checkbox' checked disabled></td></tr>";
	for ($iu=0; $iu < count($all_users); $iu++){
		if ($all_users[$iu][0]!=$row['id_author']){ // not show the author
			
			$coid = $all_users[$iu][0];
			$btname = "ub".$coid;    // name that button
			
			$isCh = "";
			if (chCoauth($coid, $row['id_coauthor']))
				$isCh = "checked";

			print("<tr><td>". $all_users[$iu][1]."</td><td>	<input type='checkbox' id='".$btname."'	name ='cthrs[]' ".$isCh.
				" value=".$coid." ".$dis."></td><td></td></tr>");
		}
	}
	echo "</table>";
	
	echo "</td></tr></table>";
	echo "<hr>";
	//------------- Title and Description ----------------------
	$LanguagesSTR = $row['Langs'];
	$LanguagesARR = explode(";",$LanguagesSTR);

	if (count($LanguagesARR)<2)
		$LanguagesARR[1] = "";
	if (count($LanguagesARR)<3)
		$LanguagesARR[2] = "";
	if (count($LanguagesARR)<4)
		$LanguagesARR[3] = "";
	?>

	<span style='float:left'>Title &amp; Description</span>
	<!--  Language Example  -->
	<span style='float:right'><button type='button' class='toggler off' id='btlangExample' name='btlangExample' 
				onclick="toggle_visibility('langExample','btlangExample')">Example</button></span><br/>
	<br>	 
	<div id='langExample' style='display:none;background:#000;padding:5px'>
		<textarea class=lang name=LangExample disabled>en</textarea>
		<textarea class='title' name=title disabled>Title goes here</textarea><br/>
		<textarea class=desc name=description disabled>Description goes here. 'en' stands for Engish according to the ISO 639-1 standard. For a full list of 639-1 codes see http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes</textarea>
	</div>

	
	<span style='float:left;margin-left:20pt'>Main translation</span>
	<center><br/>
	<?php
	// Language 1
	echo "<textarea class=lang name=Lang1 ".$dis.">".$LanguagesARR[0]."</textarea>".
		 "<textarea class=title name=title ".$dis.">".$row['title']."</textarea><br/>".
		 "<textarea class=desc name=description cols=50 ".$dis.">".$row['description']."</textarea><br/><br/>";
	
	echo "<span style='float:left;margin-left:20pt'>Secondary translation</span><br>";
	// Language 2
	echo "<textarea class=lang name=Lang2 ".$dis.">".$LanguagesARR[1]."</textarea>".
		 "<textarea class='title' name=titleB ".$dis.">".$row['titleB']."</textarea><br/>".
		 "<textarea class=desc name=descriptionB ".$dis.">".$row['descriptionB']."</textarea><br/><br/>";
	
	echo "<span style='float:left;margin-left:20pt'>Third translation</span><br>";
	// 	Language 3
		echo "<textarea class=lang name=Lang3 ".$dis.">".$LanguagesARR[2]."</textarea>".
			 "<textarea class='title' name=titleC ".$dis.">".$row['titleC']."</textarea><br/>".
			 "<textarea class=desc name=descriptionC ".$dis.">".$row['descriptionC']."</textarea><br/><br/>";
	?>
	
	<span style="float:right"><button type='button' class='toggler off' id='btmorelangs' name='btmorelangs' 
				onclick="toggle_visibility('langsmore','btmorelangs')">More Languages</button></span><br/>
	<?php 
	echo "<div id='langsmore' style='display:none;'><br>";
	
	    echo "<span style='float:left;margin-left:20pt'>Fourth translation</span><br>";
		// Language 4
		echo "<textarea class=lang name=Lang4 ".$dis.">".$LanguagesARR[3]."</textarea>".
			 "<textarea class='title' name=titleD ".$dis.">".$row['titleD']."</textarea><br/>".
			 "<textarea class=desc name=descriptionD ".$dis.">".$row['descriptionD']."</textarea><br/>";

	echo "</div>";
	echo "</center>";
	?>
	<br>
	<hr>
</td>
<!--		 Map			 -->
<script type="text/javascript">
	var map;
	var geocoder;

	function initialize() {
		geocoder = new google.maps.Geocoder();

		// set position of marker
		var js_lat  = "<?php echo $row['latitude']; ?>";
		var js_long = "<?php echo $row['longitude']; ?>";
		
		// set Map
		var mapOptions = {zoom: 14, center: new google.maps.LatLng(js_lat, js_long), 
			 		  mapTypeId: google.maps.MapTypeId.ROADMAP};
		map = new google.maps.Map(document.getElementById('map_canvas_small'), mapOptions);	 
		
		// icon of marker
		var iconpng = "<?php if (file_exists($server.$Main_im)){
			echo str_replace("AR","markerAR",$Main_im);}else{echo "";} ?>";

		// set Marker
		var marker = new google.maps.Marker({position: new google.maps.LatLng(js_lat,js_long),
			 map:map, draggable: true});
		if (iconpng.length>0){
			marker = new google.maps.Marker({position: new google.maps.LatLng(js_lat,js_long),
					 	map:map, draggable: true, icon:iconpng});
		}

		// drag event
		google.maps.event.addListener(marker, 'dragend',
			function(evt){
				js_lat= evt.latLng.lat().toFixed(8);
 				js_long=evt.latLng.lng().toFixed(8);	

				document.getElementById('latitude').value  = js_lat;
 				document.getElementById('longitude').value = js_long;

 				// automatic geocoding: Present location information 
				geocoder.geocode({'latLng': new google.maps.LatLng(js_lat, js_long)},
					function(results, status) {
						var firstRes = results[0].address_components;
						for (var i=0; i<firstRes.length; i++){ 
							if(firstRes[i].types=="route")
								document.getElementById('streetnameaddress').value= firstRes[i].long_name;
							else if (firstRes[i].types=="street_number")
								document.getElementById('numberaddress').value = firstRes[i].long_name;
							else if(firstRes[i].types[0]=="country")
								document.getElementById('country').value = firstRes[i].long_name;
							else if(firstRes[i].types=="postal_code")
	   							document.getElementById('postalcode').value = firstRes[i].long_name;
						} // end for
					} // end function
				); // end geocoder
			}); //  of listener
	}; // end of initialize

	google.maps.event.addDomListener(window, 'load', initialize);
</script>

<!--  Map information form -->
<td style="border-left: 1px solid #eee;padding:5px;text-align:center">
	
	<table class="edittableaddress" width='100%'><tr>
		<td>Longitude<?php echo "<input type=text class='address' size=7 name=longitude id=longitude ".$dis." value='".$row['longitude']."'>";?>deg.</td>
		<td>Latitude<?php echo "<input type=text class='address' size=7 name=latitude id=latitude ".$dis." value='".$row['latitude']."'>";?> deg.</td>
		<td>Altitude<?php echo "<input type=text class='address' size=5 name=altitude id=altitude ".$dis." value='".intval(floor($row['altitude']))."'>";?>m.</td>
	</tr>
	</table>
	
	<div id="map_canvas_small" style="margin:10px; height:400px; width:90%; height:400px"></div>
	
	<table class="edittableaddress" >
	<tr><td>Street<br/><?php echo "<input type=text class='address' size=10 name=streetnameaddress id=streetnameaddress ".$dis." value='".htmlspecialchars($row['streetnameaddress'])."'>";?></td>
		<td>No<br/>		<?php echo "<input type=text class='address' size=2 name=numberaddress id=numberaddress ".$dis." value='".htmlspecialchars($row['numberaddress'])."'>";?></td>
		<td>PC<br/><?php echo "<input type=text size=2 class='address' name=postalcode id=postalcode d".$dis." value='".htmlspecialchars($row['postalcode'])."'>";?></td>
		<td>Country<br/><?php echo "<input type=text class='address' size=8 name=country id=country ".$dis." value='".$row['country']."'>";?></td>
	<tr>
	</table>

	<hr>
	<!-- Image -->
	<?php echo "<img src=".$Main_im." alt='No image' width=240/>";?>
	<?php echo "<input type=file name='Image0' id=Image0 ".$dis." >(jpg)";?>
	<hr>
	</td>
	</tr>
	
</table>
</div>
<!--   ------------ END OF GENERAL INFO --------------------------  -->



<!----------------------------------
			3D	Models 
--------------------------------->
<!--  SCRIPT to change model tab  -->
<script type="text/javascript">
var js_var = "<?php echo $row['id']; ?>";
//var back_im = "<?php echo $Main_im ?>";
</script>

<!--	PHP variables  -->
<?php 
$instr_obj = "3D file (Wavefront obj):<br/><br/>
		 Coordinates of vertices/edges/faces.<br/> 
		 Hint: Size of models in metrics should not exceed <b>2 meters</b>.<br/><br/>";
  
$instr_mtl = "3D Material file (mtl):<br/><br/>
			Colors/Textures of obj file.<br/><br/>";

$instr_textures = "Textures (zip):<br/><br/>
					 A zip file of a set of Images (.zip).<br/>
					 All into images\' folder.<br/>
					 Rectangle JPG images<br/>
					 Max:256x256 pixels<br/><br/>";

$instr_tracking = "<span style='font-size:10pt'> A png 200x200 image <br/> at grayscale mode.<br/>Non symmetrical;<br/>No thin lines.<br/></span>"
?>

<div id="t2" style="display:none"> <!--  t2 is the 3dmodels -->


<!--  Tabs -->
<span id="m1h" onclick="javascript:activate3dModelsTab('m1')">Model 1</span>
<span id="m2h" onclick="javascript:activate3dModelsTab('m2')">Model 2</span>
<span id="m3h" onclick="javascript:activate3dModelsTab('m3')">Model 3</span>

<div id="tab3dModelsCtrl" ><!--  Tab Control allows for changing tabs (see script at beginning) -->
	<!---	  MODEsL 1, 2, 3			-->
	<?php 
	for ($iM = 1; $iM <= 3; $iM++ ){ 
		echo "<div id='m".$iM."' style='display:".(($iM>1)? "none":"").";'>";
		echo "<table class='tbdetails3D' align='center'>";

		echo "<tr><td>";
			echo "<center><canvas id='cv".$iM."' class='canvas3d' width='500' height='320'></canvas></center>";
		
			echo "<script type='text/javascript'>";
				echo "canvas".$iM." = document.getElementById('cv".$iM."');";
				echo "viewer".$iM." = new JSC3D.Viewer(canvas".$iM.");";
				echo "viewer".$iM.".setParameter('SceneUrl',".DirModels3D."+ js_var + '/".$iM."/AR_' +js_var+'_".$iM.".obj');";
						//viewerA.setParameter('BackgroundImageUrl', back_im);
				echo "viewer".$iM.".setParameter('RenderMode','texturesmooth');";
				echo "viewer".$iM.".init();";//viewerA.initBackground();
				echo "viewer".$iM.".update();";
			echo "</script>";
			echo "South view";
		echo "</td>";
		
		echo "<td style='text-align:center;vertical-align:middle;'>A screenshot of the 3D model (jpg) <br/>";
		echo "<img src='".$DirModels3D.$row['id']."/".$iM."/AR_".$row['id']."_".$iM.".jpg' class='img3d' alt='no image'/>";
		echo "<br/>";
		
		$resSizes = $dbHandle->getFilesSizes($row['id'].'.'.$iM.'01');
		while($rowSizes = mysql_fetch_assoc($resSizes)){
			print round($rowSizes['size']/1000). " kB";
		}

		echo "<br>";
		echo "<input type=file name='Image".$iM."' id=Image".$iM.">";
		echo "</td>";
		echo "</tr>";
		echo "</table>";
		
		//  Details
		echo "<table class='tbdetails3D' align='center'>";
		echo "<tr>";
			echo "<td>".$instr_obj."<input type=file name='obj".$iM."' id=obj".$iM." ".$dis." ></td>";//  Obj 
			echo "<td>".$instr_mtl."<input type=file name='mtl".$iM."' id=mtl".$iM." ".$dis." ></td>";//  Mtl
			echo "<td>".$instr_textures."<input type=file name='zip".$iM."' id=zip".$iM." ".$dis." ></td>";//  Textures
			
			//,'.$row['id'].'.'.$iM.'01';
			$resSizes = $dbHandle->getFilesSizes($row['id'].'.'.$iM);
			if($rowSizes = mysql_fetch_assoc($resSizes)){
				print "<td>3d model zipped: ".round($rowSizes['size']/1000). " kB";
				
				echo "<br /><br />Press ";
				echo "<a onclick=\"return confirm('Are you sure?');\" href=api_v3/ar_delete_model.php?iM=".$iM."&id=".$row['id'].
				" style='color:#F00;text-decoration:none'>here</a>"; //Redirect to self to delete
				
				echo " to delete this model";
								
				
				echo "</td>";
			}
			
		echo "</tr></table>";
		
		
		
		
		
		echo "</div>";
	}?>
	
 
<!--	------- End of Models -----------		 -->

<!-- ------------ Log Screen ---------------- -->	

</div><!-- tabCtrl -->
(Resize:Shift + left mouse) &nbsp; (Pan:Ctrl  + left mouse)
<button style='float:right' type='button' name='log3d' onclick="toggle_visibility('logger3d')">View Log</button>
<div id="logger3d" style='display:none'></div>
<script type="text/javascript">JSC3D.console.setup('logger3d', '120px');</script>
</div><!--  3D Models all -->


<!-- =============================
               VISUAL RECOGNITION  
        ======================================== -->
<div id="t3" style="display:none"> <!-- t3 VisRec -->

<!--  Tabs -->
<span id="v1h" onclick="javascript:activateVisRecTab('v1')">Tracking</span>
<span id="v2h" onclick="javascript:activateVisRecTab('v2')">Classification [legendary]</span>


<div id="tabVisRecCtrl" >

	<!--  ============== Tracking ======================== -->
	<div id='v1' >
		<table class='tablelevel4'>
		<tr>
			<td><?php echo "Select image to associate.<br/>".$instr_tracking;?></td>
			<?php
			if (file_exists($DirModels3D.$row['id'].'/1/AR_'.$row['id'].'_1.obj')){
				echo "<td><img src='".$DirModels3D.$row['id']."/1/".$row['id']."_1.png' width=200 /><br>";
				echo "<input type=file accept='image/png' name='ImageTracking' id=ImageTracking style='max-width:200px'>";
				
				if(file_exists($_SERVER['DOCUMENT_ROOT'].$DirModels3D.$row['id'].'/1/'.$row['id'].'_1.png')){
				}
				
				echo "</td>";
				echo "<td><center>";
				
				echo '<b>Orientation</b><br><br>';
				print "From which angle the photo was taken?<br>";
				//print 'South<input type="range" name="trackingImRotation" value="'.$trackImRot.'" min="0" max="359" step="1" onchange="updateTextInput(this.value);">South';
				//print '<br><input type="text" name="trackImRot" id="trackImRot" value="'.$trackImRot.'" size="3" maxlength="3" style="font-family:monospace">';

				echo '<table>';
				echo '<tr><td></td><td>South to North</td><td></td></tr>';
				echo '<tr><td>East to West</td><td>'; 
				$strrange = '<input class="knob" name="trackImRot" id="trackImRot" '.
						'value="'.$row['trackImRot'].'" data-min="0" data-max="359" data-step="1" '.
						'data-width="150" data-height="150" data-cursor="true" data-fgcolor="#222222" data-thickness=".3">';
				echo $strrange;
				echo '</td><td>West to East</td></tr>';
				echo '<tr><td></td><td>North to South</td><td></td></tr>';
				echo '</table>';
				
				echo '<br><br>';
				echo '<b>Scale</b><br><br>';
				echo 'How far is the target object from you according to the photo?<br>';
				
				echo '<input name="trackImScale" id="trackImScale" '.
						'value="'.$row['trackImScale'].'" type="range" min="0.3" max="500" step="0.1"
							oninput="this.form.showDistance.value=this.value" style="display: block; width:250px">';
				 
				echo '<input type="text" id="showDistance" value="'.$row['trackImScale'].'" min=0.3 max=500 step="0.1"
					 oninput="this.form.trackImScale.value=this.value" maxlength="4" size="4" style="display: block;"> meters';
				echo '</div>';

				//echo "<td><figure style='background-color:#ff0;display:table;width:140px;'><center>
				//<a href='images/glue.png' class='group1'><img alt='Junaio channel QR'  src='images/glue.png' width=50></a>
				//	<figcaption>Use 'Junaio' for Android or iPhone</figcaption></center></figure></td>";
			} else {
				echo "<td>First upload a 3D model!</td>";
			}
		?>
		</tr></table>
	</div>


	<div id='v2' style='display:none'>
		<!--  ============== Classification ================================ -->
		<?php
		// 1 find from ARModels table the name of id  = idof3DModel
		$res_VRec = $dbHandle->getModelsAsString(); 
		
		echo "Associate model<select name='id_VRec' ".$dis.">";
		echo "<option value=' '></option>";
		while($row_VRec = mysql_fetch_assoc($res_VRec))
			echo "<option value=".$row_VRec['id']." ".($row_VRec['id']==$row['id_VRec']?" selected":"").">".$row_VRec['name']."</option>";
		echo "</select>";
		?>
		<br>
		<p id="info" align="center" style="display: none"><span class='infoMark'>!</span></p>

		<span id="c1h" onclick="javascript:activateClassificationTab('c1')">Concepts</span>
		<span id="c2h" onclick="javascript:activateClassificationTab('c2')">Models</span>
	
		<div id="tabClassificationCtrl" style='padding-top:37px'>
			<div id="c1"><?php require "concepts.php";?></div>
			<div id="c2" style='display:none'><?php require "models.php";?>	</div>
		</div>

		<!-- START from the appropriate TAB -->
		<?php
		// Delete Model
		if (isset($_GET['deletemodel']) && isset($_GET['id']))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c2h").click(); </script>';
		// Create Model
		elseif (isset($_POST['idEntity']) && isset($_POST['modelname']))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c2h").click(); </script>';
		// Delete concept	
		elseif (isset($_GET['id']) && isset($_GET['deleteconcept']))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c1h").click(); </script>';
		// Concept visualize
		elseif (isset($_GET['id']) && isset($_GET['nameconcept']))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c1h").click(); </script>';
		// Concept page shift
		elseif (isset($_GET['id']) && isset($_GET['nameconcept']) && isset($_GET['page']) && isset($_GET['ipp']))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c1h").click(); </script>';
		// Concept add from ZIP
		elseif (isset($_GET['id']) && isset($_GET['nameconcept']))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c1h").click(); </script>';
		// Concept add from FLICKR
		elseif(isset($_POST['concept']) && isset($_POST['noimages']) && (!$zip_upload))
			echo '<script>$("#t3h").click(); $("#v1h").click(); $("#c1h").click(); </script>';
		// 	Model delete 
		elseif (isset($_GET['startfrom']) && $_GET['startfrom']=='models')
			echo '<script>$("#v2h").click();</script>';
		// Model create
		elseif(isset($_POST['modelname']))
			echo '<script>$("#v2h").click();</script>';
		?> 
	</div>
	<!-- =============================================== -->
	

	<!-- =============================================== -->
	</div><!-- tabImrecCtrl -->
</div><!-- t3 -->

<!-- ----------------------------------------------------------
	Questions  
--------------------------------------------------------------	-->
<!-- 
<div id='t4' style='display:none;'>
	
<?php
// $result= $dbHandle->getQuestions($_REQUEST['id']);

// while( $row=mysql_fetch_array($result) ){ 
// 	// QUESTION
// 	echo "<p>(".$row["lang"].") ".$row["question"];

// 	//ANSWERS
// 	for ($j=1; $j<= 8; $j++)
// 		if ($row["answer".$j])
// 			echo "<span style='background:#aaa; margin-left:10px;'>".$row["answer".$j]."</span>";
	
// 	echo "</p>";
// }
		
// if (!mysql_num_rows($result)){print ("<div style='text-align:center'>No Questions</div>");}
?>
</div>
 --> 
</div> <!-- Tab Main Ctrl -->
</form>

<!--		GOOGLE ANALYTICS	 -->
<script type="text/javascript">
	var _gaq = _gaq || [];
	_gaq.push(['_setAccount', 'UA-37557296-1']);
	_gaq.push(['_trackPageview']);

	(function() {
		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	 })();
</script>
</body>
</html>