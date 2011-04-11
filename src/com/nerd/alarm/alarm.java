package com.nerd.alarm;


import android.app.ListActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
//import android.os.Debug;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.speech.tts.TextToSpeech;
//import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class alarm extends ListActivity {
    
	private static final int MY_DATA_CHECK_CODE = 1;
	database_adapter db = new database_adapter(this); 
		
	//private CursorAdapter dataSource;
	//ListView lv = (ListView)this.findViewById(android.R.id.list);   
	
	/*listAdapter = new SimpleCursorAdapter(this, 
			 R.layout.alarm_list, data, 
            fields, new int[] {R.id.alarmTime,R.id.firstLine, R.id.secondLine });*/
	
	private static final String fields[] = { "time", "title","repeat",BaseColumns._ID };
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
    protected void onResume(){
        super.onResume();
        loadAlarms();
        
    }
   
    protected void onClose(){
        db.close();
    }
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    
   
    
    public void loadAlarms(){
    	//---get all Alarms---
        //db.open();
    	Cursor a = db.getAlarms();
        DisplayAlarm(a);
        //db.close();
    }
    
    public void addalarm(View view) {
		Intent addIntent = new Intent(this,addalarm.class); 
		startActivity(addIntent);
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
		    default:
		    	Toast.makeText(alarm.this, getString(R.string.delete_all), Toast.LENGTH_SHORT).show();
		    	/* Ensure we cancel all enabled alarms when doing this */
		    	return (db.deleteAllAlarms());
		    	
		    	/*
		    	Intent displayIntent = new Intent(this,display_records.class); 
				startActivity(displayIntent);
		    	return true;*/
		    }
		}
	 
	   public void setalarm(View view) {
	        Toast.makeText(alarm.this, "set alarm", Toast.LENGTH_SHORT).show();
	        //Intent intent = new Intent(this, AlarmService.class);
	        //startService(intent);

	      Intent alarmIntent = new Intent(this, AlarmActivity.class);
	        long currentTime = SystemClock.elapsedRealtime();
	        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

	        alarmManager.set(AlarmManager.ELAPSED_REALTIME, currentTime + 3000, pendingIntent);
	    }
    
	   public void DisplayAlarm(Cursor data){
		    
	    	 setListAdapter(new SimpleCursorAdapter(this, 
	    			 R.layout.alarm_list, data, 
                     fields, new int[] {R.id.alarmTime,R.id.firstLine, R.id.secondLine }));
	    	 
		   }
}
