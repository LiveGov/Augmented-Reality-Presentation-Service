/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveandgov.ar.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class OS_Utils {

	/**
	 * Load Bitmap for Entity found by 'id' stored locally in file system in Models3D.
	 *  
	 * @param id : of the Entity
	 * @return Bitmap of jpg image of the 3d model.
	 */
	public static Bitmap loadImagefromSD(String folderModels3DSTR, int id){

		String Folder_STR = folderModels3DSTR + "/" + id + "/";

		String fileSCRSHOT_STR = OS_Utils.findfileAndroid("AR_"+id+"_1.jpg", Folder_STR);

		if (fileSCRSHOT_STR.length() > 0) {
			// Resize and load
			BitmapFactory.Options options = new BitmapFactory.Options();  
			//options.inSampleSize = 6;  // Resize
			Log.e("DECODING", Folder_STR + "/" + fileSCRSHOT_STR);
			Bitmap bm = BitmapFactory.decodeFile(Folder_STR + "/" + fileSCRSHOT_STR, options);
			//bmImg  = Bitmap.createScaledBitmap(bmImg, 40, 40, false);
			return bm;    	
		}
		else{
			Log.e("loadImagefromSD", "Nothing at:" + Folder_STR + "/" + "AR_"+id+"_1.jpg");
			return null;
		}
	}


	/**
	 *         Save a string into a file in SD 
	 */
	public static boolean write2File(String filename, String string){
		try {

			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(string);
			out.close();
			fstream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}



	//========  CreateFolder ==============
	/**             
	 * Create a folder
	 * 
	 * @param WhatDir the path of the folder
	 * @return true or false if successfully created
	 */
	public static boolean CreateFolder(String WhatDir){

		File folder = new File(WhatDir);

		boolean success = false;
		if (!folder.exists()) {
			success = folder.mkdirs();
		} else {
			return true;
		}

		return success;
	}



	/**      
	 * Check if URL exists 
	 *        
	 * @param urlSTR
	 * @return
	 */
	public static boolean exists(String URLName){
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");

			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			
			return false;
		}
	}


	/**
	 * Check if a fname exists locally.
	 * 
	 * @param FILE_NAME
	 * @return
	 */
	public static boolean fileexists( String FILE_NAME){
		File file = new File(FILE_NAME);
		return file.exists();
	}

	/**
	 * Check if connected to internet.
	 * 
	 * @param ctx :  Context 
	 * @return true or false
	 */
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


	/**
	 * Save image to locally to sd 
	 * @param bm
	 * @throws IOException
	 */
	public static boolean saveImage2SD(Bitmap bm, String localpath, String filename) throws IOException{

		String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + localpath;

		File dir = new File(folder_path);
		if(!dir.exists())
			dir.mkdirs();

		File file = new File(dir, filename);
		try {
			FileOutputStream fOut = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
			fOut.flush();
			fOut.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}


	/**
	 * Remotely recognize captured image
	 * 
	 * @param url
	 * @param nameValuePairs
	 */
	public static HttpResponse remote_rec(String url, List<NameValuePair> nameValuePairs, Context ctx) {


		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for(int index=0; index < nameValuePairs.size(); index++) {
				
				if(nameValuePairs.get(index).getName().equalsIgnoreCase("upload")) 
					entity.addPart("upload", new FileBody(new File (nameValuePairs.get(index).getValue())));
				else 
					entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
			}

			httpPost.setEntity(entity);
			HttpResponse httpresponse = httpClient.execute(httpPost, localContext);
			
			return httpresponse;
			
			
		} catch (Exception e) {
			//Toast.makeText(ctx, "Can not send for recognition. Internet accessible?", Toast.LENGTH_LONG).show();
			Log.e("ERROR",  "Can not send for recognition. Internet accessible?");
			return null;
		}

		
	}


	/**
	 *  Find the file in folder that its name contains initials 
	 * 	
	 * @param initials
	 * @param folder
	 * @return
	 */
	public static String findfileAndroid(String initials, String folder){

		String res = "";

		File yourDir = new File(folder);

		String name = "";
		for (File f : yourDir.listFiles()) {
			if (f.isFile())
				name = f.getName();
			if (name.contains(initials))
				res = name;
		}

		return res;
	}


}
