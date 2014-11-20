package eu.liveGov.libraries.livegovtoolkit.objects.questionaire;

public class AnswerOption {
	private int id;
	private String answeroptiontext;
	private boolean selected;
	
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
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
