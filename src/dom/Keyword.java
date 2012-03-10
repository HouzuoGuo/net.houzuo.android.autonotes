package dom;

public final class Keyword {
	public Keyword(final String word) {
		this.setWord(word);
	}
	
	public String getWord() {
		return this.word;
	}
	
	public void setWord(final String word) {
		this.word = word;
	}
	
	@Override
	public String toString() {
		return this.word;
	}
	
	private String word;
}
