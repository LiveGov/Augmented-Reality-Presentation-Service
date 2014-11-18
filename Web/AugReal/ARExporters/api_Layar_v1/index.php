<?php
header( 'Content-type: application/json; charset=utf-8' );
require_once('../../config.inc.php'); // database configuration.
require_once('Utils.php');
require '../../My_Utils.php'; // MyMkdir, CheckSet,LogToFile

//-------------------- SCRIPT ----------------------------------
// Put needed parameter names from GetPOI request in an array called $keys.
$keys = array( 'layerName', 'lat', 'lon', 'radius' );

$requestParams = array();
$requestParams = getRequestParams($keys);
$db = connectDb(); /*  PDO, please see http://php.net/manual/en/book.pdo.php.*/

/* Construct the response into an associative array.*/
$response = array();
$response['layer'] = $requestParams['layerName']; // Assign cooresponding values to mandatory JSON response keys.


// Use Gethotspots() function to retrieve POIs with in the search range.
$response['hotspots'] = getHotspots($db, $requestParams);

// if there is no POI found, return a custom error message.
if (!$response['hotspots'] ) {
	$response['errorCode'] = 20;
	$response['errorString'] = 'No POI found. Please adjust the range.';
} else {
	$response['errorCode'] = 0;
	$response['errorString'] = 'ok';
}
 
$jsonresponse = json_encode( $response );
echo $jsonresponse;

//------------------- MAIN FUNCTION ---------------------------------

// Put received POIs into an associative array. The returned values are
// assigned to $reponse['hotspots'].
//
// Arguments: db ; The handler of the database. value , array ; 
//		An array which contains all the needed parameters retrieved from GetPOI request. 
// Returns: array ; An array of received POIs.
function getHotspots( $db, $value) {
  $hotspots = array();
	/* The distance is caculated based on the Haversine formula. */
	
  // Use PDO::prepare() to prepare SQL statement. This statement is used due to
  // security reasons and will help prevent general SQL injection attacks.
  // ':lat1', ':lat2', ':long' and ':radius' are named parameter markers for
  // which real values will be substituted when the statement is executed.
  // $sql is returned as a PDO statement object. 
  $sql = $db->prepare( '
  			SELECT id,
               title,
  		titleB,
  		titleC,
  		titleD,
  				id_app,
               description,
  		 descriptionB,
  		descriptionC,
  		descriptionD,
  				androidapp,
  				iphoneapp,
               latitude,
               longitude,
               (((acos(sin((:lat1 * pi() / 180)) * sin((latitude * pi() / 180)) +
                  	  cos((:lat2 * pi() / 180)) * cos((latitude * pi() / 180)) * 
                      cos((:long  - longitude) * pi() / 180))
                      ) * 180 / pi()
               )* 60 * 1.1515 * 1.609344 * 1000
               ) as distance
  			  FROM AugReal_ARMain WHERE id_app=2
  		HAVING distance < :radius
      ORDER BY distance ASC
         LIMIT 0, 10 ' );

  // PDOStatement::bindParam() binds the named parameter markers to the
  // specified parameter values. 
  $sql->bindParam( ':lat1', $value['lat'], PDO::PARAM_STR );
  $sql->bindParam( ':lat2', $value['lat'], PDO::PARAM_STR );
  $sql->bindParam( ':long', $value['lon'], PDO::PARAM_STR );
  $sql->bindParam( ':radius', $value['radius'], PDO::PARAM_INT );

  $sql->execute();
  // Iterator for the response array.
  $i = 0; 
  // Use fetchAll to return an array containing all of the remaining rows in
  // the result set.
  // Use PDO::FETCH_ASSOC to fetch $sql query results and return each row as an
  // array indexed by column name.
  $rawPois = $sql->fetchAll(PDO::FETCH_ASSOC);
 
  
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
	
  
  $lang = "EN";
  if (isset($_GET["lang"]))
   	$lang = $_GET["lang"];

  //---------------------------------------------
  /* Process the $pois result */
  // if $rawPois array is not  empty 
  if ($rawPois) {
    // Put each POI information into $hotspots array.
 	  foreach ( $rawPois as $rawPoi ) {
 	  	$poi = array();
      	$poi['id'] = $rawPoi['id'];
      	$poi['imageURL'] = "http://".$_SERVER['SERVER_NAME']."/AugReal/Models3D_DB/".$poi['id']."/1/AR_".$poi['id']."_1.jpg"; //$rawPoi['imageURL'];
	      // 	Get anchor object information
    	$poi['anchor']['geolocation']['lat'] = changetoFloat($rawPoi['latitude']);
      	$poi['anchor']['geolocation']['lon'] = changetoFloat($rawPoi['longitude']);

      // get text object information
      	if ($lang=="ES" || $lang=="EU" || $lang=="es" || $lang=="eu"){
       		$poi['text']['title'] = $rawPoi['titleB'];
       		$poi['text']['description'] = $rawPoi['descriptionB'];
       		$downloadCaption = "Descargue la app oficial";
      	} else {
	      	$poi['text']['title'] = $rawPoi['title'];
        	$poi['text']['description'] = $rawPoi['description'];
      		$downloadCaption = "Download the official app";
      	}

      	$poi['text']['footnote'] = "Live+Gov";// $rawPoi['footnote'];
      
      	$poi['actions'] = array(); 
//       $poi['actions']['showActivity'] = "true";
//       $poi['actions']['activityMessage'] = "Fetching the questionaire ...";
//       $poi['actions']['autoTriggerRange'] = "50";
//       $poi['actions']['autoTrigger'] = "false";
      
      
      
      $datamoblink = null;
      if ($deviceplatform=='iphone'){
      	if ($rawPoi['iphoneapp'])
      		$datamoblink = 'itms-apps://itunes.apple.com/app/'.$rawPoi['iphoneapp'];
      } else {
      	if ($rawPoi['androidapp'])
      		$datamoblink = 'market://details?id='.$rawPoi['androidapp']; // https://play.google.com/store/apps/
      }
      
      if ($datamoblink){
      	$actions = array('uri'=> $datamoblink,
      			'label'=> $downloadCaption,
      			'activityType'=>1,
      			'showActivity'=>true,
      			'activityMessage'=>'Opening google play ...',
      			'autoTriggerRange' => 50,
      			'autoTrigger'=>false);

      	array_push($poi['actions'], $actions);
      }
      
      $poi['object']['contentType'] = "model/vnd.layar.l3d"; 
      $poi['object']['url'] 		= "http://".$_SERVER['SERVER_NAME']. "/AugReal/Models3D_DB/".$rawPoi['id']."/1/AR_".$rawPoi['id']."_1.l3d";
      $poi['object']['reducedURL']  = "http://".$_SERVER['SERVER_NAME']. "/AugReal/Models3D_DB/".$rawPoi['id']."/1/AR_".$rawPoi['id']."_1.l3d";
      $poi['object']['size'] = 1;
      $poi['object']['scale'] = 1;
      $poi['object']['distance'] = 1;

     // Put the poi into the $hotspots array.
     $hotspots[$i] = $poi;
     $i++;
    }//foreach
  }//if
  return $hotspots;
}//getHotspots
?>