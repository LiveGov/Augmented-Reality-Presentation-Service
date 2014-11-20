package eu.liveGov.libraries.livegovtoolkit.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import eu.liveGov.libraries.livegovtoolkit.interfaces.WebcallsListener;
import eu.liveGov.libraries.livegovtoolkit.objects.ServiceApiErrorObject;

/**
 * Keep log of actions during the app.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class LogFileHelper implements WebcallsListener
{
    private static String logFileLocation;
    
    private static final Logger logger = LoggerFactory.getLogger( DownloadHelper.class );
    
    public static String getLogFileLocation()
    {
	return logFileLocation;
    }
    
    public static void setLogFileLocation( String logFileLocation )
    {
	LogFileHelper.logFileLocation = logFileLocation;
    }
    
    public void postLogFile(Context con)
    {
	if ( logFileLocation != null )
	{
	    logger.info( "postLogFile; prepaire to send log file" );
	    File file = new File( logFileLocation );
	    File tmpFile = new File( logFileLocation + ".tmp" );
	    if( tmpFile.exists())
		tmpFile.delete();
	    try
	    {
		
		copy( file, tmpFile );
		new FileOutputStream(file, false).close(); //creates an empty log file.
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		logger.info( "Starting new log file... ({})", nowAsISO );
		String json = "{\"filename\" : \"A_" + UserInformationHelper.getAnonymousUserId( con ) + "_log.log\", \"enddate\" : \"" + nowAsISO + "\"}";
		new DownloadHelper( this ).postLogFile( tmpFile, json);
	    } catch ( IOException ioe )
	    {
		logger.error( "postLogFile; {}", ioe );
		try
		{
		    tmpFile.delete();
		} catch ( Exception e )
		{
		}
	    }
	}
    }
    
    private void copy( File src, File dst ) throws IOException
    {
	InputStream in = new FileInputStream( src );
	OutputStream out = new FileOutputStream( dst );
	
	// Transfer bytes from in to out
	byte[] buf = new byte[1024];
	int len;
	while ( ( len = in.read( buf ) ) > 0 )
	{
	    out.write( buf, 0, len );
	}
	in.close();
	out.close();
    }
    
    @Override
    public void webcallReady( HttpResponse response )
    {
	if ( response != null && response.getStatusLine().getStatusCode() != 200 )
	{
	    try
	    {		
		Gson gson = new Gson();
		JsonReader jr = new JsonReader( new InputStreamReader( response.getEntity().getContent() ) );
		ServiceApiErrorObject result = gson.fromJson( jr, ServiceApiErrorObject.class );
		logger.error( "webcallReady;statuscode: {}, mesaage:{}", response.getStatusLine().getStatusCode(), result.getMessage() );
	    } catch ( Exception e )
	    {
		logger.error( "webcallReady;statuscode: {}", response.getStatusLine().getStatusCode() );
	    }
	}
	File tmpFile = new File( logFileLocation + ".tmp" );
	if ( tmpFile.exists() )
	    tmpFile.delete();
    }
}
