package com.whyun.message.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whyun.message.bean.KeyInfo;
import com.whyun.message.key.KeyMap;
import com.whyun.util.MyLog;

public class KeyTableOperator {
	private static final String KEY_TABLE_NAME = "key_table";
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "keydb";
	private static final MyLog logger = MyLog.getLogger(KeyTableOperator.class);
	
	public static final String KEY_TABLE_ID = "id";
	public static final String KEY_TABLE_UP_FIELD = "up_";
	public static final String KEY_TABLE_DOWN_FIELD = "down_";
	public static final String KEY_TABLE_LEFT_FIELD = "left_";
	public static final String KEY_TABLE_RIGHT_FIELD = "right_";
	public static final String KEY_TABLE_X_FIELD = "x_";
	public static final String KEY_TABLE_Y_FIELD = "y_";
	public static final String KEY_TABLE_A_FIELD = "a_";
	public static final String KEY_TABLE_B_FIELD= "b_";
	public static final String KEY_TABLE_START_FIELD = "start_";
	public static final String KEY_TABLE_SELECT_FIELD = "select_";
	
	public static final String KEY_TABLE_FIELD_NAME = "name";
	
	private KeyDBHelper dbHelper;
	
	
	public KeyTableOperator(Context context) {
		if (context != null) {
			dbHelper = new KeyDBHelper(context);
		} else {
			
		}
	}
	
	public boolean addKey(String name, ContentValues values) {
		boolean result = false;
		logger.debug(values.toString());
		if (dbHelper != null) {

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			result = db.insert(KEY_TABLE_NAME, null, values) != -1;
			db.close();
		} else {
			
		}
		
		return result;
	}
	
	public boolean updateKey(int id,ContentValues values) {
		boolean result = false;
		if (dbHelper != null) {
			
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			result = db.update(KEY_TABLE_NAME, values, "id=?", new String[]{id+""}) >= 0;
			db.close();
		} else {
			
		}
		return result;
	}
	
