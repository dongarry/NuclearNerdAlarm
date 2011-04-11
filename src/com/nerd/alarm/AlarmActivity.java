package com.nerd.alarm;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AlarmActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private long mAlarmID =0;
    private String myText1;
    private String myText2;
    
    database_adapter db = new database_adapter(this); 
    
    
    public void onCreate(Bundle savedInstanceState) {
    	// Debug.waitForDebugger();
    	//http://stackoverflow.com/questions/628659/how-can-i-manage-audio-volumes-sanely-in-my-android-app/674207#674207
    	setVolumeControlStream(AudioManager.STREAM_MUSIC); // We want the user to be able to turn us down..
    	super.onCreate(savedInstanceState);
        
    	myText2=getString(R.string.alarmtext2);
    	
/*
    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Nerd Alarm");
    	wl.acquire();
    	*/
    	/*
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
    		    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
    		    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    	*/
        // see http://getablogger.blogspot.com/2008/01/android-pass-data-to-activity.html
        // http://www.androidcompetencycenter.com/2009/03/tutorial-how-to-start-a-new-activity/
        
        
        setContentView(R.layout.alarm_wake);
        
        textToSpeech = new TextToSpeech(this, this);
        
        loadDetails();
        
      //icon snooze handle
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                Toast.makeText(AlarmActivity.this, "Snooze on", Toast.LENGTH_SHORT).show();
            }
        });
        
        //wl.release();
   }
    
    public void loadDetails (){
    	Bundle extras = getIntent().getExtras(); 
        if(extras !=null)
        {
        	mAlarmID = extras.getLong("AlarmID");
        }
        
        
        if (mAlarmID!=0) {
        	//Load the alarm settings
        	TextView mTtitleDisplay = (TextView) findViewById(R.id.wakeup);
        	db.open();
            Cursor a = db.getAlarm(mAlarmID);	
            myText1=a.getString(2); // Title    	
            mTtitleDisplay.setText(myText1); 
            db.close();
        }
        else
        {
        	finish();
        	//myText1=getString(R.string.alarmtext1);
        }
        saySomething();
    }
    
    public void handleSpeak(View view) {
        saySomething();
    }

    private void saySomething() {
    	
    	textToSpeech.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak(myText2, TextToSpeech.QUEUE_ADD, null);
    }

    public void onInit(int i) {
        //saySomething();
    }
}
