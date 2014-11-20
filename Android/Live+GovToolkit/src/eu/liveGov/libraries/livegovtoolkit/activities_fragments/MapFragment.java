package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Constants;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.helper.ProposalHelper;
import eu.liveGov.libraries.livegovtoolkit.interfaces.ResourcesListener;
import eu.liveGov.libraries.livegovtoolkit.location.LocationService;
import eu.liveGov.libraries.livegovtoolkit.objects.FetchResources;
import eu.liveGov.libraries.livegovtoolkit.objects.ProposalObject;

/**
 * Fragment to show OpenStreet Map with all the items.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class MapFragment extends Fragment implements OnItemGestureListener<OverlayItem>, OnClickListener, ResourcesListener, LocationListener {

	public static MapView mOsmv;
	public static MapController mMapController;
	private ItemizedOverlayWithFocus<OverlayItem> mLocationOverlay;
	private SimpleLocationOverlay mCurrentLocationOverlay;
	private ResourceProxy mResourceProxy;
	private View _popupView;
	private HashMap<OverlayItem, Integer> _markerMap;

	private BroadcastReceiver mReceiverLocChanged;
	boolean isReg_mReceiverLocChanged = false;
	private IntentFilter intentFilter;

	private boolean firstTime = true; // firstTime opened 

	private static final Logger logger = LoggerFactory.getLogger(MapFragment.class);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		FetchResources.addResourcesListener(this);

		mResourceProxy = new DefaultResourceProxyImpl(getActivity());
		mOsmv = new MapView(getActivity(), 256, mResourceProxy);

		mMapController = (MapController) mOsmv.getController();
		mMapController.setZoom(15);

		if (LocationService.cl == null) {
			LocationService.cl = new Location("USER");
			LocationService.cl.setLatitude(Constants.locUserPred_Lat);
			LocationService.cl.setLongitude(Constants.locUserPred_Long);
		}
		
		mMapController.setCenter(new GeoPoint(LocationService.cl.getLatitude(), LocationService.cl.getLongitude()));
		mCurrentLocationOverlay = new SimpleLocationOverlay(getActivity());
		mCurrentLocationOverlay.setEnabled(true);
		mCurrentLocationOverlay.setLocation(new GeoPoint((int) (LocationService.cl.getLatitude()*1E6),
					(int) (LocationService.cl.getLongitude()*1E6)
					));

			mOsmv.getOverlays().add(mCurrentLocationOverlay);
		
		mOsmv.setBuiltInZoomControls(true);
		mOsmv.setMultiTouchControls(true);
		AddProposals();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mapview, container, false);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.mapview);
		layout.addView(mOsmv, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));

		return view;
	}

	/**
	 *    RESUME
	 */
	 @Override
	 public void onResume() {
		 Functions.startLocationService(getActivity());

		 //--------------- Receiver for Location Changed -------
		 intentFilter = new IntentFilter("locarsens");
		 mReceiverLocChanged = new BroadcastReceiver() {
			 @Override
			 public void onReceive(Context context, Intent intent) {

				 if (intent.getStringExtra("LocChanged").equals("ok")){
					 onLocationChanged(null);
				 }
			 }
		 };

		 if (!isReg_mReceiverLocChanged){
			 getActivity().registerReceiver(mReceiverLocChanged, intentFilter);
			 isReg_mReceiverLocChanged = true;
		 }


		 super.onResume();		
	 }

	 /**
	  *  PAUSE
	  */
	 @Override
	 public void onPause() {
		 Functions.stopLocationService(getActivity(), false);

		 if (isReg_mReceiverLocChanged){
			 getActivity().unregisterReceiver(mReceiverLocChanged);
			 isReg_mReceiverLocChanged = false;
		 }

		 super.onPause();
	 }


	 public void updateCurrentLocation(Location l) {

	 }

	 public void AddProposals() {
		 if (getActivity() == null) { // it's possible that there isn't a
			 // activity available. So this means we
			 // don't have to update it.
			 return;
		 }

		 ArrayList<OverlayItem> mItems = new ArrayList<OverlayItem>();
		 if (_markerMap == null) {
			 _markerMap = new HashMap<OverlayItem, Integer>();
		 } else {
			 _markerMap.clear();
		 }
		 ArrayList<ProposalObject> objects = ProposalHelper.getProposals();
		 for (int i = 0; i < objects.size(); i++) {
			 ProposalObject po = objects.get(i);
			 GeoPoint point = new GeoPoint(po.get_lat(), po.get_lng());
			 OverlayItem olItem = new OverlayItem((po.get_id() + ""),
					 po.get_title(), po.get_description(), point);

			 mItems.add(olItem);
			 _markerMap.put(olItem, po.get_id());
		 }
		 Drawable marker = this.getResources().getDrawable(R.drawable.mapmarker);
		 if (mLocationOverlay == null) {
			 mLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
					 mItems, marker, marker, Color.WHITE, this, mResourceProxy);
		 } else {
			 mLocationOverlay.removeAllItems();
			 mLocationOverlay.addItems(mItems);
		 }
		 mOsmv.getOverlays().remove(mLocationOverlay);
		 mOsmv.getOverlays().add(mLocationOverlay);
		 mOsmv.invalidate();
	 }

	 @Override
	 public void onDestroyView() {
		 super.onDestroyView();
		 LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.mapview);
		 if (layout != null) {
			 layout.removeAllViews();
		 }

		 mOsmv.getTileProvider().clearTileCache();
		 System.gc();

	 }

	 @Override
	 public boolean onItemLongPress(int arg0, OverlayItem arg1) {
		 return false;
	 }

	 @Override
	 public boolean onItemSingleTapUp(int position, OverlayItem item) {
		 if ((_markerMap != null) && (_markerMap.size() > 0)
				 && _markerMap.containsKey(item)) {
			 logger.info("onItemSingleTapUp;item marker '" + item.getTitle() + "' pressed");
			 SetPopup(item);
		 } else {
			 logger.info("onItemSingleTapUp;item marker, item not available in itemlist");
			 SetPopup(null);
		 }
		 return false;
	 }

	 private void SetPopup(OverlayItem item) {
		 if (item == null) {
			 if (_popupView != null) {
				 mOsmv.removeView(_popupView);
				 mOsmv.setOnClickListener(null);
				 mOsmv.setClickable(false);
			 }
		 } else {
			 mOsmv.setOnClickListener(outsideClick);
			 if (_popupView == null) {
				 _popupView = getLayoutInflater(new Bundle()).inflate(R.layout.map_itempopup, mOsmv, false);

				 ImageButton imbClose = (ImageButton) _popupView.findViewById(R.id.imbInfoClose);
				 imbClose.setOnClickListener(new View.OnClickListener() {

					 @Override
					 public void onClick(View v) {
						 mOsmv.removeView(_popupView);
					 }
				 });

				 _popupView.setOnClickListener(this);
			 }
			 mOsmv.removeView(_popupView);
			 _popupView.setTag(item.getUid());
			 TextView tv = (TextView) _popupView.findViewById(R.id.map_popup_title);

			 tv.setText(item.getTitle());
			 tv.setTag(item.getUid());
			 TextView dv = (TextView) _popupView.findViewById(R.id.map_popup_description);
			 dv.setText(item.getSnippet());
			 dv.setTag(item.getUid());

			 ImageView iv = (ImageView) _popupView.findViewById(R.id.map_popup_Thumbnail);

			 ProposalObject proposalObject = ProposalHelper.getProposalById(Integer.parseInt(item.getUid()));

			 iv.setImageBitmap(proposalObject.get_image(iv.getWidth() / 2, iv.getHeight() / 2));

			 MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					 ViewGroup.LayoutParams.WRAP_CONTENT, item.getPoint(),
					 MapView.LayoutParams.BOTTOM_CENTER, 0, 0);

			 mOsmv.addView(_popupView, mapParams);
		 }

	 }

	 private OnClickListener outsideClick = new OnClickListener() {

		 @Override
		 public void onClick(View v) {
			 SetPopup(null);
		 }
	 };


	 @Override
	 public void onClick(View v) {
		 try {
			 if (_popupView != null && v.getId() == _popupView.getId()) {
				 String idAsString = (String) v.getTag();
				 if (idAsString != null && !idAsString.equals("")) {
					 int proposalId = Integer.parseInt(idAsString);

					 int fragPlaceID = getArguments().getInt("fragPlaceID");
					 View placeView = getActivity().findViewById(fragPlaceID);
					 if (fragPlaceID != 0 && placeView != null) {
						 logger.info("onClick;Opening proposalid: " + proposalId + " as fragment.");
						 FragmentTransaction ft = getFragmentManager().beginTransaction();

						 DetailFragment df = new DetailFragment();
						 Bundle b = new Bundle();
						 b.putInt("idEntity", proposalId);
						 df.setArguments(b);

						 ft.replace(fragPlaceID, df);
						 ft.commit();
					 } else {
						 logger.info("onClick;Opening proposalid: " + proposalId + " as intent.");
						 Intent i = new Intent(getActivity(), DetailActivity.class);
						 i.putExtra("idEntity", proposalId);
						 startActivity(i);
					 }
					 SetPopup(null);
				 }
			 } else {
				 SetPopup(null);
			 }
		 } catch (Exception ex) {
			 logger.error("onClick;OsmdroidMapActivity.onClick", ex);
		 }
	 }

	 @Override
	 public void resourcesUpdated() {
		 logger.info("proposalsUpdated;new proposals arrived");
		 AddProposals();
	 }

	 @Override
	 public void onLocationChanged(Location dummy) {
		 
		 if (LocationService.cl==null)
			 LocationService.cl = new Location("USER");
		 
		 if (mCurrentLocationOverlay==null)
			 Log.e("mCurrentLocationOverlay", "null");
		 
		 mCurrentLocationOverlay.setLocation(new GeoPoint((int) (LocationService.cl.getLatitude()*1E6),
				 (int) (LocationService.cl.getLongitude()*1E6)
				 ));

		 if (firstTime ){
			 mMapController.animateTo(new GeoPoint(LocationService.cl.getLatitude(), LocationService.cl.getLongitude()));
			 firstTime = false;
		 }

		 mOsmv.invalidate();
	 }

	 @Override
	 public void onProviderDisabled(String provider) {
	 }

	 @Override
	 public void onProviderEnabled(String provider) {
	 }

	 @Override
	 public void onStatusChanged(String provider, int status, Bundle extras) {
	 }

}
