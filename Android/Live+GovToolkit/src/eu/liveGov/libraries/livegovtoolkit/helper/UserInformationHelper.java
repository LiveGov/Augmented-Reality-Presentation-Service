package eu.liveGov.libraries.livegovtoolkit.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.interfaces.UserInformationUpdateListener;
import eu.liveGov.libraries.livegovtoolkit.interfaces.WebcallsListener;
import eu.liveGov.libraries.livegovtoolkit.objects.UserInformation;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;

/**
 * Download and handle user information questionnaire.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class UserInformationHelper implements WebcallsListener, QuestionaireListener
{
	private static final String USER_ID = "ANONYMOUS_USER_ID";
	private static final Logger logger = LoggerFactory.getLogger( UserInformationHelper.class );

	// REM
	private String questionaireCode = "USER-" + "XX";// Locale.getDefault().getLanguage();

	private QuestionaireHelper _questionaireHelper = new QuestionaireHelper();

	private ArrayList<UserInformationUpdateListener> _listeners = new ArrayList<UserInformationUpdateListener>();

	private Context _context;

	public UserInformation loadUserInformartion( Context c, boolean alwaysSendUpdate )
	{
		_context = c;
		logger.info( "loadUserInformartion;" );
		SharedPreferences sharedPreferences = c.getSharedPreferences( "UserInformation", Context.MODE_PRIVATE );
		int userId = sharedPreferences.getInt( USER_ID, UserInformation.UNDEFINED_ID );
		UserInformation ui = new UserInformation( userId );
		if ( userId != UserInformation.UNDEFINED_ID )
		{
			getUserInfoQuestionaire( c );
			if ( alwaysSendUpdate )
			{
				sendAnonymousUpdated(); // If the caller wants it: let the listeners know that the anonymousUserId is ready.
			}
		} else
		{
			requestNewAnonymous( c );
		}
		return ui;
	}

	private void getUserInfoQuestionaire( Context c )
	{
		if ( UserInformation.getQuestionaire() == null )
		{
			_questionaireHelper.addListener( this );
			_questionaireHelper.getQuestionaireByCode( questionaireCode, c );
		} else
		{
			questionaireUpdated( null );
		}
	}

	public void sendUserInfoToServer( Questionaire userinfo, Context c )
	{
		_questionaireHelper.saveQuestionaire( userinfo, c );
	}

	public static int getAnonymousUserId( Context c )
	{
		SharedPreferences sharedPreferences = c.getSharedPreferences( "UserInformation", Context.MODE_PRIVATE );
		return sharedPreferences.getInt( USER_ID, UserInformation.UNDEFINED_ID );
	}

	public static void saveAnonymousUserId( Context c, int id )
	{
		logger.info( "saveAnonymousUserId; id:{}", id );
		SharedPreferences sharedPreferences = c.getSharedPreferences( "UserInformation", Context.MODE_PRIVATE );
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt( USER_ID, id );
		editor.commit();
	}

	public UserInformationHelper( UserInformationUpdateListener listener )
	{
		addListener( listener );
	}

	public UserInformationHelper()
	{
	}

	@Override
	public void webcallReady( HttpResponse response )
	{
		if ( response != null && response.getStatusLine().getStatusCode() == 200 )
		{
			try
			{
				Gson gson = new Gson();
				InputStream is = response.getEntity().getContent();
				JsonReader jr = new JsonReader( new InputStreamReader( is ) );
				UserInformation result = gson.fromJson( jr, UserInformation.class );
				saveAnonymousUserId( _context, result.getAnonymousUserId() );
				UserInformation currentUI = loadUserInformartion( _context, false );
				currentUI.setAnonymousUserId( result.getAnonymousUserId() );

			} catch ( Exception e )
			{
				logger.error( "webcallReady; Exception: {}", e.getCause() );
			}
			getUserInfoQuestionaire( _context );
		} else
		{
			if ( response == null )
			{
				logger.error( "webcallReady;  No internet" );
			} else
			{
				logger.error( "webcallReady; http statuscode: {}", response.getStatusLine().getStatusCode() );
			}
		}
		sendAnonymousUpdated();
	}

	private void sendAnonymousUpdated()
	{
		for ( UserInformationUpdateListener uiul : _listeners )
		{
			uiul.anonymousUpdated();
		}
	}

	public void requestNewAnonymous( Context con )
	{
		logger.info( "requestNewAnonymous;" );
		_context = con;
		final TelephonyManager tm = (TelephonyManager) con.getSystemService( Context.TELEPHONY_SERVICE );

		String deviceId = tm.getDeviceId();
		logger.info( "requestNewAnonymous; id: A_" + deviceId );
		new DownloadHelper( this ).createAnonymousUser( "A_" + deviceId );
	}

	public void addListener( UserInformationUpdateListener listener )
	{
		_listeners.add( listener );
	}

	public void removeListener( UserInformationUpdateListener listener )
	{
		_listeners.remove( listener );

	}

	@Override
	public void sumbitButtonClicked( Questionaire questionaire )
	{
	}

	@Override
	public void questionaireSendtoServerUpdated( boolean successful )
	{
		logger.info( "uestionaireSendtoServerUpdated: {}", successful );
		if ( !successful )
		{ // isn't Successful, reset the user info
			UserInformation.setQuestionaire( null );
		}
		for ( UserInformationUpdateListener uiul : _listeners )
		{
			uiul.userinfoQuestionaireUpdated();
		}
	}

	@Override
	public void questionaireUpdated( Questionaire questionaire )
	{
		if ( questionaire != null && questionaire.getCode().equalsIgnoreCase( questionaireCode ) )
			UserInformation.setQuestionaire( questionaire );
		for ( UserInformationUpdateListener uiul : _listeners )
		{
			uiul.userinfoQuestionaireUpdated();
		}
	}

	@Override
	public void questionaireResultUpdated( QuestionaireResult questionaireResult )
	{
	}

	@Override
	public void questionaireBothUpdated( Questionaire q, QuestionaireResult qr )
	{
		questionaireUpdated( q );
	}
}
