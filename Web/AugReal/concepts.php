<?php
$concepts = $dbHandle->getConcepts();

//--------- on click ------------
if (isset($_GET['nameconcept']))
	$name = $_GET['nameconcept'];
else // first time starting this page get first 
	$name = $concepts[0]['name'];

// if the first one is the one deleted, then get the second one
if (isset($_GET['deleteconcept']) && $_GET['deleteconcept']==$concepts[0]['name'])
		$name = $concepts[1]['name'];
//------------------------------------------------------
?>

<script type="text/javascript">
function toggleNewConcept(){
	if (document.getElementById('newconcept').style.display!=''){
		document.getElementById('newconcept').style.display='';
		document.getElementById('toggNewConc').innerHTML = 'Cancel';
	} else {
		document.getElementById('newconcept').style.display='none';
		document.getElementById('toggNewConc').innerHTML = 'Add Concept';
	}
}


/* -----------------------------------------------
 *				Estimate Features 
 * --------------------------------------------------*/
function beginFeatureTraining(button , name){
	button.innerHTML = "<img src='images/hourglass.png' width='20px'/>";
	button.style.background.color='rgba(0,0,0,0.5);';
	button.style.border = 'none';
	button.style.cursor = "default";
	
	var xmlhttp;
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
  		xmlhttp=new XMLHttpRequest();
  	}else{// code for IE6, IE5
  		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  	}
	xmlhttp.onreadystatechange=function()
  	{	
  		if (xmlhttp.readyState==4 && xmlhttp.status==200){
			if(xmlhttp.responseText == '1'){
				button.innerHTML = "<img src='images/greenCheckMark.png' width='20px'/>";
				button.style.background.color='rgba(0,0,0,0.5);';
				button.style.border = 'none';
				button.disabled = true;
			}else if(xmlhttp.responseText == '-1'){
				alert('server is overloaded with requests please try again later');
			}else{
				//tdElement.innerHTML = "<img src='VisRec/redX.png' width='30px' height='30px'>";
			}
		}
	};
	xmlhttp.open("POST","ajax.php",true);
	xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	var reqStr = "name=" + name;
	xmlhttp.send(reqStr);
}

/* ------------------------------------
 *         Upload Images and create a new Concept
 * 1) Zip :   execute the this php with POST 
 * 2) Flickr: execute the ajax.php with POST
 * --------------------------------------- */
function wrapper_js_NewConcept(){
	var uploadFromZip = false;

	
	if(document.getElementById("zipImages").value != "") {
		uploadFromZip = true;
	} 
	
	var cName = document.getElementById("conceptname").value;

	if(cName.length < 2 || (document.getElementById("conceptname").value == '')){
		document.getElementById("error2").innerHTML="concept name must have at least 2 characters";
		return;
	}

	var regex =  /^[a-zA-Z ]*$/gim;
	if(!regex.test(cName)){
		document.getElementById("error2").innerHTML="concept name invalid (only characters and whitespaces are allowed)";
		return;
	}

	// ZIP
	if(uploadFromZip){
		document.getElementById("info").innerHTML="<span class='infoMark'>!</span> Creating concept '" + cName + "' from zip file";
		document.getElementById("info").style.display="";
		document.getElementById("addconcept_form").submit();
	} else{
	//FLICKR
		var imagesno = document.getElementById("imageno").value;
		if(imagesno > 0 && imagesno < 5000){
			cName = cName.replace(/ /g,'+');
			
			var curElement = $("#row_last");

			curElement.before("<tr id='" + cName.replace(/\+/g,'_') + "'><td>" + 
					cName + "</td><td>" + imagesno + "<img src='images/hourglass.png' width='10px' style='margin-left:10px'>" + 
					"</td><td colspan=3'>Not Available yet</td></tr>");

			var posting = $.post("ajax.php", {cname : cName, imagesnum : imagesno});
			posting.done(function (data){
				var parts = data.split(";");

				if(parts[0] == "-1"){
					$("#" + parts[1].replace(/\+/g,'_')).remove();
				}else if(parts[0] == imagesno){
					$("#" + parts[1].replace(/\+/g,'_')).html('<td><a href="?id='+ idEntity +'&nameconcept=' + 
							encodeURIComponent(parts[1]) +'">' + parts[1] + '</a></td><td>' + parts[0] + 
							'</td><td><button type=button onclick="beginFeatureTraining(this,\''+ parts[1] + 
							'\');">Calculate</button></td><td>Today</td><td>-</td></tr>');

					$("#" + parts[1].replace(/\+/g,'_')).find("td").css('background-color', '#AFA');

					window.location.href = '?id='+ idEntity +'&nameconcept=' + encodeURIComponent(parts[1]);
				}else{
					$("#" + parts[1].replace(/\+/g,'_')).html('<td><a href="?id='+ idEntity +'&nameconcept=' + encodeURIComponent(parts[1]) +'">' 
							+ parts[1] + '</a></td><td>' + parts[0] + 
							'</td><td><button type=button onclick="beginFeatureTraining(this,\'' 
							+ parts[1] + '\');">Calculate</button></td><td>Today</td><td>-</td></tr>');

					$("#" + parts[1].replace(/\+/g,'_')).find("td").css('background-color', '#FFA');
					
				}
			});
		}else{
			document.getElementById("error2").innerHTML="number of images must be between 0 and 5000";
		}
	}
}
</script>

