package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

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
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;
import eu.liveGov.libraries.livegovtoolkit.view.questionaireResult.QuestionaireResultView;


/**
 * Show the results of the questionnaires in pie charts.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class QuestionaireResultFragment extends Fragment
{
    private QuestionaireResult _questionaireResult;
    private String title;
    private int id;
    private static final Logger logger = LoggerFactory.getLogger(QuestionaireResultFragment.class);
    private Tracker tracker;
    
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
    
    public void setQuestionnaireResult( QuestionaireResult questionaireResult){
	logger.info( "setQuestionnaireResult;load questionnaireResult id:" + questionaireResult.getId() );
	_questionaireResult = questionaireResult;
    }
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getQRId() {
		return id;
	}

	public void setQRId(int id) {
		this.id = id;
	}

	@Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
	View view = inflater.inflate( R.layout.questionaire_response_fragment, container, false );
	if ( _questionaireResult != null )
	{
	    LinearLayout layout = (LinearLayout) view.findViewById( R.id.questionaire_response_fragment );
	    QuestionaireResultView questionaire = new QuestionaireResultView( getActivity(), _questionaireResult );
	    layout.addView( questionaire, new LinearLayout.LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT ) );
	    
	}
	return view;
    }
    
    @Override
    public void onResume()
    {
	super.onResume();
	if(title != null){
		tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName() + " - " + title + " (" + id + ")");
	    tracker.send( MapBuilder.createAppView().build() );
	} else {
		tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName() + " - Id: " + id);
	    tracker.send( MapBuilder.createAppView().build() );
	}
    }
    
    @Override
    public void onDestroyView()
    {
	super.onDestroyView();
	LinearLayout layout = (LinearLayout) getActivity().findViewById( R.id.questionaire_response_fragment );
	if ( layout != null )
	{
	    layout.removeAllViews();
	}
    }
}
