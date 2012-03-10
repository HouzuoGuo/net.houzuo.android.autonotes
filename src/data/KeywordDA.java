package data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import dom.Keyword;

public final class KeywordDA {
	public KeywordDA(final Context context) {
		this.dbHelper = new DBHelper(context);
	}
	
	public List<Keyword> all() {
		final List<Keyword> keywords = new ArrayList<Keyword>();
		final Cursor cursor = this.db.query("KEYWORD", this.columns, null, null,
						null, null, "WORD");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			keywords.add(KeywordDA.toKeyword(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return keywords;
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public Keyword create(final String word) {
		final ContentValues values = new ContentValues();
		values.put("WORD", word);
		this.db.insert("KEYWORD", null, values);
		return new Keyword(word);
	}
	
	public void delete(final Keyword keyword) {
		final SQLiteStatement s = this.db
						.compileStatement("DELETE FROM KEYWORD WHERE WORD = ?");
		s.bindString(1, keyword.getWord());
		s.execute();
	}
	
	public void open() throws SQLException {
		this.db = this.dbHelper.getWritableDatabase();
	}
	
	private static Keyword toKeyword(final Cursor cursor) {
		return new Keyword(cursor.getString(0));
	}
	
	private final String[] columns = { "WORD" };
	private SQLiteDatabase db;
	private final DBHelper dbHelper;
}
