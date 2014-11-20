/*
 *  How to use me
 *  
 *  
 
 RestClient client = new RestClient(LOGIN_URL);
client.AddParam("accountType", "GOOGLE");
client.AddParam("source", "tboda-widgalytics-0.1");
client.AddParam("Email", _username);
client.AddParam("Passwd", _password);
client.AddParam("service", "analytics");
client.AddHeader("GData-Version", "2");

try {
    client.Execute(RequestMethod.POST);
} catch (Exception e) {
    e.printStackTrace();
}

String response = client.getResponse();

Originally Made by: Information Technologies Institute, ITI-CERTH.
Modified by: R. de Kok, Yucat
*/

package eu.liveGov.libraries.livegovtoolkit.Utils;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * This is the http method to send data.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class RestClient
{
    
    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;
    private String _json;
    private File _file;
    
    // private File file;
    
    private String url;
    private HttpResponse httpResponse;
    
    public enum RequestMethod
    {
	GET, POST, POSTLOG;
    }
    
    public HttpResponse getHttpResponse()
    {
	return httpResponse;
    }
    
    public RestClient( String url )
    {
	this.url = url;
	params = new ArrayList<NameValuePair>();
	headers = new ArrayList<NameValuePair>();
    }
    
    public void setJson( String json )
    {
	_json = json;
    }
    
    public void setFile( File file )
    {
	this._file = file;
    }
    
    public void AddParam( String name, String value )
    {
	params.add( new BasicNameValuePair( name, value ) );
    }
    
    public void AddHeader( String name, String value )
    {
	headers.add( new BasicNameValuePair( name, value ) );
    }
    
    public void Execute( RequestMethod method, int soTime, int connTime ) throws Exception
    {
	switch ( method )
	{
	    case GET:
	    {
		// add parameters
		String combinedParams = "";
		if ( !params.isEmpty() )
		{
		    combinedParams += "?";
		    for ( NameValuePair p : params )
		    {
			String paramString;
			paramString = p.getName() + "=" + URLEncoder.encode( p.getValue(), "UTF-8" );
			
			if ( combinedParams.length() > 1 )
			{
			    combinedParams += "&" + paramString;
			} else
			{
			    combinedParams += paramString;
			}
		    }
		}
		
		HttpGet request = new HttpGet( url + combinedParams );
		
		// add headers
		for ( NameValuePair h : headers )
		{
		    request.addHeader( h.getName(), h.getValue() );
		}
		
		executeRequest( request, url, soTime, connTime );
		break;
	    }
	    case POST:
	    {
		HttpPost request = new HttpPost( url );
		
		// add headers
		for ( NameValuePair h : headers )
		{
		    request.addHeader( h.getName(), h.getValue() );
		}
		if ( !_json.isEmpty() )
		{
		    request.addHeader( "Content-type", "application/json" );
		    request.setEntity( new StringEntity( _json, "UTF8" ) );
		    
		} else if ( !params.isEmpty() )
		{
		    request.setEntity( new UrlEncodedFormEntity( params, "UTF-8" ) );
		}
		
		executeRequest( request, url, soTime, connTime );
		break;
	    }
	    case POSTLOG:
	    {
		HttpPost request = new HttpPost( url );
		
		// add headers
		for ( NameValuePair h : headers )
		{
		    request.addHeader( h.getName(), h.getValue() );
		}
		
		MultipartEntity reqEntity = new MultipartEntity();		
		
		if ( !_json.isEmpty() )
		{
		    reqEntity.addPart( "LogfileRequest", new StringBody( _json, "application/json", Charset.forName( "UTF-8" ) ) );
		} else if ( !params.isEmpty() )
		{
		    // ignore the params for now.
		}	
		
		if ( _file != null )
		{
		    reqEntity.addPart( "logfile", new FileBody( _file ) );
		}
		
		request.setEntity( reqEntity );
		
		executeRequest( request, url, soTime, connTime );
		break;
	    }
	}
    }
    
    private void executeRequest( HttpUriRequest request, String url, int soTime, int connTime ) throws Exception
    {
	HttpClient client = new DefaultHttpClient();
	
	// ----- Set timeout --------------
	// HttpParams httpParameters = new BasicHttpParams();
	//
	// // Set the timeout in milliseconds until a connection is established.
	// // The default value is zero, that means the timeout is not used.
	// int timeoutConnection = 1000;
	// HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	// // Set the default socket timeout (SO_TIMEOUT)
	// // in milliseconds which is the timeout for waiting for data.
	// int timeoutSocket = 1000;
	// HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	
	client.getParams().setParameter( "http.socket.timeout", soTime );
	client.getParams().setParameter( "http.connection.timeout", connTime );
	
	// ----------------------------------
	
	try
	{
	    httpResponse = client.execute( request );	    
	} catch (Exception e) {
	    client.getConnectionManager().shutdown();
	    e.printStackTrace();
	    throw e;
	}
    }
}
