package eu.liveGov.libraries.livegovtoolkit.objects.questionaire;

import java.util.ArrayList;

public class Question
{
    public final static int TYPE_SINGLE_SELECT = 0;
    public final static int TYPE_MULTI_SELECT = 1;
    public final static int TYPE_TEXT_ONLY = 2;
    public final static int TYPE_SINGLE_SELECT_TEXT = 3;
    public final static int TYPE_MULTI_SELECT_TEXT = 4;
    
    private int id;
    private String questiontext;
    private int displaytype = -1;
    private ArrayList<AnswerOption> answeroptions;
    private String description;
    private String textfieldhint;
    private String comment;
    
    public String getComment()
    {
	return comment;
    }
    
    public void setComment( String comment )
    {
	this.comment = comment;
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
    
    public int getDisplaytype()
    {
	return displaytype;
    }
    
    public void setDisplaytype( int displaytype )
    {
	this.displaytype = displaytype;
    }
    
    public ArrayList<AnswerOption> getAnsweroptions()
    {
	if ( answeroptions == null )
	    return new ArrayList<AnswerOption>();
	return answeroptions;
    }
    
    public void setAnsweroptions( ArrayList<AnswerOption> answeroptions )
    {
	this.answeroptions = answeroptions;
    }
    
    public String getDescription()
    {
	return description;
    }
    
    public void setDescription( String description )
    {
	this.description = description;
    }
    
    public void setAnsweroptionIdChecked( int id )
    {
	for ( AnswerOption answerOption : answeroptions )
	{
	    if ( answerOption.getId() == id )
	    {
		answerOption.setSelected( true );
	    } else
	    {
		answerOption.setSelected( false );
	    }
	}
    }
    
    public AnswerOption findAnsweroptionId( int id )
    {
	for ( AnswerOption answerOption : answeroptions )
	{
	    if ( answerOption.getId() == id )
	    {
		return answerOption;
	    }
	}
	return null;
    }
    
    public String getTextfieldhint()
    {
	return textfieldhint;
    }
    
    public void setTextfieldhint( String textfieldhint )
    {
	this.textfieldhint = textfieldhint;
    }
    
    public boolean isFilledIn()
    {
	if ( answeroptions != null )
	{
	    for ( AnswerOption answerOption : answeroptions )
	    {
		if ( answerOption.isSelected() )
		{
		    return true;
		}
	    }
	}
	return (comment != null && !comment.isEmpty());
    }
    
    public boolean isPartlyFilledIn()
    {
	return isFilledIn();
    }
    
}
