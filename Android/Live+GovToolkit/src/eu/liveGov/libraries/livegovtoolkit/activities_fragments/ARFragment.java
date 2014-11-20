package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.metaio.sdk.SensorsComponentAndroid.Callback;
import com.metaio.sdk.jni.ECOLOR_FORMAT;
import com.metaio.sdk.jni.IBillboardGroup;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IGeometryVector;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.ImageStruct;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.Utils.ReadLogCatAndMail;
import eu.liveGov.libraries.livegovtoolkit.location.LocationService;
import eu.liveGov.libraries.livegovtoolkit.location.ShakeService;
import eu.liveGov.libraries.livegovtoolkit.location.ShakeService.LocalBinder;
import eu.liveGov.libraries.livegovtoolkit.objects.FetchResources;
import eu.liveandgov.ar.core.ARViewFragment;
import eu.liveandgov.ar.utilities.Entity;
import eu.liveandgov.ar.utilities.Graphic_Utils;
import eu.liveandgov.ar.utilities.OS_Utils;

/**
 * This Fragment implements, LBS, IBS, and IBS* AR.
 * 
 * The user guidelines are as follows: The fragment starts by loading the LBS
 * channel and showing only the billboards and two buttons: 1. Toggle 3d button:
 * Toggle between LBS 3d models and LBS billboards:
 * 
 * 
 * A. Billboard mode: Click on billboard to go to ActivityPosterior B. LBS 3d
 * mode: Click on Entity (3d model) i. if the clicked Entity has no alternative
 * 3d model then go to ActivityPosterior ii. if clicked model has alternative 3d
 * models then a new button appears named as 'Shift models' which can be used to
 * replace currently clicked model with the other alternative models iii. if the
 * clicked Entity is 'reclicked' then go to ActivityPosterior iv. if another
 * Entity is clicked then the 'Shift model' refers to the newly clicked model
 * 
 * 2. LBS (AR Type button) switch between LBS, IBS, and IBS*. In IBS models are
 * clickable. In IBS* mode, a new button name as "Recon" appears. Recon button
 * sends the current preview screen image to the remote server for recognition.
 * If the image is recognized to belong to an Entity (positive recognition
 * score) and the Entity is within the range of the downloaded data, then the
 * Entity appears in ActivityPosterior.
 * 
 * 
 * 
 * @copyright Copyright (C) 2012 - 2014 Information Technology Institute
 *            ITI-CERTH. All rights reserved.
 * @license GNU Affero General Public License version 3 or later; see
 *          LICENSE.txt
 * @author Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr).
 * 
 */
public class ARFragment extends ARViewFragment {

	// ------------ PUBLIC -----------------------
	/** Delay render thread to keep temperature low when AR not visible */
	public static int sleep = 0;
	
	/** Archive of Location fixes that it is used in Debug details activity */
	public static ArrayList<Location> arch_loc = new ArrayList<Location>();
	
	//----------- STATIC ------------------------------
	/** if Data is received but mGLSurface is not ready yet then true.*/
	private static boolean isIgnitionPending = false;
	//------- PRIVATE ------------------------------

	// Shake
	private boolean isLocShakeRegCallback = false;
	boolean isShakeServBound = false;
	public ShakeService mShakeService = null; // for the Shake service
	BroadcastReceiver mReceiver_Shake; // To stop the lock 
	private boolean isReg_mReceiver_Shake = false;
	
	// Image Recognition Star
	BroadcastReceiver mReceiver_ImRec; // To implement Image Recognition
	private boolean isReg_mReceiver_ImRec = false;
	BroadcastReceiver mReceiver_ImageLost; // This Receiver is useful for dissapearing the Models 1/3 button
	private boolean isReg_mReceiver_ImageLost = false;

	// DataFetched
	BroadcastReceiver mReceiver_DataFetched;
	private boolean isReg_mReceiver_DataFetched = false;


	Handler handler_update_ModelsBt;
	private boolean updateBillboardTextures = false;
	
	
	//---------- DEBUG -------------
	private boolean DEBUG_FLAG = true;
	private int debugFlagNumber = 0;
	private String TAG = getClass().getName();
	private Tracker tracker; // Analytics

	//--------- To Review --------
	private Callback myCallBackMetaioLocSensor = new Callback() {

		@Override
		public void onLocationSensorChanged(LLACoordinate currloc) {
			put2arch(currloc);

			//-------------------- George Method ----------------
			if(pastLoc==null){
				//if there is no last location -> accept current location
				setLocGlobal(currloc);
				invalidateAndDebug();
				Tost("Null acc = " + currloc.getAccuracy());
				return;
			}

			boolean isBetter = currloc.getAccuracy() - pastLoc.getAccuracy() <-10.0; // -10
			boolean isEqual  = Math.abs(currloc.getAccuracy() - pastLoc.getAccuracy()) < 0.5; // -10 George: no need to put -10.0 here

			float dist;
			float[] result = new float[1];
			Location.distanceBetween(currloc.getLatitude(), currloc.getLongitude(), pastLoc.getLatitude(), pastLoc.getLongitude(), result);
			dist = result[0];

			boolean isClose  = dist<=1.0f;
			boolean isTooFar = dist>30.0f;

			boolean isOld = Functions.getCurrTime() - pastLoc.getTime() > 15000;

			if(isBetter){
				//if received location with better accuracy accept it
				setLocGlobal(currloc);

				//if shake view is shown -> hide it
				hideShake();

				Tost("Better acc = " + currloc.getAccuracy());
			} else if(isEqual){   //received location with same accuracy
				if(isClose){  //if distance is less than 1 meter accept it (no jump effect on 3d models)
					setLocGlobal(currloc);
					//if shake view is shown -> hide it
					hideShake();
					Tost("Equal Close acc = " + currloc.getAccuracy());
				} else if (!isClose){ 
					//if distance is more than 1 meter deny new location
					//if more than 15 sec have passed since last update -> show shake view

					// currLoc is rejected, must set curloc = pastloc otherwise it is accepted and objects move!!1
					setToPast(currloc);
					if(isOld){
						if(!isShakeServBound){
							showShake();

							Tost("Equal but not Close acc = " + currloc.getAccuracy());
						} else if (isShakeServBound){
							/*if enough time has passed and shake view is shown and distance is > 30m ->
				                     shake the shake view (animation) to remind user to shake because
				                     the accuracy of the AR location is too inaccurate
							 */
							if(isTooFar){
								//								hideShake();
								Tost("Equal, not Close, and too far acc = " + currloc.getAccuracy());
							}
						}
					}
				}
			} else {  //accuracy of curr loc is worse
				if(isOld){    //if 15 sec have passed since last update, show the shake view
					showShake();
					// currLoc is rejected, must set curloc = pastloc otherwise it is accepted and objects move!!1
					setToPast(currloc);
					Tost("Worse and Old acc = " + currloc.getAccuracy());
				}
			}
			//---------------------- End of George method -------
			invalidateAndDebug();
		}
		//--------------------------------------------------------------------
		@Override
		public void onHeadingSensorChanged(float[] orientation) {}

		@Override
		public void onGravitySensorChanged(float[] gravity) {}
	};

