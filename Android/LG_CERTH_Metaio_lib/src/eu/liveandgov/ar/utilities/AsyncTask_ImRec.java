/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveandgov.ar.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import eu.liveandgov.ar.core.Constants_API;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Perform image recognition using the IBS* method developed by CERTH
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class AsyncTask_ImRec extends AsyncTask<String, String, HttpResponse> {

	String filepath; 
	String app_id;
	Context ctx;

	/**
	 * Asynchronously call image recognition service.
	 * 
	 * @param filepath_in : the image to recognize temporarily stored in sd.
	 * @param app_id_in : the application framework. See at http://augreal.mklab.iti.gr/doc/doc_Recognizer.html for current options.
	 * @param ctx_in : The context to use.
	 */
	public AsyncTask_ImRec(String filepath_in, String app_id_in, Context ctx_in) {
		filepath = filepath_in;
		app_id = app_id_in; 
		ctx = ctx_in;
	}
	
	@Override
	protected HttpResponse doInBackground(String... params) {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("app_id", "5"));
		nameValuePairs.add(new BasicNameValuePair("upload", filepath));
				
		HttpResponse httpResponse = OS_Utils.remote_rec(Constants_API.url_php_IBSSTAR, nameValuePairs, 
				ctx); // 0 is the score, 1 is the label, 2 id the associated Entity id (if any).
		
		return httpResponse;
	}


	@Override
	protected void onPostExecute(HttpResponse httpResponse) {
		
		String results = null;
		try {
			results = EntityUtils.toString(httpResponse.getEntity());
		} catch (ParseException e) {
		} catch (IOException e) {
		}
		
		if (results == null){
			ctx.sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("imrec", new String[]{"none","none","none"})); ;
		} else {
			ctx.sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("imrec", results.split(";"))); ;
		}
		
		super.onPostExecute(httpResponse);
	}
	

}