	public boolean deleteKey(int id) {
		boolean result = false;
		if (dbHelper != null) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			result = db.delete(KEY_TABLE_NAME, "id=?", new String[] {id+""}) > 0;
			db.close();
		} else {
			
		}
		return result;
	}
	
	public Cursor getAll() {
		Cursor cursor = null;
		if (dbHelper != null) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			cursor = db.rawQuery("select * from " + KEY_TABLE_NAME,
					null);
//			db.close();
		} else {
			
		}
		return cursor;
	}
	
	public ArrayList<KeyInfo> getAllList() {
		Cursor c = getAll();
		ArrayList<KeyInfo> list = null;
		if (c != null && c.getCount() > 0) {
			list = new ArrayList<KeyInfo>();
			KeyInfo info = new KeyInfo();
			while (c.moveToNext()) {  
				info = cursor2Info(c);
				list.add(info);
			}
		}
		if (c != null) {
			c.close();
		}
		return list;
	}
	
	public Cursor getKeySetting(int id) {
		Cursor cursor = null;
		if (dbHelper != null) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			cursor = db.query(KEY_TABLE_NAME,
					null,"id=?",new String[]{id+""},null,null,null);
			db.close();
		} else {
			
		}
		return cursor;
	}
	
	public KeyInfo getKeySettingInfo(int id) {
		Cursor c = getKeySetting(id);
		KeyInfo info = null;
		
		if (c != null && c.getCount() > 0) {
			info = cursor2Info(c);			
		}	
		if (c != null) {
			c.close();
		}
		return info;
	}
	
	public static KeyInfo cursor2Info(Cursor cursor) {
		KeyInfo info = null;
		if (cursor != null) {
			info = new KeyInfo();
			info.setKeyId(cursor.getInt(
					cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_ID)));
			info.setKeyname(cursor.getString(
					cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_FIELD_NAME)));
			
			info.setUp1(KeyMap.CODE_2_KEY_MAP.get(
				(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_UP_FIELD + "key1"))));
			info.setUp2(KeyMap.CODE_2_KEY_MAP.get(
				(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_UP_FIELD + "key2"))));
			info.setUp3(KeyMap.CODE_2_KEY_MAP.get(
				(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_UP_FIELD + "key3"))));
			
			info.setDown1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_DOWN_FIELD + "key1"))));
			info.setDown2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_DOWN_FIELD + "key2"))));
			info.setDown1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_DOWN_FIELD + "key3"))));
			
			info.setLeft1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_LEFT_FIELD + "key1"))));
			info.setLeft2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_LEFT_FIELD + "key2"))));
			info.setLeft3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_LEFT_FIELD + "key3"))));
			
			info.setRight1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_RIGHT_FIELD + "key1"))));
			info.setRight2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_RIGHT_FIELD + "key2"))));
			info.setRight3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_RIGHT_FIELD + "key3"))));
			
			info.setX1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_X_FIELD + "key1"))));
			info.setX2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_X_FIELD + "key2"))));
			info.setX3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_X_FIELD + "key3"))));
			
			info.setY1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_Y_FIELD + "key1"))));
			info.setY2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_Y_FIELD + "key2"))));
			info.setY3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_Y_FIELD + "key3"))));
			
			info.setA1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_A_FIELD + "key1"))));
			info.setA2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_A_FIELD + "key2"))));
			info.setA3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_A_FIELD + "key3"))));
			
			info.setB1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_B_FIELD + "key1"))));
			info.setB2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_B_FIELD + "key2"))));
			info.setB3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_B_FIELD + "key3"))));
			
			info.setStart1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_START_FIELD + "key1"))));
			info.setStart2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_START_FIELD + "key2"))));
			info.setStart3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_START_FIELD + "key3"))));
			
			info.setSelect1(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_SELECT_FIELD + "key1"))));
			info.setSelect2(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_SELECT_FIELD + "key2"))));
			info.setSelect3(KeyMap.CODE_2_KEY_MAP.get(
					(byte)cursor.getInt(cursor.getColumnIndex(KeyTableOperator.KEY_TABLE_SELECT_FIELD + "key3"))));
			
		}
		return info;
	}
	
	
	
	private static class KeyDBHelper extends SQLiteOpenHelper {
		

		public KeyDBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE \""+KEY_TABLE_NAME+"\" (\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
					+ "\""+KEY_TABLE_FIELD_NAME+"\"  TEXT(20) NOT NULL,"
					+ "\"up_key1\"  INTEGER,"
					+ "\"up_key2\"  INTEGER,"
					+ "\"up_key3\"  INTEGER,"
					+ "\"down_key1\"  INTEGER,"
					+ "\"down_key2\"  INTEGER,"
					+ "\"down_key3\"  INTEGER,"
					+ "\"left_key1\"  INTEGER,"
					+ "\"left_key2\"  INTEGER,"
					+ "\"left_key3\"  INTEGER,"
					+ "\"right_key1\"  INTEGER,"
					+ "\"right_key2\"  INTEGER,"
					+ "\"right_key3\"  INTEGER,"
					+ "\"x_key1\"  INTEGER,"
					+ "\"x_key2\"  INTEGER,"
					+ "\"x_key3\"  INTEGER,"
					+ "\"y_key1\"  INTEGER,"
					+ "\"y_key2\"  INTEGER,"
					+ "\"y_key3\"  INTEGER,"
					+ "\"a_key1\"  INTEGER,"
					+ "\"a_key2\"  INTEGER,"
					+ "\"a_key3\"  INTEGER,"
					+ "\"b_key1\"  INTEGER,"
					+ "\"b_key2\"  INTEGER,"
					+ "\"b_key3\"  INTEGER,"
					+ "\"start_key1\"  INTEGER,"
					+ "\"start_key2\"  INTEGER,"
					+ "\"start_key3\"  INTEGER,"
					+ "\"select_key1\"  INTEGER,"
					+ "\"select_key2\"  INTEGER,"
					+ "\"select_key3\"  INTEGER"
					+ ")";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}

	}
}
