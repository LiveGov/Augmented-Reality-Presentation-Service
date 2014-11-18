<script type="text/javascript">

/* ------------------------------------------------
 * 				New Model button
 * --------------------------------------------------*/
function toggleNewModel(){
	if (document.getElementById('tableNewModel').style.display!=''){
		document.getElementById('btnewmodel').innerHTML = 'Cancel';
		document.getElementById("tableNewModel").style.display='';
	}else {
		document.getElementById('btnewmodel').innerHTML = 'Train a new model';
		document.getElementById("tableNewModel").style.display='none';
	}
}

/* ------------------------------------------------------------
 *			MODELS: Auto select Complementary Concepts button
 *	------------------------------------------------------------ */
function autoComplementarySelect(){
	var positives = new Array();
	var positive = $(".positive").find("input:checkbox:checked");
	positive.each(function(index){
		positives.push($(this).closest("tr").attr("id"));
	});

	if(positives.length < 1)
		return;
	
	var checkboxes = $("input:checkbox");
	checkboxes.each(function( index ){
		if(positives.indexOf($(this).closest("tr").attr("id")) < 0){
			if($(this).closest("td").attr("class") == "negative"){
				$(this).prop('checked',true);
			}else{
				$(this).prop('checked',false);
			}
		}else{
			if($(this).closest("td").attr("class") == "negative")
				$(this).prop('checked',false);
		}
		console.log( index + ": " + $(this).text() );
	});
}

/* ---------------------------------------
 *              Train Model 
 * -----------------------------------------  */
function trainModel(){
	var errorId = 0;
	/*  1 : name not selected
		2 : no positive folder
		3 : same folder selected as positive and negative
	*/

	if (document.getElementById("modelname").value=='')
		errorId = 1; // no name selected

	if(errorId == 0){
		var namesChecked = new Array();
		var form = document.getElementById("createmodel");
		var elements = form.elements;
		//get checked items
		for( var i = 0; i < elements.length; i++)
			if(elements.item(i).checked)
				namesChecked.push(elements.item(i).value);

		//check if zero positives
		var count = 0;
		for(var i = 0; i < namesChecked.length; i++)
			if(namesChecked[i].charAt(namesChecked[i].length-1) == '!')
				count ++;

		if(count==0)
			errorId = 2;
	}

	if(errorId == 0){
		//check if same concept as positive and negative
		var names = new Array();
		for(var i = 0; i < namesChecked.length; i++)
			names.push(namesChecked[i].substr(0,namesChecked[i].length-1));


		names.sort();
		var last = names[0];
		for(var i = 1; i < names.length; i++){
			if(names[i] == last) 
				errorId = 3;
			last = names[i];
		}
	}

	var regex =  /^[a-zA-Z_0-9]*$/gim;
	if(!regex.test(document.getElementById("modelname").value))
		errorId = 4;

	switch(errorId){
		case 0:
			document.getElementById("info").innerHTML = "<span class='infoMark'>!</span>Training model with name " + document.getElementById("modelname").value + "..";
			document.getElementById("info").style.display = "";
			window.scrollTo(0,0);
			form.submit();
			break;
		case 1:
			document.getElementById("error1").innerHTML = "no name selected";
			document.getElementById("error1").style.display = "";
			break;
		case 2:
			document.getElementById("error1").innerHTML = "no positive folder selected";
			document.getElementById("error1").style.display = "";
			break;
		case 3:
			document.getElementById("error1").innerHTML = "cant select same folder as positive and negative";
			document.getElementById("error1").style.display = "";
			break;
		case 4:
			document.getElementById("error1").innerHTML = "model name must not contain any special characters";
			document.getElementById("error1").style.display = "";
			break;
	}
}
</script>


<?php
//=========== delete a model ====================
if(isset($_GET["deletemodel"]))
{
	if($dbHandle->modelExistsInDb($_GET["deletemodel"])){
		echo '<p id="informationbox"><span class="infoMark">!</span>';
		if($MODIFY)
			$dbHandle->deleteModel($_GET["deletemodel"]);
		else
			echo 'You need to login to delete a model';
		echo '</p>';
	} else
		echo $_GET["deletemodel"]." model does not exist";
}

