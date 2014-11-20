package eu.liveGov.libraries.livegovtoolkit.view.questionaireResult;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.AnswerOptionResult;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionResult;

/**
 * Single question results. Pie charts settings are here. 
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class QuestionResultView extends LinearLayout
{
	private QuestionResult _qestionResponse;

	String trans = "88";
	
	/** Colors to be used for the pie slices. */
	private final int[] COLORS = new int[] { Color.parseColor("#"+ trans +"00ff00"), 
											 Color.parseColor("#"+ trans +"0000ff"), 
											 Color.parseColor("#"+ trans +"ff00ff"), 
											 Color.parseColor("#"+ trans +"00ffff"), 
											 Color.parseColor("#"+ trans +"ff0000"), 
											 Color.parseColor("#"+ trans +"cccccc"), 
											 Color.parseColor("#"+ trans +"ffff00"), 
											 Color.parseColor("#"+ trans +"444444") };

	public QuestionResultView( Context context ){
		super( context );
	}

	public QuestionResultView( Context context, QuestionResult qr ){
		this( context );
		_qestionResponse = qr;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		inflater.inflate( R.layout.questionaire_question_response, this );

		LinearLayout graphHolder = (LinearLayout) findViewById( R.id.questionaire_graphHolder );
		TextView questionText = (TextView) findViewById( R.id.questionaire_questiontext );
		
		if ( _qestionResponse != null && _qestionResponse.getAnsweroptions().size() > 0 && _qestionResponse.getAnsweredCount() > 0 ){
			questionText.setText( _qestionResponse.getQuestiontext() );
			//questionText.setBackgroundColor(Color.GREEN);
			addPieChart( graphHolder );
		}
	}

	private void addPieChart( LinearLayout parent )
	{
		if (_qestionResponse.getAnsweroptions().size() == 0 ){
			return;
		}

		CategorySeries pieDataset = new CategorySeries( _qestionResponse.getQuestiontext() );
		DefaultRenderer renderer = new DefaultRenderer();
		
		renderer.setStartAngle(180);
		renderer.setDisplayValues(true);
		renderer.setShowLegend( true );
		renderer.setZoomButtonsVisible( false );
		renderer.setZoomEnabled( false );
		renderer.setPanEnabled( false );
		renderer.setApplyBackgroundColor( true );

		int[] margins = {10,30,10,30};

		renderer.setMargins( margins	);
		renderer.setBackgroundColor( Color.WHITE );
		renderer.setLabelsTextSize( 24 );
		renderer.setLabelsColor( Color.BLACK );
		renderer.setLegendTextSize(28f);
		renderer.setLegendHeight(100);

		int i = 0;
		for ( AnswerOptionResult aor : _qestionResponse.getAnsweroptions() ){
			int count = aor.getAnsweredCount();
			String s = aor.getAnsweroptiontext();
			if(s == null) 
				s = "";
			
			pieDataset.add(s, count);
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor( COLORS[i % COLORS.length] );
			simpleRenderer.setShowLegendItem(true);
			renderer.addSeriesRenderer( simpleRenderer );
			i++;
		}

		GraphicalView pieView = ChartFactory.getPieChartView( getContext(), pieDataset, renderer );
		parent.addView( pieView );
	}
}
