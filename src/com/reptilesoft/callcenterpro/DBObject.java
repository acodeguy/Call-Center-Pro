package com.reptilesoft.callcenterpro;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBObject extends SQLiteOpenHelper {
	
	private final String TAG = "DBObject.java";
	
	private static final String DB_NAME = "callcenter.db";
	private static final String TBL_PROFILE = "tbl_profile";
	private static final String TBL_BLACKLIST = "tbl_blacklist";
	private static final String TBL_SETTING = "tbl_settings";
	private static final String TBL_TIMED_PROFILE = "tbl_timed_profiles";
	private static final int DB_VER = 10;
	
	/******* DB QUERIES *******/
	private static final String QRY_CREATE_TBL_PROFILE = "create table " + TBL_PROFILE
		+ "(_id integer primary key autoincrement, "
		+ "name text not null, "
		+ "ringer_mode integer, "
		+ "ringer_vol integer, "
		+ "ringer_vib integer, "
		+ "notif_vol integer, "
		+ "notif_vib integer, "
		+ "alarm_vol integer, "
		+ "system_vol integer, "
		+ "media_vol integer, "
		+ "voice_vol integer, "
		+ "brightness integer, "
		+ "custom_brightness integer, "
		+ "ringtone text, "
		+ "notification text, "
		+ "divert_calls_to_vm integer, "
		+ "airplane_mode integer, " // airplane mode will override below wireless settings...
		+ "wifi integer, "
		+ "bluetooth integer, "
		+ "data integer);";
	
	private static final String QRY_DELETE_PROFILE = "delete from " + TBL_PROFILE + " WHERE _id=";
	
	/******* CONSTRUCTOR ******/
	public DBObject(Context context) {
		super(context, DB_NAME, null, DB_VER);
		Log.d(TAG, "Created: " + DB_NAME);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(QRY_CREATE_TBL_PROFILE);
		db.execSQL("CREATE TABLE " + TBL_TIMED_PROFILE + "(_id integer primary key autoincrement," +
				"active integer," +
				"profileId integer not null," + // id of profile to activate at time
				"activationHour integer not null," +
				"activationMinute integer not null," +
				"Mo integer," +
				"Tu integer," +
				"We integer," +
				"Th integer," +
				"Fr integer," +
				"Sa integer," +
				"Su integer);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
		
		Log.d(TAG, "onUpgrade(" + db + ", " + old_version + ", " + new_version + ", dropping all tables...");
		db.execSQL("drop table if exists " + TBL_PROFILE);
		db.execSQL("drop table if exists " + TBL_TIMED_PROFILE);
		//db.execSQL("drop table " + TBL_SETTINGS);
		onCreate(db); // create the new db
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		//Log.d(TAG, "onOpen(" + db + ")");
	}
	
	/****** OBJECT FUNCTIONS *******/
	
	public void createTimedProfile(int idToAdd, int activationHour, int activationMinute, boolean[] onWhichDays) {
		/* add a new timed profile to db */
		
		int Mo=0,Tu=0,We=0,Th=0,Fr=0,Sa=0,Su=0;
		// which days?
		if(onWhichDays[0]==true) // monday
			Mo = 1;
		if(onWhichDays[1]==true) // tu
			Tu = 1;
		if(onWhichDays[2]==true) // we
			We = 1;
		if(onWhichDays[3]==true) // th
			Th = 1;
		if(onWhichDays[4]==true) // fr
			Fr = 1;
		if(onWhichDays[5]==true) // sa
			Sa = 1;
		if(onWhichDays[6]==true) // sun
			Su = 1;
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("insert into " + TBL_TIMED_PROFILE + "(profileId,activationHour,activationMinute,Mo,Tu,We,Th,Fr,Sa,Su) VALUES(" +
				idToAdd + "," +
				activationHour + "," +
				activationMinute + "," +
				Mo + "," +
				Tu + "," +
				We + "," +
				Th + "," +
				Fr + "," +
				Sa + "," +
				Su + ");");
				
	}
	
	public void deleteTimedProfile(int idToDelete) {
		/* delete a timed profile */
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TBL_TIMED_PROFILE + " WHERE _id=" + idToDelete);
	}
	
	public void updateTimedProfile(int idToDelete, long activationTime, String[] onWhichDays) {
		/* delete a timed profile */
	}
	
	public Cursor getTimedProfileList() {
		// get all profiles currently stored in db
		
		SQLiteDatabase db = this.getReadableDatabase();
		// make the query
		Cursor cursor = db.query(TBL_TIMED_PROFILE,
				null,
				null,
				null,
				null,
				null,
				null);
		
		//db.close();
		return cursor;
	}
	
	public Cursor getTimedProfileInfo(int profileId) {
		
		/* pull out specifics */
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TBL_TIMED_PROFILE,
				null,
				"_id=" + profileId,
				null,
				null,
				null,
				null);
		
		return cursor;
		
	}
	
	public Cursor getPhoneProfileList() {
		// get all profiles currently stored in db
		Log.d(TAG, "getPhoneProfileList()");
		
		String SORT = "name asc"; // for ordering in SQL query
		
		SQLiteDatabase db = this.getReadableDatabase();
		// make the query
		Cursor cursor = db.query(TBL_PROFILE,
				null,
				null,
				null,
				null,
				null,
				SORT);
		
		Log.d(TAG, "getPhoneProfileList() returning cursor");
		
		//db.close();
		return cursor;
	}
	
	public void createProfile(
			String name,
			int ringer_mode,
			int ringer_vol,
			int ringer_vib,
			int notif_vol,
			int notif_vib,
			int media_vol,
			int alarm_vol,
			int system_vol,
			int voice_vol,
			String ringtone,
			String notification,
			int brightness,
			int custom_brightness,
			int divert_calls_to_vm,
			int airplane,
			int wifi,
			int bluetooth,
			int data) {
		
		// create a new profile in the database
		
		Log.d(TAG, "createProfile()");
		
		Log.d(TAG, "NAME: " + name);
		Log.d(TAG, "MODE: " + ringer_mode);
		Log.d(TAG, "RVOL: " + ringer_vol);
		Log.d(TAG, "MVOL: " + media_vol);
		Log.d(TAG, "NVOL: " + notif_vol);
		Log.d(TAG, "VVOL: " + voice_vol);
		Log.d(TAG, "SVOL: " + system_vol);
		Log.d(TAG, "AVOL: " + alarm_vol);
		Log.d(TAG, "RINGTONE: " + ringtone);
		Log.d(TAG, "NOTIFICATION: " + notification);
		Log.d(TAG, "BRIGHT: " + brightness);
		Log.d(TAG, "DIVERT: " + divert_calls_to_vm);
		Log.d(TAG, "AIR: " + airplane);
		Log.d(TAG, "WIFI: " + wifi);
		Log.d(TAG, "BLUE: " + bluetooth);
		Log.d(TAG, "DATA: " + data);
		
		final String QRY_INSERT = "insert into " + TBL_PROFILE
			+ "(name," 
			+ " ringer_mode, "
			+ "ringer_vol, "
			+ "ringer_vib, "
			+ "notif_vol, "
			+ "notif_vib, "
			+ "media_vol, "
			+ "alarm_vol, "
			+ "system_vol, "
			+ "voice_vol, "
			+ "ringtone, "
			+ "notification, "
			+ "brightness, "
			+ "custom_brightness, "
			+ "divert_calls_to_vm, "
			+ "airplane_mode, "
			+ "wifi, "
			+ "bluetooth, "
			+ "data)"
			/******* VALUES ******/
			+ "values('"
			+ name + "', "
			+ ringer_mode + ", "
			+ ringer_vol + ", "
			+ ringer_vib + ", "
			+ notif_vol + ", "
			+ notif_vib + ", "
			+ media_vol + ", "
			+ alarm_vol + ", "
			+ system_vol + ", "
			+ voice_vol + ", "
			+ "'" + ringtone + "', "
			+ "'" + notification + "', "
			+ brightness + ", "
			+ custom_brightness + ", "
			+ divert_calls_to_vm + ", "
			+ airplane + ", "
			+ wifi + ", "
			+ bluetooth + ", "
			+ data + ");";
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(QRY_INSERT);
		Log.d(TAG, "QUERY: " + QRY_INSERT);
		//db.close();
	}
	
	public void deleteProfile(int id) {
		
		Log.d(TAG, "deleteProfile(" + id +")");
		// delete profile from db
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(QRY_DELETE_PROFILE + id);
		//db.close();
	}
	
	public Cursor getProfile(int id) {
		
		Log.d(TAG, "getProfile(" + id + ")");
		
		// get profile and return cursor
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TBL_PROFILE,
				null,
				"_id=" + id,
				null,
				null,
				null,
				null);
		
		Log.d(TAG, "getProfile() returning cursor for ID #" + id);
		//db.close();
		
		return cursor;
		}
	
	public void updateProfile(
			int id,
			String name,
			int ringer_mode,
			int ringer_vol,
			int ringer_vib,
			int notif_vol,
			int notif_vib,
			int media_vol,
			int alarm_vol,
			int system_vol,
			int voice_vol,
			String ringtone,
			String notification,
			int brightness,
			int custom_brightness,
			int divert_calls_to_vm,
			int airplane,
			int wifi,
			int bluetooth,
			int data) {
		
		String QRY_UPDATE = "UPDATE " + TBL_PROFILE
			+ " SET name = '" + name
			+ "', ringer_mode = " + ringer_mode
			+ ", ringer_vol = " + ringer_vol
			+ ", ringer_vib = " + ringer_vib
			+ ", notif_vol = " + notif_vol
			+ ", notif_vib = " + notif_vib
			+ ", media_vol = " + media_vol
			+ ", alarm_vol = " + alarm_vol
			+ ", system_vol = " + system_vol
			+ ", voice_vol = " + voice_vol
			+ ", ringtone = '" + ringtone + "'"
			+ ", notification = '" + notification + "'"
			+ ", brightness = " + brightness
			+ ", custom_brightness = " + custom_brightness
			+ ", divert_calls_to_vm = " + divert_calls_to_vm
			+ ", airplane_mode = " + airplane
			+ ", wifi = " + wifi
			+ ", bluetooth = " + bluetooth
			+ ", data = " + data + " WHERE _id = " + id + ";";
		
		Log.d(TAG, "updateProfile(" + QRY_UPDATE + ")");
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(QRY_UPDATE);
		//db.close();
	}
}