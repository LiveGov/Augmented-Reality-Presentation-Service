package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.Utils.Listview_Adapter;
import eu.liveGov.libraries.livegovtoolkit.helper.ProposalHelper;
import eu.liveGov.libraries.livegovtoolkit.interfaces.ResourcesListener;
import eu.liveGov.libraries.livegovtoolkit.location.LocationService;
import eu.liveGov.libraries.livegovtoolkit.objects.FetchResources;
import eu.liveGov.libraries.livegovtoolkit.objects.ProposalObject;

/**
 * Display all items (Urban Plans) in a list.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class ListaFragment extends ListFragment implements ResourcesListener {
	Listview_Adapter _adapter;
	private static final Logger logger = LoggerFactory.getLogger(ListaFragment.class);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		FetchResources.addResourcesListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		container.setBackgroundColor(Color.WHITE);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ProposalObject po = (ProposalObject) getListAdapter().getItem(position);

		// This is also used in the MapFragment.
		int fragPlaceID = getArguments().getInt("fragPlaceID");
		View placeView = getActivity().findViewById(fragPlaceID);
		if (fragPlaceID != 0 && placeView != null) {
			logger.info("onListItemClick;Opening proposalid: " + po.get_id() + " as fragment.");
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			DetailFragment df = new DetailFragment();
			Bundle b = new Bundle();
			b.putParcelable("po", po);
			df.setArguments(b);
			ft.replace(fragPlaceID, df);
			ft.commit();
		} else {
			logger.info("onListItemClick;Opening proposalid: " + po.get_id() + " as intent.");
			Intent i = new Intent(getActivity(), DetailActivity.class);
			i.putExtra("po", po);
			startActivity(i);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		resourcesUpdated(); // manually reload the proposal
		if (_adapter.getCount() == 0) {
			setListShown(false);
		}
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		Functions.stopLocationService(getActivity(), false);
		super.onPause();
	}

	@Override
	public void onResume() {
		Functions.startLocationService(getActivity());
		super.onResume();
	}

//	public void sortList() {
//		Location cl = LocationService.getInstance(getActivity()).getLastKnowLocation();
//		if (cl != null)
//			_adapter.sortOnLocation(cl);
//	}

	@Override
	public void resourcesUpdated() {
		if (getActivity() != null) { // it's possible that there isn't a
										// activity available. So this means we
										// don't have to update it.
			logger.info("proposalsUpdated;new proposals arrived");
			
			ArrayList<ProposalObject> objects = ProposalHelper.getProposals();
			
			_adapter = new Listview_Adapter(getActivity());
			
			for (ProposalObject po : objects) {
				_adapter.addProposal(po);
			}
			//sortList();
			setListAdapter(_adapter);
			setListShown(true);
		}
	}

}
