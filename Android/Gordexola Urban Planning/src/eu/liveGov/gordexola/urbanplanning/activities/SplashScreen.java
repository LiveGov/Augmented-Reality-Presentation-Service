package eu.liveGov.gordexola.urbanplanning.activities;

import java.net.URL;

import com.google.analytics.containertag.proto.MutableServing.Resource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import eu.liveGov.urbanplanning.gordexola.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveandgov.ar.utilities.Download_Data;

/**
 * SpashScreen checks if internet, location services are on. If telephony internet is on then warns the user about data downloading.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class SplashScreen extends Activity {

	Context ctx;
	boolean firstTimeStart = true;
	private AlertDialog _dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		ctx = this;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		checkRequirements();
	}

	private void checkRequirements(){

		// WIFI or MOBILE
		boolean hasInternet         = Functions.checkInternetConnection(ctx);
		String typeInternet         = Functions.checkInternetType(ctx);

		boolean hasNetLocationProvider = Functions.hasNetLocationProviderEnabled(ctx);
		boolean hasGPSLocationProvider = Functions.hasGPSLocationProviderEnabled(ctx);

		if (! (hasNetLocationProvider && hasGPSLocationProvider)) {
			Builder dialog = new AlertDialog.Builder(ctx);
			dialog.setMessage(ctx.getResources().getString(R.string.location_not_enabled));

			dialog.setCancelable(false);
			dialog.setPositiveButton(ctx.getResources().getString(R.string.open_location_settings),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {

					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(myIntent, 1241);
				}
			});

			dialog.setNegativeButton(ctx.getString(R.string.cancel), 
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					finish();
				}	
			});
			_dialog = dialog.create();
			_dialog.show();

		} else {
			
			
			// Check if first time start
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			firstTimeStart = prefs.getBoolean("firstTimeStart", true);


			if (!hasInternet) {
				Builder dialog = new AlertDialog.Builder(ctx);
				dialog.setMessage(ctx.getResources().getString(R.string.no_internet));
				dialog.setCancelable(false);
				dialog.setPositiveButton(
						ctx.getResources().getString( R.string.turned_on_internet),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface paramDialogInterface, int paramInt) {
								checkRequirements();
							}});

				dialog.setNegativeButton(ctx.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface paramDialogInterface, int paramInt) {
						finish();
					}});

				_dialog = dialog.create();
				_dialog.show();
			} else if (!typeInternet.equals("WIFI") && firstTimeStart){ // Check when internet not Wifi and if the first time started
				Log.e("typeInternet", typeInternet);
				new getFilesSizesTask().execute(null, null, null);
			} else {
				ctx.startActivity(new Intent(ctx,MainActivity.class));
				finish();
			}
		}
	}

	/**
	 * Get the number of kb of all 3d models (zip) and their screenshots (jpg)
	 * 
	 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
	 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
	 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
	 *
	 */
	private class getFilesSizesTask extends AsyncTask<URL, Integer, String> {
		protected String doInBackground(URL... urls) {

			return Download_Data.getDataFileSizes("2");
		}

		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {

			// Dialog for proceeding with telephony internet
			Builder dialog = new AlertDialog.Builder(ctx);


			float resultFloat = 0;

			try {
				resultFloat = (float) ((int) (Float.parseFloat(result)/100))/10;
			} catch (Exception e){
				Toast.makeText(ctx, "Bad Internet Connection", Toast.LENGTH_LONG).show();
				finish();
			}

			String messageWifi = getResources().getString(R.string.wifi_recommend); 
			messageWifi = messageWifi.replace("XXX", Float.toString(resultFloat));		

			dialog.setMessage(messageWifi);
			dialog.setCancelable(false);
			dialog.setPositiveButton(
					ctx.getResources().getString(R.string.continuewithtelephony),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick( DialogInterface paramDialogInterface, int paramInt) {
							ctx.startActivity(new Intent(ctx, MainActivity.class));
							finish();
						}});

			dialog.setNegativeButton(ctx.getResources().getString(R.string.quit),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(
						DialogInterface paramDialogInterface, int paramInt) {
					finish();
				}});

			_dialog = dialog.create();
			_dialog.show();

			Editor prefs = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
			prefs.putBoolean("firstTimeStart", false);
			prefs.commit();
		}
	}
}