package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class About extends Activity implements OnClickListener {

	private ImageButton img_btn_visit_site;
	private Button btn_rs_apps;
	PackageManager pm;
	PackageInfo pi;
	TextView text_view_app_name_ver;
	
	@Override
	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		pm = getPackageManager();
		try{
			pi=pm.getPackageInfo("com.reptilesoft.callcenterpro",0);
		}catch(NameNotFoundException e){
			e.printStackTrace();
		}
		
		setContentView(R.layout.about);
		
		// get views
		img_btn_visit_site = (ImageButton)findViewById(R.id.btn_visit_site);
		img_btn_visit_site.setOnClickListener(this);
		
		btn_rs_apps=(Button)findViewById(R.id.btn_rs_apps);
		btn_rs_apps.setOnClickListener(this);
		
		text_view_app_name_ver=(TextView)findViewById(R.id.app_title_ver);
		text_view_app_name_ver.setText("Call Center Pro v"+pi.versionName);
	}

	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.btn_visit_site:
			// launch browser
			Intent i = new Intent(android.content.Intent.ACTION_SEND);
			i.setType("plain/text");
			i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"reptilesoft@gmail.com"});
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, "[CCP-1.7.2] Bug report / comments");
			startActivity(Intent.createChooser(i, "Send mail..."));
			break;
		case R.id.btn_rs_apps:
			Intent rs_apps=new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:%22Reptile%20Soft%22"));
			startActivity(rs_apps);
			break;
		}
	}
}