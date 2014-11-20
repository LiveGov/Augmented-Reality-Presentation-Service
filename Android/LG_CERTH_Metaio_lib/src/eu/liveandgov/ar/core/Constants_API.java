/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveandgov.ar.core;

import eu.liveandgov.ar.utilities.OS_Utils;

import android.os.Environment;

/**
 * This class contains the paths (php urls) for downloading data, and the local paths to store it. 
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class Constants_API {
	
	//-------------------- PHPs ----------------------------
	/** Server name to connect */
	public static String ServerName          = "http://augreal.mklab.iti.gr";
	
	public static String VersionFolder = "/";
	
	/** Php to get the LBS channel */
	public static String url_php_LBS         = ServerName + VersionFolder +"/api_Metaio_v2/LBS/index.php";
	
	/** Php to get the IBS channel */
	public static String url_php_IBS         = ServerName + VersionFolder +"/api_Metaio_v2/IBS/index.php";
	
	/** Php to get the IBSSTAR channel */
	public static String url_php_IBSSTAR =	ServerName + VersionFolder + "/api_v3/recognizer.php";
	
	/** Sever path to download the 3d models */
	public static String ServerModelsPath = ServerName + "/Models3D_DB/";
	
	/** Distance (range) to download (in meters) */
	public static String rangeSTR = "5000000";
	
	/** Application Channel: XYZV : X = 0 or 1 for YUCAT, Y= 0 or 1 for Biscaytik, Z = 0 or 1 for Mattersoft   */
	public static String idapp = "0100";
	
}
