/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveGov.libraries.livegovtoolkit.location;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

/**
 * Detect shaking of device so as to start updating the location (avoid jumping of objects in AR view)
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class ShakeService extends Service {

	Context ctx;
	SensorManager mySensorManager;
	private static final int SHAKE_CHECK_THRESHOLD = 200;
	private static final int IGNORE_EVENTS_AFTER_SHAKE = 1000;
	private long lastUpdate;
	private long lastShake = 0;

	private float last_x = 0, last_y=0, last_z=0; 

	private static final long KEEP_DATA_POINTS_FOR = 1500;
	private static final long MINIMUM_EACH_DIRECTION = 4;
	private static final float POSITIVE_COUNTER_THRESHHOLD =  3.0f;
	private static final float NEGATIVE_COUNTER_THRESHHOLD = -3.0f;
	
	private final IBinder binder=new LocalBinder();
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return 1; 
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}


	public class LocalBinder extends Binder {
		public ShakeService getService() {
			return ShakeService.this;
		}
	}
	
	
	private class DataPoint {
		public float x, y, z;
		public long atTimeMilliseconds;
		public DataPoint(float x, float y, float z, long atTimeMilliseconds) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.atTimeMilliseconds = atTimeMilliseconds;
		}
	}

	private List<DataPoint> dataPoints = new ArrayList<DataPoint>();





	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		ctx = getApplicationContext();
		
		// Register sensor
		mySensorManager = (SensorManager)getSystemService(ctx.SENSOR_SERVICE);
		List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		mySensorManager.registerListener(mySensorEventListenerACCELEROMETER,
				mySensors.get(0), SensorManager.SENSOR_DELAY_UI); //SENSOR_DELAY_GAME

		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {

		/** 1. Unregister sensors */
		mySensorManager.unregisterListener(mySensorEventListenerACCELEROMETER);

		stopSelf();
		
		super.onDestroy();
	}

	//======================  mySensorEventListenerACCELEROMETER =============
	/**
	 * 
	 *   Obtain 3 values of accelerometers and combine them with geomagnetic values to find orientation.
	 * 
	 */
	public SensorEventListener mySensorEventListenerACCELEROMETER = new SensorEventListener(){
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}

		@Override
		public void onSensorChanged(SensorEvent event){

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				long curTime = System.currentTimeMillis();
				// if a shake in last X seconds ignore.
				if (lastShake != 0 && (curTime - lastShake) < IGNORE_EVENTS_AFTER_SHAKE) return;

				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];

				if (last_x != 0 && last_y != 0 && last_z != 0 && (last_x != x || last_y != y || last_z != z)) {
					DataPoint dp = new DataPoint(last_x-x, last_y-y, last_z-z, curTime);
					//Log.i("XYZ",Float.toString(dp.x)+"   "+Float.toString(dp.y)+"   "+Float.toString(dp.z)+"   ");
					dataPoints.add(dp);

					if ((curTime - lastUpdate) > SHAKE_CHECK_THRESHOLD) {
						lastUpdate = curTime;
						checkForShake();
					}
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}	
		}
	};

	public void checkForShake() {
		long curTime = System.currentTimeMillis();
		long cutOffTime = curTime - KEEP_DATA_POINTS_FOR;
		while(dataPoints.size() > 0 && dataPoints.get(0).atTimeMilliseconds < cutOffTime) dataPoints.remove(0);

		int x_pos =0, x_neg=0, x_dir = 0, y_pos=0, y_neg=0, y_dir=0, z_pos=0, z_neg = 0, z_dir = 0;
		
		
		for(DataPoint dp: dataPoints){
			if (dp.x > POSITIVE_COUNTER_THRESHHOLD && x_dir < 1) {
				++x_pos;
				x_dir = 1;
			}
			if (dp.x < NEGATIVE_COUNTER_THRESHHOLD && x_dir > -1) {
				++x_neg;
				x_dir = -1;
			}
			if (dp.y > POSITIVE_COUNTER_THRESHHOLD && y_dir < 1) {
				++y_pos;
				y_dir = 1;
			}
			if (dp.y < NEGATIVE_COUNTER_THRESHHOLD && y_dir > -1) {
				++y_neg;
				y_dir = -1;
			}
			if (dp.z > POSITIVE_COUNTER_THRESHHOLD && z_dir < 1) {
				++z_pos;
				z_dir = 1;
			}
			if (dp.z < NEGATIVE_COUNTER_THRESHHOLD && z_dir > -1) {
				++z_neg;			
				z_dir = -1;
			}
		}
		//Log.i("CHANGE",Integer.toString(x_pos)+" - "+Integer.toString(x_neg)+"  "+Integer.toString(y_pos)+" - "+Integer.toString(y_neg)+"  "+Integer.toString(z_pos)+" - "+Integer.toString(z_neg));

		if ((x_pos >= MINIMUM_EACH_DIRECTION && x_neg >= MINIMUM_EACH_DIRECTION) || 
				(y_pos >= MINIMUM_EACH_DIRECTION && y_neg >= MINIMUM_EACH_DIRECTION) || 
				(z_pos >= MINIMUM_EACH_DIRECTION && z_neg >= MINIMUM_EACH_DIRECTION) ) {
			
			lastShake = System.currentTimeMillis();
			last_x = 0; last_y=0; last_z=0;
			dataPoints.clear();
			triggerShakeDetected();
			return;
		}

	}

	protected void triggerShakeDetected() {
		ctx.sendBroadcast(new Intent("shakeDet").putExtra("Shaked", "o yes"));
	}
}
