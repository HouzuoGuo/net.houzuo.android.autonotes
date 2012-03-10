package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DBHelper extends SQLiteOpenHelper {
	
	public DBHelper(final Context context) {
		super(context, DBHelper.NAME, null, DBHelper.VERSION);
	}
	
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(DBHelper.CREATE_KEYWORD);
		db.execSQL(DBHelper.CREATE_DAY);
		db.execSQL(DBHelper.CREATE_NOTE);
	}
	
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
					final int newVersion) {
		return;
	}
	
	static {
		CREATE_KEYWORD = "CREATE TABLE KEYWORD (" + "WORD TEXT PRIMARY KEY" + ");";
		CREATE_DAY = "CREATE TABLE DAY ("
						+ "DAY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "DATE TEXT NOT NULL" + ");";
		CREATE_NOTE = "CREATE TABLE NOTE ("
						+ "NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT," + "DAY_ID INTEGER,"
						+ "TIME TEXT NOT NULL," + "CONTENT TEXT NOT NULL,"
						+ "FOREIGN KEY (DAY_ID) REFERENCES DAY(DAY_ID)" + ");";
	}
	public final static String CREATE_KEYWORD, CREATE_DAY, CREATE_NOTE;
	public final static String NAME = "AUTONOTES";
	public final static int VERSION = 1;
}
