package eu.liveGov.libraries.livegovtoolkit.view.questionaireResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.CategoryResult;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionResult;

public class CategoryResultView extends LinearLayout
{

	public CategoryResultView( Context context )
	{
		super( context );
	}

	public CategoryResultView( Context context, CategoryResult category)
	{
		this( context );

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		inflater.inflate( R.layout.questionaire_category_response, this );

		LinearLayout questionsHolder = (LinearLayout) findViewById( R.id.Questionaire_questions_holder );
		if(category != null)
			addQuestions( questionsHolder, category );
	}

	private void addQuestions(LinearLayout parent, CategoryResult category){
		for(QuestionResult qr : category.getQuestions()){
			if(qr.getAnsweroptions().size() > 0){
				QuestionResultView qv = new QuestionResultView( getContext(), qr );
				parent.addView( qv );
			}
		}
	}
}
