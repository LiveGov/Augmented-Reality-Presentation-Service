package eu.liveGov.libraries.livegovtoolkit.view.questionaireResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.CategoryResult;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;

/**
 * Custom scrollview to view the results of the questionnaires.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class QuestionaireResultView extends ScrollView
{
	QuestionaireResult _questionaireResult;

	public QuestionaireResultView( Context context ){
		super( context );
	}

	public QuestionaireResultView( Context context, QuestionaireResult qr )  {
		this( context );
		_questionaireResult = qr;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		inflater.inflate( R.layout.questionaire_response, this );

		TextView resultDescription = (TextView) findViewById( R.id.questionaire_result_description );
		resultDescription.setText( _questionaireResult.getUsermessage() );

		LinearLayout categoriesHolder = (LinearLayout) findViewById( R.id.questionaire_result_catagory_holder );
		addCategories( categoriesHolder );	
	}

	private void addCategories(LinearLayout parent){
		for(CategoryResult cr : _questionaireResult.getCategories()){
			CategoryResultView crv = new CategoryResultView( getContext(), cr );
			parent.addView( crv );
		}
	}
}
