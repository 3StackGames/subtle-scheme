package com.three_stack.subtle_scheme;

public class Question extends Belief {
	
	private String question;
	private String answer;
	
	public Question(String question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}