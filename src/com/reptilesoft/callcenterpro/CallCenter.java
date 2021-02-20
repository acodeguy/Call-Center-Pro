package com.reptilesoft.callcenterpro;


import java.util.Random;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

public class CallCenter extends TabActivity {
	
	private final String TAG = "CallCenter.java";
	private Intent i;
	private TabHost tabHost;
	private TabHost.TabSpec spec;
	private Resources res;
	Random generator;
	private SharedPreferences prefs;
	final static int AD_PROBABILITY=6;
	public static Context context;
	
	public static boolean start_on_boot = false,
		listener_service = false;
	
	int reminder_freq = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        // testing
        //startService(new Intent(this, CallCenterService.class));
        // show remidner about slider for notifications
        Toast.makeText(this, "Make sure that the \"Use incoming call volume for notifications\" is unchecked in Sound->Volume", Toast.LENGTH_LONG).show();
        // show ad dialog
        generator=new Random();

        int result=generator.nextInt(AD_PROBABILITY);
        switch(result)
        {
        // do nothing
        case 1:
        case 2:
        case 4:
        case 5:
        	break;
        	// give ad
        case 0:
        case 3:
        	//startActivity(new Intent(this,DeliverAd.class));
        	break;
        }
        
        // build app tab views
        buildAppTabs();
        
       // getPrefs(); // get shared preferences
        
        /* start app service */
        //startService(new Intent(this, CallCenterService.class));
        
        // delete below
       /* Log.d(TAG, "setting alarm");
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(getApplicationContext(), AppReceiver.class);
        i.putExtra("value", true);
        sendBroadcast(i);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, Time.parse("05/21/2010 12:07:00"), pi);
        */
    }
    
    @Override
    public void onDestroy() {
    	
    	//stopService(new Intent(this, CallCenterService.class));
    	super.onDestroy();
    }
    
    public void buildAppTabs() {
    	// build the main application tabs, for selecting which sub-app to use
    	
    	res = getResources();
    	tabHost = getTabHost();
    	
    	/******* CALL CENTER MAIN *******/
    	/*i = new Intent(this, TabCallCenter.class);
    	spec = tabHost.newTabSpec("callcenter").setIndicator("Home",
    			res.getDrawable(R.drawable.icon))
    			.setContent(i);
    	tabHost.addTab(spec);*/
    	
    	/******* PHONE PROFILES *******/
    	i = new Intent(this, TabPhoneProfiles.class);
    	spec = tabHost.newTabSpec("profiles").setIndicator("Profiles",
    			res.getDrawable(R.drawable.levels))
    			.setContent(i);
    	tabHost.addTab(spec);
    	
    	/******* TIMED PROFILES *******/
    	/*i = new Intent(this, TabTimedProfile.class);
    	spec = tabHost.newTabSpec("callcenter").setIndicator("Timed profiles",
    			res.getDrawable(R.drawable.icon))
    			.setContent(i);
    	tabHost.addTab(spec);*/
    	
    	/******* BLACKLIST *******/
    	i = new Intent(this, TabBlackList.class);
    	spec = tabHost.newTabSpec("blacklist").setIndicator("Blacklist",
    			res.getDrawable(R.drawable.filter))
    			.setContent(i);
    	tabHost.addTab(spec);
    }
    
    public void getPrefs() {
		
		/* read prefs */
    	
    	Log.d(TAG, "getPrefs()");
    	
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
    	start_on_boot = prefs.getBoolean("start_on_boot", false);
    	listener_service = prefs.getBoolean("key_voicemail_notifier", false);
    	
    		
    	/* take some actions, start shit up, based on prefs retreived */
    	
    	/*if(listener_service)
    	{
    		startService(new Intent(this, CallCenterService.class));
    	}
    	else
    		Toast.makeText(getApplicationContext(), "Service not started.", Toast.LENGTH_SHORT).show();
    	*/
    	/* show some toast */
		//Toast.makeText(getApplicationContext(), "START ON BOOT: " + start_on_boot, Toast.LENGTH_SHORT).show();
		//Toast.makeText(getApplicationContext(), "START ON BOOT: " + start_on_boot, Toast.LENGTH_SHORT).show();
	}
}