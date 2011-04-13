package com.nerd.alarm;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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
    private PowerManager.WakeLock mWakeLock;
    private Timer mPowerTimer;
    private KeyguardLock mKeyguardLock; 
    private int mCounter=0;
    database_adapter db = new database_adapter(this); 

    
    
    public void onCreate(Bundle savedInstanceState) {
    	// Debug.waitForDebugger();
    	setVolumeControlStream(AudioManager.STREAM_MUSIC); // We want the user to be able to turn us down..
    	super.onCreate(savedInstanceState);
    	setTitle(getString(R.string.alarmtitle));
        
    	myText2=getString(R.string.alarmtext2);
    	
    	//http://stackoverflow.com/questions/628659/how-can-i-manage-audio-volumes-sanely-in-my-android-app/674207#674207
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
    
    @Override
    protected void onPause(){
        super.onPause();
        cancelWakeLock();
        //exitKeyguard(); 
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        cancelWakeLock();
        //exitKeyguard(); 
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
            //mCounter=a.getInt(5); 
            mTtitleDisplay.setText(myText1); 
            db.updateStatistic(mAlarmID,mCounter,101);
            db.close();
            
        }
        else
        {
        	finish();
        	//myText1=getString(R.string.alarmtext1);
        }
        PowerUp();
        //UnlockKeyguard();
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
        saySomething();
    }
    
    private void PowerUp() 
    { 
    	//http://groups.google.com/group/android-developers/browse_thread/thread/cc8d7ab760946ee4
    	PowerManager pm = (PowerManager) getSystemService (Context.POWER_SERVICE); 
        cancelWakeLock(); 
           
         //PowerManager.ON_AFTER_RELEASE          
           // and we keep it on for a bit after release - seem to actualy stop the device powering up when 
           // not on charge 
            mWakeLock = pm.newWakeLock( 
                         PowerManager.FULL_WAKE_LOCK |  // could use bright instead 
                         PowerManager.ACQUIRE_CAUSES_WAKEUP  // so we actually wake the device  
                         , "Nerd Alarm"); 
             
            mWakeLock.acquire(); 
            
         mPowerTimer = new Timer(); 

            
            mPowerTimer.schedule( 
                             new TimerTask() 
                             { 
                            	 @Override 
                            	 	public void run() 
                            	 	{ 
                            		 	//Toast.makeText(AlarmActivity.this, "Power timer - releasing", Toast.LENGTH_SHORT).show();
                            		 	cancelWakeLock(); 
                            	 	} 
                            } , 10000); 
                            

   } 

   private void cancelWakeLock() 
    { 
            if( mWakeLock != null ) 
            { 
                    mWakeLock.release(); 
                    mWakeLock = null; 
            } 
    }

   
   private void UnlockKeyguard() 
   { 
           if( mKeyguardLock != null ) 
           { 
                   return; // already unlocked 
           } 
           /*
           KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
           KeyguardLock kgl = kgm.newKeyguardLock("NerdAlarm");

           if (kgm.inKeyguardRestrictedInputMode())
               kgl.disableKeyguard();
           
           kgm.exitKeyguardSecurely(null);
           */
           
           KeyguardManager  kg = (KeyguardManager ) getSystemService (Context.KEYGUARD_SERVICE); 
           
           if( kg.inKeyguardRestrictedInputMode()) 
           { 
                   mKeyguardLock = kg.newKeyguardLock( "Nerd Alarm" ); 
                   mKeyguardLock.disableKeyguard(); 
           } 
           else 
           { 
                   mKeyguardLock = null; // make sure we are tidy 
           } 
   } 
   
   
   private void exitKeyguard() 
   { 
           if( mKeyguardLock != null ) // we disabled keyguard 
           { 
                   KeyguardManager  kg = (KeyguardManager ) getSystemService (Context.KEYGUARD_SERVICE); 
                   if( kg.inKeyguardRestrictedInputMode()) 
                   { 
                           kg.exitKeyguardSecurely((OnKeyguardExitResult) this); 
                           // show the user the keyguard, so they can dismiss it - response happens via the callback 
                           //onKeyguardExitResult 
                   } 
                   else 
                   { 
                           mKeyguardLock = null; 
                           saySomething(); 
                           // do the actions we wanted to do but had to defer till the user unlocked us 
                   } 
           } 
           else 
           { 
        	   saySomething(); 
           } 
   } 

   public void onKeyguardExitResult(boolean success) 
   { 
           if( success ) 
           { 	
        	   	   //mKeyguardLock.reenableKeyguard(); 
                   mKeyguardLock = null; // don't want this any more 
                   saySomething(); 
           } 
           else 
           { 
                   // user didn't dismiss keyguard, doing nothing 
                   if( mKeyguardLock != null ) // should never be null 
                           mKeyguardLock.reenableKeyguard(); 
           } 
   } 
  
   
}