	//--------- Location ----------
	private Location pastLoc = null;
	private RelativeLayout rlShake;
	private Intent IntShakeServ; 
	private TextView tvShake;
	private ImageView imvShake;
	private int accuracy_loc_previous = 0; // Warn for location accuracy
	
	///-----------------------------
	boolean isIgnited = false;
	boolean flag_contents_loaded = false;
	boolean isGLSurfaceReady = false;

	// ----------- DATA ------------
	int arType_current = 0;
	String[] modesSTRA = new String[] {"LBS","IBS"};

	//  Geometries LBS
	private IBillboardGroup billGroup; // Group of 2D billboards that helps to avoid overlapping
	private IGeometry[] mGeo2D;  // 2D billboards geometries
	private IGeometry[] mGeo3D;  // 3D models geometries
	private int LBS_3DSCALE_FACTOR = 1000; /** This is how much models will grow on LBS (x millims units)*/

	private int[] mGeo3DMem; // Memory which object is visible per 3d case (1,2 or 3)
	private int[] mGeo3DTotalModels; // Total Models 1 or 2 or 3 per case

	// Geometries IBS
	int N = 0; // IBS
	private IGeometry[] mGeo; /** The 3d models of the IBS channel loaded a priori */
	//public int IBS_3DSCALE_FACTOR = 10; /** This is how much models will grow on paper (x millims units)*/

	private int[] mGeoMem; // Memory which object is visible per 3d case (1,2,3)
	private int[] mGeoTotalModels; // Total Models 1 or 2 or 3 per case

	private IRadar mRadar;

	int N_2d = 0; List<Integer> indices2D = new ArrayList<Integer>(); // 2D indices
	int N_3d = 0; List<Integer> indices3D = new ArrayList<Integer>(); // 3D indices

	int index3D_ClickedLoc = -1;

	//-------- Visualizations -----------
	ToggleButton togg3d;
	Button bt_models, toggType, bt_Recognize;
	
	private LinearLayout lltarget; 
	private LinearLayout llaract;


	//-----------------------------
	private Class<?> classToCall = DetailActivity.class;
	private String dirModels;

	/*============================================================
	 *                          On Create 
	 *=============================================================*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dirModels = getTheContext().getFilesDir().getAbsolutePath() +"/Models3D_DB/";

		tracker = EasyTracker.getInstance(getActivity());
		billGroup = metaioSDK.createBillboardGroup();

		IntShakeServ = new Intent(getTheContext(), ShakeService.class);

		handler_update_ModelsBt = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				bt_models.setText("Model " + msg.arg1 + "/" + msg.arg2);
				super.handleMessage(msg);
			}
		};

		// ---------------- Receiver for Data Down Completed ---------------------------------------
		IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");

		mReceiver_DataFetched = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String res = intent.getStringExtra("DataFetched"); // 1
				if (res!= null){
					Log.e("ARFragment","exec mReceiver_DataFetched " + res + " " + isGLSurfaceReady + " " + isIgnitionPending);

					if (isGLSurfaceReady) {
						Log.e("isSurf","true");
						ignite("SurfaceReady");
						toggType.setEnabled(true);
						togg3d.setEnabled(true);
						isIgnitionPending = false;
					} else {
						Log.e("isSurf","false");
						isIgnitionPending = true;
					}
				}
			}
		};

		Log.e("ARFRAG", "Register Receiver Data Fetched");
		getTheContext().registerReceiver(mReceiver_DataFetched,	intentFilter);
		isReg_mReceiver_DataFetched = true;

		//--------------- Receiver for Authenticated -------
		mReceiver_ImageLost = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String imLost = intent.getStringExtra("ImageLost");         // 1
				String imFound = intent.getStringExtra("ImageFound");         // 1

				if (imLost!=null){
					bt_models.setVisibility(View.INVISIBLE);
					index3D_ClickedLoc = -1;	
				}

				if (imFound!=null && modesSTRA[arType_current].equals("IBS")){
					bt_models.setVisibility(View.VISIBLE);
					index3D_ClickedLoc = Integer.parseInt(imFound.substring(4)) - 1;
					bt_models.setText("Model 1/"+mGeo3DTotalModels[index3D_ClickedLoc]);
				}
			}
		};

		if (!isReg_mReceiver_ImageLost){
			getTheContext().registerReceiver(mReceiver_ImageLost, intentFilter);
			isReg_mReceiver_ImageLost = true;
		}

		/* --------------- Shake Message Receiver to stop lock  --------------------- */
		mReceiver_Shake = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String res = intent.getStringExtra("Shaked"); // 1

