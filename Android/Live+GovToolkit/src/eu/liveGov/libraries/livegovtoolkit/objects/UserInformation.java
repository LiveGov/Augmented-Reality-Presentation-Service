package eu.liveGov.libraries.livegovtoolkit.objects;

import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;

public class UserInformation
{
    
    public final static int UNDEFINED_ID = -1;
    private static Questionaire _questionnaire;
    private int anonymoususerid = UNDEFINED_ID;
    
    public UserInformation()
    {
    } 
    
    public UserInformation( int anonymousUserId )
    {
	setAnonymousUserId( anonymousUserId );
    }
    
    public int getAnonymousUserId()
    {
	return anonymoususerid;
    }
    
    public void setAnonymousUserId( int anonymousUserId )
    {
	this.anonymoususerid = anonymousUserId;
    }
    
    public static void setQuestionaire (Questionaire questionnaire)
    {
	_questionnaire = questionnaire;
    }
    
    public static Questionaire getQuestionaire()
    {
	return _questionnaire;
    }
    
    public boolean isEmpty()
    {
	return (_questionnaire == null || !_questionnaire.isFilledIn());
    }
}
