package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.Utils.Functions;
import eu.liveGov.libraries.livegovtoolkit.helper.ProposalHelper;
import eu.liveGov.libraries.livegovtoolkit.helper.QuestionaireHelper;
import eu.liveGov.libraries.livegovtoolkit.helper.UserInformationHelper;
import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.objects.ProposalObject;
import eu.liveGov.libraries.livegovtoolkit.objects.UserInformation;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;


/**
 * Fragment to display details about an item clicked from list, map or AR.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class DetailFragment extends Fragment implements QuestionaireListener
{
	private ProposalObject _proposalObject;
	private Fragment _currentFragment;
	private ProgressBar _progressBar;

	private String _questionaireCode;
	private String _objectCode;

	private Tracker tracker;

	private QuestionaireHelper _questionaireHelper;

	private static final Logger logger = LoggerFactory.getLogger( DetailFragment.class );

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		_questionaireHelper = new QuestionaireHelper();
		_questionaireHelper.addListener( this );
		Bundle bundle = this.getArguments();
		if ( bundle != null )
		{
			Parcelable p = bundle.getParcelable( "po" );
			int poID = bundle.getInt( "idEntity" );

			if ( p != null )
			{
				logger.info( "onCreate;Load proposalObject from parcible: " + ( (ProposalObject) p ).get_id() );
				_proposalObject = (ProposalObject) p;
			} else if ( poID != 0 )
			{
				logger.info( "onCreate;Load proposalObject from id: " + poID );
				_proposalObject = ProposalHelper.getProposalById( poID );
			}
		}
		loadQuestionaire();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);

		this.tracker = EasyTracker.getInstance(this.getActivity());
	}


	private void loadQuestionaire()
	{
		// REM
		_questionaireCode = "UP-" + _proposalObject.get_id() + "-" + "XX";//Locale.getDefault().getLanguage();
		//_objectCode = "cl_5_" + _proposalObject.get_id();
		_objectCode = "" + _proposalObject.get_id();
		_questionaireHelper.getQuestionaireByCode( _questionaireCode, _objectCode, getActivity() );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.details_fragment, container, false );

		_progressBar = (ProgressBar) view.findViewById( R.id.detailsProgressBar );

		if ( _proposalObject != null )
		{
			ImageView thumb = (ImageView) view.findViewById( R.id.detailsThumbnail );
			int width = thumb.getWidth();
			int height = thumb.getWidth();

			thumb.setImageBitmap( _proposalObject.get_image( width, height ) );


			( (TextView) view.findViewById( R.id.detailsTvDescription ) ).setText( _proposalObject.get_description() );
			( (TextView) view.findViewById( R.id.detailstvTitle ) ).setText( _proposalObject.get_title() );
		}

		changeFragment( _currentFragment );

		return view;
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
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName() + " - " + _proposalObject.get_title() + " (" + _proposalObject.get_id() + ")");
		this.tracker.send( MapBuilder.createAppView().build() );
	}

	@Override
	public void onDestroy()
	{
		logger.info( "onDestroy; closing the detailFragment of proposal id:{}", _proposalObject.get_id() );
		_questionaireHelper.removeListener( this );
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
		if ( isVisible() )
		{
			_progressBar.setVisibility( View.GONE );
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace( R.id.details_fragment, fragment );
			ft.commit();
		}
	}

	private void hideFragment()
	{
		if ( _currentFragment != null )
		{
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.hide( _currentFragment );
			ft.commit();
		}
		_progressBar.setVisibility( View.VISIBLE );
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
		if ( UserInformationHelper.getAnonymousUserId( getActivity() ) == UserInformation.UNDEFINED_ID )
		{ // give "no AnonymousUserId"-Error. Highly unlikely, because he checks when starting the app..
			logger.error( "sumbitButtonClicked; AnonymousUserId is not set; downloading it again..." );
			new AlertDialog.Builder( getActivity() ).setTitle( getString( R.string.detailfragment_no_anonymous_id_title ) ).setMessage( getString( R.string.detailfragment_no_anonymous_id_Message ) ).setPositiveButton( getString( android.R.string.ok ), new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int which )
				{
					// don't have to do anything, other than showing the error.
				}
			} ).show();
			UserInformationHelper uih = new UserInformationHelper();
			uih.requestNewAnonymous( getActivity() );

		} else if ( UserInformation.getQuestionaire() == null || !UserInformation.getQuestionaire().isFilledIn() )
		{
			logger.error( "sumbitButtonClicked; trying to sumbit questionnaire without enough user information." );
			new AlertDialog.Builder( getActivity() ).setTitle( getString( R.string.detailfragment_no_userinfo_title ) ).setMessage( getString( R.string.detailfragment_no_userinfo_Message ) ).setPositiveButton( getString( android.R.string.ok ), new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int which )
				{
					Intent i = new Intent( getActivity(), UserInformationActivity.class );
					getActivity().startActivity( i );
				}
			} ).show();
		} else if ( questionaire != null )
		{
			hideFragment();
			logger.info( "sumbitButtonClicked;SumbitButton clicked." );
			_questionaireHelper.saveQuestionaire( questionaire, getActivity() );
		}
	}

	@Override
	public void questionaireUpdated( Questionaire questionaire )
	{

		if ( questionaire == null )
		{
			logger.info( "QuestionaireUpdated;failed to load the new questionnaire." );
			Toast.makeText( getActivity(), R.string.detailfragment_failed_to_load_questionaire, Toast.LENGTH_SHORT ).show();
			showFragment();
		} else
		{
			logger.info( "QuestionaireUpdated;load the new questionnaire." );
			QuestionaireFragment qf = new QuestionaireFragment();
			qf.setTitle(_proposalObject.get_title());
			qf.setqFId(_proposalObject.get_id());
			qf.setQuestionaire( questionaire );
			qf.addListener( this );
			changeFragment( qf );
		}
	}

	@Override
	public void questionaireResultUpdated( QuestionaireResult questionaireResult )
	{
		if ( questionaireResult == null )
		{
			logger.info( "QuestionaireResultUpdated;Failed to load the new questionnaire results." );
			showFragment();
		} else
		{
			logger.info( "QuestionaireResultUpdated;load the new questionnaire results." );
			QuestionaireResultFragment qrf = new QuestionaireResultFragment();
			qrf.setTitle(_proposalObject.get_title());
			qrf.setQRId(_proposalObject.get_id());
			qrf.setQuestionnaireResult( questionaireResult );
			changeFragment( qrf );
		}
	}

	@Override
	public void questionaireSendtoServerUpdated( boolean successful )
	{
		logger.info( "questionaireSendtoServerUpdated; successful: {}", successful );
		loadQuestionaire();
	}

	@Override
	public void questionaireBothUpdated( Questionaire q, QuestionaireResult qr )
	{
		questionaireResultUpdated( qr );
	}


}
