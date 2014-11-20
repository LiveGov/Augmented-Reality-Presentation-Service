package eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult;

import java.util.ArrayList;

public class QuestionaireResult
{
    private ArrayList<CategoryResult> categoriesresult = new ArrayList<CategoryResult>();
    private int id;
    private String code;
    private String objectcode;
    private String description;
    private String usermessage;
    private int answeredcount;
    
    public QuestionaireResult()
    {
	categoriesresult = new ArrayList<CategoryResult>();
    }
    
    public boolean CategoryWithIdExists( int id )
    {
	return false;
    }
    
    public int getAnsweredCount()
    {
	return answeredcount;
    }
    
    public void setAnsweredCount( int answeredCount )
    {
	this.answeredcount = answeredCount;
    }
    
    public String getCode()
    {
	return code;
    }
    
    public void setCode( String code )
    {
	this.code = code;
    }
    
    public String getObjectCode()
    {
	return objectcode;
    }
    
    public void setObjectCode( String objectCode )
    {
	this.objectcode = objectCode;
    }
    
    public int getId()
    {
	return id;
    }
    
    public void setId( int id )
    {
	this.id = id;
    }
    
    public String getDescription()
    {
	return description;
    }
    
    public void setDescription( String description )
    {
	this.description = description;
    }
    
    public ArrayList<CategoryResult> getCategories()
    {
	if ( categoriesresult == null )
	    return new ArrayList<CategoryResult>();
	return categoriesresult;
    }
    
    public void setCategories( ArrayList<CategoryResult> categories )
    {
	this.categoriesresult = categories;
    }

    public String getUsermessage()
    {
	return usermessage;
    }

    public void setUsermessage( String usermessage )
    {
	this.usermessage = usermessage;
    }
    
}
