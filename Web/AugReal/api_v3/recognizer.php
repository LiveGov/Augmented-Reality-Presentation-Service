<?php

require "../DatabaseHandler.class.php";
//print_r($_FILES);

//get the correct model names based on the application id
if(isset($_GET["diag"]))
{
	
	$str = exec('../ReconEngine/recognize -r diag.jpg', $output, $return_var);
	$dbHandle = new DatabaseHandler();
	echo '../ReconEngine/diag.jpg';
	echo ';';
	if($return_var == 0)
	{
		
		$modelsToRecognize = $dbHandle->getAllModelNames();;
		$names = array();
		$predictions = array();
		foreach($output as $line)
		{
			if($line[0] == 'R')
				continue;
			$parts = preg_split('/[\s]+/',$line);
			if(in_array($parts[0],$modelsToRecognize))
			{
				array_push($names,$parts[0]);
				array_push($predictions,floatval($parts[1]));
			}
		}
		if(count($names)>0)
		{
			$max = max($predictions);
			$idx = array_search($max,$predictions);
			echo $names[$idx];
			echo ';';
			echo ($max - $dbHandle->getThresholdForModelWithName($names[$idx]));
			echo ';';
			echo $dbHandle->getModelIdForName($names[$idx]);
		}
	}
	else
	{
		echo 'ERROR';
	}
}
else
{
	$dbHandle = new DatabaseHandler();
	if($_POST["app_id"] == -1)
	{
	  	$modelsToRecognize = $dbHandle->getAllModelNames();
	}
	  else
	{
	 	$modelsToRecognize = $dbHandle->getModelsBasedOnApplicationId($_POST["app_id"]);
	}
	
	$target_path = "../ReconEngine/image.jpg";
	
	if(move_uploaded_file($_FILES['upload']['tmp_name'],$target_path))
	{
		chmod($target_path,0755);
		//echo 'completed';
		$str = exec('../ReconEngine/recognize -r image.jpg', $output, $return_var);
		
		
		
		//$str = exec('who', $whoami, $ret);
		//print_r($whoami);
		//print_r($output);	
		//echo $str;
	
		if($return_var==0){
		//print_r($output);
		//echo 's';
			$names = array();
			$predictions = array();
			foreach($output as $line)
			{
				//echo $line;
				if($line[0] == 'R')
					continue;
				$parts = preg_split('/[\s]+/',$line);
				if(in_array($parts[0],$modelsToRecognize))
				{
					array_push($names,$parts[0]);
					array_push($predictions,floatval($parts[1]));
					//array_push($thresholds, floatval($dbHandle->getThresholdForModelWithName($parts[0])));
				}
			}
			if(count($names)>0)
			{
				$max = max($predictions);
				$idx = array_search($max,$predictions);
				echo $names[$idx];
				echo ';';
				echo ($max - $dbHandle->getThresholdForModelWithName($names[$idx]));
				echo ';';
				echo $dbHandle->getModelIdForName($names[$idx]);
			}
			//print_r($names);
			//print_r($predictions);
			/* old
			if(count($names)>0)
			{
				$confidence = array();
				for($i = 0; $i < count($names); $i++)
				{
					array_push($confidence,($predictions[$i]-$thresholds[$i]));
				}
				//print_r($confidence);
				$max = max($confidence);
				$idx = array_search($max,$confidence);
				//echo 'idx = '.$idx;
				//echo 'max = '.max($predictions);
				echo $names[$idx];
				echo ';';
				echo $confidence[$idx];
				echo ';';
				echo $dbHandle->getModelIdForName($names[$idx]);
			}*/
		}
	} else {
		print_r(error_get_last());
	}	
}
?>