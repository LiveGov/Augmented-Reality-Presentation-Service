package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
//import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.location.LocationService;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;
import eu.liveGov.libraries.livegovtoolkit.view.questionare.SimpleQuestionaireView;

/**
 * Show the questions for an item (Urban Plan)
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class QuestionaireFragment extends Fragment implements QuestionaireListener
{

	ArrayList<QuestionaireListener> _listener = new ArrayList<QuestionaireListener>();
	SimpleQuestionaireView _questionaireView;
	Questionaire _questionaire;
	private Tracker tracker;
	private int id;
	private String title;

	private static final Logger logger = LoggerFactory.getLogger(QuestionaireFragment.class);

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);

		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.questionaire_fragment, container, false );

		if ( _questionaire != null )
		{
			LinearLayout layout = (LinearLayout) view.findViewById( R.id.questionaire_fragment );
			_questionaireView = new SimpleQuestionaireView( getActivity(), _questionaire );
			_questionaireView.addListener( this );
			layout.addView( _questionaireView, new LinearLayout.LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT ) );

		}
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName() + " - " + title + " (" + id + ")");
		tracker.send( MapBuilder.createAppView().build() );
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if ( _questionaireView != null )
		{
			_questionaireView.removeListener( this );
		}
		LinearLayout layout = (LinearLayout) getActivity().findViewById( R.id.questionaire_fragment );
		if ( layout != null )
		{
			layout.removeAllViews();
		}
	}

	public void setQuestionaire( Questionaire questionaire )
	{
		_questionaire = questionaire;
	}

	public String getTitle() {
		return title;
	}

	public int getQFId() {
		return id;
	}

	public void setqFId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void addListener( QuestionaireListener questionareListener )
	{
		_listener.add( questionareListener );
	}

	public void removeListener( QuestionaireListener questionareListener )
	{
		_listener.remove( questionareListener );
	}

	@Override
	public void sumbitButtonClicked( Questionaire questionaire )
	{
		logger.info( "sumbitButtonClicked;User clicked on the sumbitbutton" );
		for ( QuestionaireListener ql : _listener )
		{
			
			if (ARFragment.mSensors.getLocation()!=null){
				questionaire.setLat( Double.toString(ARFragment.mSensors.getLocation().getLatitude()));
				questionaire.setLng( Double.toString(ARFragment.mSensors.getLocation().getLongitude()));
			} else if (LocationService.cl!=null) {
				questionaire.setLat(Double.toString(LocationService.cl.getLatitude()));
				questionaire.setLng(Double.toString(LocationService.cl.getLongitude()));
			}
			
			ql.sumbitButtonClicked( questionaire );
		}
		tracker.send(MapBuilder
				.createEvent("questionnaire_submit",     // Event category (required)
						"button_press",  // Event action (required)
						title + " (" + id + ")",   // Event label
						null)            // Event value
						.build()
				);
	}

	@Override
	public void questionaireUpdated( Questionaire questionaire ){}

	@Override
	public void questionaireResultUpdated( QuestionaireResult questionaireResult ){}

	@Override
	public void questionaireSendtoServerUpdated( boolean successful ){}

	@Override
	public void questionaireBothUpdated( Questionaire q, QuestionaireResult qr ){}
}