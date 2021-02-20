package com.reptilesoft.callcenterpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class TimedReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		/* when a timer goes off, will get profile info and activate */
		
		Bundle bundle = intent.getExtras();
		int profileId = bundle.getInt("profileId");
		Toast.makeText(context, "Activating profile #" + profileId, Toast.LENGTH_SHORT).show();		
		
		Intent newIntent = new Intent(com.reptilesoft.callcenterpro.CallCenter.context, Activator.class);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.putExtra("profileId", profileId);
		context.startActivity(newIntent);
	}
}