<script type="text/javascript">
var imagesSelected = "";

function toggleImg(img)
{
	<?php if(!$MODIFY) { echo 'return;';}?>
	
	var test = img.style.opacity;
	if(img.style.opacity=="0.3")
	{
		var src = img.src;
		if(src.indexOf('/') >=0)
			src = src.substring(src.lastIndexOf('/')+1);
		
		var replaceText = ";"+src;
		imagesSelected = imagesSelected.replace(replaceText,'');
		img.style.opacity="1.0";
	}
	else{
		img.style.opacity ="0.3";
		var src = img.src;
		if(src.indexOf('/') >= 0) {
			src = src.substring(src.lastIndexOf('/')+1);
		}
		imagesSelected += ';' + src;
	}
}

function deleteImageAction()
{
	
	if(imagesSelected!=""){
		var form = document.getElementById("imagesSelectedForm");
		var field = document.getElementById("imagesToDelete").value = imagesSelected;
		document.getElementById("conceptNameHiddenForm").value="<?php echo $name;?>";
		form.submit();
		
	}
}
</script>



<?php
//-----------------------------------------------------------
//        -- ZIP UPLOAD and Create concept ---------------
//-----------------------------------------------------------
if (isset($_FILES["zipImages"]) && $_FILES["zipImages"]["name"]!=""){

	if ($_FILES["zipImages"]["error"] > 0) {
		
	}else{
		
		$DIR_STR = "ReconEngine/content/".$_POST["concept"];
		$succMkDir = mkdir($DIR_STR,0755,true);
		if (!$succMkDir)
			print_r ( error_get_last() );

		$targetPath = "ReconEngine/content/".$_POST["concept"]."/temp.zip";
		
		move_uploaded_file($_FILES["zipImages"]["tmp_name"], $targetPath);
		$zip = new ZipArchive;
		$res = $zip->open($targetPath);
		if ($res === TRUE) {
			$zip->extractTo("ReconEngine/content/".$_POST["concept"]);
			$zip->close();
			unlink($targetPath);
			// unlink($ModelsPathAtServer.$ModelTextureFileName); // Uncomment if you want to delete the zip file
		}
		$file_count = 0;
		$zip_directory = "ReconEngine/content/".$_POST["concept"];
		if($handle = opendir($zip_directory)){
			while (($file = readdir($handle)) !== false){
				if(!in_array($file, array('.','..')) && 
					(endsWith($file,".jpg") || endsWith($file,".jpeg") || endsWith($file,".png"))){
					$file_count++;
				}else{
					if(!in_array($file, array('.','..'))){
						if(is_dir($zip_directory.'/'.$file))
							Deleter::deleteDir($zip_directory.'/'.$file);
						else
							unlink($zip_directory.'/'.$file);
					}
				}
			}//while
		}//if($handle etc.)

		if($file_count!=0){
			echo '<p id="informationbox"><span class="infoMark">!</span>'.
						'zip uploaded successfully ('.$file_count.' images)</p>';
			$dbHandle->insertConcept($_POST["concept"],$file_count);
			
			echo '<script>window.location.href = "?id='.$row['id'].'&nameconcept='. $_POST["concept"].'"</script>';
		} else {
			echo '<p id="errorbox"><img src="images/exmark.png" class="exmarkimg" align="left" height="20px"/>'.
					'Warning: Concept not created.. <br> No images found in zip</p>';
			rmdir("ReconEngine/content/".$_POST["concept"]);
		}
	}// 
}//------ end of POST ZIP ---------------------------


//============= delete a concept ========
if(isset($_GET["deleteconcept"]))
{
	if($dbHandle->conceptExistsInDb($_GET["deleteconcept"])){
		echo '<p id="informationbox" align="center" ><span class="infoMark">!</span>';
		if($MODIFY)
			$dbHandle->deleteConcept($_GET["deleteconcept"]);
		else
			echo 'You need to login to delete a concept';
		echo '</p>';
	}
}