//========== Create a model =======================
if(isset($_POST['modelname'])){
	$positives = array();
	$negatives = array();
	$modelname;
	foreach ($_POST as $value){
		if(strcmp(substr($value,-1),"!")==0){
			array_push($positives,substr($value,0,-1));
		}
		else if(strcmp(substr($value,-1),"@")==0){
			array_push($negatives,substr($value,0,-1));
		}
	}
	$modelname = $_POST['modelname'];
	$buildStr = "ReconEngine/recognize -t";
	 
	 
	foreach ($positives as $positive)
		$buildStr .= (" -p ".$positive);
	foreach ($negatives as $negative)
		$buildStr .= (" -n ".$negative);
	 
	$buildStr .= " -s ".$modelname;
	if(featuresAvailable($positives,$negatives,$dbHandle))
		$buildStr = str_replace("-t","-tpi",$buildStr);
	//echo "Training model please wait... ";
	//ob_flush();
	//flush();
	exec($buildStr,$output,$return);
	 
	if(!$return){
		echo '<p id="informationbox"><span class="infoMark">!</span>Model Created</p>';
		$dbHandle->insertModel($modelname, $positives, $negatives);
	}else {
		echo '<p id="informationbox"><span class="infoMark">!</span>';

		if ($return=='127')
			echo " ERROR 127: The command ReconEngine/recognize is not found";
		elseif ($return=='126')
			echo " ERROR 126: The command ReconEngine/recognize is found but not executable";
		else
			echo " ERROR ".$return." Unknown";
		
		echo '</p>';
	}
}
?>
<!--===============================
			TABLE OF MODELS
	=============================== -->
<table class="tablelevel4"><tr><td>
<table class="edittable">
<col width="15%">
<col width="15%">
<col width="20%">
<col width="15%">
<col width="15%">
<col width="10%">
<col width="10%">
<?php if ($MODIFY_RECOGNITION)
		echo "<tr><th colspan=7><button style='float:right' type='button'
					id='btnewmodel' onclick='toggleNewModel();'>Train a new model</button></th></tr>";
?>		
		<tr><th>Name</th><th>Positive concepts</th><th>Negative concepts</th>
		<th>Threshold</th><th>Created</th><th>Associated AR Entity</th><th>Action</th></tr>
	<?php 
	$models = $dbHandle->getModels();
	foreach($models as $row_models){
	
		echo '<tr>';
		echo '<td>'.$row_models["name"].'</td>';
		echo '<td>'.str_replace(";",", ",substr($row_models["classes_positive"],0,-1)).'</td>';
		echo '<td><span style="font-size:9pt">'.str_replace(";",", ",substr($row_models["classes_negative"],0,-1)).'</span></td>';
		echo '<td>'.$row_models["threshold"].'</td>';
		echo '<td><span style="font-size:9pt">'.$row_models["date"].'</span></td>';
		echo '<td><span style="font-size:9pt">'.implode(',',$dbHandle->getEntitiesAssociated($row_models["name"])).'</span></td>';
		echo '<td>';
		
		if ($MODIFY_RECOGNITION){
		echo <<<EOT
		<img class="clickableImage" style="margin-right:10px" src="images/trash.png" width=15 onclick="if (confirm('Are you sure you want to delete this model?'))
		window.location='?deletemodel={$row_models["name"]}&id={$row['id']}';
		return false"/>
EOT;
		}
		echo '<a href="ReconEngine/models/'.$row_models["name"].'.bin"><img src="images/download.png" class="clickableImage" width=15/></a></td>';
		echo '</tr>';
	}
	?>
</table>



<!-- NEW MODEL -->
<table id="tableNewModel" style="display: none" class="edittable">
	<form name="createmodel" id="createmodel" action="" method="post">
	
	<?php echo '<input type="text" name="idEntity" value="'.$row['id'].'" style="display:none">';?>
	
	<tr><td>Give Model name<br><input type="text" id="modelname" name="modelname" /></td>
		<td><table>
		<tr><th colspan=4>Select concepts</th></tr>
		<tr><th>Name</th><th>Positive</th>
			<th>Negative<br><button type="button" onclick="autoComplementarySelect();" id="autobutton">Complementary</button></th>
			<th>Features Available</th>
		</tr>
		<?php
			$concepts = $dbHandle->getConcepts();
			foreach($concepts as $row_concept){
				echo '<tr id='.str_replace('+','_',$row_concept['name']).' class="highlightrow">';
				echo '<td>'.$row_concept['name'].'</td>';
				echo '<td class="positive"><input type="checkbox" name="'.$row_concept['name'].'!" value="'.$row_concept['name'].'!"></td>';
				echo '<td class="negative"><input type="checkbox" name="'.$row_concept['name'].'@" value="'.$row_concept['name'].'@"></td>';
				
				if($dbHandle->featuresAvailable($row_concept['name'])==0)
					echo '<td><img src="images/redX.png" width="15px"></td>';
				else if($dbHandle->featuresAvailable($row_concept['name'])==1)
					echo '<td><img src="images/greenCheckMark.png" width="15px"></td>';
				else 
					echo '<td><img src="images/hourglass.png" width="15px"></td>';
				
				echo '</tr>';
			}
		?>
		</table></td>
		<td >
			<button type="button" onclick="trainModel();">Start training</button>
		</td>
		</tr>
		<tr><td colspan="4" align="center" id="error1" style="color: #F00"></td></tr>
	</form>
</table>
</td></tr></table>