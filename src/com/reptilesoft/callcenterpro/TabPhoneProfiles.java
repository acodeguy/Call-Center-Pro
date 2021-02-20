package com.reptilesoft.callcenterpro;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TabPhoneProfiles extends ListActivity {
	
	private final String TAG = "TabPhoneProfiles.java";
	
	private ListView lv;
	
	int counter = 0;
	private int[] profileID;
	private String[] profileName;
	private int IDpos = 0;
	
	/******* MENU ITEMS *******/
	private final int MENU_ADD = 0, 
		MENU_ABOUT = 1,
		MENU_SETTINGS = 2,
		MENU_EXIT = 3;
	
	/******* USED TO ACIVATE PROFILES ********/
	String profile_name,
		ringtone="ring",
		notification="not";
	
	int ringer_mode,
		ringer_vol,
		notif_vol,
		media_vol,
		system_vol,
		voice_vol,
		alarm_vol,
		brightness,
		custom_brightness,
		airplane_mode,
		network_data,
		bluetooth,
		wifi;
	
	/******* HARDWARE MANAGERS *******/
	private AudioManager am;
	private WifiManager wm;
	//private BluetoothAdapter ba;
	
	/******* NOTIFICATION ID *******/
	public static int ACTIVE_PROFILE_ID = 1;
	
	boolean atLeastOneProfile = false;
	
	private NotificationManager notMgr;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		//setContentView(R.layout.phone_profiles_menu);
		
		initDB();
		setupListView();
		buildProfileList();
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, profileName));
		
		notMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); // notifications
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		refreshScreen();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu m) {
		
		// if the menu button is hit...
		
		m.add(0, MENU_ADD,0,"New profile").setIcon(android.R.drawable.btn_plus);
		//m.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		m.add(0, MENU_ABOUT,0,"About").setIcon(android.R.drawable.ic_menu_info_details);
		//m.add(0, MENU_EXIT, 0, "Exit").setIcon(android.R.drawable.stat_sys_warning);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem mi) {
    	
		Intent i;
		
		switch (mi.getItemId()) {
    	case MENU_ADD:
    		i = new Intent(this, AddProfile.class);
    		startActivity(i);
    		break;
    		
    	case MENU_ABOUT:
    		i = new Intent(this, About.class);
    		startActivity(i);
    		break;
    		
    		case MENU_SETTINGS:
    			startActivity(new Intent(this, Prefs.class));
    			break;
    			
    		case MENU_EXIT:
    			/* cancel all notifications and exit app */
    			notMgr.cancelAll();
    			stopService(new Intent(this, CallCenterService.class)); // stop the listener service
    			finish();
    			break;
    		
    		default: break;
    	}
		
    	return false;
    }
	
	public void setupListView() {
		
		Log.d(TAG, "setupListView()");
		
		lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// for activation, later
				IDpos = position;
				
				final CharSequence[] items = {
						"Activate", 
						"Edit", 
						"Delete"
						};

				AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
				builder.setTitle(profileName[IDpos]);
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	
				    	// get which item was clicked and give options for it;
				    	
				    	switch(item)
				    	{
				    	case 0: //activate
				    		activateProfile(profileID[IDpos]);
				    		finish();
				    		break;
				    	
				    	case 1: // Edit
				    		Intent i = new Intent(getParent(), EditProfile.class);
						    i.putExtra("id", profileID[IDpos]);
						    startActivity(i);
				    		break;
				    	
				    	case 2: // delete
				    		DBObject dbo = new DBObject(getParent());
						    dbo.getWritableDatabase();
						    dbo.deleteProfile(profileID[IDpos]);
						    dbo.close();
						    Toast.makeText(getParent(), profileName[IDpos] + " deleted.", Toast.LENGTH_SHORT).show();
						    refreshScreen();
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}// end function
		});
	}
	
	public void buildProfileList() {
		// return all profiles from db and store in global arrays profileID and profileName
		Log.d(TAG, "buildProfileList()");
		
		DBObject dbo = new DBObject(this);
		dbo.getReadableDatabase();
		
		Cursor cursor = dbo.getPhoneProfileList();
		
		//Log.d(TAG, "continuing to build profile list");
		
		if(cursor.getCount()>0) // if there is data in the cursor object
		{
			counter = 0;
			profileID = new int[cursor.getCount()];
			profileName = new String[cursor.getCount()];
			
			Log.d(TAG, "assigning IDs and names...");
			int idx_id = cursor.getColumnIndexOrThrow("_id");
			int idx_name = cursor.getColumnIndexOrThrow("name");
			
			Log.d(TAG, "moving to first record");
			cursor.moveToFirst();
			
			do
			{
				profileID[counter] = cursor.getInt(idx_id);
				profileName[counter] = cursor.getString(idx_name);
				counter++;
			}while(cursor.moveToNext());
			
			setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, profileName));
			
		}
		else
			setListAdapter(null);
		
		cursor.close();
		dbo.close();
	}
	
	public void initDB() {
		// if db doesn't exists, create and enter default profile
		
		DBObject dbo = new DBObject(this);
		dbo.getWritableDatabase();
		Cursor cursor = dbo.getPhoneProfileList();
		
		
		if(cursor.getCount()==0)
		{
			// get AM and setup profile with current values from phone
			AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			
			// get wifi state
			WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			int wifi_state = 0;
			if(wm.isWifiEnabled())
				wifi_state = 1;
			
			dbo.createProfile("Default",
					2, // ringer mode
					am.getStreamVolume(AudioManager.STREAM_RING), // ringer vol
					0, // ringer vib
					am.getStreamVolume(AudioManager.STREAM_NOTIFICATION), // notif vol
					0, // notif vib
					am.getStreamVolume(AudioManager.STREAM_MUSIC), // media
					am.getStreamVolume(AudioManager.STREAM_ALARM), // alarm
					am.getStreamVolume(AudioManager.STREAM_SYSTEM), // system
					am.getStreamVolume(AudioManager.STREAM_VOICE_CALL), // voice
					"",
					"",
					(int)Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0), // brightness
					0,
					0, // divert
					0, // airplane
					wifi_state, // wifi
					0, // bt
					0); // data
			Toast.makeText(this, "Profile created based on current phone settings", Toast.LENGTH_SHORT).show();
		}
		cursor.close();
		dbo.close();
	}
	
	public void activateProfile(int profileToActivate) {
		
		Intent i;
		
		// pull out data from cursor
		DBObject dbo = new DBObject(this);
		dbo.getReadableDatabase();
		Cursor profile = dbo.getProfile(profileToActivate);
		
		if(profile.getCount() > 0)
		{
			profile.moveToFirst();
			
			Log.d(TAG, "gettin indexes");
			// get indexes
			int idx_name = profile.getColumnIndexOrThrow("name");
			int idx_ringer_mode = profile.getColumnIndexOrThrow("ringer_mode");
			int idx_ringer_vol = profile.getColumnIndexOrThrow("ringer_vol");
			int idx_notif_vol = profile.getColumnIndexOrThrow("notif_vol");
			int idx_alarm_vol = profile.getColumnIndexOrThrow("alarm_vol");
			int idx_media_vol = profile.getColumnIndexOrThrow("media_vol");
			int idx_system_vol = profile.getColumnIndexOrThrow("system_vol");
			int idx_voice_vol = profile.getColumnIndexOrThrow("voice_vol");
			int idx_ringtone = profile.getColumnIndexOrThrow("ringtone");
			int idx_notification = profile.getColumnIndexOrThrow("notification");
			int idx_brightness = profile.getColumnIndexOrThrow("brightness");
			int idx_c_brightness = profile.getColumnIndexOrThrow("custom_brightness");
			
			//int idx_data = profile.getColumnIndexOrThrow("data");
			int idx_wifi = profile.getColumnIndexOrThrow("wifi");
			int idx_airplane = profile.getColumnIndexOrThrow("airplane_mode");
			//int idx_bt = profile.getColumnIndexOrThrow("bluetooth");
			
			int idx_divert = profile.getColumnIndexOrThrow("divert_calls_to_vm");
			
			profile_name = profile.getString(idx_name);
			
			ringer_vol = profile.getInt(idx_ringer_vol);
			notif_vol = profile.getInt(idx_notif_vol);
			system_vol = profile.getInt(idx_system_vol);
			media_vol = profile.getInt(idx_media_vol);
			alarm_vol = profile.getInt(idx_alarm_vol);
			voice_vol = profile.getInt(idx_voice_vol);
			ringtone = profile.getString(idx_ringtone);ringtone.trim();
			notification = profile.getString(idx_notification);notification.trim();
			
			//Toast.makeText(this, "r: " +ringtone, Toast.LENGTH_SHORT).show();
			//Toast.makeText(this, "n: " +notification, Toast.LENGTH_SHORT).show();
			// change ringtone/notification
			if(ringtone.contains("//"))
				{
					Toast.makeText(this, "setting...", Toast.LENGTH_SHORT).show();
					setRingtone(Uri.parse(ringtone));
				}
			if(notification.contains("//"))
				{
					setNotification(Uri.parse(notification));
				}
			// announces activated tones
			if(ringtone.contains("//") || notification.contains("//"))
				announceRingtonesActivated();
			
			custom_brightness=profile.getInt(idx_c_brightness);
			brightness = profile.getInt(idx_brightness);
			
			wifi = profile.getInt(idx_wifi);
			//network_data = profile.getInt(idx_data);
			//bluetooth = profile.getInt(idx_bt);
			airplane_mode = profile.getInt(idx_airplane);
			
			Log.d(TAG, "assigning to globals");
			// assign to globals
			ringer_mode = profile.getInt(idx_ringer_mode);
			ringer_vol = profile.getInt(idx_ringer_vol);
			
			Log.d(TAG, "init hardware managers");
			am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			
			/******* START ACTIVATING *******/
			
			// ringer mode and stream vols
			switch(ringer_mode)
			{
			case 2: // sound and vibration
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
				break;
			case 1: // sound only
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				break;
			case 0: // vibrate only
				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				break;
			case 3: // silent
				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				default: break;
			}
			
			setStreams(ringer_vol, notif_vol, media_vol, voice_vol, system_vol, alarm_vol); // set all vol streams at once!
			
			// brightness
			if(custom_brightness==1)
			{
				Log.d(TAG, "setting brightness to " + brightness);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = brightness / 100.0f;
				getWindow().setAttributes(lp);
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
			}
			
			
			
			// wifi
			if(wifi==1)
				wm.setWifiEnabled(true);
			else
				wm.setWifiEnabled(false);
			
			// data
			Log.d(TAG, "Setting data to " + network_data);
			switch(network_data)
			{
			case 0: // off
				break;
			case 2: // 2g
				break;
			case 3:
				break;
			}
			
			// bluetooth
			switch(bluetooth)
			{
			case 1: // on
				break;
			case 0: // off
				break;
				default: break;
			}
			
			// airplane mode, 0=off, 1=on
			Log.d(TAG, "seting airplane mode to " + airplane_mode);
			i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			i.putExtra("airplane mode", airplane_mode);
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, airplane_mode);
			sendBroadcast(i);
		}
		
		profile.close();
		dbo.close();
		
		showNotification(profile_name, profile_name, "Call Center Pro");
		
		// launch listener serivce
		//startService(new Intent(this, CallCenterService.class));
		
	} // end of activateProfile()
	
	public void setStreams(int ringer, int notif, int media, int voice, int system, int alarm) {
		
		am.setStreamVolume(AudioManager.STREAM_RING, ringer, AudioManager.FLAG_SHOW_UI);
		am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notif, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, media, 0);
		am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voice, 0);
		am.setStreamVolume(AudioManager.STREAM_SYSTEM, system, 0);
	}
	
	public void refreshScreen() {
		
		// after a deletion or addition...
		
		buildProfileList();

		//setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, profileName));
			
	}
	
	public void showNotification(String ticker_text, String content_text, String content_title) {
		
		Notification notification = new Notification(R.drawable.status, ticker_text, System.currentTimeMillis());

		Intent notIntent = new Intent(this, CallCenter.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notIntent, 0);
		
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		notification.setLatestEventInfo(getApplicationContext(), content_title, content_text, contentIntent);
		
		notMgr.notify(ACTIVE_PROFILE_ID, notification);
	}
	
	public void setRingtone(Uri ringtone_to_set){
			//if(RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE)!=null)
		/*if(ringtone_to_set.toString()!="")
				RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, ringtone_to_set);
	*/
		Settings.System.putString(getContentResolver(), Settings.System.RINGTONE, ringtone);
	}
	
	public void setNotification(Uri notification_to_set){
		
		Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_SOUND, notification);
	}
	
	public void announceRingtonesActivated(){
		/* display a toast of ringtones activated */

		Ringtone rt_r=null, rt_n=null;
		String str_r=null, str_n=null;
		
		rt_r=RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE));
		if(rt_r!=null)
			str_r=rt_r.getTitle(getApplicationContext());
		
		rt_n=RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION));
		if(rt_n!=null)
			str_n=rt_n.getTitle(getApplicationContext());
		
		if(str_r==null)
			str_r="Silent";
		if(str_n==null)
			str_n="Silent";
		
		Toast.makeText(getApplicationContext(), "Ringtone: "+str_r +" ("+ ringer_vol+"), Notification: "+str_n+" ("+notif_vol+")", Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(), "R URI: " + ringtone, Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(), "Ringtone: "+str_r +", Notification: "+str_n, Toast.LENGTH_SHORT).show();
	}

}