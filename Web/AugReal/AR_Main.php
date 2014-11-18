<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Augmented Reality objects</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="css/theme_ar.css">

	<!--  Map v3 -->
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3.13&key=AIzaSyBL3jul31HDAelbR_k0slDO_XP0t0jIhJ0&sensor=false"></script>
	<script src="js/My_Utils.js"></script>
</head>

<script>
var locations = []; // LatLong of Markers
var ids_js = [];    // ids of entities
var id_selected_previous;

var server = "http://"+"<?php print($_SERVER['HTTP_HOST']);?>";

//Default location
var currlat  = 40.5678; 
var currlong = 22.99;

if (navigator.geolocation) 
	navigator.geolocation.getCurrentPosition(showPosition);
</script>

<?php 
	require "auth/login_main.php"; // Login module
	require 'DatabaseHandler.class.php';  // Connect with with DB
	
 	$dbHandle = new DatabaseHandler();
	
	require("My_Utils.php");
	
	if(!$fgmembersite->CheckLogin()){
		die();
	} else {
	 	$login_rname = $_SESSION['name_of_user'];
 		$login_id = $_SESSION['id_of_user'];
 		$login_permissions = $_SESSION['permissions_of_user'];
	}
	
	$isAbleToView = chPerm('|ar_web|',$login_permissions);
	if (!$isAbleToView)
		die();
	
	$isAbleToAdd = chPerm('|ar_web_createAREntity|', $login_permissions);
	$isAbleToEdit = chPerm('|ar_web_editAREntity|', $login_permissions);
	$isAbleToDelete = chPerm('|ar_web_deleteAREntity|', $login_permissions);
?>
<?php 
	if (isset($SUCCESS_NEW))
		echo "<body onload=\"scrollToItem('item".$lastid."','1')\">";
	else 
		echo "<body>";
?>

<div class="header">
	<a href="http://liveandgov.eu"><img src="images/livegov-logo_25pxh.jpg" height="16px" style="margin:3px;margin-bottom:-3px;"/></a>
	<button type='button' style="margin-top:2px" onclick="window.open('htmls/Downloads.html');">Smartphone</button>
	<button type='button' style="margin-top:2px" onclick="window.open('doc/');">Diagnostics</button>
</div> <!--  end of header -->