//=============  Create a CONCEPT from FLICKR ==========================
$buildStr = "";
if(isset($_POST['concept']) && isset($_POST['noimages'])){

	// replace spaces with +
	$_POST['concept'] = str_replace(' ','+',$_POST['concept']);

	$buildStr ="ReconEngine/recognize -c ".$_POST['concept']. " ".$_POST['noimages'];

	if(!$dbHandle->conceptExistsInDb($_POST['concept'])){
		exec($buildStr,$output,$return);
		if(!$return){
			echo '<p id="informationbox"><span class="infoMark">!</span></p>';
			
			// Count Images 
			$i = 0;
			$dir = 'ReconEngine/content/'.$_POST['concept'].'/';
			if ($handle = opendir($dir))
				while (($file = readdir($handle)) !== false)
					if (!in_array($file, array('.', '..')) && !is_dir($dir.$file))
						$i++;
			
			// Print out how many were in the directory
			if($i == 0){
				echo "0 results for tag : ".$_POST['concept'];
				$dir = 'ReconEngine/content/'.$_POST['concept'].'/';
				foreach (scandir($dir) as $item) {
					if ($item == '.' || $item == '..') continue;
					unlink($dir.DIRECTORY_SEPARATOR.$item);
				}
				rmdir($dir);
			} else if($i < $_POST['noimages'])
				if($i == 1)
					echo "Found only 1 image..";
			else
				echo "Found only $i images..";
			else
				echo "Downloaded Images successfully";
			
			if($i != 0)
				$dbHandle->insertConcept($_POST['concept'], $i);
			
			
		}else{ //if not return
			echo '<p id="informationbox" align ="center" ><span class="infoMark">!</span></p>';
			echo " Error while downloading images";
		}
	}else {//if concept exist in database
		echo '<p id="informationbox"><span class="infoMark">!</span>Concept already exists in database</p>';
	}
}
?>

<!-- TABLE * -->
<table class="tablelevel4"><tr><td>

<!-- =============================================
				TABLE OF CONCEPTS
	============================================ -->
<table id="concepts" class='edittable' >
<col width="20%">
<col width="45%">
<col width="15%">
<col width="15%">
<col width="5%">

<?php if ($MODIFY_RECOGNITION)
	echo '<tr><th colspan="5"><button style="float:right" type="button" id="toggNewConc" onclick="toggleNewConcept();">Add Concept</button></th></tr>';
?>

<tr><th>Concept Name</th><th>Number of images</th><th>Features Available</th><th>Created</th><th>Delete</th></tr>
	<?php 
	$concepts = $dbHandle->getConcepts();
	foreach($concepts as $row_concept){

		$styleSelected = '';
		if ($name == $row_concept['name'])
			$styleSelected = 'style="background-color:#999"';

		echo '<tr '.$styleSelected.'><td ><a href="?nameconcept='.$row_concept["name"].'&id='.$row['id'].'">'.$row_concept["name"].
						'</a></td><td>'.$row_concept["image_count"].'</td>';
		echo '<td>';
		switch($dbHandle->featuresAvailable($row_concept["name"]))
		{
			case 0:	echo '<button type=button onclick="beginFeatureTraining(this,\''.$row_concept["name"].'\');">Calculate</button>';	break;
			case 1:	echo '<img src="images/greenCheckMark.png" width="20px">';	break;
			case 2:	echo '<img src="images/hourglass.png" width="20px">';		break;
			default: echo '<button type=button onclick="beginFeatureTraining(this,\''.$row_concept["name"].'\');">Calculate</button>';
		}
		echo '</td>';
		echo '<td><span style="font-size:9pt">'.substr($row_concept["date"],0,10).'</span></td>';//Date
		
		if ($MODIFY_RECOGNITION){
		echo <<<EOT
			<td><img src="images/trash.png" class="clickableImage" width=15
				onclick="if (confirm('Are you sure you want to delete this concept?'))
					window.location='?id={$row["id"]}&deleteconcept={$row_concept["name"]}'; return false" /></td>
EOT;
		} else {
			echo '<td>Denied</td>';
		}
		echo '</tr>';
	}
	
	
	
	?>
	
	
	
	<tr><td colspan="5" align="center" id="error2" style="color: #F00"></td></tr>
	
	<tr id='row_last'></tr>
	
</table>


		






</td><td> <!-- Images start here -->

<?php

$page = 0;
$imagesPerPage = 9;
if(isset($_GET["ipp"])) {$imagesPerPage = $_GET["ipp"] ;}
if(isset($_GET["page"])){$page          = $_GET["page"];}

