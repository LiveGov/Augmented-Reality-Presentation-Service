package eu.liveGov.libraries.livegovtoolkit.view.questionare;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.AnswerOption;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Question;

/**
 * Single question view.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class QuestionView extends LinearLayout
{
	private Question _question;
	private TextView _questionText;
	private LinearLayout _answeroptionHolder;

	public QuestionView( Context context ){
		super( context );
	}

	public QuestionView( Context context, Question q ) {
		super( context );
		_question = q;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		inflater.inflate( R.layout.questionaire_question, this );

		_questionText = (TextView) findViewById( R.id.questionaire_questiontext );
		if ( _questionText != null ){
			_questionText.setText( q.getQuestiontext() );
		}
		_answeroptionHolder = (LinearLayout) findViewById( R.id.questionaire_answeroptionHolder );

		if ( _question!= null )	{
			addAnswerOptions();
		}

	}

	private void addAnswerOptions()   {
		if ( _question.getDisplaytype() != -1 )	{
			int displayType = _question.getDisplaytype();
			if ( displayType == Question.TYPE_SINGLE_SELECT )  {
				addRadioButtons();
			} else if ( displayType == Question.TYPE_MULTI_SELECT ) {
				addMultiSelect();
			} else if ( displayType == Question.TYPE_TEXT_ONLY ) {
				addCommentField( _question.getTextfieldhint(), _question.getComment() );
			} else if ( displayType == Question.TYPE_SINGLE_SELECT_TEXT ) {
				addRadioButtons();
				addCommentField( _question.getTextfieldhint(), _question.getComment() );
			} else if ( displayType == Question.TYPE_MULTI_SELECT_TEXT ) {
				addMultiSelect();
				addCommentField( _question.getTextfieldhint(), _question.getComment() );
			}
		}
	}

	private void addMultiSelect() {

		for ( AnswerOption ao : _question.getAnsweroptions() ){
			CheckBox cb = new CheckBox( getContext() );
			cb.setTextAppearance( getContext(), android.R.style.TextAppearance_Small );
			cb.setText( ao.getAnsweroptiontext() );
			cb.setChecked( ao.isSelected() );
			cb.setId( ao.getId() );
			cb.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
					AnswerOption ao = _question.findAnsweroptionId( buttonView.getId() );
					ao.setSelected( isChecked );
				}
			});
			_answeroptionHolder.addView( cb );
		}

	}

	private void addCommentField( String textFieldHint, String comment ) {
		EditText text = new EditText( getContext() );
		text.setHint( textFieldHint );
		text.setText( comment );
		text.addTextChangedListener( new TextWatcher()
		{

			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count ){}

			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after ){}

			@Override
			public void afterTextChanged( Editable s ){
				_question.setComment( s.toString() );
			}
		} );
		_answeroptionHolder.addView( text );
	}

	private void addRadioButtons(){
		Context context = this.getContext();
		RadioGroup group = new RadioGroup( context );
		RadioButton button;
		int size = _question.getAnsweroptions().size();
		for ( int i = 0; i < size; i++ ) {
			AnswerOption ao = _question.getAnsweroptions().get( i );
			button = new RadioButton( context );

			button.setId( ao.getId() );
			button.setText( ao.getAnsweroptiontext() );
			button.setTextAppearance( getContext(), android.R.style.TextAppearance_Small );
			button.setChecked( ao.isSelected() );
			group.addView( button );
		}
		group.setOnCheckedChangeListener( new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged( RadioGroup group, int checkedId ) {
				RadioButton rb = (RadioButton) group.findViewById( checkedId );
				_question.setAnsweroptionIdChecked( rb.getId() );
			}
		} );

		_answeroptionHolder.addView( group );
	}

}