<div class="mainbody">
	<div id="list">
		<!--  Filter  -->
		<button type='button'
				onclick="document.getElementById('usecaseinfo').style.display=document.getElementById('usecaseinfo').style.display!=''?'':'none'">Filter</button>
				
				<!-- USE CASES TABLE  -->
		<div id='usecaseinfo' style='display:none'>
			<form action="">;
			<table class='usecasetable'>;
			<?php 
			$resultApp= $dbHandle->getARApps(); 
			$STR_APPS = ""; // Predifined initial app id
			while( $row_usecase=mysql_fetch_array($resultApp) ){
				echo "<tr><td>";
				if (isset($_REQUEST['usecasefilterflag']) && empty($_REQUEST[$row_usecase['id_app']])){ 
					$isChecked = "";
				}else{
					$isChecked = "checked";
					$STR_APPS = $STR_APPS.",".$row_usecase['id_app'];
				}
				echo "<input type='checkbox' name='".$row_usecase['id_app']."' ".$isChecked.">".$row_usecase['description'];
				echo "</td></tr>";
			}
			?>
			
			<tr><td style='background-color:none'>
			<input type='hidden' name='usecasefilterflag' value='true'>
			<input type='submit' value='Submit' style='padding:5px; margin: 0em 1em;'>
			</td></tr></table>
			</form>
		</div>
		<!--          End of use cases        -->
		
		<!-- Add -->
		<?php // REQUEST 
		if ($isAbleToAdd){
			echo "<form action='' method=post style='display: inline;'>";  // Self Call
			echo "<input type=text size=25 name=addme value=Indeed style='display:none'>";
			?>
			<input type=text size=25 name=latitude id='latitude' style='display:none'>
			<input type=text size=25 name=longitude id='longitude' style='display:none'>
			<?php
			echo "<input type=submit border=0 value='Add new'>";
			echo "</form>";
		}
			// RESPONSE
		if(isset($_REQUEST['addme']) && $isAbleToAdd==true){
			$SUCCESS_NEW = $dbHandle->insertAREntity($login_id,$login_rname,$_REQUEST['latitude'],$_REQUEST['longitude']);

			$lastid = mysql_insert_id();
			
			if ($SUCCESS_NEW)
				Log2File("WEB",SCUSER,SCPASS,SCCUSTOMERID,$_SESSION['id_of_user'],date('Y-m-d H:i:s'),'','Added: '.$lastid);

			$altitude_with_id = 60.0 + $lastid/100000;
			$dbHandle->updateAREntity($lastid,'altitude',$altitude_with_id);
		}
		?>
		
		<button type='button' onclick="window.open('htmls/InsertExample.html', 'example','location=0,width=300,height=800,top=50,left=300')" class='btHelp'>?</button>
		<!-- End of ADD -->
			<!--  ------------ ENTRIES    TABLE ------------------------  -->
			<?php
			$STR_APPS = substr($STR_APPS,1);
			//-------------- Select data of table---------------------------------
			$result= $dbHandle->getAREntities($STR_APPS);
			//----------------- Present data of table -------------------------------

			$imagefilepath="";
			$NEntities = mysql_num_rows($result);
			$iserialEntity=0;
			while( $row=mysql_fetch_array($result) ){
				$iserialEntity ++;
				echo "<table width=98% class='selectableTable' onclick='animateToMarker(".$row['latitude'] .",". $row['longitude'].",\"item".$row['id']."\")' id='item".$row['id']."'>";
				echo "<tr>";
				echo "<td width=60%>";
				echo "<span class='e_title'>".$row['title']."</span>";
				$IsTheAuthor   = $login_id == $row['id_author']; 
				$IsTheCoauthor = chCoauth($login_id, $row['id_coauthor']);
				
				echo "</td>";
				echo "<td>";
				
				// Edit
				if (($IsTheAuthor || $IsTheCoauthor) && $isAbleToEdit)
					echo "<a href=AR_Edit.php?id=".$row['id']." class='edit'>&#x279C</a>";
				else // View details
					echo "<a href=AR_Edit.php?id=".$row['id']." class='more'>&#x279C;</a>";
				
				
				echo "</td>";

				
				//echo "<span class='timestamp'>". showTime($row['date']) ."</span>
				//------------ Icon -----------------
				if ($row['jpg'])
					$imgpath = "Models3D_DB/".htmlspecialchars($row['id'])."/AR_".$row['id'].".jpg";
				else
					$imgpath = "images/ic_no_image.png";
				
				
				echo "<td ><img class='imgiconmain' src=".$imgpath." ></td>";
				echo "<td >";

				echo "<table><tr><td>";
				echo "<span class='e_id'>".$row['id']."</span>";
				echo "</td></tr><tr><td>";
				
				// 	Delete
				if ($IsTheAuthor && $isAbleToDelete==true) 
						echo "<a onclick=\"return confirm('Are you sure?');\" href=api_v3/ar_delete.php?id=".$row['id'].
								" class='delete'>&#x2715</a>"; //Redirect to self to delete
				echo "</td></tr></table>";
				
				echo "</td>";
				?>
	
				</tr>
				</table>
				<!--   end of table of entities -->

				<script>
				ids_js.push("<?php  print($row['id']) ?>");
				locations.push( new Array("<?php  print("# ".$row['id'] . ". ". $row['title']);?>","<?php 
				print($row['latitude'])?>","<?php print($row['longitude'])?>","<?php print(str_replace("AR","markerAR",$imgpath))?>") );
				</script>
				<?php
			}
		?>
	</div><!-- List -->

	<!--             -- Map --------                               -->
	<script>
		google.maps.visualRefresh = true;
		var map;
		function initialize() {

			var mapOptions = {zoom:14, 
						center: new google.maps.LatLng(currlat, currlong), 
						mapTypeId: google.maps.MapTypeId.ROADMAP};

			map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);

			//----------- put markers -----------           
			var infowindow = new google.maps.InfoWindow({maxWidth:400});
			var marker ;
			var i;

			for (i = 0; i < locations.length; i++) {  

				var iconurl = "images/ic_no_image.png";
				if (UrlExists(locations[i][3]) && locations[i][3]){
					iconurl = locations[i][3];
				}

				var micon = new google.maps.MarkerImage(
					iconurl, //url
					new google.maps.Size(40, 40), //size
					new google.maps.Point(0,0), //origin
					new google.maps.Point(20, 20) //anchor 
    			);
					
				marker = new google.maps.Marker(
					{position: new google.maps.LatLng(locations[i][1],locations[i][2]),
					map:map, icon: micon});
					
					// 	listener to open info box
				google.maps.event.addListener(marker, 'click', (
					function(marker, i) {
						return function(){
							infowindow.setContent("<a href=AR_Edit.php?id="+ids_js[i]+">"+locations[i][0]+"</a>");
						
							infowindow.open(map, marker);
							scrollToItem("item"+ids_js[i],'1');
							}})(marker, i));


				//	3D marker to display if 3d available
				if (UrlExists(server+'/Models3D_DB/'+ids_js[i]+'/1/AR_'+ids_js[i]+'_1.obj')){
					marker3d = new google.maps.Marker({
					position: new google.maps.LatLng(locations[i][1],locations[i][2]),
					map:map,
					icon:{url: server+'/Models3D_DB/CategImages/3d_logo.png', 
					scaledSize: new google.maps.Size(20, 20),
					anchor: new google.maps.Point(10, 36)}
					}
				);
				}
				}

				// Home Button 
				var homeControlDiv = document.createElement('div');
				var homeControl = new HomeControl(homeControlDiv, map);
				homeControlDiv.index = 1;
				map.controls[google.maps.ControlPosition.TOP_RIGHT].push(homeControlDiv);
				
			} // end of function initiliaze

			google.maps.event.addDomListener(window, 'load', initialize);
	</script>

	
	<div id="map_canvas"></div>
	
	
</div>
<!-- MainBody -->

<script type="text/javascript">
// Home button
function HomeControl(controlDiv, map) {

	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	controlDiv.style.padding = '5px';

	// Set CSS for the control border
	var controlUI = document.createElement('div');
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '2px';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Click to set the map to Home';
	controlDiv.appendChild(controlUI);

	// Set CSS for the control interior
	var controlText = document.createElement('div');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '12px';
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = '<b>Home</b>';
	controlUI.appendChild(controlText);

	// Setup the click event listeners: simply set the map to
	// Chicago
	google.maps.event.addDomListener(controlUI, 'click', function() {
	  map.setCenter(new google.maps.LatLng(currlat, currlong));
	});

	// My loc mark
	var locmarker = new google.maps.Marker(
		{position: new google.maps.LatLng(currlat,currlong),
		map:map}
	);
}



//--- get current location ------
function showPosition(position){
	document.getElementById('latitude').value  = position.coords.latitude;
	document.getElementById('longitude').value = position.coords.longitude;
	currlat = position.coords.latitude;
	currlong= position.coords.longitude;
}

//------- camera to marker
function animateToMarker(Lat,Lng, id_in){
	map.panTo(new google.maps.LatLng(Lat, Lng));
	scrollToItem(id_in, "0");
}

// list to item scroll
function scrollToItem(id_in, scrll){
	if (id_in.length > 0){ 

		if (scrll=="1")
			document.getElementById(id_in).scrollIntoView(true);

		if (id_selected_previous)
			document.getElementById(id_selected_previous).style.backgroundColor = '#fff';

		document.getElementById(id_in).style.backgroundColor = '#d0ecff';

		id_selected_previous = id_in;
	}
}

//   GOOGLE ANALYTICS     
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
