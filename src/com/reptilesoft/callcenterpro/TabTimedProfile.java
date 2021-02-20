package com.reptilesoft.callcenterpro;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TabTimedProfile extends ListActivity {

	final static String TAG = "TabTmedProfile";
	boolean LOGGING = false;
	final int MENU_NEW = 1;
	ListView listView;
	int[] profileId, hourToActivate, minuteToActivate;
	int iDPosition;
	String [] profileName, activationTime;
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		// are we logging?
		LOGGING = com.reptilesoft.callcenterpro.Constants.LOGGING;
		
		buildList();
		
	}
	
	public void onResume() {
		super.onResume();
		
		buildList();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu m) {
		// if the menu button is hit...
		
		m.add(0, MENU_NEW,0,"New timed profile").setIcon(android.R.drawable.btn_plus);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem mi) {
		
		switch(mi.getItemId())
		{
		case MENU_NEW:
			startActivity(new Intent(this, NewTimedProfile.class));
			break;
			default: break;
		}
		return false;
	}
	
	public void buildList() {
		/* build list view from db items */
		
		// setup list
		setListAdapter(null);
		
		listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// for activation, later
				iDPosition = position;
				
				final CharSequence[] items = {
						"Activate", 
						"Edit", 
						"Delete"
						};
				AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
				builder.setTitle("");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	
				    	// get which item was clicked and give options for it;
				    	
				    	switch(item)
				    	{
				    	case 0: //activate
				    		scheduleAlarm(profileId[iDPosition]);
				    		break;
				    	
				    	case 1: // Edit
				    		break;
				    	
				    	case 2: // delete
				    	{
				    		DBObject dbo = new DBObject(getApplicationContext());
				    		dbo.deleteTimedProfile(profileId[iDPosition]);
				    		dbo.close();
				    		buildList();
				    	}
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		DBObject dbo = new DBObject(this);
		Cursor cursor = dbo.getTimedProfileList();
		
		// first get tp info
		if(cursor.getCount() > 0)
		{
			profileId = new int[cursor.getCount()];
			minuteToActivate = new int[cursor.getCount()];
			hourToActivate = new int[cursor.getCount()];
			profileName = new String[cursor.getCount()];
			activationTime = new String[cursor.getCount()];
			int x = 0;
			
			cursor.moveToFirst();
			do {
				profileId[x] = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				activationTime[x] = cursor.getString(cursor.getColumnIndexOrThrow("activationHour")) + ":";
				activationTime[x] += cursor.getString(cursor.getColumnIndexOrThrow("activationMinute"));
				minuteToActivate[x] = cursor.getInt(cursor.getColumnIndexOrThrow("activationMinute"));
				Log.i(TAG, "min = " + minuteToActivate[x] + ")" );
				hourToActivate[x] = cursor.getInt(cursor.getColumnIndexOrThrow("activationHour"));
				Log.i(TAG, "hour = " + hourToActivate[x] + ")" );
				profileName[x] = getProfileName(profileId[x]);
				profileName[x] += " [" + activationTime[x] + "][";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Mo"))==1)
					profileName[x] += "Mo";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Tu"))==1)
					profileName[x] += ",Tu";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("We"))==1)
					profileName[x] += ",We";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Th"))==1)
					profileName[x] += ",Th";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Fr"))==1)
					profileName[x] += ",Fr";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Sa"))==1)
					profileName[x] += ",Sa";
				if(cursor.getInt(cursor.getColumnIndexOrThrow("Su"))==1)
					profileName[x] += ",Su";
				profileName[x] += "]";
				x++;
			}while(cursor.moveToNext());

			setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, profileName));
		}
		cursor.close();
		dbo.close();
	}
	
	public String getProfileName(int profileId) {
		
		Log.i(TAG, "getProfileName(" + profileId + ")");
		
		String name;
		DBObject dbo = new DBObject(this);
		Cursor cursor = dbo.getProfile(profileId);
		if(cursor.getCount()>0)
		{
			cursor.moveToFirst();
			name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
			cursor.close();
			dbo.close();
			return name;
		}
		else
		{
			cursor.close();
			dbo.close();
			return "UNKNOWN";
		}
	}
	
	public void scheduleAlarm(int profileIdToActivate) {
		
		Log.i(TAG, "scheduleAlarm(" + profileIdToActivate + ")");
		
		int hour=0, minute=0;
		DBObject dbo = new DBObject(this);
		Cursor cursor = dbo.getTimedProfileInfo(profileIdToActivate);
		if(cursor.getCount()>0)
		{
			cursor.moveToFirst();
			hour = cursor.getInt(cursor.getColumnIndexOrThrow("activationHour"));
			minute = cursor.getInt(cursor.getColumnIndexOrThrow("activationMinute"));
		}
		cursor.close();
		dbo.close();
		
		// now schedule it
		Log.i(TAG, "scheduling for " + hour + ":" + minute);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		Intent intent = new Intent(this, TimedReceiver.class);
		intent.putExtra("profileId", profileIdToActivate);
		PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}
}
