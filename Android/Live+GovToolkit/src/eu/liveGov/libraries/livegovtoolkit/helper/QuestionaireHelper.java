package eu.liveGov.libraries.livegovtoolkit.helper;

import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.interfaces.WebcallsListener;
import eu.liveGov.libraries.livegovtoolkit.objects.QuestionaireResponseObject;
import eu.liveGov.libraries.livegovtoolkit.objects.ServiceApiErrorObject;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;

/**
 * Download Questionnaires.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class QuestionaireHelper implements WebcallsListener
{
	private ArrayList<QuestionaireListener> _listeners = new ArrayList<QuestionaireListener>();
	private boolean _isSending;
	private static final Logger logger = LoggerFactory.getLogger( QuestionaireHelper.class );

	public void getQuestionaireByCode( String questionaireCode, Context con )
	{
		getQuestionaireByCode( questionaireCode, questionaireCode, con );
	}

	public void getQuestionaireByCode( String questionaireCode, String objectCode, Context con )
	{
		_isSending = false;
		
		
		Log.e("GET USER QUESTIONAIRE", " " + UserInformationHelper.getAnonymousUserId( con ) + " " + questionaireCode + " " + objectCode);
		
		new DownloadHelper( this ).getQuestionnaire( UserInformationHelper.getAnonymousUserId( con ), questionaireCode, objectCode );
	}

	protected void updateQuestionaireListeners( Questionaire q )
	{
		for ( QuestionaireListener ql : _listeners )
		{
			ql.questionaireUpdated( q );
		}
	}

	protected void updateQuestionaireResultListeners( QuestionaireResult q )
	{
		for ( QuestionaireListener ql : _listeners )
		{
			ql.questionaireResultUpdated( q );
		}
	}

	protected void updateBothListeners( Questionaire q, QuestionaireResult qr )
	{
		for ( QuestionaireListener ql : _listeners )
		{
			ql.questionaireBothUpdated( q, qr );
		}
	}

	protected void updateQuestionaireSendtoServerListeners( boolean q )
	{
		for ( QuestionaireListener ql : _listeners )
		{
			ql.questionaireSendtoServerUpdated( q );
		}
	}

	public void saveQuestionaire( Questionaire sq, Context con )
	{
		_isSending = true;
		Questionaire qre = new Questionaire( sq );
		String json = questionaireJson( qre );
		
		Log.e("json", " " + json);
		
		new DownloadHelper( this ).sendQuestionnaire( json, UserInformationHelper.getAnonymousUserId( con ) );
	}

	public static String questionaireJson( Questionaire sq )
	{
		try
		{
			Gson gson = new Gson();
			return gson.toJson( sq );
		} catch ( Exception e )
		{
			logger.info( "questionaireJson; {}", e );
			return "";
		}
	}

	public void addListener( QuestionaireListener listener )
	{
		_listeners.add( listener );
	}

	public void removeListener( QuestionaireListener listener )
	{
		_listeners.remove( listener );
	}

	@Override
	public void webcallReady( HttpResponse response )
	{
		if ( _isSending )
		{
			handleSendResponse( response );
		} else
		{
			handleGetResponse( response );
		}
		_isSending = false;
	}

	private void handleGetResponse( HttpResponse response )
	{
		if ( response != null && response.getStatusLine().getStatusCode() == 200 )
		{
			try
			{
				Gson gson = new Gson();
				JsonReader jr = new JsonReader( new InputStreamReader( response.getEntity().getContent() ) );
				
				QuestionaireResponseObject result = gson.fromJson( jr, QuestionaireResponseObject.class );
				
				if ( result.getStatus() == QuestionaireResponseObject.QUESTIONAIRE_SET  )
				{
					updateQuestionaireListeners( result.getQuestionaire() );
				} else if ( result.getStatus() == QuestionaireResponseObject.QUESTIONAIRE_RESULT_SET )
				{
					updateQuestionaireResultListeners( result.getQuestionaireResult() );
				} else if ( result.getStatus() == QuestionaireResponseObject.BOTH_SET )
				{
					updateBothListeners( result.getQuestionaire(), result.getQuestionaireResult() );
				} else
				{ // if the status is unknown, send a null object.
					updateQuestionaireListeners( null );
				}

			} catch ( Exception e )
			{
				logger.error( "webcallReady; Exception: {}", e.getCause() );
				updateQuestionaireListeners( null );
			}
		} else
		{
			if(response == null){
				logger.error( "webcallReady;  No internet" );
			}else {
				logger.error( "webcallReady; http statuscode: {}", response.getStatusLine().getStatusCode() );
			}

			updateQuestionaireListeners( null );
		}
	}

	private void handleSendResponse( HttpResponse response )
	{
		if ( response != null && response.getStatusLine().getStatusCode() == 200 )
		{
			updateQuestionaireSendtoServerListeners( true );
		} else
		{
			try
			{
				Gson gson = new Gson();
				JsonReader jr = new JsonReader( new InputStreamReader( response.getEntity().getContent() ) );
				ServiceApiErrorObject result = gson.fromJson( jr, ServiceApiErrorObject.class );
				logger.error( "handleSendResponse;statuscode: {}, mesaage:{}", response.getStatusLine().getStatusCode(), result.getMessage() );
			} catch ( Exception e )
			{

			}
			updateQuestionaireSendtoServerListeners( false );
		}
	}
}
