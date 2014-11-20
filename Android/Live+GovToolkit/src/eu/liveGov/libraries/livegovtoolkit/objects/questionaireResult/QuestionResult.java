package eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult;

import java.util.ArrayList;

public class QuestionResult
{
    private int id;

    private String questiontext;
    private String displaytype;
    private ArrayList<AnswerOptionResult> answeroptionsresult = new ArrayList<AnswerOptionResult>();
    private String description;
    private int answeredcount;
    
    public int getAnsweredCount()
    {
	return answeredcount;
    }
    
    public void setAnsweredCount( int answeredCount )
    {
	this.answeredcount = answeredCount;
    }
    
    public int getId()
    {
	return id;
    }
    
    public void setId( int id )
    {
	this.id = id;
    }
    
    public String getQuestiontext()
    {
	return questiontext;
    }
    
    public void setQuestiontext( String questiontext )
    {
	this.questiontext = questiontext;
    }
    
    public String getDisplaytype()
    {
	return displaytype;
    }
    
    public void setDisplaytype( String displaytype )
    {
	this.displaytype = displaytype;
    }
    
    public ArrayList<AnswerOptionResult> getAnsweroptions()
    {
	if(answeroptionsresult == null)
	    return new ArrayList<AnswerOptionResult>();
	return answeroptionsresult;
    }
    
    public void setAnsweroptions( ArrayList<AnswerOptionResult> answeroptions )
    {
	this.answeroptionsresult = answeroptions;
    }
    
    public String getDescription()
    {
	return description;
    }
    
    public void setDescription( String description )
    {
	this.description = description;
    }
}
