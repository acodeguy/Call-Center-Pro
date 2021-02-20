package com.reptilesoft.callcenterpro;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeliverAd extends Activity implements OnClickListener{
	
	Button button_close_ad;
	long AD_CLOSE_TIME = 10000;
	
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		setContentView(R.layout.advert);
		
		//Toast.makeText(this, "Press back to cancel", Toast.LENGTH_SHORT).show();
		
		// ads
        /*AdManager.setTestDevices( new String[] {
        		AdManager.TEST_EMULATOR, // Android emulator
        		"E83D20734F72FB3108F104ABC0FFC738", // My T-Mobile G1 Test Phone
        		} );*/
        
		// get other views
        //button_close_ad=(Button)findViewById(R.id.button_ok_close_ad);
		//button_close_ad.setOnClickListener(this);
		
		// finish activity after x seconds
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				finish();
			}
		}, AD_CLOSE_TIME);
	}

	
	public void onClick(View v) {
		/*
		switch(v.getId())
		{
		case R.id.button_ok_close_ad:
			finish();
			break;
			default:break;
		}*/
	}

}