// Images to delete from the concept
if(isset($_POST["images"])){
	$imagesToDelete = explode(";",$_POST["images"]);
	for($i=1;$i<(count($imagesToDelete));$i++){
		$path = $_POST["conceptName"]."/".$imagesToDelete[$i];
		$dbHandle->deleteImage($_POST["conceptName"],$path);
	}
}


// ---- Principal function Display Images in a table ----
$row_concept = $dbHandle->getConcept($name);


$limit_top = $page*$imagesPerPage;
$limit_bot = $limit_top + $imagesPerPage;

//------- Number of Images -------
if($row_concept["image_count"] < $limit_bot)
	$numberOfImages = $row_concept["image_count"];
else
	$numberOfImages = $limit_bot;

//----- Images file paths -----
$imagesFilePaths = array();
if ($handle = opendir('ReconEngine/content/'.$name)) {
	while (false !== ($entry = readdir($handle)))
		if ($entry != "." && $entry != ".." && !endsWith($entry,".bin"))
		array_push($imagesFilePaths,$entry);
	closedir($handle);
}

//---- Images per line --------------
$printImages = array();
for($i = ($limit_top + 1); $i <= $numberOfImages; $i++)
	array_push($printImages,$imagesFilePaths[$i-1]);

	//shuffle($imagesFilePaths);
echo '<table><tr><td>'; // table of images + GUI elements of images

echo '<table border="1">';
echo '<tr class="images">';
	$count = 0;
	foreach($printImages as $imgPath){
	echo '<td><img src="ReconEngine/content/'.urlencode($name).'/'.$imgPath.
	'" height="100px" width="140px" onclick="toggleImg(this);"></td>';
			$count++;
			if($count%3 == 0)
				echo '</tr><tr class="images">';

				if($count==$numberOfImages)
					break;
}
echo '</tr>';
echo '</table>';

// ---------- Images per page handling ----------

if($imagesPerPage > $numberOfImages){$imagesPerPage = $numberOfImages;}

if($imagesPerPage < 1)
	$imagesPerPage = 9;

$showing_top = $page*$imagesPerPage + 1;
$showing_bot = 0;

if($numberOfImages > (($page+1) *$imagesPerPage))
	$showing_bot = (($page+1) * $imagesPerPage);
else
	$showing_bot = $numberOfImages;

// Showing 1 - 9 images out of 3687
echo $showing_top.' - '.$showing_bot.' / '.$row_concept['image_count'];
if($page!=0)
	echo '<a href="?nameconcept='.urlencode($name).'&page='.($page-1).'&ipp='.$imagesPerPage.'&id='.$row['id'].'" style="margin-left:10px">Previous page</a>';


if($row_concept['image_count'] > (($page+1) *$imagesPerPage))
	echo '<a href="?nameconcept='.urlencode($name).'&page='.($page+1).'&ipp='.$imagesPerPage.'&id='.$row['id'].'" style="margin-left:10px">Next page</a>';
?>

<span style='margin-left:20px'>
	Images per page<input type="text" id="imagespp" style="width:25px" name="imagPerPage" value="<?php echo $imagesPerPage; ?>" />
	<input type="button" onclick="window.location='?id=<?php echo $row['id'] ?>&nameconcept=<?php echo urlencode($name); 
	?>&page=0&ipp='+document.getElementById('imagespp').value" value="OK"/>
</span>
<!-- End of Images per page -->

<!--  Image deletion handler  -->
<form id="imagesSelectedForm" method="post" action="" style='display:none'>
	<input type="text" name="images" id="imagesToDelete" style='display:none'/>
	<input type="text" name="conceptName" id="conceptNameHiddenForm" style='display:none'/>
</form>
<br></br>





<?php
if($MODIFY_RECOGNITION)
	echo '<button type="button" onclick="deleteImageAction();">Delete selected images</button>';
else 
	echo 'You are not authorized to delete images';
?>

</td></tr></table> <!--  Table Images -->



</td></tr></table> <!-- Table * -->

<!-- NEW CONCEPT-->
<div id='newconcept' style='display:none'>
<form name="addconcept_form" id="addconcept_form" action="" method="post" enctype="multipart/form-data" >
	<input type="text" size="10px" id="conceptname" name="concept" placeholder="Give a name"/>
	Get images from Flickr</b><input type="number" id="imageno" name="noimages" placeholder='Number' style='width:70px'/>or
	<input type="file" name="zipImages" id="zipImages"/>(zip)
	<button type="button" onclick="document.getElementById('toggNewConc').click();wrapper_js_NewConcept();">Start Upload</button>
</form>	
</div>