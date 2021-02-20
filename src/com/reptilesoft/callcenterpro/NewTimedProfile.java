package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NewTimedProfile extends Activity implements OnClickListener, OnCheckedChangeListener {
	
	Spinner profile;
	CheckBox cbMonday,cbTuesday,cbWednesday,cbThursday,cbFriday,cbSaturday,cbSunday,cbAll;
	Button btnSubmit;
	TimePicker tpSchedule;
	int[] profileID; // used with cursor to match position in spinner to _id in db
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		// main view
		setContentView(R.layout.new_timed_profile);
		
		// get views
		profile = (Spinner)findViewById(R.id.spinner_timed_profile);
		cbMonday = (CheckBox)findViewById(R.id.checkbox_monday);
		cbMonday.setOnCheckedChangeListener(this);
		cbTuesday = (CheckBox)findViewById(R.id.checkbox_tuesday);
		cbTuesday.setOnCheckedChangeListener(this);
		cbWednesday = (CheckBox)findViewById(R.id.checkbox_wednesday);
		cbWednesday.setOnCheckedChangeListener(this);
		cbThursday = (CheckBox)findViewById(R.id.checkbox_thursday);
		cbThursday.setOnCheckedChangeListener(this);
		cbFriday = (CheckBox)findViewById(R.id.checkbox_friday);
		cbFriday.setOnCheckedChangeListener(this);
		cbSaturday = (CheckBox)findViewById(R.id.checkbox_saturday);
		cbSaturday.setOnCheckedChangeListener(this);
		cbSunday = (CheckBox)findViewById(R.id.checkbox_sunday);
		cbSunday.setOnCheckedChangeListener(this);
		cbAll = (CheckBox)findViewById(R.id.checkbox_all);
		cbAll.setOnCheckedChangeListener(this);
		cbAll.setChecked(true);
		tpSchedule = (TimePicker)findViewById(R.id.time_picker);
		btnSubmit = (Button)findViewById(R.id.button_submit_timed_profile);
		btnSubmit.setOnClickListener(this);
		
		// fill the spinner with profiles
		fillSpinner();
	}
	
	public void fillSpinner() {
		/* fill spinner with profiles */
		
		DBObject dbo = new DBObject(this);
		Cursor cursor = dbo.getPhoneProfileList();
		int counter = 0;
		
		if(cursor != null)
		{
			int idx_id = cursor.getColumnIndexOrThrow("_id");
			int idx_name = cursor.getColumnIndexOrThrow("name");
			
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
			
			cursor.moveToFirst();
			do {
				adapter.add(cursor.getString(idx_name));
				counter++;
			}while(cursor.moveToNext());
			
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        profile.setAdapter(adapter);
	        profile.setOnItemSelectedListener(
	        		new OnItemSelectedListener() {
	        			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id){
	        				
	        				
	        			}
	        			
	        			public void onNothingSelected(AdapterView<?> parent) {
	        				
	        			}
	        			
	        	});
		}
		
	}
	public void saveTimedProfile() {
		/* get details from form and throw into db */
		int profileId = 0; // id of the profile to be activated
		String activationTime; // time to activate it
		boolean[] onWhichDays = new boolean[] {false,false,false,false,false,false,false}; // M,T,W, etc, true or false
		
		// which days are checked?
		if(cbAll.isChecked())
			{
				for(int i=0;i<6;i++)
					onWhichDays[i] = true;
			}
		if(cbMonday.isChecked())
			onWhichDays[0] = true;
		if(cbTuesday.isChecked())
			onWhichDays[1] = true;
		if(cbWednesday.isChecked())
			onWhichDays[2] = true;
		if(cbThursday.isChecked())
			onWhichDays[3] = true;
		if(cbFriday.isChecked())
			onWhichDays[4] = true;
		if(cbSaturday.isChecked())
			onWhichDays[5] = true;
		if(cbSunday.isChecked())
			onWhichDays[6] = true;
		
		// at what time? (hh:mm)
		activationTime = tpSchedule.getCurrentHour().toString() + ":" + tpSchedule.getCurrentMinute().toString();
		Toast.makeText(this, "Time set: " + activationTime, Toast.LENGTH_SHORT).show();
		
		// create db object and start filling
		DBObject dbo = new DBObject(this);
		dbo.createTimedProfile(profileId, tpSchedule.getCurrentHour(), tpSchedule.getCurrentMinute(), onWhichDays);
		// close
		dbo.close();
	}

	
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		
		boolean boolMondayIsChecked, boolTuesdayIsChecked, boolWednesdayIsChecked, boolAllIsChecked,
		boolThursdayIsChecked, boolFridayIsChecked, boolSaturdayIsChecked, boolSundayIsChecked;
		
		switch(button.getId())
		{
		case R.id.checkbox_all:
		{
			/* disable all others, checking them */
			if(isChecked)
			{
				boolAllIsChecked = true;
				cbMonday.setChecked(true); cbMonday.setEnabled(false);
				cbTuesday.setChecked(true);cbTuesday.setEnabled(false);
				cbWednesday.setChecked(true);cbWednesday.setEnabled(false);
				cbThursday.setChecked(true);cbThursday.setEnabled(false);
				cbFriday.setChecked(true);cbFriday.setEnabled(false);
				cbSaturday.setChecked(true);cbSaturday.setEnabled(false);
				cbSunday.setChecked(true);cbSunday.setEnabled(false);
			}
			else
			{
				boolAllIsChecked = false;
				cbMonday.setChecked(true); cbMonday.setEnabled(true);
				cbTuesday.setChecked(true);cbTuesday.setEnabled(true);
				cbWednesday.setChecked(true);cbWednesday.setEnabled(true);
				cbThursday.setChecked(true);cbThursday.setEnabled(true);
				cbFriday.setChecked(true);cbFriday.setEnabled(true);
				cbSaturday.setChecked(true);cbSaturday.setEnabled(true);
				cbSunday.setChecked(true);cbSunday.setEnabled(true);
			}
		}
		break;
		
		case R.id.checkbox_monday:
		case R.id.checkbox_tuesday:
		case R.id.checkbox_wednesday:
		case R.id.checkbox_thursday:
		case R.id.checkbox_friday:
		case R.id.checkbox_saturday:
		case R.id.checkbox_sunday:
		{
			cbAll.setChecked(false);
		}
		break;
		
		default:
			//cbAll.setChecked(false);
			break;
		}
	}

	
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.button_submit_timed_profile:
			saveTimedProfile();
			finish();
			break;
		}
		
	}

}
