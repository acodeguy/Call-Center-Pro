package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class Activator extends Activity {

	final String TAG = "Activator";
	
	public void onCreate(Bundle bundle) {
		
		super.onCreate(bundle);
		
		setContentView(R.layout.activator);
		
		// get extras
		Intent intent = getIntent();
		int profileId = intent.getIntExtra("profileId", 0); 
		Log.i(TAG, "ProfileId = " + profileId);
		
		// get settings from db
		String profileName = "profile";
		int ringerVolume = 0, notificationVolume = 0, systemVolume = 0, voiceVolume = 0, mediaVolume = 0,
		alarmVolume = 0, ringerMode = 0, brightnessLevel = 0, customBrightness = 0, airplaneMode = 0,
		wifiMode = 0;
		String ringtone, notification;
		DBObject dbo = new DBObject(com.reptilesoft.callcenterpro.CallCenter.context);
		Cursor cursor = dbo.getProfile(profileId);
		
		if(cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			// get name
			profileName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
			// get ringer mode
			ringerMode = cursor.getInt(cursor.getColumnIndexOrThrow("ringer_mode"));
			// get volumes
			ringerVolume = cursor.getInt(cursor.getColumnIndexOrThrow("ringer_vol"));
			notificationVolume = cursor.getInt(cursor.getColumnIndexOrThrow("notif_vol"));
			systemVolume = cursor.getInt(cursor.getColumnIndexOrThrow("system_vol"));
			voiceVolume = cursor.getInt(cursor.getColumnIndexOrThrow("voice_vol"));
			mediaVolume = cursor.getInt(cursor.getColumnIndexOrThrow("media_vol"));
			alarmVolume = cursor.getInt(cursor.getColumnIndexOrThrow("alarm_vol"));
			// brightness
			customBrightness = cursor.getInt(cursor.getColumnIndexOrThrow("custom_brightness"));
			brightnessLevel = cursor.getInt(cursor.getColumnIndexOrThrow("brightness"));
			// data, wifi, etc
			wifiMode = cursor.getInt(cursor.getColumnIndexOrThrow("wifi"));
			airplaneMode = cursor.getInt(cursor.getColumnIndexOrThrow("airplane_mode"));
		}
		cursor.close();
		dbo.close();
		
		// get services
		AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		// set values
		audioManager.setStreamVolume(AudioManager.STREAM_RING, ringerVolume, AudioManager.FLAG_SHOW_UI); // ringer vol
		audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, ringerVolume, 0); // notif vol
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, ringerVolume, 0); // system vol
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, ringerVolume, 0); // voice vol
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ringerVolume, 0); // media vol
		audioManager.setStreamVolume(AudioManager.STREAM_ALARM, ringerVolume, 0); // alarm vol
		// set wifi, data, etc
		if(wifiMode == 1)
			wifiManager.setWifiEnabled(true);
		else
			wifiManager.setWifiEnabled(false);
		if(airplaneMode == 1)
		{
			Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			i.putExtra("airplane mode", airplaneMode);
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, airplaneMode);
			sendBroadcast(i);
		}
		else
		{
			Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			i.putExtra("airplane mode", airplaneMode);
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, airplaneMode);
			sendBroadcast(i);
		}
		
		// finish app
		Log.i(TAG,"Activator finished, exiting...");
		finish();
	}
}
