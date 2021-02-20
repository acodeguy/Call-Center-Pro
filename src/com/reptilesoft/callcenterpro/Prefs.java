package com.reptilesoft.callcenterpro;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Prefs extends PreferenceActivity {

	@Override
	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		addPreferencesFromResource(R.xml.prefs);
	}
}