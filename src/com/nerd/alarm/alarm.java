package com.nerd.alarm;



import android.app.ListActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.speech.tts.TextToSpeech;
import android.widget.ListView;

import android.widget.Toast;

public class alarm extends ListActivity {
    private long m_alarmID;
	private static final int MY_DATA_CHECK_CODE = 1;
	database_adapter db = null; 
	private CustomSqlCursorAdapter dataSource;
	private static final String fields[] = { "time", "title","enabled","counter","mode",BaseColumns._ID };
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
   
        if (this.db == null) {
        	this.db = new database_adapter(this);
        }
        
        db.open();
        
        //Debug.waitForDebugger();
        setContentView(R.layout.main); 
        setTitle(getString(R.string.app_name));
        
        loadAlarms();
        
	//Taken from Tutorial on TexttoSpeech
    Intent checkIntent = new Intent();
    checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
    startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    
    } 
    
    @Override
        protected void onRestart() {
            super.onRestart();
            //new SelectDataTask().execute();
        }

    @Override
        protected void onPause() {
            super.onPause();
            //this.db.close();
        }
    
    @Override
    protected void onResume(){
        super.onResume();
        dataSource.getCursor().requery();
    }
    
    protected void onClose(){
        //super.onClose();
    	this.db.close();
    }
    
    
    // Edit existing alarms
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
 
        //Intent intent = new Intent(Intent.ACTION_CALL);
        Cursor _cursor = (Cursor) dataSource.getItem(position);
        m_alarmID = _cursor.getLong(_cursor.getColumnIndex(BaseColumns._ID));
    	
        if(m_alarmID>0){
        	Bundle aBundle = new Bundle();
        	aBundle.putLong("Alarm",m_alarmID);
        	Intent editIntent = new Intent(this,addalarm.class); 
        	editIntent.putExtras(aBundle);       
		    startActivityForResult(editIntent,0);
        }
    }
    
    //Check Text to Speech..
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case MY_DATA_CHECK_CODE: {
    	    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            		}
        		}
        default: {
    	    	
    	    }
        }
    }
   
    public void loadAlarms(){
    	//---get all Alarms---
        //db.open();
    	Cursor _a = db.getAlarms();
        DisplayAlarm(_a);
        //db.close();
    }
    
    public void addalarm(View view) {
    	Bundle aBundle = new Bundle();
    	aBundle.putLong("Alarm",0);
		Intent addIntent = new Intent(this,addalarm.class); 
		addIntent.putExtras(aBundle);       
	    startActivityForResult(addIntent,0);
		
    }

	// Display our own custom menu when menu is selected.
	 @Override
	
	public boolean onCreateOptionsMenu(Menu mymenu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alarm_menu, mymenu);
		return true;
		}
	 
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		    case R.id.preferences:
		      	Intent prefIntent = new Intent(this,alarm_pref.class); 
				startActivity(prefIntent);
		      	return true;
		    case R.id.clear_all:
		    	Toast.makeText(alarm.this, getString(R.string.delete_all), Toast.LENGTH_SHORT).show();
		    	/* TODO Ensure we cancel all enabled alarms when doing this */
		    	boolean _del = db.deleteAllAlarms();
		    	dataSource.getCursor().requery();
		    	return (_del);
		    	
			 default:
		    	Toast.makeText(alarm.this,getString(R.string.err_greeting) + item.getItemId(), Toast.LENGTH_SHORT).show();
		    	return true;
		    	/*
		    	Intent displayIntent = new Intent(this,display_records.class); 
				startActivity(displayIntent);
		    	return true;*/
		    }
		}

	 /*
	   public void setalarm(View view) {
	        //Toast.makeText(alarm.this, "set alarm", Toast.LENGTH_SHORT).show();
	        //Intent intent = new Intent(this, AlarmService.class);
	        //startService(intent);

	      Intent alarmIntent = new Intent(this, AlarmActivity.class);
	        long currentTime = SystemClock.elapsedRealtime();
	        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

	        alarmManager.set(AlarmManager.ELAPSED_REALTIME, currentTime + 3000, pendingIntent);
	    }
      */ 
	   
	   public void DisplayAlarm(Cursor _data){ 
		   // with help from : http://kahdev.wordpress.com/2010/09/27/android-using-the-sqlite-database-with-listview/
		   dataSource = new CustomSqlCursorAdapter(this, 
	    			 R.layout.alarm_list, _data, 
                   fields, new int[] {R.id.alarmTime,R.id.firstLine, R.id.secondLine},db);
	    	 setListAdapter(dataSource);
	   
		   }

}
