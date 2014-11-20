package eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult;

import java.util.ArrayList;

public class CategoryResult
{
    private int id;
    private String name;
    private String description;
    private ArrayList<QuestionResult> questionsresult = new ArrayList<QuestionResult>();
    private int answeredcount;
    
    public int getAnsweredCount()
    {
	return answeredcount;
    }
    
    public void setAnsweredCount( int filledInCount )
    {
	this.answeredcount = filledInCount;
    }
    
    public int getId()
    {
	return id;
    }
    
    public void setId( int id )
    {
	this.id = id;
    }
    
    public String getName()
    {
	return name;
    }
    
    public void setName( String name )
    {
	this.name = name;
    }
    
    public String getDescription()
    {
	return description;
    }
    
    public void setDescription( String description )
    {
	this.description = description;
    }
    
    public ArrayList<QuestionResult> getQuestions()
    {
	if ( questionsresult == null )
	    return new ArrayList<QuestionResult>();
	return questionsresult;
    }
    
    public void setQuestions( ArrayList<QuestionResult> questions )
    {
	this.questionsresult = questions;
    }
    
}
