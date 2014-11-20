/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveGov.gordexola.urbanplanning.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.widget.TextView;

import com.metaio.sdk.jni.LLACoordinate;

import eu.liveGov.urbanplanning.gordexola.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.activities_fragments.ARFragment;
import eu.liveGov.libraries.livegovtoolkit.location.LocationService;

/**
 * 
 * This Activity presents debug information such as KBs downloaded and uploaded, location fixes, and temperature
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class DebugInfoActivity extends Activity {

	Long kbGot;
	Long kbSend;

	private BroadcastReceiver mReceiverLocChanged;
	boolean isReg_mReceiverLocChanged = false;
	private IntentFilter intentFilter;

	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			TextView tv = (TextView) findViewById(R.id.tvTemperatureInfo);

			int  temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);

			tv.setText(((float)temperature)/10 + " C");
		}
	};

	public static int fL(double in){

		long iPart = (long) in;
		double fPart = in - iPart;

		return (int)(fPart*1E4);
	}

	public static int ft(long in){
		in = in/1000;

		return (int) (in - ((in/1000)*1000));

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_info);

		Intent mIntent = getIntent();
		kbGot = mIntent.getLongExtra("kbgot", -1); 
		kbSend = mIntent.getLongExtra("kbsend", -1);

		//--------------- Receiver for Location Changed -------
		intentFilter = new IntentFilter("locarsens");
		mReceiverLocChanged = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String vVector = intent.getStringExtra("LocChanged");
				if (vVector.equals("debug")){
					enlistArchValues(false);
				}
			}
		};

	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

		TextView tvData = (TextView) findViewById(R.id.tvDataInfo);
		tvData.setText("Got:" + (kbGot/1000) + " Kb; Sent: " + (kbSend/1000) + " Kb");

		TextView tvLoc = (TextView) findViewById(R.id.tvLocInfo);
		Location cl = LocationService.cl;

		LLACoordinate lla = ARFragment.mSensors.getLocation();

		String acl_time = "";
		if (lla.getTimestamp() > 0.001){

			int t = (int) (SystemClock.uptimeMillis()/1000 - lla.getTimestamp());
			if (t < 60)
				acl_time = " " + t + " seconds ago" ;
			else 
				acl_time = " " + t/60 + " minutes ago";
			
		} else 
			acl_time = "N/A";

		String htmlString = "<strong>Mode | Latitude | Longitude | Accuracy | Time </strong><br>"+
				"<strong>----------------------------------------------</strong><br>"+
				"Map : "+ fl(cl.getLatitude()) + " | "+ fl(cl.getLongitude()) +	" | "+ (int)cl.getAccuracy() + " | " + 
				Functions.formatTimestamp(cl.getTime()) + "<br>"+
				"<strong>----------------------------------------------</strong><br>"+
				"ARv: " +fl(lla.getLatitude())+" | "+ fl(lla.getLongitude())+" | " + (int)lla.getAccuracy() + " | "+ acl_time;

		tvLoc.setText(Html.fromHtml(htmlString));

		this.registerReceiver(batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		this.registerReceiver(mReceiverLocChanged, intentFilter);

		
		enlistArchValues(true);
		
		super.onResume();
	}

	/**
	 *   List values of archive 
	 */
	public void enlistArchValues(boolean keepold){
		TextView tvLocArch = (TextView) findViewById(R.id.tvLocationArchive);
		int NFix = ARFragment.arch_loc.size();

		String mes = "";
		if (keepold)
			mes = tvLocArch.getText() + "<br>";

		for (int i=0; i<NFix; i++){
			Location l = ARFragment.arch_loc.get(i);
			mes += (float)l.getLatitude() + "|" + (float)l.getLongitude() + "|" + l.getAccuracy() + "|" + 
					Functions.formatUTC(l.getTime()) + "<br>";
		}

		tvLocArch.setText(Html.fromHtml(mes));
	}

	//------------ Various ----------------
	public String fl(double in){
		return String.format("%.5f", in);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		this.unregisterReceiver(batteryInfoReceiver);
		this.unregisterReceiver(mReceiverLocChanged);
		super.onPause();
	}
}
