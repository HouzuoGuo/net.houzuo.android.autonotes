package dom;

public final class Note {
	public Note(final long noteID, final Day day, final String content,
					final String time) {
		this.setNoteID(noteID);
		this.setDay(day);
		this.setContent(content);
		this.setTime(time);
	}
	
	public String getBrief() {
		if (this.getContent().length() > 25) {
			return this.getContent().substring(0, 25) + "..";
		}
		return this.getContent();
	}
	
	public String getContent() {
		return this.content;
	}
	
	public Day getDay() {
		return this.day;
	}
	
	public String getLongBrief() {
		if (this.getContent().length() > 40) {
			return this.getContent().substring(0, 40) + "..";
		}
		return this.getContent();
	}
	
	public long getNoteID() {
		return this.noteID;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public void setContent(final String content) {
		this.content = content;
	}
	
	public void setDay(final Day day) {
		this.day = day;
	}
	
	public void setNoteID(final long noteID) {
		this.noteID = noteID;
	}
	
	public void setTime(final String time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return this.time + "  " + this.content;
	}
	
	private String content, time;
	private Day day;
	private long noteID;
}
