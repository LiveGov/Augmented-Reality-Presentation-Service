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
*/

package eu.liveandgov.ar.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * Rest client to download information from php API.
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class RestClient {

    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    //private File file;
    
    private String url;
    
    
    private int responseCode;
    private String message;
    private String response;

    public enum RequestMethod {
    	GET, POST;
    }

    
    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url) //, File mfile
    {
        this.url = url;
         params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void AddParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value)
    {
        headers.add(new BasicNameValuePair(name, value));
    }

    public String Execute(RequestMethod method, int soTime, int connTime)
    {
        switch(method) {
            case GET:
            {
                //add parameters
                String combinedParams = "";
                if(!params.isEmpty()){
                    combinedParams += "?";
                    for(NameValuePair p : params)
                    {
                        String paramString;
						try {
							paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
							
							if(combinedParams.length() > 1)
	                            combinedParams  +=  "&" + paramString;
	                        else
	                            combinedParams += paramString;
							
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        
                    }
                }

                HttpGet request = new HttpGet(url + combinedParams);

                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                    //Log.e("headers", h.getName());
                }

               // Log.e("REST", " " + url + " " + soTime + " " + connTime );
                
                executeRequest(request, url, soTime, connTime);
                break;
            }
            case POST:
            {
                HttpPost request = new HttpPost(url);

                //add headers
                for(NameValuePair h : headers)
                {
                    request.addHeader(h.getName(), h.getValue());
                }

                if(!params.isEmpty()){
                    try {
						request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                
                executeRequest(request, url, soTime, connTime);
                break;
            }
        }
        
        
        return response;
    }

    private void executeRequest(HttpUriRequest request, String url, int soTime, int connTime)
    {
        HttpClient client = new DefaultHttpClient();

        //----- Set timeout --------------
//        HttpParams httpParameters = new BasicHttpParams();
//        
//        // Set the timeout in milliseconds until a connection is established.
//        // The default value is zero, that means the timeout is not used. 
//        int timeoutConnection = 1000;
//        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT) 
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 1000;
//        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        
        client.getParams().setParameter("http.socket.timeout", soTime);
        client.getParams().setParameter("http.connection.timeout", connTime);
        
        
        //----------------------------------
        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			Log.e("RestClient","Can not read from InputStream");
		}
        StringBuilder sb = new StringBuilder();

        
        char[] buf = new char[1];
        
        try {
			while (reader.read(buf) != -1) 
            	sb.append(buf);   
        } catch (IOException e) {
        	Log.e("RestClient","IO Exception");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            	Log.e("RestClient","IO Exception B");
            }
        }
        return sb.toString();
    }
}
