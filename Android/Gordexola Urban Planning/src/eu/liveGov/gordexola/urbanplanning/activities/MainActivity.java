package eu.liveGov.gordexola.urbanplanning.activities;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.metaio.tools.SystemInfo;

import eu.liveGov.urbanplanning.gordexola.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.activities_fragments.UserInformationActivity;
import eu.liveGov.libraries.livegovtoolkit.activities_fragments.ARFragment;
import eu.liveGov.libraries.livegovtoolkit.activities_fragments.MapFragment;
import eu.liveGov.libraries.livegovtoolkit.activities_fragments.ListaFragment;
import eu.liveGov.libraries.livegovtoolkit.helper.LogFileHelper;
import eu.liveGov.libraries.livegovtoolkit.helper.PermissionHelper;
import eu.liveGov.libraries.livegovtoolkit.helper.UserInformationHelper;
import eu.liveGov.libraries.livegovtoolkit.interfaces.PermissionListener;
import eu.liveGov.libraries.livegovtoolkit.interfaces.UserInformationUpdateListener;
import eu.liveGov.libraries.livegovtoolkit.objects.FetchResources;
import eu.liveGov.libraries.livegovtoolkit.objects.UserInformation;
import eu.liveandgov.ar.core.ARViewFragment;

/**
 * This activity is the central activity where all the fragments are loaded.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class MainActivity extends FragmentActivity implements PermissionListener, UserInformationUpdateListener {

	//-------- To Review:----------------
	private Bundle _savedInstanceState;
	private boolean userinfoLoaded = false;

	private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
	
	//------- Tracker for Google analytics ----
	private Tracker tracker;
	// ----- Monitor Kbytes -------
	private int uid;
	private long mStartRX = 0;
	private long mStartTX = 0;
	// ------ Temperature warnings ----------
	BroadcastReceiver mReceiver_Temperature;
	private boolean isReg_mReceiver_Temperature = false;
	//---------- Tabhost --------------
	private TabHost _tabhost;
	private NonSwipeableViewPager _pager;
	private MyPagerAdapter _pagerAdapter;
	Context ctx;

	//-------- Progress bar ---------------------- 
	private ProgressBar pbarGeneral;

	//-------- Transparent dialog in Front -------- 
	static Handler handlerDialog;
	static ProgressDialog progressReceiving;


	BroadcastReceiver mReceiverARFinished;
	IntentFilter intentFilter;

	
	//------------------------------------------------------
	//----------- On Create -------------------------------
	//-----------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Process process = Runtime.getRuntime().exec("logcat -c");
		 } catch (IOException e) {
			 Log.e("MainActivity", "I was unable to clear LogCat");
		}
		_savedInstanceState = savedInstanceState;
		configureLogbackDirectly();

		tracker = EasyTracker.getInstance(this);
		setContentView(R.layout.activity_main);
		initialiseTabHost();
		initialisePager();
		ctx = this;

		//------ Location Service Start --------------------
		Functions.startLocationService(ctx);
		
		//------------ Measure kb downloaded ----------------
		uid = this.getApplicationInfo().uid;
		mStartRX = TrafficStats.getUidRxBytes(uid);
		mStartTX = TrafficStats.getUidTxBytes(uid);

		//------------ Menu hard or soft key ------------------
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			boolean hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey();

			ActionBar actionBar = getActionBar();
			if(!hasMenuKey) {
				actionBar.show();
			} else {
				actionBar.hide();

			}
		}

		//------------------ Temperature -----------------------------------
		mReceiver_Temperature = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int  temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
				if (temperature > 450){
					String mes = getString(R.string.tempwarn);
					int fullstopIndex = mes.indexOf(".");

					mes = mes.replace("XXXXX", Float.toString((((float) temperature)/10))+ "\u2103");
					String mesA = mes.substring(0, fullstopIndex);
					Toast.makeText(ctx, mesA, Toast.LENGTH_LONG).show();
					String mesB = mes.substring(fullstopIndex+2);
					Toast.makeText(ctx, mesB, Toast.LENGTH_LONG).show();
				}
			}
		};

		//--------------- Remove Transparency and Enable -------------------------------
		intentFilter = new IntentFilter("android.intent.action.MAIN"); // DataCh
		mReceiverARFinished = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String ARFINISHED     = intent.getStringExtra("ARFINISHED");
				Integer ARDATAPLUSVal     = intent.getIntExtra("DataPlus",0);

				if (ARFINISHED!=null){
					Log.e("AR Finished", "ok 2");

					if (pbarGeneral!=null)
						pbarGeneral.setProgress(90);

					Message msg = new Message();
					msg.arg1 = 2;
					handlerDialog.sendMessage(msg);

				} else if (ARDATAPLUSVal!=0){
					if (pbarGeneral!=null){

						if (ARDATAPLUSVal>90)
							ARDATAPLUSVal = 90;

						pbarGeneral.setProgress(ARDATAPLUSVal);
					}else {
						if (ARDATAPLUSVal < 15){
							Message msg = new Message();
							msg.arg1 = 1;
							handlerDialog.sendMessage(msg);
						}
					}
				}
			}
		};
		registerReceiver(mReceiverARFinished, intentFilter);

		//----- Handler for Redrawing Markers from update thread ------------
		handlerDialog = new Handler()
		{
			public void handleMessage(Message msg)
			{
				if (msg.arg1 == 1){
					if(progressReceiving == null){
						progressReceiving = ProgressDialog.show(ctx, "", "", true);
						progressReceiving.setContentView(R.layout.dialog_transparent_progress);
						pbarGeneral = (ProgressBar) progressReceiving.findViewById(R.id.allprogress);
					}
				} else {
					if (progressReceiving!=null && progressReceiving.isShowing())
						progressReceiving.dismiss();

					progressReceiving = null;
				}
				super.handleMessage(msg);
			}
		};

		if (Functions.checkInternetConnection(this) &&
				Functions.hasNetLocationProviderEnabled(this) && 
				Functions.hasGPSLocationProviderEnabled(this)){
			// Show progress transparency
			Message msg = new Message();
			msg.arg1 = 1;
			handlerDialog.sendMessage(msg);
		}
	}

	//---------- AR Entities Exist ? ----------------------
	private boolean hasItems(){
		boolean out = false;
		// If it has already some items and returning from EntityDetails, remove ProgressBar
		if (FetchResources.entitiesLBS!=null)
			if (FetchResources.entitiesLBS.size()>0){
				out = true;
			}
		return out;	
	}

	//----------- Resume -----------
	@Override
	public void onResume() {
		super.onResume();

		//=========== Start Camera of AR =====================
		if (_tabhost.getCurrentTab() == 2 && ARViewFragment.mFragmentIsPaused){
			final int cameraIndex = SystemInfo.getCameraIndex(CameraInfo.CAMERA_FACING_BACK);
			ARFragment.metaioSDK.startCamera(cameraIndex, 320, 240);
			ARViewFragment.mFragmentIsPaused = false;
		}

		//------- Temperature ------------
		registerReceiver(mReceiver_Temperature,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		isReg_mReceiver_Temperature = true;

		

		//===========================================
		// START THE APP HERE ---------------------
		//===========================================
		if(!userinfoLoaded) { 
			userinfoLoaded = true;
			UserInformationHelper uih = new UserInformationHelper();
			uih.addListener(this);
			uih.loadUserInformartion(getBaseContext(), true);
		}
	}

	//--------------- Destroy --------------------
	@Override
	protected void onDestroy() {

		if (isReg_mReceiver_Temperature){
			unregisterReceiver(mReceiver_Temperature);
			isReg_mReceiver_Temperature = false;
		}
		super.onDestroy();
		unregisterReceiver(mReceiverARFinished);
		PermissionHelper.removeListener(this);
	}

	private void clearAllTabs() {
		if (_tabhost != null) {
			_tabhost.clearAllTabs();
		}
		if (_pagerAdapter != null) {
			_pagerAdapter.removeAll();
		}
	}


	private void loadFragmentsWithPermissions(String CalledBy) {
		clearAllTabs();

		//_progressBar.setVisibility(View.GONE);
		// The id of where a 2nd fragment can be placed if neccesary. Like a
		// master/detail.
		int fragPlaceID = R.id.fragmentPlace;
		Bundle b = new Bundle();
		b.putInt("fragPlaceID", fragPlaceID);


		//if (!_hasDownloadItems) {
		if (!hasItems()){
			String locationSTR = "";  // lat,long,alt

			if (pbarGeneral!=null)
				pbarGeneral.setProgress(5);

			FetchResources mTask = null;
			mTask = new FetchResources(this, locationSTR, "--- Fetch Resources NOW ----" + CalledBy );
			mTask.execute(0);
		} else {
			Log.e("Main Activity","Send Broadcast Data Fetched");
			sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("DataFetched", "DataFetched"));
			sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("ARFINISHED", "ok"));
		}


		if (PermissionHelper.gotPermission(this,PermissionHelper.PERMISSION_LIST)) {
			ListaFragment propListFrag = new ListaFragment();
			propListFrag.setArguments(b);
			addFragment(propListFrag, "List", R.drawable.ic_list);
		}

		if (PermissionHelper.gotPermission(this, PermissionHelper.PERMISSION_MAP)) {
			MapFragment mapFrag = new MapFragment();
			mapFrag.setArguments(b);
			addFragment(mapFrag, "Map", R.drawable.ic_map);
		}

		if (PermissionHelper.gotPermission(this, PermissionHelper.PERMISSION_AR)) {
			/** Set on/off AR modes: LBS, IBS, and IBS* */
			boolean[] modesOn = new boolean[] { true, true, false };

			/**
			 * The mode to start among the true values: 0=the first true, 1=the
			 * second true, 2=the third true
			 */
			Fragment f = new ARFragment();
			//((ARFragment) f).setup(modesOn, arType_initial);
			f.setArguments(b);
			addFragment(f, "AR", R.drawable.ic_camera);

			ARFragment.sleep = 1000;
		}
	}

	private void addFragment(Fragment f, String title, int resourceID) {
		TabSpec tabSpec = _tabhost.newTabSpec(title);
		Drawable d = getResources().getDrawable(resourceID);
		tabSpec.setIndicator("", d);
		tabSpec.setContent(new MyTabHostAdapter(this));
		_tabhost.addTab(tabSpec);
		_pagerAdapter.addFragment(f);
	}

	private void initialiseTabHost() {
		_tabhost = (TabHost) findViewById(android.R.id.tabhost);
		_tabhost.setup();
		_tabhost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				int pos = _tabhost.getCurrentTab();

				if (pos==2 && ARViewFragment.mFragmentIsPaused){
					final int cameraIndex = SystemInfo.getCameraIndex(CameraInfo.CAMERA_FACING_BACK);
					ARFragment.metaioSDK.startCamera(cameraIndex, 320, 240);
					ARViewFragment.mFragmentIsPaused = false;
				}

				if (pos==2){
					if (ARFragment.metaioSDK!=null){
						ARFragment.sleep = 0;
					}
				} else {
					if (ARFragment.metaioSDK!=null){
						ARFragment.sleep = 1000;
					}
				}

				_pager.setCurrentItem(pos);
				_pager.setSwipe(false);// lock the swiping for all tabs
				tracker.set(Fields.SCREEN_NAME, "MainScreen - tab: " + _tabhost.getCurrentTabTag());
				tracker.send( MapBuilder.createAppView().build() );
				
				logger.info("onTabChanged; Tab changed to: {}",	_tabhost.getCurrentTabTag());
			}
		});
	}

	private void initialisePager() {
		_pager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
		_pager.setOffscreenPageLimit(3);
		_pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		_pager.setAdapter(_pagerAdapter);
		_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				int pos = _pager.getCurrentItem();
				_tabhost.setCurrentTab(pos);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void permissionsUpdated() {
		loadFragmentsWithPermissions("permissionsUpdated");
	}

	@Override
	public void anonymousUpdated() {
		
		logger.info("anonymousUpdated");
		PermissionHelper.addListener(this);

		if (_savedInstanceState == null) {
			new LogFileHelper().postLogFile(getApplicationContext());
			Time now = new Time();
			now.setToNow();
			logger.info("Starting the application... ({})", now.format3339(false));
			PermissionHelper ph = PermissionHelper.getInstance();
			ph.downloadPermissions(getBaseContext());

		} else {
			if (!PermissionHelper.isDownloading()) {
				loadFragmentsWithPermissions("startupApp");
			}
		}
		_savedInstanceState = null; // we don't need it anymore.

		if (pbarGeneral!=null)
			pbarGeneral.setProgress(2);

	}

	/**  
	 *  This loads the user information questionnaire 
	 * @see eu.liveGov.libraries.livegovtoolkit.interfaces.UserInformationUpdateListener#userinfoQuestionaireUpdated()
	 */
	@Override
	public void userinfoQuestionaireUpdated() {
		if (UserInformationHelper.getAnonymousUserId(getBaseContext()) == UserInformation.UNDEFINED_ID) {
			new UserInformationHelper(this).requestNewAnonymous(getBaseContext());
		}
	}

	//=================================================================================
	//==================== AUXILIARY ==================================================
	//=================================================================================

	//------------- Start -----------
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Functions.stopLocationService(getApplicationContext(), false);		
	}


	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	
	//-------------- Create Menu Options --------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	//----------- On Options Menu item clicked ---------------
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.User_information:
			i = new Intent(this, UserInformationActivity.class);
			startActivity(i);
			return true;
		case R.id.about_id:
			i = new Intent(this, AboutActivity.class);
			startActivity(i);
			return true;
		case R.id.myDebugItem_id: //myloc_id:
			i = new Intent(this, DebugInfoActivity.class);
			i.putExtra("kbgot", TrafficStats.getUidRxBytes(uid)- mStartRX );
			i.putExtra("kbsend", TrafficStats.getUidTxBytes(uid)- mStartTX);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//------------------- On Back Button Pressed -----------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Ask the user if they want to quit
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.quit_title)
			.setMessage(R.string.really_quit)
			.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).setNegativeButton(R.string.no, null).show();

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}


	/** Low memory hint */
	@Override
	public void onLowMemory() {
		Toast.makeText(ctx, "Unfortunately your device is running out of memory", Toast.LENGTH_LONG).show();
		super.onLowMemory();
	}
	
	// For Logger
	private void configureLogbackDirectly() {

		// reset the default context (which may already have been initialized)
		// since we want to reconfigure it
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.reset();

		// setup FileAppender
		PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
		encoder1.setContext(lc);
		encoder1.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		encoder1.start();

		FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
		fileAppender.setContext(lc);
		String s = this.getFileStreamPath("app.log").getAbsolutePath();
		LogFileHelper.setLogFileLocation(s);
		fileAppender.setFile(s);
		fileAppender.setEncoder(encoder1);
		fileAppender.start();

		// setup LogcatAppender
		PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
		encoder2.setContext(lc);
		encoder2.setPattern("[%thread] %msg%n");
		encoder2.start();

		LogcatAppender logcatAppender = new LogcatAppender();
		logcatAppender.setContext(lc);
		logcatAppender.setEncoder(encoder2);
		logcatAppender.start();

		// add the newly created appenders to the root logger;
		// qualify Logger to disambiguate from org.slf4j.Logger
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(Logger.ROOT_LOGGER_NAME);
		root.addAppender(logcatAppender);

		ch.qos.logback.classic.Logger euLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("eu");
		euLogger.setLevel(ch.qos.logback.classic.Level.INFO);
		euLogger.addAppender(fileAppender);
	}

}