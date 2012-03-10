package data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import dom.Day;
import dom.Note;

public final class NoteDA {
	
	public NoteDA(final Context context) {
		this.dbHelper = new DBHelper(context);
		this.dayDA = new DayDA(context);
		this.dayDA.open();
	}
	
	public List<Note> allOf(final long dayID) {
		final Cursor cursor = this.db.query("NOTE", this.columns, "DAY_ID = "
						+ dayID, null, null, null, "NOTE_ID DESC");
		final List<Note> notes = new ArrayList<Note>(100);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			notes.add(this.toNote(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return notes;
	}
	
	public void close() {
		this.dbHelper.close();
		this.dayDA.close();
	}
	
	@SuppressWarnings("boxing")
	public Note create(final Day day, final String content, final String time) {
		final ContentValues values = new ContentValues();
		values.put("DAY_ID", day.getDayID());
		values.put("TIME", time);
		values.put("CONTENT", content);
		final long lastID = this.db.insert("NOTE", null, values);
		return new Note(lastID, day, content, time);
	}
	
	public void delete(final Note note) {
		this.db.delete("NOTE", "NOTE_ID =" + note.getNoteID(), null);
	}
	
	public void open() throws SQLException {
		this.db = this.dbHelper.getWritableDatabase();
	}
	
	public List<Note> search(final String condition) {
		final Cursor cursor = this.db.query("NOTE", this.columns, null, null, null,
						null, "NOTE_ID DESC");
		final List<Note> notes = new ArrayList<Note>(100);
		cursor.moveToFirst();
		if (condition.startsWith("/") && condition.endsWith("/")) {
			final String regex = condition.substring(1, condition.length() - 1);
			while (!cursor.isAfterLast()) {
				final Note n = this.toNote(cursor);
				if (n.getContent().matches(regex)) {
					notes.add(this.toNote(cursor));
				}
				cursor.moveToNext();
			}
		} else {
			while (!cursor.isAfterLast()) {
				final Note n = this.toNote(cursor);
				if (n.getContent().toLowerCase().contains(condition)) {
					notes.add(this.toNote(cursor));
				}
				cursor.moveToNext();
			}
		}
		cursor.close();
		return notes;
	}
	
	private Note toNote(final Cursor cursor) {
		return new Note(cursor.getLong(0), this.dayDA.findById(cursor.getInt(1)),
						cursor.getString(3), cursor.getString(2));
	}
	
	private final String[] columns = { "NOTE_ID", "DAY_ID", "TIME", "CONTENT" };
	private final DayDA dayDA;
	private SQLiteDatabase db;
	private final DBHelper dbHelper;
}
