package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class AddProfile extends Activity implements SeekBar.OnSeekBarChangeListener {
	/* adding a new profile to the database */
	
	private final String TAG = "AddProfile";
	private final int RINGTONE_SELECT = 1, NOTIFICATION_SELECT = 2;
	
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
	
	Button btn_save;
	CheckBox chk_brightness, chk_ringtone, chk_notification;
	ToggleButton tgl_wifi, tgl_airplane;
	
	SeekBar sb_ringer, sb_notif, sb_alarm, sb_voice, sb_media, sb_system, sb_brightness;
	
	EditText txt_profile_name;
	
	TextView tv_ringer, tv_notif, tv_alarm, tv_media, tv_system, tv_voice, tv_brightness, txt_selected_ringtone, txt_selected_notification;
	
	ImageButton img_ringtone, img_notification;
	
	Spinner spinner;
	
	AudioManager am;
	
	RingtoneManager rm;
	Ringtone rt_selected, nt_selected;
		
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.profile);
		
		//rm = (RingtoneManager)getSystemService(Context);
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
					//ringtone="";
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
					//notification="";
				}
				
			}
		});
		txt_selected_ringtone = (TextView)findViewById(R.id.txt_selected_ringtone);
		
		/* set to default ringtone by default */
		//Uri rt_current = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
		Uri rt_current=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		Ringtone rt_now = RingtoneManager.getRingtone(getApplicationContext(), rt_current);
		if(rt_now!=null)
		{ringtone=RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE).toString();
		rt_selected=RingtoneManager.getRingtone(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE));
		txt_selected_ringtone.setText(""+rt_selected.getTitle(this));
		}
		txt_selected_notification = (TextView)findViewById(R.id.txt_selected_notification);
		
		/* set to default ringtone by default */
		Uri nt_current = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
		Ringtone nt_now = RingtoneManager.getRingtone(getApplicationContext(), nt_current);
		if(nt_now!=null)
		{		notification=RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION).toString();
		Log.d(TAG, "noficiation set");
		nt_selected=RingtoneManager.getRingtone(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
		txt_selected_notification.setText(""+nt_selected.getTitle(this));
		}
		
		img_notification = (ImageButton)findViewById(R.id.img_notification);
		img_notification.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
		img_ringtone = (ImageButton)findViewById(R.id.img_ringtone); // ringtone selection button
		img_ringtone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.img_ringtone:
					// attempt to show ringtone picker
					Intent ringtone_picker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
					ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
					ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
					ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
					//ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE));
					ringtone_picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select a ringtone");
					startActivityForResult(ringtone_picker, RINGTONE_SELECT);
					break;
					default: break;
					
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
							saveNewProfile();
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
        				case 3: // silent
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
        
     // disable tone selection buttons
		img_ringtone.setVisibility(View.INVISIBLE);
		img_notification.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onResume() {	
		super.onResume();
	}
	
	@Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) 
    {
		super.onActivityResult(reqCode, resultCode, data);

	      switch (reqCode) 
	      {
	        case (RINGTONE_SELECT):
	            if (resultCode == Activity.RESULT_OK) 
	            {
	              Uri ringtone_uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	              rt_selected = RingtoneManager.getRingtone(getApplicationContext(), ringtone_uri);
	              if(ringtone_uri!=null)
	            	  {
	            	  	Log.d(TAG, "Ringtone Uri: "+ringtone_uri);
	            	  	Toast.makeText(getApplicationContext(), rt_selected.getTitle(getApplicationContext())
	            	  			,Toast.LENGTH_SHORT).show();
	            	  	txt_selected_ringtone.setText(rt_selected.getTitle(getApplicationContext()));
	            	  	ringtone = ringtone_uri.toString(); // for the db
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
	            	  txt_selected_ringtone.setText("Silent");
	            	  ringtone="";
	            	  sb_ringer.setEnabled(false);
	              }
	            }
	        
	        	// ringtone selected, toast Uri
	        	break;
	        case NOTIFICATION_SELECT:
	        	if(resultCode == Activity.RESULT_OK){
	        		Uri notification_uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	        		nt_selected = RingtoneManager.getRingtone(getApplicationContext(), notification_uri);
	        		if(notification_uri!=null){
	        			Toast.makeText(getApplicationContext(), nt_selected.getTitle(getApplicationContext()), Toast.LENGTH_SHORT).show();
	        			Log.d(TAG, "Notification Uri: "+notification_uri);
	        			txt_selected_notification.setText(nt_selected.getTitle(getApplicationContext()));
	        			notification = notification_uri.toString(); // for the db
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
	        			txt_selected_notification.setText("Silent");
	        			notification="";
		            	sb_notif.setEnabled(false);
	        		}
	        	}

	        	default:break;
	      }

    }
	
	public void enableVolumeSliders() {
		
		sb_ringer.setEnabled(true);
		//sb_ringer.setProgress(0);
		sb_notif.setEnabled(true);
		//sb_notif.setProgress(0);
		//tv_ringer.setText("0");
		//tv_notif.setText("0");
	}
	
	public void disableVolumeSliders() {
		
		sb_ringer.setEnabled(false);
		//sb_ringer.setProgress(0);
		sb_notif.setEnabled(false);
		//sb_notif.setProgress(0);
		//tv_ringer.setText("0");
		//tv_notif.setText("0");
	}
	
	public void saveNewProfile(){
		
		dbo = new DBObject(this);
		db = dbo.getWritableDatabase();
		
		profile_name = txt_profile_name.getText().toString().replace("'", "''");
		//TODO: check this works
		
		switch(spinner.getSelectedItemPosition())
		{
		case 0: // s & v 
			ringer_mode = 2; // NORMAL
			break;
		case 1: // vibrate
			ringer_mode = 0;
			break;
		case 2: // s only
			ringer_mode = 1;
			break;
		case 3: // silent
			ringer_mode = 3;
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
		if(chk_ringtone.isChecked()==false)
			ringtone="none";
		if(chk_notification.isChecked()==false)
			notification="none";
		
		if(tgl_airplane.isChecked()) {
			airplane_mode = 1; // already initialise as 0;
		}
		
		if(tgl_wifi.isChecked()) {
			wifi = 1; // already init as 0
		}
		
		dbo.createProfile(profile_name, 
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
		
		//Log.d(TAG, "R URI: " + ringtone);
		
		Toast.makeText(this, profile_name + " added", Toast.LENGTH_SHORT);
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