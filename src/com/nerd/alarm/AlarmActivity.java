package com.nerd.alarm;

import android.app.Activity;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.AudioManager;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/* Some Credits:
 * http://stuffthathappens.com/blog/2007/09/10/urlopenstream-might-leave-you-hanging/
 * http://stackoverflow.com/questions/628659/how-can-i-manage-audio-volumes-sanely-in-my-android-app/674207#674207
 * http://getablogger.blogspot.com/2008/01/android-pass-data-to-activity.html
 * http://www.androidcompetencycenter.com/2009/03/tutorial-how-to-start-a-new-activity/
 * http://www.anddev.org/video-tut_-_playing_mediamp3_on_the_emulator-t156.html
 * http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/TextToSpeechActivity.html
 * http://groups.google.com/group/android-developers/browse_thread/thread/cc8d7ab760946ee4
 * http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * 
 * Nerd Alarm - This is the activity called when the alarm wakes up
 *  This Class require more refactoring -- move all operations under the Alarm Class umbrella..,
 *  This is on my TODO list..(as well as)
 *   Add more functionality to the Test me option - currently this just keeps the keyguard on
 * 
 */

public class AlarmActivity extends Activity implements TextToSpeech.OnInitListener {
    
	private TextToSpeech textToSpeech;
    private long mAlarmID =0;
    private int mAlarmMode=0;
    
    private PowerManager.WakeLock mWakeLock;
    private KeyguardManager mKeyguardMan; 
    private KeyguardLock mKeyguardLock=null; 
    
    private int mDefaultVolume=0;   
    private PowerManager pm = null;
    private Alarm oAlarm = null;
	private boolean bolExit=true;
	
    TextView mNerdDetails;
    AudioManager mAudioManager = null;
    
    
    public void onCreate(Bundle savedInstanceState) {
   	
    	setVolumeControlStream(AudioManager.STREAM_MUSIC); // We want the user to be able to turn us down..
    	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.alarm_wake);
    	
    	setTitle(getString(R.string.alarmtitle));
    	
