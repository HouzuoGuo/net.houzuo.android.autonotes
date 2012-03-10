package dom;

public final class Day {
	public Day(final long dayID, final String date) {
		this.setDate(date);
		this.setDayID(dayID);
	}
	
	public String getDate() {
		return this.date;
	}
	
	public long getDayID() {
		return this.dayID;
	}
	
	public void setDate(final String date) {
		this.date = date;
	}
	
	public void setDayID(final long dayID) {
		this.dayID = dayID;
	}
	
	@Override
	public String toString() {
		return this.date;
	}
	
	private String date;
	private long dayID;
}
