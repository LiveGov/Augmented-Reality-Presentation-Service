<?php

// Asynchronous requests on server 


require 'DatabaseHandler.class.php';
if(isset($_POST))
{
	$dbHandle = new DatabaseHandler();

	/* TRAINING MODEL
	 * INPUT:  name
	 * OUTPUT: 1 (success) or -1 (error) */
	if(isset($_POST["name"]))
	{
		$_POST['name'] = str_replace(' ','+',$_POST['name']);
		if($dbHandle->featuresAvailable($_POST["name"])==0)
		{
			if($dbHandle->getNumberOfConceptsCurrentlyTraining()>3){
				echo '-1';
			}else{
				$dbHandle->setFeaturesAvailableStatus($_POST["name"],2);
				$buildStr = "ReconEngine/recognize -fi ".$_POST["name"];
				exec($buildStr,$output,$return);
				if(!$return){
					$dbHandle->setFeaturesAvailableStatus($_POST["name"],1);
					echo '1';
				}
				else{
				$dbHandle->setFeaturesAvailableStatus($_POST["name"],0);
				}
			}	
		}
		$dbHandle->closeDb();
	}
	
	//DOWNLOAD IMAGES FROM FLICKR
	//INPUT: cname, imagesno
	//OUTPUT: number of images or -1 for error
	if(isset($_POST["cname"]) && isset($_POST["imagesnum"]))
	{
		$cname = $_POST["cname"];
		$cname = str_replace(' ','+',$cname);
		$buildStr = "ReconEngine/recognize -c ".$cname. " ".$_POST['imagesnum'];
		if(!$dbHandle->conceptExistsInDb($cname))
		{
        	$dbHandle->insertConcept($cname,$_POST["imagesnum"]);
			$dbHandle->setFeaturesAvailableStatus($cname,2);
			exec($buildStr,$output,$return);
        	if(!$return){
               	$i = 0;
               	$dir = 'ReconEngine/content/'.$cname.'/';
               	if ($handle = opendir($dir)) {
                  	while (($file = readdir($handle)) !== false){
                       	if (!in_array($file, array('.', '..')) && !is_dir($dir.$file))
                       		$i++;
                	}
				}
            	// prints out how many were in the directory
            	if($i == 0){
              		echo "-1;$cname";
               		$dir = 'ReconEngine/content/'.$cname.'/';
               		foreach (scandir($dir) as $item) {
                  		if ($item == '.' || $item == '..') continue;
                   		unlink($dir.DIRECTORY_SEPARATOR.$item);
               		}
               		rmdir($dir);
            	}
            	else if($i < $_POST['imagesnum'])
            		if($i == 1){
              			echo "1;$cname";
						$dbHandle->updateImagesNo($cname,$i);
					}
        	   		else{
              			echo "$i;$cname";
						$dbHandle->updateImagesNo($cname,$i);
					}
            		if($i != 0){
						if($i!=$_POST["imagesnum"]){
							$dbHandle->updateImagesNo($cname,$i);
							$dbHandle->setFeaturesAvailableStatus($cname,0);
						}
					else{
						echo "$i;$cname";
						$dbHandle->setFeaturesAvailableStatus($cname,0);
					}
				}
				else{
					$dbHandle->deleteConcept($cname, false);
				}
			}//if(!return)
			else{
				echo "-1;$cname";
				$dbHandle->deleteConcept($cname, false);
			}
		}//if concept already exists
		else{
			echo "-1;$cname";
			$dbHandle->closeDb();
		}
	}
}
else{
	print_r($_POST);
}
?>
