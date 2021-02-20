package com.reptilesoft.callcenterpro;

import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallStateListener extends PhoneStateListener {
	
	private SharedPreferences prefs;

	public void onCallStateChanged(int state, String incomingNumber) 
	  { 
	    super.onCallStateChanged(state, incomingNumber); 
	    switch(state) 
	    { 
	      case TelephonyManager.CALL_STATE_IDLE: 
	          Log.v("DEBUG", "IDLE YO"); 
	          break; 
	      case TelephonyManager.CALL_STATE_OFFHOOK: 
	          Log.v("DEBUG", "OFF HOOK"); 
	          break; 
	      case TelephonyManager.CALL_STATE_RINGING: 
	          Log.v("DEBUG", "RINGING YO"); 
	          break; 
	    } 
	  }
	/*
	@Override
	public void onCallForwardingIndicatorChanged(boolean cfi){
		
		if(cfi)
			Log.d("VM:", "YOU HAVE VM.");	
		else
			Log.d("VM:", "YOU HAVE NO VM.");
	}
	*/
	
	@Override
	public void onMessageWaitingIndicatorChanged(boolean mwi){
		
		if(mwi)
			{
				Log.d("MWI", "There is new voicemail.");
				startReminding();
			}
		else
			Log.d("MWI", "There is no voicemail to get.");
	}
	
	public void startReminding(){
		
		/* user has a voicemail, start reminding them now */
		
		int delay = 0;
		int period = 6000;
		
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask(){
			
			public void run(){
				
				/* play sound */
				Log.d("REMINDER", "You have new voicemail...");
			}
		}, delay, period);
	}
}
