package eu.liveGov.libraries.livegovtoolkit.view.questionare;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Category;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Question;

public class CategoryView extends LinearLayout
{
	private Category _category;
	private LinearLayout _questionHolder;

	public CategoryView( Context context ){
		super( context );
	}

	public CategoryView( Context context, Category c ) {
		super( context );
		_category = c;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		inflater.inflate( R.layout.questionaire_category, this );

		_questionHolder = (LinearLayout) findViewById( R.id.questionaire_questionholder );
		if ( _questionHolder != null && _category.getQuestions() != null && _category.getQuestions().size() > 0 ) 
			addQuestions();
	}

	private void addQuestions(){
		if ( _category == null )
			return;
		
		int size = _category.getQuestions().size();
		for ( int i = 0; i < size; i++ ){
			Question q = _category.getQuestions().get( i );
			_questionHolder.addView( new QuestionView( this.getContext(), q ) );
		}
	}
}
