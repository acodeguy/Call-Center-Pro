package com.reptilesoft.callcenterpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class TabBlackList extends ListActivity {
	
	private final int MENU_CLEAR_BL = 0, MENU_ADD = 1, MENU_REMOVE = 2, MENU_ADD_ALL = 3;
	
	private final int PICK_CONTACT = 1;
	
	private final String TAG = "TabBlackList.java";
	
	int counter = 0;
	
	String [] idArray, 
		wlRecord; // used for name
	private int IDpos;
	
	ListView lv;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		//setContentView(R.layout.blacklist_menu);
		setupView();
		getBlacklist();
		//registerForContextMenu(getListView());
	}
	
	@Override
	public void onResume() {
		
		Log.d(TAG, "onResume() called");
		super.onResume();
		
		getBlacklist();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu m) {
		// if the menu button is hit...
		m.add(0, MENU_ADD,0,"Add").setIcon(android.R.drawable.btn_plus);
		//m.add(0, MENU_ADD_ALL, 0, "Add all");
		//m.add(0, MENU_CLEAR_BL, 0, "Clear blacklist").setIcon(android.R.drawable.btn_minus);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Options");
		menu.add(0, MENU_REMOVE, 0, "Delete from blacklist");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case MENU_REMOVE:
			Log.d(TAG, "attempting to delete #" + idArray[info.position]);
			removeFromBlacklist(idArray[info.position]);
			onResume();
		default:
		return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		
		switch(mi.getItemId())
		{
		case MENU_ADD:
			addToBlacklist();
			onResume();
			return true;
			
		case MENU_CLEAR_BL:
			removeAll();
			onResume();
			return true;
			
		case MENU_ADD_ALL:
			addAllToBlacklist();
			onResume();
			return true;
		}
		return false;
	}
	
	@Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) 
    {
      super.onActivityResult(reqCode, resultCode, data);

      switch (reqCode) 
      {
        case (PICK_CONTACT) :
          if (resultCode == Activity.RESULT_OK) 
          {
            Uri contactData = data.getData();
            Uri uriVm = Uri.withAppendedPath(contactData, ContactsContract.Contacts.SEND_TO_VOICEMAIL);
            Cursor c =  managedQuery(contactData, null, null, null, null);
            ContentValues values = new ContentValues();
            if (c.moveToFirst()) 
            {
            	String name = c.getString(c.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
            	Log.d(TAG, "Attempting to amend contact");
            	Log.d(TAG, "putting into dataset");
            	values.put(ContactsContract.Data.SEND_TO_VOICEMAIL, 1);
            	Log.d(TAG, "updating: " + contactData +": " + uriVm + " with " + values);
            	try{
            		getContentResolver().update(contactData, values, null, null);
            		//cr.update(uriVm, values, null, null);
            	}catch(Exception e){
            		Log.e(TAG, e.toString());
            	}
            	Log.d(TAG, "done!");
            	Toast.makeText(this, name + " added", Toast.LENGTH_SHORT).show();
            	
            }
          }
          break;
      }
    }

	/* -----------------------------------------------------------------*/
	
	public void removeAll() {
		
		/* remove everyone from the blacklist */
		
		Log.d(TAG, "removeAll()");
		
		new AlertDialog.Builder(this)
			.setIcon(R.drawable.cross)
			.setTitle("Blacklist")
			.setMessage("Clear all contacts from the blacklist?")
			.setNegativeButton("No way", new OnClickListener() {
				
				public void onClick(DialogInterface d, int which) {
					
					return;
				}
			})
			.setPositiveButton("Sure", new OnClickListener() {
				
				public void onClick(DialogInterface d, int which) {
					
					Uri contact = ContactsContract.Contacts.CONTENT_URI;
					ContentValues values = new ContentValues();
					values.put(ContactsContract.Data.SEND_TO_VOICEMAIL, 0);
					getContentResolver().update(contact, values, null, null);
					values.clear();
				}
			}).show();
	}
	
	public void removeFromBlacklist(String idToDelete) {
		Log.d(TAG, "deleteFromBL()");
		Uri contact = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, idToDelete);
		Log.d(TAG, "Uri returned okay: " + contact);
		ContentValues values = new ContentValues();
		values.put(ContactsContract.Data.SEND_TO_VOICEMAIL, 0);
		try{
			Log.d(TAG, idToDelete + ": updating..." + contact + " with " + values);
    		getContentResolver().update(contact, values, null, null);
        	Log.d(TAG, "updated!");
    		//cr.update(uriVm, values, null, null);
    	}catch(Exception e){
    		Log.e(TAG, e.toString());
    	}
	}

	public void addToBlacklist() {
		
		// show the intent contact picker
		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(i, PICK_CONTACT);
	}
	
	public void getBlacklist() {
		
		/* get all on whitelist */
		
		Log.d(TAG, "getBlackList()");
		
		counter = 0;
		String[] projection = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Data.SEND_TO_VOICEMAIL,
				ContactsContract.Data._ID
		};
		String where = ContactsContract.Contacts.SEND_TO_VOICEMAIL;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, where, null, sortOrder);

		if(c.getCount() > 0){
			
			Log.d(TAG,"moving to first");
			c.moveToFirst();
			
			wlRecord = new String[c.getCount()];
			idArray = new String[c.getCount()];
			
			do{
				Log.d(TAG, "assigning name");
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				Log.d(TAG, "assinging vm status");
				String vmStatus = c.getString(c.getColumnIndex(ContactsContract.Contacts.SEND_TO_VOICEMAIL));
				Log.d(TAG, "assigning id");
				String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
				Log.d(TAG, "assigning to array: " + name + " / " + vmStatus + "ID: " + id);
				wlRecord[counter] = name;
				idArray[counter] = id;
				counter++;
			}while(c.moveToNext());
			Log.d(TAG, "setting lv");
			setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, wlRecord));
			Log.d(TAG, "lv set");
		}
		else
			{
				Log.d(TAG, "cursor empty, null list adapter");
				setListAdapter(null);
			}
	}

	
	public void setupView(){
		Log.d("View", "Setting listview");
		lv = getListView();
		Log.d(TAG, "setting text search");
		lv.setTextFilterEnabled(true);
		Log.d(TAG, "setting onclick listener");
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// for activation, later
				IDpos = position;
				
				final CharSequence[] items = {
						"Delete"
						};

				AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
				builder.setTitle(wlRecord[IDpos]);
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	
				    	// get which item was clicked and give options for it;
				    	
				    	switch(item)
				    	{
				    	case 0: // remove from blacklist
				    		removeFromBlacklist(idArray[IDpos]);
				    		onResume();
				    		break;
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
	
	public void addAllToBlacklist() {
		
		/* will add the entire phonebook to blacklist */
		
		
		//TODO dialog with OK/NO?
		new AlertDialog.Builder(this)
			.setIcon(R.drawable.cross)
			.setTitle("Blacklist")
			.setMessage("Really add your entire phonebook to the Blacklist?")
			.setNegativeButton("No way", new OnClickListener() {
				
				public void onClick(DialogInterface d, int which) {
					
					Log.d(TAG, "no way clicked");
					return;
				}
			})
			.setPositiveButton("Sure", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					Log.d(TAG, "Sure clicked");
					Uri contact = ContactsContract.Contacts.CONTENT_URI;
					ContentValues values = new ContentValues();
					values.put(ContactsContract.Data.SEND_TO_VOICEMAIL, 1);
					getContentResolver().update(contact, values, null, null);
					values.clear();
				}
			})
			.show();
	}

}
