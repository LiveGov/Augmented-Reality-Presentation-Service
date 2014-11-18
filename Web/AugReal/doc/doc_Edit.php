<html>

<header>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="theme.css">
</header>
<body>

<?php
require "Functions.php";
$server = "";
$path = "";
$server = "http://".$_SERVER['SERVER_NAME'];
$path = $server."/AugReal/api_v3/";
?>

<h1>Edit data in AR-Server</h1>

<h2 class="title">Description</h2>
	<p id="desc">This URL provides the interface to edit an AR-Entity in AR Server</p>
	<h2 class="title">URL</h2>
	<div id="apiURLContainer">
			<?php echo "<span class='apiURL'><a href='".$path."ar_edit.php?'>".$path."ar_edit.php</a></span>"; ?>
	</div>

<h2 class="title">Parameters</h2>
	<span class="method">POST</span>
	<br>
	<br> Parameters (Required): username,password,id (of the AR_Entity to edit)<br>
	
	Optional:
	<table border="1" >
    	<tr><th>Parameter </td><th>Description                    </td><th>Example                    </td></tr>
    	<tr><td>title</td><td>The title of the Entity  </td><td>New school</td></tr> 
    	<tr><td>description</td><td>The description of the Entity  </td><td>A new school is planned ...</td></tr> 
    	<tr><td>id_external</td><td>Id to link with other server   </td><td>12345                      </td></tr>
    	<tr><td>id_app     </td><td>Id of the Application </td><td>1</td></tr> 
    	<tr><td>id_VRec    </td><td>Id of Visual recognition model</td><td>10                           </td></tr>
    	<tr><td>ids_qs     </td><td>Ids of the questions to associate</td><td>1,2,3,4                   </td></tr>
    	<tr><td>jpg        </td><td>Background image (urlpath)    </td><td>myim.jpg     </td></tr> 
    	<tr><td>latitude   </td><td>Latitude in decimal degrees   </td><td>40.5121                      </td></tr> 
    	<tr><td>longitude  </td><td>Longitude in decimal degrees  </td><td>23.0455                      </td></tr>
    	<tr><td>altitude   </td><td>Altitude in meters            </td><td>150                          </td></tr>
    	<tr><td>street     </td><td>Name of the street            </td><td>Park%20Avenue (%20 is for space)</td></tr> 
    	<tr><td>number     </td><td>Street number                  </td><td>12</td></tr>
    	<tr><td>postal     </td><td>Postal code                    </td><td>51231</td></tr>
    	<tr><td>country    </td><td>Country name                   </td><td>Greece;</td></tr> 
    	<tr><td>3dobj1     </td><td>File of 1st 3D Model in Wavefront obj format</td><td>mymodel.obj  </td></tr>
    	<tr><td>3dmtl1     </td><td>File of 1st material file of 3D Model</td><td>mymodel.mtl  </td></tr>
    	<tr><td>3dzip1     </td><td>File of 1st model textures in zipped folder containing textures e.g. image/texture1.jpg</td><td>mytextures.zip</td></tr>
    	<tr><td>3djpg1     </td><td>File of 1st model screenshot</td><td>myscreenshot.jpg  </td></tr>  
  </table>
	
	<div><br/></div> 
   HINT: Use 3dobj2,3dmtl2,3dzip2,3djpg2 for 2nd model, 3dobj2,3dmtl2,3dzip2,3djpg2 for 3rd model.</br>
    <p><br /></p> 
  </div>
	
<h2 class="title">Response</h2>
	<p id="desc">
	CODE 1: Success<br>
	Permission denial code 1: The superuser is not authorized to do any authorization action on behalf of other users<br>
	Permission denial code 2: The user is not authorized to do edit action
	</p>

<h2 class="title">Diagnostics</h2>
	
	
	<?php $response = postToURL($path."ar_edit.php",'0100','','','','');
				print_response($response, $formattype='xml')?>
	
	


<h2 class="title">PHP Example</h2>
<textarea name='comments' cols='105' rows='14'>
$header = array("Authorization: Basic " . base64_encode("superuser_username:superuser_password"));
$postWhat = array('id' => '110','title' => 'TEST FFFF', 'username' => 'myusername', 'password' => 'mypassword');
$data = http_build_query($postWhat, '', '&');
$params = array(
		'http'=>array(
				'method'=>'POST',
				'header'=> implode("\r\n", $header),
				'content' => $data
				)
		);
$context = stream_context_create($params);
$data = file_get_contents($url,false,$context);
print $data;</textarea>

</body>
</html>