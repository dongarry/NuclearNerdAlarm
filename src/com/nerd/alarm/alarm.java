package com.nerd.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class alarm extends Activity {
    
	private static final int MY_DATA_CHECK_CODE = 1;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 

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
    
}