    	mKeyguardMan = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);     	
    	mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
   		mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
   		textToSpeech = new TextToSpeech(this, this);
        
    	PowerUp();
        loadDetails();
    	
    	if(oAlarm.doNerd()){    	    
	        mNerdDetails = (TextView) findViewById(R.id.nerd_info);
	        mNerdDetails.setText(oAlarm.getNerdDetails() + getString(R.string.temp));
			mNerdDetails.setTypeface(null,Typeface.BOLD);
        }
        
    	oAlarm.soundAlarm();
            
        
        if(oAlarm.doVibrate()){
	        	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			   	v.vibrate(400);
        }

        UnlockKeyguard();
        
        if(oAlarm.getAlarmTestMe())	{
        							myCustomDialog dialog = new myCustomDialog(this); 
        							dialog.setCancelable(false);
        							dialog.setTitle(getString(R.string.testme_title));
        							dialog.show();
        }
    	
        
        
        final Button button = (Button) findViewById(R.id.snooze);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	oAlarm.ResetMedia();
            	if (oAlarm.getAlarmSnooze()>0) {
		            	oAlarm.rescheduleAlarm(1); //nextSnooze
		            	Toast.makeText(AlarmActivity.this, getString(R.string.snooze) + " : " + oAlarm.getAlarmSnooze(), Toast.LENGTH_SHORT).show();
            	}    
		        closeAlarm();
            }
        });
   }
    
    @Override
    protected void onPause(){
    	super.onPause();
           }
    
    @Override
    protected void onStop(){
    	if(bolExit)	{
    		closeAlarm();
    	}
    	
    	super.onStop();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	if (mAlarmMode==2){ //Nuclear - no easy out!
		        	if (oAlarm.getAlarmSnooze()>0) {
		            	oAlarm.rescheduleAlarm(1); //nextSnooze
		            	Toast.makeText(AlarmActivity.this, getString(R.string.snooze) + " : " + oAlarm.getAlarmSnooze(), Toast.LENGTH_SHORT).show();
		        		}    
        	}    	
        	closeAlarm();
        	finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void loadDetails (){    		
    		Bundle extras = getIntent().getExtras(); 
        	if(extras !=null)	{
        						mAlarmID = extras.getLong("AlarmID");
        						mAlarmMode = extras.getInt("AlarmMode");
        	}
        	
        	if (mAlarmID!=0) {
        		
	        		switch (mAlarmMode)	{
	        							case (0): oAlarm = new BunnyAlarm(mAlarmID,AlarmActivity.this);break;
	        							case (1): oAlarm = new NerdAlarm(mAlarmID,AlarmActivity.this);break;
	        							case (2): oAlarm = new NuclearAlarm(mAlarmID,AlarmActivity.this);break;
	        							case (3): oAlarm = new NinjaAlarm(mAlarmID,AlarmActivity.this);break;
	        							case (4): oAlarm = new AngryAlarm(mAlarmID,AlarmActivity.this);break;
	        							case (5): oAlarm = new HappyAlarm(mAlarmID,AlarmActivity.this);break;
	        							}
        		
	        
			        if(oAlarm.isValid())	{			        	
				            				TextView mTtitleDisplay = (TextView) findViewById(R.id.wakeup);
				            				mTtitleDisplay.setText(oAlarm.getAlarmTitle()); 
			        						}
			        else	{
			            		finish();
			        		}
						
		    }
        
        	else	{
        			finish();
        			}        
    }
    
    
    private void resetTalk(){
    	  mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mDefaultVolume), AudioManager.FLAG_VIBRATE);
    	  if (textToSpeech != null) 	{
    	    								textToSpeech.stop();
    	    								textToSpeech.shutdown();
    	    							}
    	}
    
    
    public void handleSpeak(View view) {
    									saySomething();
    									}
    
    
    public void stopAlarm(View view) 	{
    										closeAlarm();
    									}
    
    private void saySomething() {
    	textToSpeech.speak(oAlarm.getAlarmGreeting(), TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak(oAlarm.getSpeakLine(), TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(oAlarm.getSpeakNextLine(), TextToSpeech.QUEUE_ADD, null);        
    	}
        
    public void onInit(int i) {
		  if (i == TextToSpeech.SUCCESS) {
	            //TODO Consider doing something with locales?
			  	 if(oAlarm.doTalk()){saySomething();}
	        } 
		  else {}
	}
    
    
    private void PowerUp() { 
    	pm = (PowerManager) getSystemService (Context.POWER_SERVICE); 
         
    	mWakeLock = pm.newWakeLock( 
                         PowerManager.FULL_WAKE_LOCK |  // Use Partial Wake Lock
                         PowerManager.ACQUIRE_CAUSES_WAKEUP  // so we actually wake the device  
                         , "Nerd Alarm"); 
             
            mWakeLock.acquire(); // light up for 10 secs. -->10000 
    } 

   private void cancelWakeLock() { 
            if( mWakeLock != null ) 
            { 
                    mWakeLock.release(); 
                    mWakeLock = null; 
            } 
    	}

   
   private void UnlockKeyguard() {   	   
	   		if( mKeyguardLock != null ) 
           		{return;} 
           
	   		if( mKeyguardMan.inKeyguardRestrictedInputMode()) 
           		{ 
	   				mKeyguardLock = mKeyguardMan.newKeyguardLock("NerdAlarm");
	   				mKeyguardLock.disableKeyguard(); 
           		} 
           else 
           		{ mKeyguardLock = null; } 
   } 
   
   private void closeAlarm(){
	 	 	
        oAlarm.ResetMedia();
     	resetTalk();
     	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mDefaultVolume), AudioManager.FLAG_VIBRATE);
     	   
         if (!oAlarm.isScheduled() && oAlarm.isValid())	{
        
         				if (oAlarm.getAlarmRepeat() == 0) {
         		   	   										oAlarm.setAlarmEnabled(0);
         		   	   	}
         				
         				oAlarm.rescheduleAlarm(0);
         				//Toast.makeText(AlarmActivity.this, oAlarm.getAlarmStatus(), Toast.LENGTH_SHORT).show();						
         }

         Toast.makeText(AlarmActivity.this, oAlarm.getAlarmStatus(), Toast.LENGTH_SHORT).show();						
         
        bolExit=false;
    	oAlarm = null;
        exitKeyguard(); 
        cancelWakeLock();
        finish();
   }
   
   private void exitKeyguard() { 
           if( mKeyguardLock != null ){ 
        	       if( mKeyguardMan.inKeyguardRestrictedInputMode()) 
                   	{
                	   mKeyguardMan.exitKeyguardSecurely(null);
            	   	   mKeyguardLock.reenableKeyguard(); 
            	   	   mKeyguardLock = null;
                   	} 
                   else  
                   	{mKeyguardLock = null;} 
           } 
           else {} 
   	} 
 
}
