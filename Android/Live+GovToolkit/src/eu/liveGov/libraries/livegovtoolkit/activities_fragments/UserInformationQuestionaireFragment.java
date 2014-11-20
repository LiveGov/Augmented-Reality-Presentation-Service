package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;
import eu.liveGov.libraries.livegovtoolkit.view.questionare.UserInformationView;

/**
 * Display questions inside the UserInformationActivity.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class UserInformationQuestionaireFragment extends Fragment implements QuestionaireListener {

	ArrayList<QuestionaireListener> _listener = new ArrayList<QuestionaireListener>();
	UserInformationView _questionaireView;
	Questionaire _questionaire;
	private Tracker tracker;

	private static final Logger logger = LoggerFactory.getLogger(QuestionaireFragment.class);

	@Override
	public void onCreate( Bundle savedInstanceState ){
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

		if ( _questionaire != null ){
			LinearLayout layout = (LinearLayout) view.findViewById( R.id.questionaire_fragment );
			_questionaireView = new UserInformationView( getActivity(), _questionaire );
			_questionaireView.addListener( this );
			layout.addView( _questionaireView, new LinearLayout.LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT ) );
		}
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
		this.tracker.send( MapBuilder.createAppView().build() );
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if ( _questionaireView != null )
			_questionaireView.removeListener( this );

		LinearLayout layout = (LinearLayout) getActivity().findViewById( R.id.questionaire_fragment );
		if ( layout != null )
			layout.removeAllViews();
	}

	public void setQuestionaire( Questionaire questionaire ){
		_questionaire = questionaire;
	}

	public void addListener( QuestionaireListener questionareListener ){
		_listener.add( questionareListener );
	}

	public void removeListener( QuestionaireListener questionareListener ){
		_listener.remove( questionareListener );
	}

	@Override
	public void sumbitButtonClicked( Questionaire questionaire ){
		logger.info( "sumbitButtonClicked;User clicked on the sumbitbutton" );
		for ( QuestionaireListener ql : _listener ){
			ql.sumbitButtonClicked( questionaire );
		}
	}

	@Override
	public void questionaireUpdated( Questionaire questionaire ){}

	@Override
	public void questionaireResultUpdated( QuestionaireResult questionaireResult){}

	@Override
	public void questionaireSendtoServerUpdated( boolean successful){}

	@Override
	public void questionaireBothUpdated( Questionaire q, QuestionaireResult qr ){}
}
