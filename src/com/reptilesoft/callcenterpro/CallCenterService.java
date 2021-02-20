package com.reptilesoft.callcenterpro;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CallCenterService extends Service {

	private final String TAG = "AppService";
		
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		Log.i(TAG, "service up");
		
		addTimers();
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	public void addTimers() {
		
		/* scan db for profiles and add timers to alarm */
		Timer[] t;
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		DBObject dbo = new DBObject(this);
		int i = 0;
		int[] profileId, activationProfileId;
		String[] timeToActivate, profileName;
		long[] activationTime = null;
		boolean[][] onWhichDays;
		
		Cursor cursor = dbo.getTimedProfileList();
		
		if(cursor.getCount() > 0)
		{
			Log.i(TAG,"count = " + cursor.getCount());
			
			activationProfileId = new int[cursor.getCount()];
			timeToActivate = new String[cursor.getCount()];
			onWhichDays = new boolean[cursor.getCount()][6];
			profileId = new int[cursor.getCount()];
			t = new Timer[cursor.getCount()];
			activationTime = new long[cursor.getCount()];
			
			cursor.moveToFirst(); 
			do {
				// get id, name, etc
				activationProfileId[i] = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				profileId[i] = cursor.getInt(cursor.getColumnIndexOrThrow("profileId"));
				activationTime[i] = cursor.getLong(cursor.getColumnIndexOrThrow("activationTime"));
				
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Mo"))==1)
					onWhichDays[i][0]=true;
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Tu"))==1)
					onWhichDays[i][1]=true;
				if(cursor.getInt(cursor.getColumnIndexOrThrow("We"))==1)
					onWhichDays[i][2]=true;
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Th"))==1)
					onWhichDays[i][3]=true;
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Fr"))==1)
					onWhichDays[i][4]=true;
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Sa"))==1)
					onWhichDays[i][5]=true;
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Su"))==1)
					onWhichDays[i][6]=true;
				
				// set timers
				
			}while(cursor.moveToNext());
		}
	}

}