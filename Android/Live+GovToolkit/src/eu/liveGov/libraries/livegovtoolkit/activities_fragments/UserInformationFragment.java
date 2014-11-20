package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.helper.UserInformationHelper;
import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.interfaces.UserInformationUpdateListener;
import eu.liveGov.libraries.livegovtoolkit.objects.UserInformation;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;

/**
 * Display information about the user in the UserInformationActivity.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class UserInformationFragment extends Fragment implements QuestionaireListener, UserInformationUpdateListener
{
    private Fragment _currentFragment;
    private ProgressBar _progressBar;
    private UserInformationHelper _uih;
    private Tracker tracker;
    
    private static final Logger logger = LoggerFactory.getLogger( DetailFragment.class );
    
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
	View view = inflater.inflate( R.layout.user_info_fragment, container, false );
	
	_progressBar = (ProgressBar) view.findViewById( R.id.userinfoProgressBar );
	
	changeFragment( _currentFragment );
	
	return view;
    }
    
    @Override
    public void onViewCreated( View view, Bundle savedInstanceState )
    {
	_uih = new UserInformationHelper();
	_uih.addListener( this );
	_uih.loadUserInformartion( getActivity(), false );
	super.onViewCreated( view, savedInstanceState );
    }
    
    @Override
    public void onPause()
    {
	Functions.stopLocationService( getActivity(), false );
	super.onPause();
    }
    @Override
    public void onResume()
    {
	Functions.startLocationService( getActivity() );
	
	super.onResume();
	this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
    this.tracker.send( MapBuilder.createAppView().build() );
    }
    
    @Override
    public void onDestroy()
    {
	_uih.removeListener( this );
	super.onDestroy();
    }
    
    private void changeFragment( Fragment fragment )
    {
	if ( fragment == null )
	{
	    _currentFragment = null;
	    return;
	}
	_currentFragment = fragment;
	_progressBar.setVisibility( View.GONE );
	FragmentTransaction ft = getFragmentManager().beginTransaction();
	ft.replace( R.id.userinfo_fragment, fragment );
	ft.commit();
    }
    
    private void showFragment()
    {
	if ( _currentFragment != null )
	{
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    ft.show( _currentFragment );
	    ft.commit();
	}
	_progressBar.setVisibility( View.GONE );
    }
    
    @Override
    public void sumbitButtonClicked( Questionaire questionaire )
    {
	if ( questionaire != null )
	{
	    UserInformation.setQuestionaire( questionaire );
	    (new UserInformationHelper()).sendUserInfoToServer(questionaire, getActivity());
	    tracker.send(MapBuilder
	    	      .createEvent("User_information",     // Event category (required)
	    	                   "submit_press",  // Event action (required)
	    	                   "Userinformation sumbitted",   // Event label
	    	                   null)            // Event value
	    	      .build()
	    	  );
	    
	}
	getActivity().finish();
    }
    
    @Override
    public void questionaireUpdated( Questionaire questionaire )
    {
	
	if ( questionaire == null )
	{
	    logger.info( "QuestionaireUpdated;failed to load the new questionnaire." );
	    Toast.makeText( getActivity(), R.string.user_information_failed_to_load_questionaire, Toast.LENGTH_SHORT ).show();
	    showFragment();
	} else
	{
	    logger.info( "QuestionaireUpdated;load the new questionnaire." );
	    UserInformationQuestionaireFragment qf = new UserInformationQuestionaireFragment();
	    qf.setQuestionaire( questionaire );
	    qf.addListener( this );
	    changeFragment( qf );
	}
    }
    
    @Override
    public void questionaireResultUpdated( QuestionaireResult questionaireResult ){}
    
    @Override
    public void questionaireSendtoServerUpdated( boolean successful ){}
    
    @Override
    public void questionaireBothUpdated( Questionaire q, QuestionaireResult qr )
    {questionaireUpdated( q );}
    
    @Override
    public void anonymousUpdated(){}
    
    @Override
    public void userinfoQuestionaireUpdated(){
    	Questionaire questionaire = UserInformation.getQuestionaire();
    	if ( questionaire == null ){
    		logger.info( "QuestionaireUpdated;failed to load the new questionnaire." );
    		Toast.makeText( getActivity(), R.string.user_information_failed_to_load_questionaire, Toast.LENGTH_SHORT ).show();
    		showFragment(); 
    	}else{
    		logger.info( "QuestionaireUpdated;load the new questionnaire." );
    		UserInformationQuestionaireFragment qf = new UserInformationQuestionaireFragment();
    		qf.setQuestionaire( questionaire );
    		qf.addListener( this );
    		changeFragment( qf );
    	}
    }
}
