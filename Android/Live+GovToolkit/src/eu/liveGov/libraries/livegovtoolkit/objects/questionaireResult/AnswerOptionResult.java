package eu.liveGov.libraries.livegovtoolkit.objects.questionaireResult;

public class AnswerOptionResult {
	private int id;
	private String answeroptiontext;
	private int answeredcount;
	
	public int getAnsweredCount() {
		return answeredcount;
	}

	public void setAnsweredCount(int answeredCount) {
		this.answeredcount = answeredCount;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAnsweroptiontext() {
		return answeroptiontext;
	}
	public void setAnsweroptiontext(String answeroptiontext) {
		this.answeroptiontext = answeroptiontext;
	}
}
