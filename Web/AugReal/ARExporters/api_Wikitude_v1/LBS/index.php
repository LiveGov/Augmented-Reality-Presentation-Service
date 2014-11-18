<!-- 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 -->
<html>
<head>

<meta name='viewport' content="width=device-width"  />


<title>My ARchitect World</title>

<script src="architect://architect.js"></script>
<script src="ARchitect_Library/js/ade.js"></script>
<link rel="stylesheet" href="ARchitect_Library/css/poi-radar.css" /> <!-- positioning of poi-radar -->
<link rel="stylesheet" 	href="ARchitect_Library/jquery/jquery.mobile-1.3.2.min.css" /><!-- jquery mobile CSS -->
<link rel="stylesheet" 	href="ARchitect_Library/jquery/jquery-mobile-transparent-ui-overlay.css" /><!-- required to set background transparent & enable "click through" -->
<script type="text/javascript"	src="ARchitect_Library/jquery/jquery-1.9.1.min.js"></script><!-- jquery JS files -->
<script type="text/javascript" 	src="ARchitect_Library/jquery/jquery.mobile-1.3.2.min.js"></script> 
<script src="ARchitect_Library/js/marker.js"></script> <!-- marker representation-->
<script type="text/javascript" 	src="ARchitect_Library/js/nativePoiDetail.php"></script> <!-- World logic-->
<script type="text/javascript" src="ARchitect_Library/js/radar.js"></script> <!-- radar component -->
</head>
<body>

	<?php

	//--------- Lang ----------
	$headers = apache_request_headers();
	$lang = substr($headers['Accept-Language'],0,2);
		
	if(isset($lang)){
		if ($lang=='es' || $lang=='eu'){
			$Reload = 'Recargar';
			$Range  = 'Alcance';
			$Details = 'Detalles';
			$TryLoc = 'Tratando de averiguar dónde se encuentra';
			$Close  = 'Cerrar';
			$Distance= 'Distancia';
			$MoreOffApp = 'Más en app oficial';
			$Visible = 'Visible';
		} else {
			$Reload = 'Reload';
			$Range  = 'Range';
			$Details = 'Details';
			$TryLoc = 'Trying to find out where you are';
			$Close  = 'Close';
			$Distance= 'Distance';
			$MoreOffApp = 'More in official app';
			$Visible = 'Visible';
		}
	} else {
		$Reload = 'Reload';
		$Range  = 'Range';
		$Details = 'Details';
		$TryLoc = 'Trying to find out where you are';
		$Close  = 'Close';
		$Distance= 'Distance';
		$MoreOffApp = 'More in official app';
		$Visible = 'Visible';
	}

	//----------- FindDevicePlatform -------------------
 	$deviceplatform = null;
 		
 	foreach (getallheaders() as $name => $value) {
 		if ($name=='User-Agent')
 			$deviceplatform = $value;
 	}
 		
 	if (strpos($deviceplatform,'android'))
 		$deviceplatform = 'android';
 	else if (strpos($deviceplatform,'iPhone'))
 		$deviceplatform= 'iphone';
 	else
 		$deviceplatform= 'web';
	//---------------------------------------------
	?>

	<div data-role="page" id="page1" style="background: none;">
	    <?php print '<div style="display:none;" id="deviceplatform" data="'.$deviceplatform.'"></div>' ?>
	    <?php print '<div style="display:none;" id="lang" data="'.$lang.'"></div>' ?>
		<!-- MAIN PAGE CONTENT -->
		<!-- header of UI holding feature buttons -->
		<div id="header-status" data-role="header" data-position="fixed"
			data-theme="c">
			<a href="javascript: World.showRange();" data-icon="gear"
				data-inline="true" data-mini="true"><?php echo $Range?></a> <a
				href="javascript: World.reloadPlaces()" data-icon="refresh"><?php echo $Reload?></a>
			<h1></h1>
		</div>

		<!-- the radar div - Wikitude SDK radar will be drawn into this div -->
		<div class="radarContainer_left" id="radarContainer"></div>

		<!-- transparent footer-->
		<div data-role="footer" class="ui-bar" data-theme="f"
			data-position="fixed" style="text-align: center;">

			<!-- small status-button -->
			<a style="text-align: right;" id="popupInfoButton" href="#popupInfo"
				data-rel="popup" data-role="button" class="ui-icon-alt"
				data-inline="true" data-transition="pop" data-icon="alert"
				data-theme="e" data-iconpos="notext">Log</a>
			</p>

			<!-- popup displayed when button clicked -->
			<div data-role="popup" id="popupInfo" class="ui-content"
				data-theme="e" style="max-width: 350px;">
				<p style="text-align: right;" id="status-message"><?php echo $TryLoc?></p>
			</div>

		</div>

		<!-- PANELS, ONLY VISIBLE ON DEMAND -->
		<!-- panel containing POI detail information -->
		<div data-role="panel" id="panel-poidetail" data-position="right"
			data-display="overlay" style="background-color: #F0F0F0;"
			data-theme="c">

			<!-- header with "close" button -->
			<div data-role="header" data-theme="c">
				<h1><?php echo $Details?></h1>
				<a href="#header" data-rel="close"><?php echo $Close?></a>
			</div>

			<!-- content of POI detail page, you may also add thumbnails etc. here if you like -->
			<div data-role="content">

				<!-- title -->
				<h3 id="poi-detail-title"></h3>

				<!-- description -->
				<h4 id="poi-detail-description"></h4>

				<!-- distance -->
				<h4><?php echo $Distance?>: <a id="poi-detail-distance"></a></h4>

				<!-- more button-->
				<!-- 
				<a href="javascript: World.onPoiDetailMoreAButtonClicked();"
					data-role="button" data-icon="arrow-r" data-iconpos="right"
					data-inline="true">More in Web page</a>
					 -->
					<a
					href="javascript: World.onPoiDetailMoreBButtonClicked();"
					data-role="button" data-icon="arrow-r" data-iconpos="right"
					data-inline="true"><?php echo $MoreOffApp?></a>
			</div>
		</div>

		<!-- range panel -->
		<div data-role="panel" id="panel-distance" data-position="left"
			data-display="overlay" style="background-color: #F0F0F0;"
			data-theme="c">

			<!-- header with close button -->
			<div data-role="header" data-theme="c">
				<h1><?php echo $Range?></h1>
				<a href="#header" data-rel="close"><?php echo $Close?></a>
			</div>

			<!-- distance information, calculated/updated in code  -->
			<div data-role="content">

				<!-- Range in m/km-->
				<h4><?php echo $Range?>:<a id="panel-distance-value"></a></h4>

				<!-- Amount of visible places -->
				<h4><?php echo $Visible?>: <a id="panel-distance-places"></a></h4>

				<!-- default slider -->
				<input id="panel-distance-range" type="range" data-highlight="true"
					name="rangeSlider" min="0" max="100" value="100"
					data-show-value="false" step="5" data-popup-enabled="false"/>
			</div>
		</div>
	</div>
</body>
</html>