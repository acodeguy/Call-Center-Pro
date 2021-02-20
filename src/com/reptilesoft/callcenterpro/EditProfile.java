package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EditProfile extends Activity implements SeekBar.OnSeekBarChangeListener {

	private final String TAG = "EditProfile.java";
	
	int profileId = 0;
	
	private SQLiteDatabase db;
	private DBObject dbo;
	
	/******* GLOBALS TABLE VARIABLES *******/
	private static String profile_name,
		ringtone,
		notification;
	
	private static int ringer_mode = 0,
		ringer_vol = 0,
		ringer_vib = 0,
		notif_vol = 0,
		notif_vib = 0,
		media_vol = 0,
		voice_vol = 0,
		alarm_vol = 0,
		system_vol = 0,
		brightness = 0,
		custom_brightness=0,
		divert_calls_to_vm = 0,
		airplane_mode = 0,
		network_2g = 0,
		network_3g = 0,
		wifi = 0,
		bluetooth = 0,
		network_data = 0,
		current_profile_choice=0;
	
	/******* MAX VOLS *******/
	private static int VOL_MAX_RINGER,
		VOL_MAX_NOTIF,
		VOL_MAX_MEDIA,
		VOL_MAX_VOICE,
		VOL_MAX_SYSTEM,
		VOL_MAX_ALARM;
	
	private final int RINGTONE_SELECT = 1, NOTIFICATION_SELECT = 2;
	
	Button btn_save;
	CheckBox chk_brightness, chk_ringtone, chk_notification;
	ToggleButton tgl_wifi, tgl_airplane;
	
	ImageButton img_ringtone, img_notification;
	
	SeekBar sb_ringer, sb_notif, sb_alarm, sb_voice, sb_media, sb_system, sb_brightness;
	
	EditText txt_profile_name;
	
	TextView tv_ringer, tv_notif, tv_alarm, tv_media, tv_system, tv_voice, tv_brightness, 
	tv_ringtone_selected, tv_notification_selected;
	
	Spinner spinner;
	
	AudioManager am;
	
	Ringtone rt_selected, nt_selected;
	
	Cursor profile;
	
	Context ctx;
	
	Uri ringtone_uri, notification_uri;
	
	@Override
	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		setContentView(R.layout.profile);
		
		ctx = getApplicationContext();
		
		Intent i = getIntent();
		profileId = i.getIntExtra("id", 0);
		Log.d(TAG, "Gonna edit profile ID #" + profileId);
		
		 // setup max volumes
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        
        VOL_MAX_RINGER = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        VOL_MAX_NOTIF = am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        VOL_MAX_MEDIA = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        VOL_MAX_ALARM = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        VOL_MAX_SYSTEM = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        VOL_MAX_VOICE = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        
        // get views
        
        tv_ringer = (TextView)findViewById(R.id.txt_ringer_value);
        tv_notif = (TextView)findViewById(R.id.txt_notif_value);
        tv_alarm = (TextView)findViewById(R.id.txt_alarm_value);
        tv_media = (TextView)findViewById(R.id.txt_media_value);
        tv_system = (TextView)findViewById(R.id.txt_system_value);
        tv_voice = (TextView)findViewById(R.id.txt_voice_value);
        tv_brightness = (TextView)findViewById(R.id.txt_brightness_value);
        tv_ringtone_selected = (TextView)findViewById(R.id.txt_selected_ringtone);
        tv_notification_selected = (TextView)findViewById(R.id.txt_selected_notification);
        
        img_ringtone = (ImageButton)findViewById(R.id.img_ringtone);
        img_ringtone.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        	switch(v.getId()){
        	case R.id.img_ringtone:
        		// attempt to show ringtone picker
				Intent ringtone_picker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
				ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
				ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
				//ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI));
				ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select a ringtone");
				startActivityForResult(ringtone_picker, RINGTONE_SELECT);
				//TODO: Implement Activity callback method
        		break;
        		default:
        			break;
        	}
        	}
        });
		
        img_notification = (ImageButton)findViewById(R.id.img_notification);
        img_notification.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		switch(v.getId()){
        		case R.id.img_notification:
        			Intent notification_picker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
					notification_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
					notification_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
					notification_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
					//notification_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION));
					notification_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select a notification");
					startActivityForResult(notification_picker, NOTIFICATION_SELECT);
					break;
					default:break;
        		}
        	}
        });
        
		txt_profile_name = (EditText)findViewById(R.id.edit_name);
		
		chk_brightness=(CheckBox)findViewById(R.id.chk_brightness);
		chk_brightness.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton btn, boolean checked){
				if(checked)
				{
					sb_brightness.setVisibility(View.VISIBLE);
					custom_brightness=1;
				}
				if(!checked)
				{
					sb_brightness.setVisibility(View.INVISIBLE);
					custom_brightness=0;
				}
			}
		});
		
		chk_ringtone=(CheckBox)findViewById(R.id.chk_custom_ringtone);
		chk_ringtone.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton btn, boolean checked){
				if(checked)
				{
					img_ringtone.setEnabled(true);
					img_ringtone.setVisibility(View.VISIBLE);
					//ringtone="";
				}
				else
				{
					img_ringtone.setEnabled(false);
					ringtone="";
				}
				
			}
		});
		
		chk_notification=(CheckBox)findViewById(R.id.chk_custom_notification);
		chk_notification.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton btn, boolean checked){
				if(checked)
				{
					img_notification.setEnabled(true);
					img_notification.setVisibility(View.VISIBLE);
					//notification="";
				}
				else
				{
					img_notification.setEnabled(false);
					notification="";
				}
				
			}
		});
		
		sb_ringer = (SeekBar)findViewById(R.id.sb_ringer);
		sb_ringer.setMax(VOL_MAX_RINGER);
		sb_ringer.setOnSeekBarChangeListener(this);
		
		sb_notif = (SeekBar)findViewById(R.id.sb_notification);
		sb_notif.setMax(VOL_MAX_NOTIF);
		sb_notif.setOnSeekBarChangeListener(this);
		
		sb_media = (SeekBar)findViewById(R.id.sb_media);
		sb_media.setMax(VOL_MAX_MEDIA);
		sb_media.setOnSeekBarChangeListener(this);
		
		sb_alarm = (SeekBar)findViewById(R.id.sb_alarm);
		sb_alarm.setMax(VOL_MAX_ALARM);
		sb_alarm.setOnSeekBarChangeListener(this);
		
		sb_voice = (SeekBar)findViewById(R.id.sb_voice);
		sb_voice.setMax(VOL_MAX_VOICE);
		sb_voice.setOnSeekBarChangeListener(this);
		
		sb_system = (SeekBar)findViewById(R.id.sb_system);
		sb_system.setMax(VOL_MAX_SYSTEM);
		sb_system.setOnSeekBarChangeListener(this);
		
		sb_brightness = (SeekBar)findViewById(R.id.sb_brightness);
		sb_brightness.setMax(255);
		sb_brightness.setOnSeekBarChangeListener(this);
		
		// toggles
		
		tgl_airplane = (ToggleButton)findViewById(R.id.tgl_airplane);
		tgl_airplane.setOnCheckedChangeListener(
				new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton button, boolean isChecked) {
						
						switch(button.getId())
						{
						case R.id.tgl_airplane:
							if(tgl_airplane.isChecked()) // airplane mode ON!
								{
									tgl_wifi.setEnabled(false);
									tgl_wifi.setChecked(false);
								}
							else
								{
									tgl_wifi.setEnabled(true);
								}
							break;
						}
					}
				});
		tgl_wifi = (ToggleButton)findViewById(R.id.tgl_wifi);
		
		
		btn_save = (Button)findViewById(R.id.btn_save);
		btn_save.setOnClickListener(
				new OnClickListener() {
					
					public void onClick(View v) {
						switch(v.getId())
						{
						case R.id.btn_save:
							updateProfile(profileId);
							finish();
							break;

						default: break;
						}
					}
				});
		
		// setup spinner
		spinner = (Spinner) findViewById(R.id.spinner_ringer_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.ringer_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
        		new OnItemSelectedListener() {
        			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id){
        				
        				switch(pos)
        				{
        				case 0: // s & v
        					enableVolumeSliders();
        					img_ringtone.setEnabled(true);
        					img_notification.setEnabled(true);
        					break;
        				case 1: // vibrate only
        					disableVolumeSliders();
        					img_ringtone.setEnabled(false);
        					img_notification.setEnabled(false);
        					ringtone="";
        					notification="";
        					break;
        				case 2: // sound only
        					enableVolumeSliders();
        					img_ringtone.setEnabled(true);
        					img_notification.setEnabled(true);
        					break;
        				case 3: //silent
        					disableVolumeSliders();
        					img_ringtone.setEnabled(false);
        					img_notification.setEnabled(false);
        					ringtone="";
        					notification="";
        					break;
        				}
        				current_profile_choice=pos;
        			}
        			
        			public void onNothingSelected(AdapterView<?> parent) {
        				
        			}
        			
        		});
        
        DBObject dbo = new DBObject(this);
        profile = dbo.getProfile(profileId);
        
        Log.d(TAG, "got profile back, continuing...");
        
        if(profile.getCount() > 0)
        {
        	profile.moveToFirst();
        	
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
			
			// get Uri (as strings) from db
			ringtone = profile.getString(idx_ringtone);
			notification = profile.getString(idx_notification);
			
			brightness = profile.getInt(idx_brightness);
			custom_brightness=profile.getInt(idx_c_brightness);
			
			wifi = profile.getInt(idx_wifi);
			//network_data = profile.getInt(idx_data);
			//bluetooth = profile.getInt(idx_bt);
			airplane_mode = profile.getInt(idx_airplane);
			
			Log.d(TAG, "assigning to globals");
			// assign to globals
			ringer_mode = profile.getInt(idx_ringer_mode);
			ringer_vol = profile.getInt(idx_ringer_vol);
        	
        }
        
        dbo.close();
        
        setupViewToProfileData();
	}
	
	@Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) 
    {
		super.onActivityResult(reqCode, resultCode, data);

	      switch (reqCode) 
	      {
	        case RINGTONE_SELECT:
	            if (resultCode == Activity.RESULT_OK) 
	            {
	              ringtone_uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	              rt_selected = RingtoneManager.getRingtone(getApplicationContext(), ringtone_uri);
	              if(ringtone_uri!=null)
	            	  {
	            	  	Toast.makeText(getApplicationContext(), rt_selected.getTitle(getApplicationContext()), Toast.LENGTH_SHORT).show();
	            	  	tv_ringtone_selected.setText(rt_selected.getTitle(getApplicationContext()));
	            	  	ringtone = ringtone_uri.toString();
	            	  	switch(current_profile_choice)
	            	  	{
	            	  	case 0: //s & v
	            	  		sb_ringer.setEnabled(true);
	            	  		break;
	            	  	case 1: // vibrate only
	            	  		sb_ringer.setEnabled(false);
	            	  		break;
	            	  	case 2: // sound only
	            	  		sb_ringer.setEnabled(true);
	            	  		break;
	            	  	case 3: // silent
	            	  		sb_ringer.setEnabled(false);
	            	  		break;
	            	  		default:
	            	  			break;
	            	  	}
	            	  }
	              else
	            	  {
	            	  	tv_ringtone_selected.setText("Silent");
	            	  	ringtone = "";
	            	  	sb_ringer.setEnabled(false);
	            	  }
	            }
	        	// ringtone selected, toast Uri
	        	break;
	        case NOTIFICATION_SELECT:
	            if (resultCode == Activity.RESULT_OK) 
	            {
	              notification_uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	              nt_selected = RingtoneManager.getRingtone(getApplicationContext(), notification_uri);
	              if(notification_uri!=null)
	            	  {
	            	  	Toast.makeText(getApplicationContext(), nt_selected.getTitle(getApplicationContext()), Toast.LENGTH_SHORT).show();
	            	  	tv_notification_selected.setText(nt_selected.getTitle(getApplicationContext()));
	            	  	notification = notification_uri.toString();
	            	  	switch(current_profile_choice)
	            	  	{
	            	  	case 0: //s & v
	            	  		sb_notif.setEnabled(true);
	            	  		break;
	            	  	case 1: // vibrate only
	            	  		sb_notif.setEnabled(false);
	            	  		break;
	            	  	case 2: // sound only
	            	  		sb_notif.setEnabled(true);
	            	  		break;
	            	  	case 3: // silent
	            	  		sb_notif.setEnabled(false);
	            	  		break;
	            	  		default:
	            	  			break;
	            	  	}
	            	  }
	              else
	            	  {
	            	  	tv_notification_selected.setText("Silent");
	            	  	notification="";
	            	  	sb_notif.setEnabled(false);
	            	  }
	            }
	        	// ringtone selected, toast Uri
	        	break;
	      }

    }
	
	public void setupViewToProfileData() {
		
		Log.d(TAG, "setting up profile name: " + profile_name + ", ringer vol: " + ringer_vol);
		
		/* set the progress bars to their db values */
		txt_profile_name.setText(profile_name);
		sb_ringer.setProgress(ringer_vol);
		sb_notif.setProgress(notif_vol);
		sb_media.setProgress(media_vol);
		sb_voice.setProgress(voice_vol);
		sb_alarm.setProgress(alarm_vol);
		sb_system.setProgress(system_vol);
		
		if(custom_brightness==1)
		{
			chk_brightness.setChecked(true);
			sb_brightness.setVisibility(View.VISIBLE);
			sb_brightness.setProgress(brightness);
		}
		if(custom_brightness==0)
		{
			sb_brightness.setVisibility(View.INVISIBLE);
			chk_brightness.setChecked(false);
		}
		
		// ringtone and notification
		if(ringtone.contains("//")) // part of a valid Uri
		{
			ringtone_uri = Uri.parse(ringtone);
			rt_selected = RingtoneManager.getRingtone(this, ringtone_uri);
			tv_ringtone_selected.setText(rt_selected.getTitle(getApplicationContext()));
			chk_ringtone.setChecked(true);
			img_ringtone.setVisibility(View.VISIBLE);
			img_ringtone.setEnabled(true);
		}
		else
		{
			tv_ringtone_selected.setText("Silent");
			img_ringtone.setVisibility(View.INVISIBLE);
		}
		
		if(notification.contains("//"))
		{
			notification_uri = Uri.parse(notification);
			nt_selected = RingtoneManager.getRingtone(this, notification_uri);
			tv_notification_selected.setText(nt_selected.getTitle(getApplicationContext()));
			chk_notification.setChecked(true);
			img_notification.setVisibility(View.VISIBLE);
			img_notification.setEnabled(true);
		}
		else
			{
				tv_notification_selected.setText("Silent");
				img_notification.setVisibility(View.INVISIBLE);
			}
		
		/* set airplane mode */
		if(airplane_mode == 1)
		{
			tgl_airplane.setChecked(true);
		}
		else
			{
				tgl_airplane.setChecked(false);
				tgl_wifi.setChecked(false);
			}
		
		/* set wifi */
		if(wifi == 1)
		{
			tgl_wifi.setChecked(true);
		}
		else
			tgl_wifi.setChecked(false);
		
		switch(ringer_mode)
		{
		case 0: // vibrate
			spinner.setSelection(1);
			break;
			
		case 1: // sound only
			spinner.setSelection(2);
			break;
			
		case 2: // s&v
			spinner.setSelection(0);
			break;
		
		case 3: // silent
			spinner.setSelection(3);
			break;
			
			default: break;
		}
	}
	
	public void enableVolumeSliders() {
		
		sb_ringer.setEnabled(true);
		//sb_ringer.setProgress(0);
		
		sb_notif.setEnabled(true);
		//sb_notif.setProgress(0);
	}
	
	public void disableVolumeSliders() {
		
		sb_ringer.setEnabled(false);
		//sb_ringer.setProgress(0);
		
		sb_notif.setEnabled(false);
		//sb_notif.setProgress(0);
	}
	
	public void updateProfile(int id) {
		
		dbo = new DBObject(this);
		db = dbo.getWritableDatabase();
		
		profile_name = txt_profile_name.getText().toString();
		
		switch(spinner.getSelectedItemPosition())
		{
		case 0: // s & v
			ringer_mode = 2;
			break;
		case 1: // vibrate
			ringer_mode = 0;
			break;
		case 2: // s only
			ringer_mode = 1;
			break;
		}
		
		ringer_vol = sb_ringer.getProgress();
		notif_vol = sb_notif.getProgress();
		media_vol = sb_media.getProgress();
		voice_vol = sb_voice.getProgress();
		alarm_vol = sb_alarm.getProgress();
		system_vol = sb_system.getProgress();
		brightness = sb_brightness.getProgress();
		
		// if chk are checked then user is customizing, else save strings emtpy!
		if(!chk_ringtone.isChecked())
			ringtone="none";
		if(!chk_notification.isChecked())
			notification="none";
		
		if(tgl_airplane.isChecked())
			airplane_mode = 1; // already initialise as 0;
		else
			airplane_mode = 0;
		
		if(tgl_wifi.isChecked())
			wifi = 1; // already init as 0		
		else
			wifi = 0;
		
		dbo.updateProfile(
				id, 
				profile_name, 
				ringer_mode, 
				ringer_vol, 
				ringer_vib, 
				notif_vol, 
				notif_vib, 
				media_vol, 
				alarm_vol, 
				system_vol, 
				voice_vol, 
				ringtone,
				notification,
				brightness, 
				custom_brightness,
				divert_calls_to_vm, 
				airplane_mode, 
				wifi, 
				bluetooth, 
				network_data);
		
		dbo.close();
		
		Toast.makeText(ctx, profile_name + " edited. Changes will take affect once profile is re-activated.", Toast.LENGTH_LONG).show();
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		
		Context c = getApplicationContext();
		
		switch(seekBar.getId())
		{
		case R.id.sb_ringer:
			tv_ringer.setText( "(" + seekBar.getProgress() + "/" + VOL_MAX_RINGER +")" );
			break;
		case R.id.sb_media:
			tv_media.setText( "(" + seekBar.getProgress() + "/" + VOL_MAX_MEDIA +")" );
			break;
		case R.id.sb_alarm:
			tv_alarm.setText( "(" + seekBar.getProgress() + "/" + VOL_MAX_ALARM +")" );
			break;
		case R.id.sb_system:
			tv_system.setText( "(" + seekBar.getProgress() + "/" + VOL_MAX_SYSTEM +")" );
			break;
		case R.id.sb_voice:
			tv_voice.setText( "(" + seekBar.getProgress() + "/" + VOL_MAX_VOICE +")" );
			break;
		case R.id.sb_notification:
			tv_notif.setText( "(" + seekBar.getProgress() + "/" + VOL_MAX_NOTIF +")" );
			break;
		case R.id.sb_brightness:
			tv_brightness.setText( "(" + seekBar.getProgress() + "/255)" );
			break;
			
			default: break;
		}
	}
}