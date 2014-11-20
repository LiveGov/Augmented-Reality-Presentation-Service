package eu.liveGov.libraries.livegovtoolkit.objects.questionaire;

import java.util.ArrayList;

public class Category
{
    private int id;
    private String name;
    private String description;
    
    private ArrayList<Question> questions;
    
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
    
    public ArrayList<Question> getQuestions()
    {
	if ( questions == null )
	    return new ArrayList<Question>();
	return questions;
    }
    
    public void setQuestions( ArrayList<Question> questions )
    {
	this.questions = questions;
    }
    
    public boolean isFilledIn()
    {
	for(Question question : questions)
	{
	    if( !question.isFilledIn() )
		return false;
	}
	return true;
    }
    
    public boolean isPartlyFilledIn()
    {
	for(Question question : questions)
	{
	    if( question.isPartlyFilledIn() )
		return true;
	}
	return false;
    }
    
}
