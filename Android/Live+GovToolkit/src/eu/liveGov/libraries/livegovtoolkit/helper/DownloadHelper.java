package eu.liveGov.libraries.livegovtoolkit.helper;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;
import android.util.Base64;
import eu.liveGov.libraries.livegovtoolkit.Utils.Constants;
import eu.liveGov.libraries.livegovtoolkit.Utils.RestClient;
import eu.liveGov.libraries.livegovtoolkit.Utils.RestClient.RequestMethod;
import eu.liveGov.libraries.livegovtoolkit.interfaces.WebcallsListener;


/**
 * Download/upload various information (user, questionaires, logs) 
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class DownloadHelper
{
	private static final Logger logger = LoggerFactory.getLogger( DownloadHelper.class );

	private ArrayList<WebcallsListener> _listeners;

	public DownloadHelper( WebcallsListener listener )
	{
		_listeners = new ArrayList<WebcallsListener>();
		_listeners.add( listener );
	}

	public void addListener( WebcallsListener listener )
	{
		_listeners.add( listener );
	}

	public void removeListener( WebcallsListener listener )
	{
		_listeners.remove( listener );
	}

	private String[] getBasicAuthHeader( byte[] b64 )
	{
		String s64 = Base64.encodeToString( b64, Base64.NO_WRAP );
		String[] s = { "Authorization", "Basic " + s64 };
		return s;
	}

	private String[] getServiceCenterBasicAuthHeader()
	{
		byte[] b64 = new String( Constants.UP_SERVICE_CENTER_USER_NAME + ":" + Constants.UP_SERVICE_CENTER_PASSWORD ).getBytes();
		return getBasicAuthHeader( b64 );
	}

	public void postLogFile( File logfile, String json, int moduleId ){	
		restCaller( Constants.SERVICE_CENTER + Constants.POST_LOG_FILE + "/" + moduleId, RequestMethod.POSTLOG, json, logfile, getServiceCenterBasicAuthHeader(), 120000, 120000 );
	}

	public void postLogFile( File logfile, String json ){	
		postLogFile( logfile, json, Constants.MODULE_ID );
	}

	public void createAnonymousUser( String user_unique ){
		String json = "{ \"unique\": \"" + user_unique + "\"}";
		restCaller( Constants.SERVICE_CENTER + Constants.POST_CREATE_ANONYMOUS_USER, RequestMethod.POST, json, getServiceCenterBasicAuthHeader(), 120000, 120000 );
	}

	public void getPermissions(){
		restCaller( Constants.SERVICE_CENTER + Constants.GET_PERMISSIONS, RequestMethod.GET, null, getServiceCenterBasicAuthHeader(), 120000, 120000 );
	}

	private String[] getDialogAndVisualizationBasicAuthHeader(){
		byte[] b64 = new String( Constants.UP_DIALOG_AND_VISUALIZATION_SERVICE_USER_NAME + ":" + Constants.UP_DIALOG_AND_VISUALIZATION_SERVICE_PASSWORD ).getBytes();
		return getBasicAuthHeader( b64 );
	}

	public void getQuestionnaire( int anonynousUserId, String questionnaireCode ){
		getQuestionnaire( anonynousUserId, questionnaireCode, "" );
	}

	public void getQuestionnaire( int anonynousUserId, String questionnaireCode, String objectCode ){
		String objectCodeSuffix = ( objectCode.equals( "" ) ) ? "" : "/" + objectCode;
		String url = Constants.DIALOG_AND_VISUALIZATION_SERVICE + Constants.GET_QUESTIONNAIRE + 
				anonynousUserId + "/" + questionnaireCode + objectCodeSuffix;
		
		restCaller( url, RequestMethod.GET, null, getDialogAndVisualizationBasicAuthHeader(), 120000, 120000 );
	}

	public void sendQuestionnaire( String json_questionaire, int anonynousUserId ){
		String url = Constants.DIALOG_AND_VISUALIZATION_SERVICE + Constants.POST_QUESTIONNAIRE + anonynousUserId;
		restCaller( url, RequestMethod.POST, json_questionaire, getDialogAndVisualizationBasicAuthHeader(), 120000, 120000 );
	}

	public void restCaller( String url, RequestMethod rm, String json, File file, String[] headers, int soTimeout, int connTimeout ){
		DownloadTaskConfig dtg = new DownloadTaskConfig();
		dtg.url = url;
		dtg.rm = rm;
		dtg.json = json;
		dtg.soTimeout = soTimeout;
		dtg.connTimeout = connTimeout;
		dtg.headers = headers;
		dtg.file = file;
		new DownloadTask().execute( dtg );
	}

	public void restCaller( String url, RequestMethod rm, String json, String[] headers, int soTimeout, int connTimeout ){
		DownloadTaskConfig dtg = new DownloadTaskConfig();
		dtg.url = url;
		dtg.rm = rm;
		dtg.json = json;
		dtg.soTimeout = soTimeout;
		dtg.connTimeout = connTimeout;
		dtg.headers = headers;
		dtg.file = null;
		new DownloadTask().execute( dtg );
	}

	public void notifyListeners( HttpResponse response ){
		for ( WebcallsListener wcl : _listeners ){
			wcl.webcallReady( response );
		}
	}

	protected class DownloadTaskConfig{
		public String url;
		public RequestMethod rm;
		public String json;
		public String[] headers;
		public int soTimeout;
		public int connTimeout;
		public File file;
	}

	protected class DownloadTask extends AsyncTask<DownloadTaskConfig, Void, RestClient>{

		@Override
		protected RestClient doInBackground( DownloadTaskConfig... params ){
			DownloadTaskConfig config = params[0];

			RestClient restClient = new RestClient( config.url );

			if ( config.json != null )
				restClient.setJson( config.json );

			if ( config.headers != null ){
				for ( int i = 0; i < config.headers.length; i = i + 2 )
					restClient.AddHeader( config.headers[i], config.headers[i + 1] );
			}
			
			if( config.file != null)
				restClient.setFile( config.file );
			
			try	{
				restClient.Execute( config.rm, config.soTimeout, config.connTimeout );
			} catch ( Exception e ){
				logger.error( "RestCaller; Error while downloading: {}", e.getMessage() );
				return null;
			}
			return restClient;
		}

		@Override
		protected void onPostExecute( RestClient result ){
			logger.info( "onPostExecute; Downloading finnished" );
			if ( result == null ){
				logger.error( "onPostExecute; No result" );
				notifyListeners( null );
			} else{
				notifyListeners( result.getHttpResponse() );
			}
		}
	}
		
}