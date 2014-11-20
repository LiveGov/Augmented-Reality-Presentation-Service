package eu.liveGov.libraries.livegovtoolkit.location;

import java.util.ArrayList;

import eu.liveGov.libraries.livegovtoolkit.Utils.Constants;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


/**
 * Check if location providers are enabled. Store the location from AR fragment for general use.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class LocationService implements LocationListener {

	private boolean _isRegistered; // Boolean to check if updates are registered
	private boolean _isHandlerRunning; // Boolean to check if handler is running
	private final LocationManager _locationManager; // Location manager for registering updates
	private final String TAG = "LocationService"; //Debug tag
	private final Handler stopHandler = new Handler(); 	// The handler for stopping updates
	private ArrayList<LocationListener> listeners = new ArrayList<LocationListener>();
	
	
	public static Location cl = new Location("USER");
	long tstartloc = System.currentTimeMillis();

	// Runnable for stopping updates
	private final Runnable stopRunnable = new Runnable() {
		public void run() {
			Log.w(TAG, "Runnable - stopping runnable");
			_isHandlerRunning = false;
			stopUpdates();
		}
	};

	// Singleton
	private static LocationService _instance;

	private LocationService(Context c) {
		
		cl.setLatitude(Constants.locUserPred_Lat);
		cl.setLatitude(Constants.locUserPred_Long);
		
		_locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		cl = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	}

	public static LocationService getInstance(Context c) {
		if (_instance == null) {
			_instance = new LocationService(c);
		}
		return _instance;
	}

	/**
	 * Register updates when its not registered and cancels the handler if its
	 * running.
	 */
	public boolean startUpdates(int minTime, float minDistance) {
		if (!_isRegistered) {
			Log.v(TAG, "startUpdates - starting updates");

			String provider = LocationManager.GPS_PROVIDER;
			_locationManager.requestLocationUpdates(provider, minTime,minDistance, this);
			provider = LocationManager.NETWORK_PROVIDER;
			_locationManager.requestLocationUpdates(provider, minTime,minDistance, this);
			_isRegistered = true;
			return true;
		} else {
			if (_isHandlerRunning) {
				cancelTimer();
				_isHandlerRunning = false;
			}
			return false;
		}
	}

	/**
	 * Removes updates when its registered.
	 */
	public boolean stopUpdates() {
		if (_isRegistered) {

			if (_isHandlerRunning) {
				Log.v(TAG, "stopUpdates - canceling timer");
				cancelTimer();
				_isHandlerRunning = false;
			}
			_locationManager.removeUpdates(this);
			_isRegistered = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Cancels the handler if its running
	 */
	private void cancelTimer() {
		stopHandler.removeCallbacks(stopRunnable);
	}

	/**
	 * Starts a handler which removes (calls stopUpdates()) the updates after a
	 * predefined time.
	 */
	public void stopUpdatesWithTimer(int delayMillis) {
		if (_isRegistered) {
			if (!_isHandlerRunning) {
				stopHandler.postDelayed(stopRunnable, delayMillis);
				_isHandlerRunning = true;
			}
		}
	}

	
	public boolean hasNetLocationProviderEnabled() {
		boolean network_enabled = false;
		
		try {
			network_enabled = _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		
		return network_enabled;
	}
	
	public boolean hasGPSLocationProviderEnabled() {
		boolean gps_enabled = false;
		try {
			gps_enabled = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		
		return gps_enabled;
	}

	public void addListener(LocationListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LocationListener listener) {
		listeners.remove(listener);
	}

	public void onLocationChanged(Location location) {
		for (LocationListener l : listeners) {
			l.onLocationChanged(location);
		}
	}

	public void onProviderDisabled(String provider) {
		for (LocationListener l : listeners) {
			l.onProviderDisabled(provider);
		}
	}

	public void onProviderEnabled(String provider) {
		for (LocationListener l : listeners) {
			l.onProviderEnabled(provider);
		}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		for (LocationListener l : listeners) {
			l.onStatusChanged(provider, status, extras);
		}
	}
}