package eu.liveGov.libraries.livegovtoolkit.objects;

import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;

public class QuestionaireResponseObject
{
    public static final int QUESTIONAIRE_SET = 0;
    public static final int QUESTIONAIRE_RESULT_SET = 1;
    public static final int BOTH_SET = 2;
    
    private int status;
    private Questionaire questionnaire;
    private QuestionaireResult questionnaireresult;
    
    public int getStatus()
    {
        return status;
    }
    public void setStatus( int status )
    {
        this.status = status;
    }
    public Questionaire getQuestionaire()
    {
        return questionnaire;
    }
    public void setQuestionaire( Questionaire questionaire )
    {
        this.questionnaire = questionaire;
    }
    public QuestionaireResult getQuestionaireResult()
    {
    	
        return questionnaireresult;
    }
    public void setQuestionaireResult( QuestionaireResult questionaireResult )
    {
        this.questionnaireresult = questionaireResult;
    }
    
    
    
}
