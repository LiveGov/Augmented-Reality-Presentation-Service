/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveGov.libraries.livegovtoolkit.objects;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import eu.liveGov.libraries.livegovtoolkit.interfaces.ResourcesListener;
import eu.liveandgov.ar.core.Constants_API;
import eu.liveandgov.ar.utilities.Download_Data;
import eu.liveandgov.ar.utilities.Entity;
import eu.liveandgov.ar.utilities.OS_Utils;
import eu.liveandgov.ar.utilities.ReadAREL;

/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 *
 *  Download all necessary data for AR and store into the local folder /data/MetaioSDK/.<br/><br/>
 *  
 *  LBS: Location based AR channel data;<br/> 
 *  IBS: Image based AR channel data.<br/><br/>
 *  
 *  It consists of the following steps:<br/>
 *  
 * 1. Download LBS content written in xml (AREL) language                  <br/>
 * 2. Save content locally into an XML file                                <br/> 
 * 3. Parse the AREL and store into memory using an arraylist of Entities  <br/>
 * 4. Download images and 3dmodels as described in the xml file            <br/>
 * 5. Download the IBS channel content                                     <br/>
 * 6. Save the content into an xml local file                              <br/>
 * 7. Parse the AREL and store into memory using the arraylist Entities    <br/> 
 * 8. Download 3d models and icons of IBS channel                          <br/> 
 * 9. Download TrackingXML.zip which contains all the necessary data to perform the image tracking for IBS <br/>
 * 
 *  
 */
public class FetchResources extends AsyncTask<Integer, Integer, Boolean>
{

	boolean DEBUG_FLAG = true;
	View v;
	String TAG = getClass().getName();
	
	private String userLocation = null;
	private Tracker tracker = null;

	/** The data of the LBS Entities are accessible here. LBS contains Entities of two types: 2d (Billboards) or 3d (Models). */
	public static List<Entity> entitiesLBS = new ArrayList<Entity>();

	/** The data of the IBS Entities are accessible here. IBS are Image based related AR entities. */
	public static List<Entity> entitiesIBS = new ArrayList<Entity>();

	private static ArrayList<ResourcesListener> _resourcesListeners = new ArrayList<ResourcesListener>();

	Context ctx;

	//---------------------------------------------------
	/** Root folder for local storage*/
	public static String localFolderSTR; 

	/** Filename for local xml of the LBS channel */
	public static String fn_local_LBS_xml;

	/** Filename for local xml of the IBS channel */
	public static String fn_local_IBS_xml;

	/** Folder for local storage of Entities (3d models and icons) */ 
	public static String folder_Models3D;
	
	
	/** 
	 *   Constructor
	 */
	public FetchResources( Activity act, String userLocation_in, String calledWho)
	{
		Log.e("FetchResource called by", calledWho);
		userLocation = userLocation_in;
		tracker = EasyTracker.getInstance(act);
		ctx  = act;
	}

	public static void addResourcesListener( ResourcesListener rul )
	{
		_resourcesListeners.add( rul );
	}

	public static void removeResourcesListener( ResourcesListener rul )
	{
		_resourcesListeners.remove( rul );
	}

	protected static void resourcesUpdated()
	{
		for ( ResourcesListener r : _resourcesListeners )
		{
			r.resourcesUpdated();
		}
	}

	/**  Initialize Progress bar */
	@Override
	protected void onPreExecute()
	{
	}

	/**  Update Progress bar */
	@Override
	protected void onProgressUpdate( Integer... values )
	{
		
		ctx.sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("DataPlus", values[0]));
		
