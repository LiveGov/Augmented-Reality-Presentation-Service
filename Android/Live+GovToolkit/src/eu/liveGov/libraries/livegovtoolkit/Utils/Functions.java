package eu.liveGov.libraries.livegovtoolkit.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.metaio.sdk.jni.LLACoordinate;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.Toast;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.location.LocationService;

public class Functions {

	public static boolean StringIsNULLOrEmpty(String s) {
		if ((s == null) || (s == Constants.EmptyString)) {
			return true;
		} else {
			return false;
		}
	}

	public static String ConvertInputStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}


	public static boolean StringArrayContains(String needle, String[] haystack) {
		if (haystack != null && haystack.length > 0) {
			for (int i = 0; i < haystack.length; i++) {
				if (!StringIsNULLOrEmpty(haystack[i])
						&& haystack[i].equals(needle)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void startLocationService(Context c) {
		LocationService.getInstance(c).startUpdates(
				Constants.LOCATION_POLLING_INTERVAL,
				Constants.LOCATION_MIN_DISTANCE);
	}

	public static void stopLocationService(Context c, boolean longTime) {
		if (longTime) {
			LocationService.getInstance(c).stopUpdatesWithTimer(
					Constants.LOCATION_REMOVE_UPDATES_LONG);
		} else {
			LocationService.getInstance(c).stopUpdatesWithTimer(
					Constants.LOCATION_REMOVE_UPDATES);
		}
	}

	public static boolean hasNetLocationProviderEnabled(Context c) {
		return LocationService.getInstance(c).hasNetLocationProviderEnabled();
	}

	public static boolean hasGPSLocationProviderEnabled(Context c) {
		return LocationService.getInstance(c).hasGPSLocationProviderEnabled();
	}


	public void removeLocationListener(Context c, LocationListener listener) {
		LocationService.getInstance(c).removeListener(listener);
	}

	public static boolean checkInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	public static String checkInternetType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return netInfo.getTypeName();
		}
		return "";
	}


	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		long distanceInMeters = Math.round(6371000 * c);
		return distanceInMeters;
	}

	public static long getCurrTime()
	{
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		//int utc = (int)(now / 1000);
		return now; //(utc);

	}


	public static String formatTimestamp(long timeUTC){

		CharSequence relTime = DateUtils.getRelativeTimeSpanString(
				timeUTC, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

		return relTime.toString();
	}

	public static String formatUTC(long timeUTC){ 
		Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();

		/* date formatter in local timezone */
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(tz);


	     cal.setTimeInMillis(timeUTC);
	     return sdf.format(cal.getTime());
	}

	
	
	public String calcVel(LLACoordinate c, Location p){

		double dx = Functions.getDistance(p.getLatitude(), p.getLongitude(), c.getLatitude(), c.getLongitude());

		double k = ((double)SystemClock.uptimeMillis())/1000 - c.getTimestamp();
		double dt = (((double)Functions.getCurrTime())/1000 - k) -  ((double)p.getTime())/1000;

		String out = " " + ((int)dx) + " " + ((int)dt) + " ";
		if (dt!=0)
			out += (int)(dx/dt);
		else 
			out += 101;

		return out;
	}

	public static double u2t(double utc){
		return utc/1000 - ((double)SystemClock.uptimeMillis())/1000;
	}
	
	// Warning about location timestamp
	public void warnTime(long time, Resources r, Context c){
		if (time > 360){
			String mes = r.getString(R.string.loc_old_timestamp);
			mes = mes.replace("XXX", Long.toString(time));
			Toast.makeText(c, mes, Toast.LENGTH_LONG).show();
		}
	}
}