				if (res!= null){
					if (isShakeServBound){
						getTheContext().unbindService(mShakeConnection);
						isShakeServBound = false;	
						setShakeView(View.INVISIBLE);
						pastLoc = null;
					}

					Log.e("Shake ", "Shake Detected");
				}
			}
		};
	}


	//================== END OF LOCATION ===================================

	/** 
	 * Load the contentents and Ignite the AR 
	 * 
	 * */
	public void ignite(String FromWhere) {

		if (mGLSurfaceView == null) {
			Log.e("mGLSurfaceView", "mGLSurfaceView is NULL");
			Toast.makeText(getTheContext(), "mGLSurfaceView is NULL", Toast.LENGTH_LONG).show();
		} else {


			mGLSurfaceView.queueEvent(new Runnable() {
				@Override
				public void run() {
					if (!flag_contents_loaded) {
						flag_contents_loaded = loadContents("Ignition");

						if (flag_contents_loaded){
							Log.e("IGNITE WITH:", 
								" (LBS:" + FetchResources.entitiesLBS.size() + ")" +
										" (IBS:" + FetchResources.entitiesIBS.size() + ")" +  
										" (IBSL:" + N + ") (LBS BILL:" + N_2d + ") (LBS 3D:" + N_3d +")");
							
							

							
						} else {

							//----------------------------------------------------------------------
							String mes = "Unable to load Contents: DEBUG_FLAG="+debugFlagNumber;
							Log.e("ERROR ARFragment", mes);							
							
							//------------ Get Version Number
							String versionNum = "";
							try {
								versionNum = getTheContext().getPackageManager().getPackageInfo(
										getTheContext().getPackageName(), 0).versionName;
							} catch (NameNotFoundException e) {
								versionNum = "--";
							}
							
							
							String LogCat = ReadLogCatAndMail.ReadCogCat();
							ReadLogCatAndMail.sendLogByMail(getTheContext(),LogCat, versionNum);
							//---------------------------------------
							
							return;
						}


						try {
							if (modesSTRA[arType_current].equals("LBS")){
								metaioSDK.setTrackingConfiguration("GPS");
								if (!isLocShakeRegCallback){
									mSensors.registerCallback(myCallBackMetaioLocSensor);
									isLocShakeRegCallback = true;
								}
							}else if (modesSTRA[arType_current].equals("IBS")) {
								metaioSDK.setTrackingConfiguration( FetchResources.localFolderSTR + "/IBS/Tracking.zip");
							}

							getTheContext().sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("ARFINISHED", "ok"));

							isIgnited = true;
						} catch (Exception e) {
							deb("IGNITION", "ERROR");
						}
					}
				}
			});
		}
	}
	//----------- End of Ignite ---------------
	@Override
	public void onSurfaceCreated() {
		super.onSurfaceCreated();
		isGLSurfaceReady = true;

		if (FetchResources.entitiesLBS!=null)
			if (isIgnitionPending && FetchResources.entitiesLBS.size()>0){
				Log.e("ARFragment ", "onSurfaceReady sendbroadcast DataFetched");
				getTheContext().sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("DataFetched", "DataFetched"));
			}
	}

	@Override
	public void onSurfaceDestroyed() {
		super.onSurfaceDestroyed();
		isGLSurfaceReady = false;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Create the view
		View v = super.onCreateView(inflater, container, savedInstanceState);
		bt_models = (Button) v.findViewById(R.id.bt_ChangeModel);
		bt_Recognize = (Button) v.findViewById(R.id.bt_Recognize);
		bt_Recognize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				metaioSDK.requestScreenshot(FetchResources.localFolderSTR + "/temp.jpg");
				tracker.send(MapBuilder.createEvent("AR view", "button_press", "Recognize button pressed",   // Event label
						null).build());
			}
		});

		rlShake  = (RelativeLayout) v.findViewById(R.id.rlShake);
		tvShake = (TextView) v.findViewById(R.id.tvShake);
		imvShake = (ImageView) v.findViewById(R.id.imvShake);

		if (!isShakeServBound){
			getTheContext().bindService(IntShakeServ, mShakeConnection, Context.BIND_AUTO_CREATE);
			isShakeServBound = true;
		}

		return v;
	}

	/* ----------------------------------
	 *     onResume 
	 -------------------------------------- */
	@Override
	public void onResume() {
		super.onResume();

		deb("FAT", "onResume");

		getTheContext().registerReceiver(mReceiver_Shake,	new IntentFilter("shakeDet"));
		isReg_mReceiver_Shake = true;

		if (!isShakeServBound){
			getTheContext().bindService(IntShakeServ, mShakeConnection, Context.BIND_AUTO_CREATE);
			isShakeServBound = true;
		}

		bt_models.setVisibility(View.INVISIBLE);

		index3D_ClickedLoc = -1;
		llaract = (LinearLayout)v.findViewById(R.id.llaract);
		llaract.bringToFront();


		// -------- toggle 3d button ------------
		togg3d = (ToggleButton) v.findViewById(R.id.bt_ToggleInfo);
		if (modesSTRA[arType_current].equals("LBS"))
			togg3d.setVisibility(View.VISIBLE);
		else
			togg3d.setVisibility(View.INVISIBLE);


		togg3d.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				bt_models.setVisibility(View.INVISIBLE);
				index3D_ClickedLoc = -1;

				for (int i = 0; i < mGeo2D.length; i++)
					if (mGeo2D[i] != null) // Change visibility also depending on distance (far away do not show)
						mGeo2D[i].setVisible(
								CalcDist(FetchResources.entitiesLBS.get(indices3D.get(i)).location) < 100 *1000?!isChecked:false);


				for (int i = 0; i < mGeo3D.length; i++)
					if (mGeo3D[i] != null) // Change visibility also depending on distance (far away do not show)
						mGeo3D[i].setVisible(
								CalcDist(FetchResources.entitiesLBS.get(indices3D.get(i)).location) < 100 *1000?isChecked:false);

				tracker.send(MapBuilder.createEvent("AR view",     // Event category (required)
						"button_press",  // Event action (required)
						"3D toggeled to:" + ((isChecked)? "On" : "Off"),   // Event label
						null)            // Event value
						.build()
						);
			}
		});

		// ------- Toggle IBS or LBS -------------------
		toggType = (Button) v.findViewById(R.id.bt_ToggleType);

		lltarget = (LinearLayout) v.findViewById(R.id.lltarget);

		toggType.setVisibility(View.VISIBLE);
		toggType.setBackgroundResource(R.drawable.whitebutton);
		//toggType.bringToFront();

		if (modesSTRA.length == 1)
			toggType.setVisibility(View.INVISIBLE);

		if (modesSTRA[arType_current].equals("LBS")){
			toggType.setText(getTheContext().getResources().getString(R.string.scan));
			lltarget.setVisibility(View.INVISIBLE);
		}else {
			toggType.setText(getTheContext().getResources().getString(R.string.nearby));
			lltarget.setVisibility(View.VISIBLE);	
		}
		lltarget.bringToFront();

		toggType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				deb("modesSTRA.length", " " + modesSTRA.length + " " + arType_current);

				if (modesSTRA.length == 1)
					return;

				if (modesSTRA.length == 2) {
					if (arType_current == 0)
						arType_current = 1;
					else
						arType_current = 0;
				}

				if (modesSTRA.length == 3) {
					if (arType_current == 0)
						arType_current = 1;
					else if (arType_current == 1)
						arType_current = 2;
					else if (arType_current == 2)
						arType_current = 0;
				}

				if(modesSTRA[arType_current].equals("LBS")){
					rlShake.setVisibility(View.VISIBLE);
				} else {
					rlShake.setVisibility(View.INVISIBLE);
				}

				if (modesSTRA[arType_current].equals("LBS")){
					toggType.setText(getTheContext().getResources().getString(R.string.scan));
					lltarget.setVisibility(View.GONE);
				}else {
					toggType.setText(getTheContext().getResources().getString(R.string.nearby));
					lltarget.setVisibility(View.VISIBLE);
				}

				lltarget.bringToFront();

				tracker.send(MapBuilder
						.createEvent("AR view",     // Event category (required)
								"button_press",  // Event action (required)
								"toggeled mode to: " + modesSTRA[arType_current],   // Event label
								null)            // Event value
								.build()
						);

				if (modesSTRA[arType_current].equals("IBS*"))
					bt_Recognize.setVisibility(View.VISIBLE);
				else
					bt_Recognize.setVisibility(View.GONE);

				// --- toggle off the 3d LBS ------- (otherwise causes mem leak)
				// ----
				if ((modesSTRA[arType_current].equals("IBS") || modesSTRA[arType_current]
						.equals("IBS*")) && togg3d.isChecked())
					togg3d.performClick();

				bt_models.setVisibility(View.INVISIBLE);
				index3D_ClickedLoc = -1;

				// --------- toggle 3d button for LBS make invisible ----
				if (!modesSTRA[arType_current].equals("LBS"))
					togg3d.setVisibility(View.INVISIBLE);
				else {
					togg3d.setVisibility(View.VISIBLE);
					togg3d.setChecked(false);
				}

				mGLSurfaceView.queueEvent(new Runnable() {
					@Override
					public void run() {
						// ---- Unload geometries
						if (isIgnited) {
							IGeometryVector gs = metaioSDK.getLoadedGeometries();
							for (int i = 0; i < gs.size(); i++) {
								metaioSDK.unloadGeometry(gs.get(i));
								gs.get(i).delete();
							}
							billGroup = metaioSDK.createBillboardGroup();
							// ------ run garbage collector -----------
							System.gc();
							// and load new
							flag_contents_loaded = loadContents("Button");
						}
					}
				});

				// ----------- Set recognition method -------
				if (modesSTRA[arType_current].equals("IBS")) {

					metaioSDK.setTrackingConfiguration(FetchResources.localFolderSTR + "/IBS/Tracking.zip");
				} else if (modesSTRA[arType_current].equals("LBS")){
					metaioSDK.setTrackingConfiguration("GPS");

				}else if (modesSTRA[arType_current].equals("IBS*")) {

					metaioSDK.setTrackingConfiguration("");

					// ------------------------- Im Rec Star -------------------------
					if (!isReg_mReceiver_ImRec) {
						IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");

						mReceiver_ImRec = new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								final String[] res = intent
										.getStringArrayExtra("imrec"); // 1

								if (res.length == 2) // no EntityId was found to
									// match label res[0]
									Toast.makeText(ctxL, res[0] + " " + res[1],
											Toast.LENGTH_LONG).show();
								else {// EntityId was found
									Toast.makeText(
											ctxL,
											res[0] + " " + res[1] + " "
													+ res[2], Toast.LENGTH_LONG)
													.show();

									if (Float.parseFloat(res[1]) > 0) {

										// ---- find the serial index if any
										// ----
										int serial_index = -1;
										for (int i = 0; i < FetchResources.entitiesIBS
												.size(); i++) {
											if (FetchResources.entitiesIBS
													.get(i).id.equals(res[2])) {
												serial_index = i;
												break;
											}
										}
										// -------------------------------

										if (serial_index != -1) { // if serial
											// found then start new activity
											Intent mI = new Intent(ctxL, classToCall);
											mI.putExtra("idEntity", Integer.parseInt(res[2]));
											ctxL.startActivity(mI);
										} else { // serial not found means not
											// downloaded
											Toast.makeText(	ctxL, res[0] + ": "	+ res[2]
													+ " is not downloaded because it is too far.",
													Toast.LENGTH_LONG).show();
										}
									} else { // if negative score then not found
										// Toast.makeText(ctxL, "Negative score means not recognized", Toast.LENGTH_LONG).show();
									}
								}
							}
						};
						getTheContext().registerReceiver(mReceiver_ImRec,
								intentFilter);
						isReg_mReceiver_ImRec = true;
					}
					// -----------------------------------------------

				}

			}
		});

		// ---------- Shift between 3d models for the same Entity
		// --------------------
		bt_models.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mGLSurfaceView.queueEvent(new Runnable() {
					@Override
					public void run() {

						if (modesSTRA[arType_current].equals("LBS") && index3D_ClickedLoc != -1) {
							// remove from radar
							mRadar.remove(mGeo3D[index3D_ClickedLoc]);
							// remove from AR
							metaioSDK.unloadGeometry(mGeo3D[index3D_ClickedLoc]);

							// get new
							Entity e = FetchResources.entitiesLBS.get(indices3D.get(index3D_ClickedLoc));

							// Max number of 3d models for this entity
							int NModels3D_this = Integer.parseInt(e.nModels3d);

							// ------- current 3d model visible --------------------
							int curr_alt_mod = mGeo3DMem[index3D_ClickedLoc]; // mGeo3D[index3D_ClickedLoc].currModel3D;

							// - calc next model as modulo of curr + 1 / N total ---
							int next_alt_mod = (curr_alt_mod % NModels3D_this) + 1;

							// - find url by replace 1 to 2 or 2 to 3 or 3 to 1
							//String url = dirModels + e.modelfile;

							// - find url by replace 1 to 2 or 2 to 3 or 3 to 1
							String url = dirModels +"/"+ e.id + "/AR_" + e.id + "_"+next_alt_mod+"junaio.zip";

							// set current 3d model number to object
							mGeo3DMem[index3D_ClickedLoc] = next_alt_mod;

							// ------------------------------
							IGeometry mGeo3DNew = metaioSDK.createGeometry(url);

							mRadar.add(mGeo3DNew);

							mGeo3D[index3D_ClickedLoc] = mGeo3DNew;
							mGeo3DMem[index3D_ClickedLoc] = next_alt_mod;
							mGeo3DTotalModels[index3D_ClickedLoc] = NModels3D_this;

							if (mGeo3D[index3D_ClickedLoc] != null){
								mGeo3D[index3D_ClickedLoc].setScale(LBS_3DSCALE_FACTOR);
								mGeo3D[index3D_ClickedLoc].setRotation(new Rotation(new Vector3d(0.5f * (float)Math.PI, 0, 0)));
								mGeo3D[index3D_ClickedLoc].setName(e.id);
							}

							updateGeometriesLocation(mSensors.getLocation());

							Message msg = new Message();
							msg.arg1 = next_alt_mod;
							msg.arg2 = NModels3D_this;
							handler_update_ModelsBt.sendMessage(msg);
						} else if (modesSTRA[arType_current].equals("IBS")) {

							// remove from AR
							metaioSDK.unloadGeometry(mGeo[index3D_ClickedLoc]);

							// get new
							Entity e = FetchResources.entitiesIBS.get(index3D_ClickedLoc);

							// Max number of 3d models for this entity
							int NModels3D_this = Integer.parseInt(e.nModels3d);

							// ------- current 3d model visible --------------------
							int curr_alt_mod = mGeoMem[index3D_ClickedLoc]; // mGeo3D[index3D_ClickedLoc].currModel3D;

							// - calc next model as modulo of curr + 1 / N total
							// ---
							int next_alt_mod = (curr_alt_mod % NModels3D_this) + 1;

							// - find url by replace 1 to 2 or 2 to 3 or 3 to 1
							String url = dirModels +"/"+ e.id + "/AR_" + e.id + "_"+next_alt_mod+"junaio.zip";

							// set current 3d model number to object
							mGeoMem[index3D_ClickedLoc] = next_alt_mod;
							// ------------------------------
							IGeometry mGeo3DNew = metaioSDK.createGeometry(url);

							mGeo[index3D_ClickedLoc] = mGeo3DNew;
							mGeoMem[index3D_ClickedLoc] = next_alt_mod;
							mGeoTotalModels[index3D_ClickedLoc] = NModels3D_this;

							if (mGeo[index3D_ClickedLoc] != null) {
								mGeo[index3D_ClickedLoc].setScale(e.trackImScale);
								mGeo[index3D_ClickedLoc].setRotation(new Rotation(0, ((float)e.trackImRot) /180f*(float)Math.PI,0));
								mGeo[index3D_ClickedLoc].setName(e.id);
								mGeo[index3D_ClickedLoc].setCoordinateSystemID(index3D_ClickedLoc+1);
							}
							Message msg = new Message();
							msg.arg1 = next_alt_mod;
							msg.arg2 = NModels3D_this;
							handler_update_ModelsBt.sendMessage(msg);
						}
					}
				});
				tracker.send(MapBuilder
						.createEvent("AR view",     // Event category (required)
								"button_press",  // Event action (required)
								"Shift between 3d models from the same Entity",   // Event label
								null)            // Event value
								.build()
						);
			}
		});



		imvShake.bringToFront();
		tvShake.bringToFront();
		imvShake.invalidate();
		tvShake.invalidate();

		if(modesSTRA[arType_current].equals("LBS")){
			rlShake.setVisibility(View.VISIBLE);
		} else {
			rlShake.setVisibility(View.INVISIBLE);
		}

		rlShake.bringToFront();
		rlShake.invalidate();


		// ================= Initialize map location =====================================

		
		if (LocationService.cl!=null)
			if (Functions.getCurrTime() != 0)
				LocationService.cl.setTime(Functions.getCurrTime());

	}

	/* DESTROY */
	@Override
	public void onDestroy() {
		//FetchResources.removeResourcesListener(this);
		//		if (isReg_mReceiver_ImRec)
		//			getTheContext().unregisterReceiver(mReceiver_ImRec);

		isReg_mReceiver_ImRec = false;

		if (isReg_mReceiver_DataFetched){
			Log.e("ARFRAG", "UnRegister Receiver Data Fetched");
			getTheContext().unregisterReceiver(mReceiver_DataFetched);
			isReg_mReceiver_DataFetched = false;
			isIgnitionPending = true;
		}
		//
		if (isReg_mReceiver_ImageLost){
			getTheContext().unregisterReceiver(mReceiver_ImageLost);
			isReg_mReceiver_ImageLost = false;
		}

		if (isReg_mReceiver_Shake){
			getTheContext().unregisterReceiver(mReceiver_Shake);
			isReg_mReceiver_Shake = false;
		}

		if (isShakeServBound){
			getTheContext().unbindService(mShakeConnection);
			isShakeServBound = false;	
		}

		super.onDestroy();
	}



	/**
	 * Create the geometries and add to Radar
	 */
	protected boolean loadContents(String caller) {

		Log.e("LoadContents", caller);

		
		try {
			debugFlagNumber = 1;

			if (modesSTRA[arType_current].equals("LBS")) {
				// //---------------------- LBS -------------------
				N_2d = 0;
				indices2D = new ArrayList<Integer>();
				N_3d = 0;
				indices3D = new ArrayList<Integer>();


				metaioSDK.setRendererClippingPlaneLimits(5, 1000*1000);
				//				metaioSDK.setLLAObjectRenderingLimits(5*1000, 50 * 1000 * 1000);
				//				metaioSDK.setRendererClippingPlaneLimits(5, 1000 * 1000);

				debugFlagNumber = 2;

				for (int i = 0; i < FetchResources.entitiesLBS.size(); i++) {
					Entity e = FetchResources.entitiesLBS.get(i);

					if (e.type.equals("LBS 3D")) {
						N_3d++;
						indices3D.add(Integer.valueOf(i));
					} else {
						N_2d++;
						indices2D.add(Integer.valueOf(i));
					}
				}

				debugFlagNumber = 3;
				// ----------- 2 D ------------------
				mGeo2D = new IGeometry[N_2d];
				deb("BILL", " " + N_2d);

				for (int i = 0; i < N_2d; i++) {
					Entity e = FetchResources.entitiesLBS.get(indices2D.get(i));

					Bitmap bm = null;

					if (OS_Utils.fileexists(dirModels + e.iconfile)) {
						bm = BitmapFactory.decodeFile(dirModels + e.iconfile);
					} else {
						bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_no_image);
					}

					bm = Bitmap.createScaledBitmap(bm, 56, 56, true);

					// --- Calculate Distance -------
					int dist = CalcDist(e.location);

					// ---------- Create Billboard --------
					mGeo2D[i] = metaioSDK.createGeometryFromImage(
							Graphic_Utils.createBillboardTexturePath(e.title, dist, bm, getTheContext(), getResources(),
									R.drawable.poi_background), true);

					mGeo2D[i].setLLALimitsEnabled(true);
					billGroup.addBillboard(mGeo2D[i]);

					mGeo2D[i].setName(e.id);
					bm.recycle();
					bm = null;

					mGeo2D[i].setVisible(dist<100*1000?true:false);
				}
				debugFlagNumber = 4;
				// ------------- 3D ---------------
				deb("LBSENT", " " + N_3d);
				mGeo3D = new IGeometry[N_3d];
				mGeo3DMem = new int[N_3d];
				mGeo3DTotalModels = new int[N_3d];
				debugFlagNumber = 5;
				for (int i = 0; i < N_3d; i++) {
					Entity e = FetchResources.entitiesLBS.get(indices3D.get(i));

					mGeo3D[i] = metaioSDK.createGeometry(dirModels + e.modelfile);
					mGeo3DMem[i] = 1;
					mGeo3DTotalModels[i] = Integer.parseInt(e.nModels3d);

					if (mGeo3D[i] != null) {
						mGeo3D[i].setScale(LBS_3DSCALE_FACTOR);
						mGeo3D[i].setRotation(new Rotation(new Vector3d(0.5f*(float)Math.PI,0,0.25f*(float)Math.PI)));
						mGeo3D[i].setName(e.id);
						mGeo3D[i].setVisible(false);
						//mGeo3D[i].setDynamicLightingEnabled(true);
					} else {
						Log.e("3D", "is null");
					}
				}
				debugFlagNumber = 6;
				// ------- Location update -------------
				updateGeometriesLocation(mSensors.getLocation());

				debugFlagNumber = 7;
				// ------ Create radar ------
				if (mRadar != null)
					mRadar.delete();

				mRadar = metaioSDK.createRadar();

				// ---- radar back image --
				Bitmap bmradar = BitmapFactory.decodeResource(getResources(),R.drawable.radar);
				int bytes = bmradar.getWidth() * bmradar.getHeight() * 4;
				ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new
				// buffer
				bmradar.copyPixelsToBuffer(buffer); // Move the byte data to the
				// buffer
				byte[] bmarray = buffer.array(); // Get the underlying array
				// containing the data.
				ImageStruct imageradar = new ImageStruct(bmarray,
						bmradar.getWidth(), bmradar.getHeight(), ECOLOR_FORMAT.ECF_A8R8G8B8, false, 0);
				mRadar.setBackgroundTexture("radar", imageradar);
				mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);

				debugFlagNumber = 8;

				// add geometries to the radar
				for (int i = 0; i < N_2d; i++)
					mRadar.add(mGeo2D[i]);

				for (int i = 0; i < N_3d; i++)
					mRadar.add(mGeo3D[i]);

				mRadar.setVisible(true);

				debugFlagNumber = 9;

			} else if (modesSTRA[arType_current].equals("IBS")) {

				// ---------------------- IBS -------------------
				if (mRadar != null)
					mRadar.setVisible(false);

				N = FetchResources.entitiesIBS.size();
				deb("IBSENT", " " + N);
				mGeo = new IGeometry[N];

				mGeoMem = new int[N];
				mGeoTotalModels = new int[N];

				debugFlagNumber = 10;

				for (int i = 0; i < N; i++) {
					Entity e = FetchResources.entitiesIBS.get(i);

					if (e.modelfile.length() > 0) {
						if (OS_Utils.fileexists(getTheContext().getFilesDir().getAbsolutePath()+"/Models3D_DB/" + e.modelfile)) {
							mGeo[i] = metaioSDK.createGeometry(getTheContext().getFilesDir().getAbsolutePath()+"/Models3D_DB/" + e.modelfile);

							if (mGeo[i] != null) {
								//Log.e("trackImRot", " " + e.trackImRot);

								mGeo[i].setVisible(false);
								mGeo[i].setCoordinateSystemID(i + 1);
								mGeo[i].setScale( e.trackImScale);
								mGeo[i].setRotation(new Rotation(0, ((float)e.trackImRot) /180f*(float)Math.PI,0));
								mGeo[i].setName(e.id);
								mGeo[i].setVisible(true);
								//mGeo[i].setDynamicLightingEnabled(true);

								Log.e("iARFragment ", " " + i);

								mGeoMem[i] = 1;

								if (e.nModels3d.length() > 0) {
									mGeoTotalModels[i] = Integer.parseInt(e.nModels3d);
								} else
									Log.e("e.nModels3d", " ");
							} else {
								Log.e("IBS","3d model for "
										+ e.title + " can not be loaded due to model problems");
							}
						} else {
							Log.e("IBS", "3d model for [" + e.title
									+ "] not found locally at [" + e.modelfile + "]");
						}
					} else {
						Log.e("IBS", "There is no 3d model for " + e.title);
					}
				}
				// ------------------------------------------------------------------
			}

			debugFlagNumber = 12;
			

		} catch (Exception e) {
			Log.e("LoadContents problem", "DEBUG FLAG " + debugFlagNumber); // + " " + e.getLocalizedMessage().toString());
			try{
				EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent("debug", "arfragmentcrash", debugFlagNumber+"", 0l).build());
			}
			catch(Exception e2){}
			//George: testing
			//Dim: getTheContext().getApplicationContext() is null
			//Toast.makeText(getTheContext().getApplicationContext(), "Caught exception in AR " + e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}

		Log.e("LoadContents ok", "DEBUG FLAG " + debugFlagNumber + " SUCCCESS");
		return true;
	}

	/* (non-Javadoc)
	 * @see eu.liveandgov.ar.core.ARViewFragment#onDrawFrame()
	 */
	@Override
	public void onDrawFrame() {
		if (sleep >0){
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				Log.e("ARFragment", "Unable to sleep on DrawFrame");
			}
		}

		if (updateBillboardTextures && modesSTRA[arType_current].equals("LBS")){
			// ----------------------- 2D ------------
			for (int i = 0; i < N_2d; i++) {
				Entity e = FetchResources.entitiesLBS.get(indices2D.get(i));

				if (mGeo2D[i] != null) {
					Bitmap bmIcon;
					if (OS_Utils.fileexists(dirModels + e.iconfile)) {
						bmIcon = BitmapFactory.decodeFile(dirModels + e.iconfile);
					} else {
						bmIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_no_image);
					}

					bmIcon = Bitmap.createScaledBitmap(bmIcon, 56, 56, true);

					Bitmap bm = Graphic_Utils.createBillboardTextureBitmap(e.title, CalcDist(e.location), bmIcon, getTheContext(), getResources(),
							R.drawable.poi_background);

					// Allocate a buffer to copy bitmap bytes, assuming that bitmap configuration is Bitmap.Config.ARGB_8888
					java.nio.ByteBuffer b = java.nio.ByteBuffer.allocate(bm.getWidth()*bm.getHeight()*4);

					// Copy the bitmap buffer
					bm.copyPixelsToBuffer(b);

					// Create image struct
					ImageStruct image = new ImageStruct( b.array(), bm.getWidth(), bm.getHeight(), ECOLOR_FORMAT.ECF_A8R8G8B8, true, 
							Functions.getCurrTime());

					// Set the texture to a geometry
					mGeo2D[i].setTexture("texture", image);

					// recycle the bitmap
					bm.recycle();
					bm = null;

					mGeo2D[i].setVisible(CalcDist(e.location)<100*1000?!togg3d.isChecked():false);
				} else {
					Log.e("mGeo2D[i]" + " " + i, "is null");
				}
			}
			updateBillboardTextures = false;
		}

		super.onDrawFrame();
	}



	// ==============================================
	/**
	 * On Touch billboard or 3d model.
	 * 
	 * @param geometry The geometry touched
	 */
	protected void onGeometryTouched(final IGeometry geometry) {

		//geometry.setDynamicLightingEnabled(true);

		// ---- on Billboard or 3d model touched -> Go to details activity
		String id_STR = geometry.getName();
		int id = Integer.parseInt(id_STR);

		if (id > 0) {
			// -------- Hit a LBS or IBS 3d model -------
			if (togg3d.isChecked() || modesSTRA[arType_current].equals("IBS")) {

				if (modesSTRA[arType_current].equals("LBS")) {
					for (int i = 0; i < N_3d; i++) {
						if (mGeo3D[i].getName().equals(id_STR)) {

							// if Click already clicked model then go to details
							if (index3D_ClickedLoc == i) {
								Intent mI = new Intent(getTheContext(), classToCall);
								mI.putExtra("idEntity", id);
								startActivity(mI);
								return;
							}

							index3D_ClickedLoc = i;

							// --- if Alternative 3d models exist then shift
							// button enable
							if (mGeo3DTotalModels[index3D_ClickedLoc] > 1) {
								bt_models.setText("Model 1/"+mGeo3DTotalModels[index3D_ClickedLoc]);
								bt_models.setVisibility(View.VISIBLE);
								//bt_models.bringToFront();
							} else {
								// No alternative models then show details
								Intent mI = new Intent(getTheContext(), classToCall);
								mI.putExtra("idEntity", id);
								startActivity(mI);
								return;
							}
							break;
						}
					}
				}

				if (modesSTRA[arType_current].equals("IBS")) {
					for (int i = 0; i < N; i++) {
						if (mGeo[i].getName().equals(id_STR)) {
							// if Click already clicked model then go to details
							if (index3D_ClickedLoc == i) {
								Intent mI = new Intent(getTheContext(),
										classToCall);
								mI.putExtra("idEntity", id);
								startActivity(mI);
								return;
							}

							index3D_ClickedLoc = i;

							// --- if Alternative 3d models exist then shift
							// button enable
							if (mGeoTotalModels[index3D_ClickedLoc] > 1) {
								bt_models.setVisibility(View.VISIBLE);
								bt_models.setText("Model 1/"+mGeoTotalModels[index3D_ClickedLoc]);
								//bt_models.bringToFront();
							} else {
								// No alternative models then show details
								Intent mI = new Intent(getTheContext(),classToCall);
								mI.putExtra("idEntity", id);
								startActivity(mI);
								return;
							}
							break;
						}
					}
				}

			} else { // ------ Hit a billboard ---------
				bt_models.setVisibility(View.INVISIBLE);
				Intent mI = new Intent(getTheContext(), classToCall);
				mI.putExtra("idEntity", id);
				startActivity(mI);
				return;
			}
		}
	}


	/**
	 * Update the location of the geometries
	 * 
	 * @param location
	 */
	private void updateGeometriesLocation(LLACoordinate location) {

		
		if (LocationService.cl==null){
			LocationService.cl = new Location("USER");
		}
		
		//-------- Update also the map --------------------
		LocationService.cl.setAccuracy((float) location.getAccuracy());
		LocationService.cl.setLatitude((float) location.getLatitude());
		LocationService.cl.setLongitude((float) location.getLongitude());
		LocationService.cl.setTime(Functions.getCurrTime());
		
		Bundle extras = new Bundle();
		extras.putDouble("LastBoot", location.getTimestamp());
		
		LocationService.cl.setExtras(extras);

		getTheContext().sendBroadcast(new Intent("locarsens").putExtra("LocChanged","ok"));   // for the Map 
		
		
		// ----------------------- 2D ------------
		for (int i = 0; i < N_2d; i++) {
			Entity e = FetchResources.entitiesLBS.get(indices2D.get(i));

			LLACoordinate loc = new LLACoordinate(e.location.getLatitude(),
					e.location.getLongitude(), 0, e.location.getAccuracy());

			if (mGeo2D[i] != null) {
				mGeo2D[i].setTranslationLLA(loc);
			} else {
				Log.e("mGeo2D[i]" + " " + i, "is null");
			}
		}

		// -------------------- 3D ----------------
		for (int i = 0; i < N_3d; i++) {
			Entity e = FetchResources.entitiesLBS.get(indices3D.get(i));

			LLACoordinate loc = new LLACoordinate(e.location.getLatitude(),
					e.location.getLongitude(), 0, e.location.getAccuracy());

			if (mGeo3D[i] != null) {
				mGeo3D[i].setTranslationLLA(loc);
			} else {
				Log.e("mGeo3D[i]" + " " + i, "is null");
			}
		}
	}

	//---------- Warning about location accuracy ---------------
	public void warnAcc(double accuracy){
		if (accuracy > 150){
			String mes = getResources().getString(R.string.loc_low_accuracy);
			mes = mes.replace("XXX", Integer.toString((int) accuracy));
			Toast.makeText(getActivity(), mes, Toast.LENGTH_LONG).show();
			accuracy_loc_previous = (int) accuracy;
		} else if (accuracy < 150 && accuracy_loc_previous > 150){

			String mes = getResources().getString(R.string.loc_improved_accuracy);
			mes = mes.replace("XXX", Integer.toString((int) accuracy));

			Toast.makeText(getActivity(), mes, Toast.LENGTH_LONG).show();
			accuracy_loc_previous = (int) accuracy;
		}
	}

	/* ==================================
	 *     OnPause
	  ===================================*/
	@Override
	public void onPause() {
		if (isShakeServBound){
			getTheContext().unbindService(mShakeConnection);
			isShakeServBound = false;	
		}
		super.onPause();
	}

	// -- Calculate distance from user -----------------
	private int CalcDist(Location l){
		Location locuser = new Location("locuser");
		locuser.setLatitude(mSensors.getLocation().getLatitude());
		locuser.setLongitude(mSensors.getLocation().getLongitude());
		return (int) l.distanceTo(locuser);
	}

	/*--------- getContext -------------*/
	@Override
	protected Context getTheContext() {
		return getActivity();
	}

	// ===========================================
	public void deb(String mes, String mes2) {
		if (DEBUG_FLAG)
			Log.d(TAG, mes + ":" + mes2);
	}

	//==============================================
	//----------- Location and Shake ----------
	//==============================================
	
	/*=========================================================
	 *    The ServiceConnection to start or not Shake listener  
	 ==========================================================*/
	private ServiceConnection mShakeConnection = new ServiceConnection() {

		@Override 
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mShakeService = binder.getService();

			if(mShakeService != null){
				Log.d("service-bind", "Service is bonded successfully!");
			} else {
				Log.e("service-bind", "null");
			}
			isShakeServBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.d("service-bind", "disconnected");
			isShakeServBound = false;
		}
	};
	
	// George: You need to set currLoc = pastLoc otherwise currLoc is accepted and objects move !!!!!
	private void setToPast(LLACoordinate currLoc){
		if(pastLoc != null){
			currLoc.setAccuracy(pastLoc.getAccuracy());
			currLoc.setLatitude(pastLoc.getLatitude());
			currLoc.setLongitude(pastLoc.getLongitude());
		}
	}
	//-------- UPDATE the Global Location -------
	public void setLocGlobal(LLACoordinate currloc){

		Bundle extras = new Bundle();
		extras.putDouble("LastBoot", currloc.getTimestamp());

		pastLoc = new Location("mepast");
		pastLoc.setAccuracy((float) currloc.getAccuracy());
		pastLoc.setLatitude((float) currloc.getLatitude());
		pastLoc.setLongitude((float) currloc.getLongitude());
		pastLoc.setTime(Functions.getCurrTime());
		pastLoc.setExtras(extras);

		LocationService.cl.setAccuracy((float) currloc.getAccuracy());
		LocationService.cl.setLatitude((float) currloc.getLatitude());
		LocationService.cl.setLongitude((float) currloc.getLongitude());
		LocationService.cl.setTime(Functions.getCurrTime());
		LocationService.cl.setExtras(extras);

		getTheContext().sendBroadcast(new Intent("locarsens").putExtra("LocChanged","ok"));   // for the Map 
		getTheContext().sendBroadcast(new Intent("locarsens").putExtra("LocChanged","debug"));// for the Debug Activity
		
		warnAcc(currloc.getAccuracy());
		updateBillboardTextures = true;
	}

	public void invalidateAndDebug(){
		imvShake.bringToFront();
		tvShake.bringToFront();
		imvShake.invalidate();
		tvShake.invalidate();

		getTheContext().sendBroadcast(new Intent("locarsens").putExtra("LocChanged","debug"));// for the Debug Activity
	}

	public void showShake(){
		getTheContext().bindService(IntShakeServ, mShakeConnection, Context. BIND_AUTO_CREATE);
		isShakeServBound = true;
		setShakeView(View.VISIBLE);
	}

	public void hideShake(){
		if (isShakeServBound){
			getTheContext().unbindService(mShakeConnection);
			isShakeServBound = false;	
		}

		setShakeView(View.INVISIBLE);
		Tost("Hide");
	}

	public void setShakeView(int vis){
		tvShake.setVisibility(vis);
		imvShake.setVisibility(vis);

		if(modesSTRA[arType_current].equals("LBS")){
			rlShake.setVisibility(View.VISIBLE);
		} else {
			rlShake.setVisibility(View.INVISIBLE);
		}

		imvShake.bringToFront();
		tvShake.bringToFront();
		imvShake.invalidate();
		tvShake.invalidate();
	}

	public void Tost(String mes){
		//Toast.makeText(getTheContext(), mes, Toast.LENGTH_LONG).show();
	}

	//---------- Archive of locations ------------
	public void put2arch(LLACoordinate lla){

		Location l = new Location("me");
		l.setLatitude(lla.getLatitude());
		l.setLongitude(lla.getLongitude());
		l.setAccuracy((float) lla.getAccuracy());
		l.setTime(Functions.getCurrTime());

		arch_loc.add(l);
		//Log.e("Put2arch", "Put2arch: " + arch_loc.size());
	}

	
//	//---------- mode setup -------------------------
//	public void setup(boolean[] modesOn_in, int arType_initial_in) {
//
//		ArrayList<String> modesSTR_arrL = new ArrayList<String>();
//
//		if (modesOn_in[0])
//			modesSTR_arrL.add("LBS");
//
//		if (modesOn_in[1])
//			modesSTR_arrL.add("IBS");
//
//		if (modesOn_in[2])
//			modesSTR_arrL.add("IBS*");
//
//		modesSTRA = modesSTR_arrL.toArray(new String[0]);
//		arType_current = arType_initial_in;
//	}
}