package eu.liveGov.libraries.livegovtoolkit.helper;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import eu.liveGov.libraries.livegovtoolkit.interfaces.PermissionListener;
import eu.liveGov.libraries.livegovtoolkit.interfaces.WebcallsListener;
import eu.liveGov.libraries.livegovtoolkit.objects.PermissionObject;
import eu.liveGov.libraries.livegovtoolkit.objects.Permissions;

/**
 * Download and handle permissions from Service Center.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class PermissionHelper implements WebcallsListener
{
	public static final String PERMISSION_LIST = "up_client_listview";
	public static final String PERMISSION_MAP = "up_client_mapview";
	public static final String PERMISSION_AR = "up_client_arview";
	private static final String PERMISSION_SHARED_PREFS = "SP_PERMISSIONS";

	private Context _context;
	private static PermissionHelper _ph;

	private static ArrayList<PermissionListener> _listeners = new ArrayList<PermissionListener>();
	protected static boolean isDownloading = false;

	private static final Logger logger = LoggerFactory.getLogger( PermissionHelper.class );

	public static PermissionHelper getInstance(){
		if ( _ph == null )
			_ph = new PermissionHelper();
		return _ph;
	}

	public void downloadPermissions( Context con ){
		logger.info( "downloadPermissions;" );
		if ( !isDownloading ){
			isDownloading = true;
			_context = con;
			new DownloadHelper( getInstance() ).getPermissions();
		}
	}

	public static boolean isDownloading(){
		return isDownloading;
	}

	public static boolean gotPermission( Context context, String permission ){
		SharedPreferences shared = context.getSharedPreferences( PERMISSION_SHARED_PREFS, Context.MODE_PRIVATE );
		return shared.getBoolean( permission, true );
	}

	public static void savePermissions( Context context, Permissions permissions ){
		logger.info( "savePermissions;" );
		SharedPreferences sharedPrefs = context.getSharedPreferences( PERMISSION_SHARED_PREFS, Context.MODE_PRIVATE );
		SharedPreferences.Editor editor = sharedPrefs.edit();

		editor.putBoolean( PERMISSION_LIST, false );
		editor.putBoolean( PERMISSION_MAP, false );
		editor.putBoolean( PERMISSION_AR, false );

		if ( PermissionObjectListContainsName( permissions.getPermissions(), PERMISSION_LIST ) )
			editor.putBoolean( PERMISSION_LIST, true );
		
		if ( PermissionObjectListContainsName( permissions.getPermissions(), PERMISSION_MAP ) )
			editor.putBoolean( PERMISSION_MAP, true );

		if ( PermissionObjectListContainsName( permissions.getPermissions(), PERMISSION_AR ) )
			editor.putBoolean( PERMISSION_AR, true );

		editor.commit();
	}

	private static boolean PermissionObjectListContainsName( ArrayList<PermissionObject> permissionObjects, String name ){
		for ( PermissionObject po : permissionObjects ){
			if ( po.getName().equals( name ) )
				return true;
		}
		return false;
	}

	public static void addListener( PermissionListener listener ){
		_listeners.add( listener );
	}

	public static void removeListener( PermissionListener listener){
		_listeners.remove( listener );
	}

	protected static void permissionsUpdated( Permissions result ){
		for ( PermissionListener pl : _listeners )
			pl.permissionsUpdated();
	}

	@Override
	public void webcallReady( HttpResponse response ){
		if ( response != null && response.getStatusLine().getStatusCode() == 200 ){
			try	{
				Gson gson = new Gson();
				JsonReader jr = new JsonReader( new InputStreamReader( response.getEntity().getContent() ) );

				PermissionObject[] objects = gson.fromJson( jr, PermissionObject[].class );
				Permissions result = new Permissions();
				result.setPermissions( new ArrayList<PermissionObject>( Arrays.asList( objects ) ) );

				if ( result.getPermissions().size() > 0  ){
					PermissionHelper.savePermissions( _context, result );
				} else {
					logger.error( "Application doesn't have any permissions." );
				}
				PermissionHelper.permissionsUpdated( result );
			} catch ( Exception e )	{
				logger.error( "webcallReady; Exception: {}", e.getCause() );
				PermissionHelper.permissionsUpdated( null );
			}
		} else	{
			if(response == null){
				logger.error( "webcallReady;  No internet" );
			}else {
				logger.error( "webcallReady; http statuscode: {}", response.getStatusLine().getStatusCode() );
			}
			PermissionHelper.permissionsUpdated( null );
		}
		PermissionHelper.isDownloading = false;
	}
}