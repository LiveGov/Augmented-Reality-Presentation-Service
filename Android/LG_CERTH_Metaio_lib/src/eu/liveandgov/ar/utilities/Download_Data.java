// Downloading AR data central       
package eu.liveandgov.ar.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Intent;
import android.util.Log;
import eu.liveandgov.ar.utilities.RestClient.RequestMethod;

/**
 * Downloading of AR data with REST services
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class Download_Data {

	/** AR3DLib tag for logging messages*/
	public static ArrayList<String[]> MemDown = new ArrayList<String[]>(); // Store 1. File 2. When it was downloaded (so not download the same twice!)

	public Download_Data() {}

	/** 
	 * RestCaller is for calling REST, that is downloading data.
	 * 
	 * @param url     URL to call
	 * @param rm      Request method (RestClient.GET, RestClient.POST) 
	 * @param args    Parameters for url in pairs (e.g. {"name","Mike","surname","McDonald"} )   
	 * @return        String of the response of the server in JSON format  
	 */
	public static String RestCaller(String url, RequestMethod rm, String[] args, int soTimeout, int connTimeout  ){ 
		RestClient clientR = new RestClient(url);

		if (args!=null)
			for (int i=0; i< args.length; i=i+2) {
				clientR.AddParam(args[i], args[i+1]);
			}
		try {
			clientR.Execute(rm, soTimeout, connTimeout);
		} catch (Exception e) {
			Log.e("Download_Data", "Download_Data.RestCaller:I can not execute: " + url );
		}
		return clientR.getResponse();
	}

	/**
	 * Get the number of kb of all relative files (3d, and image of 3d)
	 * @return
	 */
	public static String getDataFileSizes(String AppId){
	
		String params[] = new String[]{"AppId",AppId};
		
		String phpurl = "http://augreal.mklab.iti.gr/api_v3/provideFileSizes.php";
		
		String response =  RestCaller(phpurl, RestClient.RequestMethod.GET, params, 30000, 30000);

		if (response == null)
			Log.e("Download_Data", "getFileSizes CAN NOT ACCESS SERVER:"+phpurl);
		
		return response;
	}
	
	

	/**
	 * Download LBS xml data
	 * 
	 * @param phpurl : the php to call with rest
	 * @param distthres: distance from user position to download entities
	 * @param userLocation: User position [latitude:float],[longitude:float],[altitude:float], e.g. "40.567,22.99,100"
	 * @return
	 */
	public static String DownData_LBS_DATA(String phpurl, String params[]){

		String response =  RestCaller(phpurl, RestClient.RequestMethod.GET, params, 30000, 30000);

		if (response == null)
			Log.e("Download_Data", "LBS CAN NOT ACCESS SERVER:"+phpurl);

		return response;
	}

	/**
	 * Download IBS channel xml data
	 * 
	 * @param phpurl
	 * @return
	 */
	public static String DownData_IBS_DATA(String phpurl, String[] params){

		String response =  RestCaller(phpurl,  RestClient.RequestMethod.GET, params, 2000, 2000);

		if (response == null)
			Log.e("Download_Data", "IBS CAN NOT ACCESS SERVER:"+phpurl);

		return response;
	}


	/**
	 * Download Hash for a file
	 * 
	 * @param phpurl
	 * @return
	 */
	public static String DownHash(String fname){

		String phpurl = "http://augreal.mklab.iti.gr/api_v3/provideFileHashes.php";
		
		String response = "";
		if (fname.equals("tracking.zip"))
				response =  RestCaller(phpurl, RestClient.RequestMethod.GET, new String[]{"id","2.tracking"}, 2000, 2000);
		else
				response =  RestCaller(phpurl, RestClient.RequestMethod.GET, new String[]{"filename",fname}, 2000, 2000);
		
		if (response == null){
			Log.e("Download_Data DownHash", "DOWN HASH CAN NOT ACCESS URL:"+phpurl+ " for " + fname);
		}

		return response;
	}

	/**                      DownAndCopy
	 * 
	 * Download file from URL and Copy to Android file system folder
	 * 
	 * @param fileUrl
	 * @param StringAndroidPath
	 */
	public static boolean DownAndCopy(String fileUrlSTR, String StringAndroidPath, boolean preservefilename, String Caller){

		if (compareRemoteWithLocal(fileUrlSTR, StringAndroidPath)){
			//Log.e("fileUrlSTR", "SKIP WITH HASH");
			return true;
		} else {
			Log.e("TRY TO DOWNLOAD BY " + Caller, fileUrlSTR);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("mm");

		// Check if downloaded at least just 2 minutes ago
		for (String[] mem : MemDown)
			if (fileUrlSTR.equals(mem[0])){
				int diff = Integer.parseInt( sdf.format(new Date())) - Integer.parseInt(mem[1]);
				Log.e("diff", " " + diff);
				if (diff < 2 ){
					Log.d("Download_Data", "I am not downloading " + fileUrlSTR + " because it was downloaded " + diff + " minutes ago");
					return true;	
				}
			}



		if (!OS_Utils.exists(fileUrlSTR)){
			Log.e("Download_Data", "URL: " + fileUrlSTR + " called from "+Caller+" not exists to copy it to "+StringAndroidPath);
			return false;
		}

		int DEBUG_FLAG = 0;

		HttpURLConnection conn;
		URL fileUrl = null;
		try {
			fileUrl = new URL(fileUrlSTR);
		} catch (MalformedURLException e1) {
			return false;
		}


		try {
			conn = (HttpURLConnection)fileUrl .openConnection();

			DEBUG_FLAG = 1;

			conn.setDoInput(true); conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);

			DEBUG_FLAG = 2;

			int current = 0;
			byte[] buffer = new byte[10];
			while ((current = bis.read(buffer)) != -1) 
				baf.append(buffer, 0, current);

			DEBUG_FLAG = 3;

			/* Convert the Bytes read to a String. */
			File fileAndroid;

			try{

				if(preservefilename){
					int iSlash = fileUrlSTR.lastIndexOf("/");
					fileAndroid = new File(StringAndroidPath + "/" + fileUrlSTR.substring(iSlash+1));
				}else 
					fileAndroid = new File(StringAndroidPath);

			} catch(Exception e){
				Log.e("Download_Data.DownAndCopy","I can not create "+ StringAndroidPath);
				bis.close();
				conn.disconnect();
				return false;
			}

			DEBUG_FLAG = 4;

			FileOutputStream fos = new FileOutputStream(fileAndroid);
			fos.write(baf.toByteArray());

			DEBUG_FLAG = 5;

			bis.close();
			fos.close();
			conn.disconnect();


			MemDown.add(new String[]{fileUrlSTR,  sdf.format(new Date())});


			return true; //returns including the filename
		} catch (IOException e) {
			Log.e("Download_Data", "Download_Data: Error when trying to download:  " +  fileUrl.toString() + " to " + StringAndroidPath + " DEBUG_FLAG=" + 
					DEBUG_FLAG);
			return false;
		}
		
		
	}


	public static boolean compareRemoteWithLocal(String remote, String local){


		
		int st = remote.lastIndexOf("/")+1;
		String fn = remote.substring(st);

		String isComma = local.substring(local.length()-4,local.length()-3);
		
		
		if (!isComma.equals(".")){ // if local has only the path not path+filename
			local += "/" + fn; 		// loc has no filename
		}
		
		// Get remote hash
		String rhash = DownHash(fn);
		
		// Get local hash
        String lhash = "";
        
        
        
		try {
			lhash = md5(new File(local));
			if (lhash.equals(rhash))
				return true;
			
		} catch (NoSuchAlgorithmException e) {
		} catch (IOException e) {
		}

		return false;
	}

	public static String md5(File file) throws IOException, NoSuchAlgorithmException
	{
		char[] hexDigits = "0123456789abcdef".toCharArray();
		InputStream is = new FileInputStream(file);
	    byte[] bytes = new byte[4096];
	    int read = 0;
	    MessageDigest digest = MessageDigest.getInstance("MD5");
	    while ((read = is.read(bytes)) != -1)
	    {
	        digest.update(bytes, 0, read);
	    }

	    byte[] messageDigest = digest.digest();

	    StringBuilder sb = new StringBuilder(32);

	    // Oh yeah, this too.  Integer.toHexString doesn't zero-pad, so
	    // (for example) 5 becomes "5" rather than "05".
	    for (byte b : messageDigest)
	    {
	        sb.append(hexDigits[(b >> 4) & 0x0f]);
	        sb.append(hexDigits[b & 0x0f]);
	    }

	    return sb.toString();
	}
	


}