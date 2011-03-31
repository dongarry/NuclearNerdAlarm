package com.nerd.alarm;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.view.Window;
import android.speech.tts.TextToSpeech;
import android.widget.CursorAdapter;
import android.widget.ListView;
//import android.widget.SimpleCursorAdapter;
//import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class alarm extends Activity {
    
	private static final int MY_DATA_CHECK_CODE = 1;
	//private CursorAdapter dataSource;
	//ListView list = (ListView) findViewById(R.id.My_alarms);
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //We want our screens to be different!
        database_adapter db = new database_adapter(this); 
        setContentView(R.layout.main); 
        
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        
        setTitle(getString(R.string.app_name));
        
      //---get all Alarms---
        db.open();
        
        /* test
        long id;
        id = db.insertAlarm(
        		"10:05",
        		"Title 1",
        		0);        
        */
        
        Cursor a = db.getAllAlarms();
        
        if (a.moveToFirst())
        {	
        	do {          
                DisplayAlarm(a);
            } while (a.moveToNext());
        }
        db.close();
        
        
	//Taken from Tutorial on TexttoSpeech
    Intent checkIntent = new Intent();
    checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
    startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    
    public void addalarm(View view) {
		Intent addIntent = new Intent(this,addalarm.class); 
		startActivity(addIntent);
		Toast.makeText(alarm.this, "This is a test", Toast.LENGTH_SHORT).show();
//      System.out.print("this is a test");
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
		    	Toast.makeText(alarm.this, "Delete Clicked, Careful now!", Toast.LENGTH_SHORT).show();
		    	Intent displayIntent = new Intent(this,display_records.class); 
				startActivity(displayIntent);
		    	return true;
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
		   Toast.makeText(alarm.this, "set alarm :" + data.getString(2), Toast.LENGTH_SHORT).show();
	       
		   /*String RepeatDesc;

		   RepeatDesc=data.getString(3);
		   String[] fields = new String[] {"title", "repeat"};
		           
		   
		   if (RepeatDesc=="0"){
			   RepeatDesc="All Week";}
			   else{
				   RepeatDesc="M T W Th F"; }
	    	        
	    	        dataSource = new SimpleCursorAdapter(this,
	    	            R.layout.alarm_list, data, fields,
	    	            new int[] { R.id.firstLine, R.id.secondLine });
	    	        list.setAdapter(dataSource);*/
	   }
}