		super.onProgressUpdate( values );
	}

	/** Download and store data. It consists of the following steps:
	 *  
	 * 1. Download LBS content written in xml (AREL) language                  <br/>
	 * 2. Save content locally into an XML file                                <br/> 
	 * 3. Parse the AREL and store into memory using an arraylist of Entities  <br/>
	 * 4. Download images and 3dmodels as described in the xml file            <br/>
	 * 5. Download the IBS channel content                                     <br/>
	 * 6. Save the content into an xml local file                              <br/>
	 * 7. Parse the AREL and store into memory using the arraylist Entities    <br/> 
	 * 8. Download 3d models and icons of IBS channel                          <br/> 
	 * 9. Download TrackingXML.zip which contains all the necessary data to perform the image tracking for IBS <br/>
	 * */
	@Override
	protected Boolean doInBackground( Integer... params )
	{
		try
		{
			long startTime = System.nanoTime();
			long endTime;
			long duration;
			// Create local folders

			/** Root folder for local storage*/
			localFolderSTR     = ctx.getFilesDir().getAbsolutePath();
			fn_local_LBS_xml   = localFolderSTR + "/LBS/arel.xml";
			fn_local_IBS_xml   = localFolderSTR + "/IBS/arel.xml";
			folder_Models3D    = localFolderSTR + "/Models3D_DB/";

			/** Create local folders */
			OS_Utils.CreateFolder(localFolderSTR);
			OS_Utils.CreateFolder(localFolderSTR + "/LBS");
			OS_Utils.CreateFolder(localFolderSTR + "/IBS");
			OS_Utils.CreateFolder(localFolderSTR + "/Models3D_DB");

			Log.e("localFolderSTR", localFolderSTR);
			
			// -------------------------- LBS ----------
			// -- 1. Download LBS content written in xml (AREL) language
			String lbs_content = Download_Data.DownData_LBS_DATA( Constants_API.url_php_LBS, 
					new String[] { "distthres", Constants_API.rangeSTR, "l", 
					userLocation, "idapp", Constants_API.idapp, "lang", Locale.getDefault().getLanguage() } );

			deb( "LBS xml content downloaded", lbs_content );
			publishProgress( 10 );

			// -- 2. Save content locally into an XML file
			OS_Utils.write2File( fn_local_LBS_xml, lbs_content );

			deb( "LBS xml stored locally", "ok" );
			publishProgress( 20 );

			// -- 3. Parse the AREL and store into memory using an arraylist of Entities
			entitiesLBS = new ArrayList<Entity>();
			ReadAREL xmlParser = new ReadAREL();
			entitiesLBS = xmlParser.parse( new ByteArrayInputStream( lbs_content.getBytes() ) );

			deb( "LBS xml parsed", " " + entitiesLBS.size() );
			publishProgress( 30 );

			// 4. Download images and 3dmodels as described in the xml file
			String iconurlPrev = "";

					
			// for each entity
			for ( int i = 0; i < entitiesLBS.size(); i++ )
			{

				// - Create a local folder
				String localfolder_Entity = folder_Models3D + "/" + entitiesLBS.get( i ).id;
				OS_Utils.CreateFolder( localfolder_Entity );

				// ----- Download and store 3D models ---
				if ( entitiesLBS.get( i ).type.equals( "LBS 3D" ) )
				{

					// 1st 3d model of Entity
					Download_Data.DownAndCopy( entitiesLBS.get( i ).modelurl, localfolder_Entity, true, "FetchResources.doInBackground.LBS 1" );

					if ( Integer.parseInt( entitiesLBS.get( i ).nModels3d ) > 1 )
					{

						String url_alt_model = entitiesLBS.get( i ).modelurl;

						// 2nd 3d model of Entity
						url_alt_model = url_alt_model.replace( "/1/AR", "/2/AR" );
						url_alt_model = url_alt_model.replace( "_1junaio", "_2junaio" );
						Download_Data.DownAndCopy( url_alt_model, localfolder_Entity, true, "FetchResources.doInBackground.LBS 2" );

						// 3rd 3d model of Entity
						if ( Integer.parseInt( entitiesLBS.get( i ).nModels3d ) > 2 )
						{
							url_alt_model = url_alt_model.replace( "/2/AR", "/3/AR" );
							url_alt_model = url_alt_model.replace( "_2junaio", "_3junaio" );
							
							Download_Data.DownAndCopy( url_alt_model, localfolder_Entity, true, "FetchResources.doInBackground.LBS 3" ); // 3rd model 3d
						}
					}
				}

				// Download icon of Entity
				if ( !entitiesLBS.get( i ).iconurl.equals( iconurlPrev ) && entitiesLBS.get( i ).iconurl.length() > 0 )
				{
					Download_Data.DownAndCopy( entitiesLBS.get( i ).iconurl, localfolder_Entity, true, "FetchResources.doInBackground.LBS 4" ); // icon
					iconurlPrev = entitiesLBS.get( i ).iconurl;
					
					publishProgress( 30 + i );
				}
			}

			deb( "LBS 3d objects downloaded", "ok" );

			endTime = System.nanoTime();
			duration = endTime - startTime;
			startTime = System.nanoTime();

			tracker.send(MapBuilder
					.createTiming("fetch resources",    // Timing category (required)
							(duration / 1000000),       // Timing interval in milliseconds (required)
							"[step 1] LBS parsing time",  // Timing name
							null)           // Timing label
							.build());



			publishProgress( 50 );
			// ----------------------- IBS --------------------------
			// -- 5. Download the channel content
			String ibs_content = Download_Data.DownData_IBS_DATA( Constants_API.url_php_IBS, new String[] { "lang", Locale.getDefault().getLanguage() } );

			deb( "IBS xml content downloaded", ibs_content );
			publishProgress( 60 );

			// -- 6. Save the content into an xml local file
			OS_Utils.write2File( fn_local_IBS_xml, ibs_content );

			deb( "IBS content stored locally", "ok" );
			publishProgress( 70 );

			// -- 7. Parse the AREL and store into memory using the arraylist Entities
			entitiesIBS = new ArrayList<Entity>();
			ReadAREL xmlParserIBS = new ReadAREL();
			entitiesIBS = xmlParserIBS.parse( new ByteArrayInputStream( ibs_content.getBytes() ) );

			deb( "IBS content parsed", " " + entitiesIBS.size() );
			publishProgress( 80 );

			// -- 8. Download 3d models and icons of IBS channel
			for ( int i = 0; i < entitiesIBS.size(); i++ )
			{

				String localfolder_Entity = folder_Models3D + "/" + entitiesIBS.get( i ).id;

				OS_Utils.CreateFolder( localfolder_Entity );

				Download_Data.DownAndCopy( entitiesIBS.get( i ).modelurl, localfolder_Entity, true, "FetchResources.doInBackground.IBS 1" ); // model 3d

				if ( Integer.parseInt( entitiesIBS.get( i ).nModels3d ) > 1 )
				{

					String url_alt_model = entitiesIBS.get( i ).modelurl;

					// 2nd 3d model of Entity
					url_alt_model = url_alt_model.replace( "/1/AR", "/2/AR" );
					url_alt_model = url_alt_model.replace( "_1junaio", "_2junaio" );
					Download_Data.DownAndCopy( url_alt_model, localfolder_Entity, true, "FetchResources.doInBackground.LBS 2" );

					// 3rd 3d model of Entity
					if ( Integer.parseInt( entitiesIBS.get( i ).nModels3d ) > 2 )
					{
						url_alt_model = url_alt_model.replace( "/2/AR", "/3/AR" );
						url_alt_model = url_alt_model.replace( "_2junaio", "_3junaio" );
						Download_Data.DownAndCopy( url_alt_model, localfolder_Entity, true, "FetchResources.doInBackground.LBS 3" ); // 3rd model 3d
					}
				}

				Download_Data.DownAndCopy( entitiesIBS.get( i ).iconurl, localfolder_Entity, true, "FetchResources.doInBackground.IBS 2" ); // icon
				
				publishProgress( 80 + i );
			}

			endTime = System.nanoTime();
			duration = endTime - startTime;
			startTime = System.nanoTime();

			tracker.send(MapBuilder
					.createTiming("fetch resources",    // Timing category (required)
							(duration / 1000000),       // Timing interval in milliseconds (required)
							"[step 2] IBS parsing time",  // Timing name
							null)           // Timing label
							.build());

			// 9. Download TrackingXML___randomnumber___.zip which performs the image tracking for IBS
			Download_Data.DownAndCopy( entitiesIBS.get( 0 ).trackingurl, localFolderSTR + "/IBS/Tracking.zip", false, 
					"FetchResources.doInBackground.IBS Tracking 4" );

			endTime = System.nanoTime();
			duration = endTime - startTime;

			tracker.send(MapBuilder
					.createTiming("fetch resources",    // Timing category (required)
							(duration / 1000000),       // Timing interval in milliseconds (required)
							"[step 3] IBS tracking zip downloaded",  // Timing name
							null)           // Timing label
							.build());

			deb( "IBS tracking zip downloaded", "ok" );
			publishProgress( 100 );

		} catch ( Exception e )
		{
			deb( TAG, "ERROR" );
			return false;
		}

		return true;
	}

	/**
	 *  if everything went well, activate buttons
	 */
	@Override
	protected void onPostExecute( Boolean result )
	{
		ctx.sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("DataFetched", "DataFetched"));
		resourcesUpdated();
	}

		
	
	public void deb( String mes, String mes2 )
	{
		if ( DEBUG_FLAG )
			Log.d( TAG, mes + ":" + mes2 );
	}

}
