package eu.liveGov.libraries.livegovtoolkit.interfaces;

import eu.liveGov.libraries.livegovtoolkit.objects.questionaire.Questionaire;
import eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult.QuestionaireResult;

public interface QuestionaireListener
{
    public void sumbitButtonClicked(Questionaire questionaire);
    public void questionaireSendtoServerUpdated( boolean successful);
    public void questionaireUpdated(Questionaire questionaire);
    public void questionaireResultUpdated(QuestionaireResult questionaireResult);
    public void questionaireBothUpdated( Questionaire q, QuestionaireResult qr );
    
}
