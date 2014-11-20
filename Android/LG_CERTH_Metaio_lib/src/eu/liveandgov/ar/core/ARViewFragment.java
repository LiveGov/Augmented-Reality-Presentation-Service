package eu.liveandgov.ar.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.MetaioSurfaceView;
import com.metaio.sdk.SensorsComponentAndroid;
import com.metaio.sdk.jni.ERENDER_SYSTEM;
import com.metaio.sdk.jni.ESCREEN_ROTATION;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MetaioSDK;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector2di;
import com.metaio.tools.Memory;
import com.metaio.tools.Screen;
import com.metaio.tools.SystemInfo;

import eu.liveandgov.ar.utilities.AsyncTask_ImRec;

/**
 * This implements AR as a fragment. It is not included in MetaioSDK and it was written by scratch.
 *  
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
public abstract class ARViewFragment extends Fragment implements MetaioSurfaceView.Callback, OnTouchListener {

	static { IMetaioSDKAndroid.loadNativeLibs(); }

	public static SensorsComponentAndroid mSensors; // rotation sensors
	protected MetaioSurfaceView mGLSurfaceView; // Open GL surface

	public View v; // All views in here

	// Interconnections
	public static IMetaioSDKAndroid metaioSDK;
	protected boolean mRendererInitialized;
	protected Vector2di mCameraResolution;
	protected abstract void onGeometryTouched(IGeometry geometry); // when a 3d model is touched

	// Vars
	public Context ctxL;
	private Handler handlerCamera;
	private boolean isDestroyed = false;
	public static boolean mFragmentIsPaused = false;
	protected abstract Context getTheContext();

	// ---------------------
	// Get the whole view
	protected View getGUIView() {
		return v;
	}
	
	protected void startCamera(){
		// Select the back facing camera by default
		final int cameraIndex = SystemInfo.getCameraIndex(CameraInfo.CAMERA_FACING_BACK);
		mCameraResolution = metaioSDK.startCamera(cameraIndex, 320, 240);
	}

	/** Create MetaioSDK, handler for startCamera, ScreenshotCapture for IBS*  */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		deb("ARViewFragment" , "onCreate ARViewFragment");
		
		super.onCreate(savedInstanceState);

		metaioSDK = null;
		mGLSurfaceView = null;
		mRendererInitialized = false;

		ctxL = getTheContext();

		// ----------- Asynchronous start stop camera -----------------------
		handlerCamera = new Handler(){
			public void handleMessage(Message msg)
			{
				if (msg.arg1==1)
					startCamera();	
				super.handleMessage(msg);
			}
		};
		


		try {
			mSensors = new SensorsComponentAndroid( ctxL );
			
			metaioSDK = MetaioSDK.CreateMetaioSDKAndroid(ctxL, getResources().getString(R.string.metaioSDKSignature));
			metaioSDK.registerSensorsComponent(mSensors);
			mSensors.start(SensorsComponentAndroid.SENSOR_ALL);
			
			metaioSDK.registerCallback(new IMetaioSDKCallback()
			{
				
				/* (non-Javadoc)
				 * @see com.metaio.sdk.jni.IMetaioSDKCallback#onTrackingEvent(com.metaio.sdk.jni.TrackingValuesVector)
				 */
				@Override
				public void onTrackingEvent(TrackingValuesVector trackingValues) {
					//for (int i=0; i<trackingValues.size(); i++){
						if (trackingValues.get(0).getState() == com.metaio.sdk.jni.ETRACKING_STATE.ETS_LOST){
							ctxL.sendBroadcast(new Intent("android.intent.action.MAIN").putExtra("ImageLost","confirm"));
						} 
						else if (trackingValues.get(0).getState() == com.metaio.sdk.jni.ETRACKING_STATE.ETS_FOUND){
							ctxL.sendBroadcast(new Intent("android.intent.action.MAIN").
									putExtra("ImageFound",trackingValues.get(0).getCosName()));
						}
					//}
					
					super.onTrackingEvent(trackingValues);
				}
				
				@Override
				public void onSDKReady()
				{
					super.onSDKReady();
					// ----------- Asynchronous start camera -----------------------
					Message msg = new Message();
					msg.arg1 = 1;
					handlerCamera.sendMessage(msg);
				}
				
				/* (non-Javadoc)
				 * @see com.metaio.sdk.jni.IMetaioSDKCallback#onCameraImageSaved(java.lang.String)
				 */
				@Override
				public void onScreenshotSaved(String filepath) {
										
					try {
						// convert to 4:3 ratio or 3:4. Otherwise service not working at all!
						FileInputStream streamIn;
						streamIn = new FileInputStream(filepath);
						Bitmap bitmap = BitmapFactory.decodeStream(streamIn);
						
						int w,h;
						if (bitmap.getHeight() > bitmap.getWidth()){
							h = 640;
							w = 480;
						} else {
							w = 640;
							h = 480;
						}
						bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);

						FileOutputStream out = new FileOutputStream(filepath);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
					    out.close();
					    // Recognize now
					    new AsyncTask_ImRec(filepath, "5", ctxL).execute();
					} catch (Exception e) {

					}
					
				};
			});
		}catch (Exception e)	{
			Log.e("CARF", "ARViewActivity.onCreate: failed to create or intialize metaio SDK: "+e.getMessage());
		}
	}


	/** Create the View */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		deb("ARViewFragment" , "onCreateView ARViewFragment");

		v = inflater.inflate(R.layout.activity_ar, container, false);
		return v;
	}

	/** Create OpenGL surface */
	@Override
	public void onStart() {
		super.onStart();
		deb("ARViewFragment" , "onStart ARViewFragment");
		
		try{
			if (mGLSurfaceView ==null){
			// Set up GL surface view
			mGLSurfaceView = new MetaioSurfaceView(ctxL);
			mGLSurfaceView.registerCallback(this);
			mGLSurfaceView.setKeepScreenOn(true);
			mGLSurfaceView.setOnTouchListener(this);
			}
		} catch (Exception e) {
			Log.e("CARF","SurfaceView Prob");
		}
	}



	
	/** Resume metaioSDK, open Camera, and resume OpenGL surface  */
	@Override
	public void onResume() {
		super.onResume();
		deb("ARViewFragment" , "onResume ARViewFragment");
		metaioSDK.resume();
		
		if (mGLSurfaceView != null)
		{
			if (mGLSurfaceView.getParent() == null)	{
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

				mGLSurfaceView.setBackgroundColor(Color.argb(0,0,0,0));
				mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
				mGLSurfaceView.setZOrderMediaOverlay(true);
				((ViewGroup) v).addView(mGLSurfaceView, params);
			}

			// make sure to resume the OpenGL surface
			mGLSurfaceView.onResume();
		}
		
		
		v.bringToFront();
	}



	/** Pause OpenGL Surface and metaioSDK */
	@Override
	public void onPause() {
		deb("ARViewFragment","onPause ARViewFragment");
		super.onPause();

		if (mGLSurfaceView != null)
			mGLSurfaceView.onPause();

		mFragmentIsPaused = true;
		metaioSDK.pause();		
	}

	/** Stop camera */
	@Override
	public void onStop() {
		super.onStop();
		deb("ARViewFragment","onStop ARViewFragment");
		metaioSDK.stopCamera();
		
		//mSensors.stop(SensorsComponentAndroid.SENSOR_ALL);
	}


	/** Remove all views */
	@Override
	public void onDestroyView() {
		deb("ARViewFragment","onDestroyView ARViewFragment");
		if (mGLSurfaceView != null)
			((ViewGroup) v).removeAllViews();
		
		super.onDestroyView();
	}
	
	/**
	 * Delete metaioSDK object, unregister sensors, call garbage collector 
	 */
	@Override
	public void onDestroy() {

			deb("ARViewFragment","onDestroy");
			super.onDestroy();

		if (!isDestroyed){
			MetaioDebug.log("ARViewActivity.onDestroy");

			if (metaioSDK != null) 
			{
				metaioSDK.delete();
				metaioSDK = null;
			}

			MetaioDebug.log("ARViewActivity.onDestroy releasing sensors");
			if (mSensors != null)
			{
				mSensors.registerCallback(null);
				mSensors.release();
				mSensors.delete();
				mSensors = null;
			}

			Memory.unbindViews(v);

			System.runFinalization();
			System.gc();
			isDestroyed = true;
		}
	}

	/** On screen rotation changed */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		final ESCREEN_ROTATION rotation = Screen.getRotation(getActivity());
		metaioSDK.setScreenRotation(rotation);
		MetaioDebug.log("onConfigurationChanged: "+rotation);
	}


	//==============================================================
	//           SURFACE CALLBACK STARTS HERE
	//==============================================================

	/** Start renderer if not already done */
	@Override
	public void onDrawFrame() {
		try {	
			if (mRendererInitialized)
				metaioSDK.render();
		} catch (Exception e) {
			MetaioDebug.log(Log.ERROR, "ARViewActivity.onDrawFrame: Rendering failed with error "+e.getMessage());
		}
	}

	/** On OpenGL surface changed: resize renderer viewport */
	@Override
	public void onSurfaceChanged(int width, int height) {
		MetaioDebug.log("ARViewActivity.onSurfaceChanged: "+width+", "+height);
		metaioSDK.resizeRenderer(width, height);	
	}


	/** On OpenGL surface created : Initialize renderer */
	@Override
	public void onSurfaceCreated() {
		
		deb("ARViewFragment" , "onSurfaceCreated");

		MetaioDebug.log("ARViewActivity.onSurfaceCreated: GL thread: "+Thread.currentThread().getId());
		try	{
			// initialized the renderer
			if(!mRendererInitialized)
			{
				metaioSDK.initializeRenderer(mGLSurfaceView.getWidth(), mGLSurfaceView.getHeight(),
						Screen.getRotation(getActivity()), ERENDER_SYSTEM.ERENDER_SYSTEM_OPENGL_ES_2_0 );

				mRendererInitialized = true;
			}
			else
			{
				MetaioDebug.log("ARViewActivity.onSurfaceCreated: Reloading textures...");
				metaioSDK.reloadOpenGLResources();
			}

			// connect the audio callbacks
			//MetaioDebug.log("ARViewActivity.onSurfaceCreated: Registering audio renderer...");
			//metaioSDK.registerAudioCallback( mGLSurfaceView.getAudioRenderer() );

			MetaioDebug.log("ARViewActivity.onSurfaceCreated");

		}
		catch (Exception e)
		{
			MetaioDebug.log(Log.ERROR, "ARViewActivity.onSurfaceCreated: "+e.getMessage());
		}
	}



	/* On OpenGL surface destroyed */
	@Override
	public void onSurfaceDestroyed() {
		deb("ARViewFragment","onSurfaceDestroyed");
		
		MetaioDebug.log("ARViewActivity.onSurfaceDestroyed(){");
		mGLSurfaceView = null;
		//metaioSDK.registerAudioCallback(null);
		
	}

	//=========================================================
	//               SURFACE CALLBACKs END HERE
	//=========================================================

	/* OnTouch object */
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		IGeometry geometry = null;
		
		if (mRendererInitialized && event.getAction() == MotionEvent.ACTION_UP)	{
			try	{
				final int xCenter = (int) event.getX();
				final int yCenter = (int) event.getY();

				for (int i=0; i<25; i++){
				
					int xPen = i/5 - 2;
					int yPen = i%5 - 2;
					
					int x = xCenter + xPen;
					int y = yCenter + yPen;
					
					// 	ask the SDK if a geometry has been hit
					geometry = metaioSDK.getGeometryFromScreenCoordinates(x, y, true);
					
					if (geometry!=null)
						break;
				}
								
				if (geometry != null)	{
					MetaioDebug.log("ARViewActivity geometry found: "+geometry);
					onGeometryTouched(geometry);
				}
			}catch (Exception e){
				MetaioDebug.log(Log.ERROR, "onTouch: "+e.getMessage());
			}
		}

		return true;
	}



    /* Handle low memory */
	@Override
	public void onLowMemory() {
		MetaioDebug.log(Log.ERROR, "Low memory");
		MetaioDebug.logMemory(ctxL);
	}

	public void deb(String a, String b){
		Log.d("ARViewFragment", a + ":" + b);
	} 
}