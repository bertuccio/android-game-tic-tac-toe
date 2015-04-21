package es.uam.eps.dadm.adrian_lorenzo.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

	private static final String ID = "id";
	private static final String NAME = "username";
	private static final String PASSWORD = "password";
	private static final String UUID = "uuid";

	private static final String PARTIDA = "partida";
	private static final String DURACION = "duracion";
	private static final String NPIEZAS = "npiezas";
	private static final String FECHA = "fecha";

	private static final String DATABASE_NAME = "ccc.db";
	private static final String TABLE_USERS = "users";
	private static final String TABLE_ROUNDS = "rounds";
	private static final String TABLE_SCORES = "scores";
	private static final String SCORE = "score";
	private static final String TABLE_GAME = "game";
	private static final int DATABASE_VERSION = 1;

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public DatabaseAdapter(Context context) {
		helper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db);

		}

		public void createTable(SQLiteDatabase db) {

			String str1 = "CREATE TABLE " + TABLE_USERS + " (" + ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
					+ " TEXT UNIQUE, " + PASSWORD + " TEXT, "+ UUID
					+ " TEXT );";

			String str2 = "CREATE TABLE " + TABLE_GAME + " (" + ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
					+ " TEXT );";

			String str3 = "CREATE TABLE " + TABLE_SCORES + " (" + ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " + NAME
					+ " TEXT , " + SCORE
					+ " INTEGER);";

			try {
				db.execSQL(str1);
				db.execSQL(str2);
				db.execSQL(str3);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}
	}

	public DatabaseAdapter open() throws SQLException {
		db = helper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public long insertUser(String username, String password) {
		ContentValues values = new ContentValues();
		String uuid = null;
		values.put(NAME, username);
		values.put(PASSWORD, password);
		values.put(UUID, uuid);
		return db.insert(TABLE_USERS, null, values);
	}
	
	public long insertUser(String username, String password, String uuid) {
		ContentValues values = new ContentValues();

		values.put(NAME, username);
		values.put(PASSWORD, password);
		values.put(UUID, uuid);
		return db.insert(TABLE_USERS, null, values);
	}

	public boolean deleteUser(long id) {
		db.delete(TABLE_ROUNDS, ID + "=" + id, null);
		return db.delete(TABLE_USERS, ID + "=" + id, null) > 0;
	}

	public Cursor getAllUsers() {
		return db.query(TABLE_USERS, new String[] { NAME }, null, null, null,
				null, null);
	}

	public boolean isRegistered(String username, String password) {
		Cursor cursor = db.query(TABLE_USERS, new String[] { NAME, PASSWORD },
				NAME + " ='" + username + "' AND " + PASSWORD + "= '"
						+ password + "'", null, null, null, NAME + " DESC");
		int count = cursor.getCount();
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		if (count == 0)
			return false;
		else
			return true;
	}

	public long insertGame(String username) {

		ContentValues values = new ContentValues();
		values.put(NAME, username);
		return db.insert(TABLE_GAME, null, values);
	}

	public long insertWinner(String username) {

		ContentValues values = new ContentValues();
		values.put(NAME, username);
		values.put(SCORE, 1);
		return db.insert(TABLE_SCORES, null, values);
	}

	public long insertLooser(String username) {

		ContentValues values = new ContentValues();
		values.put(NAME, username);
		values.put(SCORE, -1);
		return db.insert(TABLE_SCORES, null, values);
	}

	public Cursor getNumPartidas() {

		return db.rawQuery("SELECT COUNT(*) FROM game", null);
	}
	
	public String getUserUuid(String username) {
		
		Cursor cursor = db.rawQuery("SELECT "+ UUID +" FROM "+ TABLE_USERS + 
				" WHERE " + NAME + " ='"+username+"'", null);
		if(cursor.moveToFirst()){
			return cursor.getString(cursor.getColumnIndex(UUID));
		}
		else{
			return null;
		}
		
	}
	

	public Cursor getWinsScores(String username) {

		return db.rawQuery("SELECT COUNT(" + SCORE + ") asd FROM "
				+ TABLE_SCORES + " WHERE " + SCORE + "=1 AND " + NAME + "='"
				+ username + "'", null);

	}

	public Cursor getDefeatsScores(String username) {

		return db.rawQuery("SELECT COUNT(" + SCORE + ") asd FROM "
				+ TABLE_SCORES + " WHERE " + SCORE + "=-1 AND " + NAME + "= '"
				+ username + "'", null);

	}

	public static boolean check(String username, String password,
			Activity activity) {

		DatabaseAdapter db = new DatabaseAdapter(activity);
		db.open();
		boolean in = db.isRegistered(username, password);
		db.close();
		if (in) {

			return true;
		} else {

			return false;
		}

	}

}
