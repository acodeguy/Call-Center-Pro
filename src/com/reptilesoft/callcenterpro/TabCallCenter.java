package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class TabCallCenter extends Activity {
	
	private final String TAG = "TabCallCenter";
	
	final static int MENU_ABOUT = 0, MENU_SETTINGS = 1, MENU_QUIT = 2;
	
	/* for arrays, spinner */
	private String [] profile_name;
	private int[] profile_id;
	
	Cursor cursor_profile;
	DBObject dbo;
	
	int numberOnBlacklist = 0;
	
	Spinner spinner;
	
	TextView tv_num_blacklisted;
	
	@Override
	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		
		setContentView(R.layout.call_center);
		
		getProfileList();
		setupSpinner();
		
		//tv_num_blacklisted = (TextView)findViewById(R.id.txt_number_blacklisted);
		
		//getNumberOnBlacklist();
		
		//tv_num_blacklisted.setText("" + numberOnBlacklist);
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		Log.d(TAG, "onStart()");
		// get stored prefs
		//getPrefs();
	}
	
	@Override
	public void onResume() {
		
		Log.d(TAG, "onResume()");
		
		super.onResume();
		
		//getProfileList();
		//setupSpinner();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu m) {
		// if the menu button is hit...
		m.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		m.add(0, MENU_ABOUT,0,"About").setIcon(android.R.drawable.ic_dialog_info);
		m.add(0, MENU_QUIT, 0, "Quit").setIcon(android.R.drawable.stat_notify_error);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem mi) {
    	switch (mi.getItemId()) {
    	
    	case MENU_ABOUT:
    		Intent i = new Intent(this, About.class);
    		startActivity(i);
    		break;
    		
    	case MENU_QUIT:
    		finish();
    		break;
    		
    	case MENU_SETTINGS:
    		startActivity(new Intent(this, Prefs.class));
    		break;
    	}
    	return false;
    }
	
	public void setupSpinner() {
		
		// setup spinner
		spinner = (Spinner) findViewById(R.id.spinner_qs_ringer_mode);
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, profile_name, android.R.layout.simple_spinner_item);
        */
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		
		Log.d(TAG, "entering loop, which should loop " + profile_id.length + " times");
		for (int x=0; x < profile_id.length; x++)
		{
			adapter.add(profile_name[x]);
			Log.d(TAG, "adding to spinner: " + profile_name[x]);
		}
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
        		new OnItemSelectedListener() {
        			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id){
        				
        				
        			}
        			
        			public void onNothingSelected(AdapterView<?> parent) {
        				
        			}
        			
        	});
	}
	
	public void getProfileList() {
		
		/* get profile list from db */
		
		dbo = new DBObject(this);
		
		cursor_profile = dbo.getPhoneProfileList();
		
		if( cursor_profile.getCount() >0 ) // more than one profile in cursor
		{
			profile_id = new int[cursor_profile.getCount()];
			profile_name = new String[cursor_profile.getCount()];
			
			int idx_id = cursor_profile.getColumnIndexOrThrow("_id");
			int idx_name = cursor_profile.getColumnIndexOrThrow("name");
			
			int counter = 0;
			
			cursor_profile.moveToFirst();
			
			do {
				profile_id[counter] = cursor_profile.getInt(idx_id);
				profile_name[counter] = cursor_profile.getString(idx_name);
				Log.d(TAG, "retrieved: " + profile_id[counter] + "(" + profile_name[counter] + ")");
				counter++;
			}while(cursor_profile.moveToNext());
		}
		
		dbo.close();
	}
	
	public void getNumberOnBlacklist() {
		
		/* query db and get number of contacts blacklisted */
		
		String[] projection = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Data.SEND_TO_VOICEMAIL,
				ContactsContract.Data._ID
		};
		String where = ContactsContract.Contacts.SEND_TO_VOICEMAIL;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, where, null, sortOrder);
		
		numberOnBlacklist = c.getCount();
	}
}