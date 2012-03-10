package data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import dom.Day;

public final class DayDA {
	
	public DayDA(final Context context) {
		this.dbHelper = new DBHelper(context);
	}
	
	public List<Day> all() {
		final List<Day> notes = new ArrayList<Day>();
		final Cursor cursor = this.db.query("DAY", this.columns, null, null, null,
						null, "DAY_ID DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			notes.add(DayDA.toDay(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return notes;
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public Day create(final String date) {
		final ContentValues values = new ContentValues();
		values.put("DATE", date);
		final long lastID = this.db.insert("DAY", null, values);
		return new Day(lastID, date);
	}
	
	public void delete(final Day day) {
		this.db.delete("DAY", "DAY_ID =" + day.getDayID(), null);
		this.db.delete("NOTE", "DAY_ID = " + day.getDayID(), null);
	}
	
	public Day findById(final int id) {
		final Cursor cursor = this.db.query("DAY", this.columns, "DAY_ID = " + id,
						null, null, null, null);
		cursor.moveToFirst();
		Day day = null;
		if (!cursor.isAfterLast()) {
			day = DayDA.toDay(cursor);
		}
		cursor.close();
		if (day == null) {
			day = new Day(0, "");
		}
		return day;
	}
	
	public Day findDate(final String date) {
		final Cursor cursor = this.db.query("DAY", this.columns, "DATE = '" + date
						+ "'", null, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			return DayDA.toDay(cursor);
		}
		cursor.close();
		return null;
	}
	
	public void open() throws SQLException {
		this.db = this.dbHelper.getWritableDatabase();
	}
	
	private static Day toDay(final Cursor cursor) {
		return new Day(cursor.getLong(0), cursor.getString(1));
	}
	
	private final String[] columns = { "DAY_ID", "DATE" };
	private SQLiteDatabase db;
	private final DBHelper dbHelper;
}
