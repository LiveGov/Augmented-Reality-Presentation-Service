/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveGov.libraries.livegovtoolkit.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import android.content.Context;
import android.content.Intent;

/**
 * In case of errors you can send the log with mail to the developer
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class ReadLogCatAndMail {
	
	public static String ReadCogCat(){
		
		 try {
		      Process process = Runtime.getRuntime().exec("logcat -d");
		      BufferedReader bufferedReader = new BufferedReader(
		      new InputStreamReader(process.getInputStream()));
		                       
		      StringBuilder log=new StringBuilder();
		      String line;
		      while ((line = bufferedReader.readLine()) != null) {
		        log.append(line);
		      }
		      
		      return log.toString();
		    } catch (IOException e) {
		    	return "";
		    }
	}
	
	public static void sendLogByMail(Context c, String s, String v){
   		Intent emailIntentQuest = new Intent(android.content.Intent.ACTION_SEND);  
   		String aEmailListQuest[] = { Constants.developerMail, };  
   
   		emailIntentQuest.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailListQuest);  
   		emailIntentQuest.putExtra(android.content.Intent.EXTRA_SUBJECT, "Urban Planning bug at v."+ v);  
   
   		emailIntentQuest.setType("plain/text");  
   		emailIntentQuest.putExtra(android.content.Intent.EXTRA_TEXT,s);
   
   		c.startActivity(Intent.createChooser(emailIntentQuest, "Send your email with:"));
	}
}
