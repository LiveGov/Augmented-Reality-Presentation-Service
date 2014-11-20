package eu.liveGov.libraries.livegovtoolkit.view.questionare;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.interfaces.QuestionaireListener;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Category;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;

/**
 * Custom scrollview for user information fragment.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class UserInformationView extends ScrollView
{
	private Questionaire _questionaire;
	private LinearLayout _categoryHolder;
	private Button _save;

	ArrayList<QuestionaireListener> _listener;

	public UserInformationView( Context context, Questionaire questionaire )
	{
		super( context );
		_listener = new ArrayList<QuestionaireListener>();
		_questionaire = questionaire;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		inflater.inflate( R.layout.user_information_view, this );

		_categoryHolder = (LinearLayout) findViewById( R.id.questionaire_main );
		_save = (Button) findViewById( R.id.btnUserInfoSave );
		_save.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				for(QuestionaireListener ql : _listener){
					ql.sumbitButtonClicked(_questionaire);
				}
			}
		} );	

		if ( _categoryHolder != null )
		{
			addCategories();
		}
	}

	private void addCategories()
	{
		if(_questionaire == null)
			return;
		int size = _questionaire.getCategories().size();
		for ( int i = 0; i < size; i++ )
		{
			Category c = _questionaire.getCategories().get( i );
			_categoryHolder.addView( new CategoryView( this.getContext(), c ) );
		}
	}

	public void addListener(QuestionaireListener questionareListener){
		_listener.add( questionareListener );
	}

	public void removeListener(QuestionaireListener questionareListener){
		_listener.remove( questionareListener );
	}
}
