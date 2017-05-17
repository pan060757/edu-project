package entityrank.entity;

public class Sentence extends Node {

	private String sentenceStr;

	public Sentence(String sentenceStr) {
		super();
		this.sentenceStr = sentenceStr;
	}

	public String getSentenceStr() {
		return sentenceStr;
	}

	public void setSentenceStr(String sentenceStr) {
		this.sentenceStr = sentenceStr;
	}
	
}
