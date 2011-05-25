package com.nerd.alarm;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private String mySpeak1,mySpeak2,mySpeak3, mNextAlarm;
    
    private PowerManager.WakeLock mWakeLock;
    private Timer mPowerTimer;
    private KeyguardLock mKeyguardLock; 
    
    private int mCounter=0,mRepeat=0, mMode=0,mEnabled=0, mHour,mMinute;
    private int mTest=0,mSnooze=0, mDefaultVolume=0,mPlay,mStatus=0;
    private boolean mVibrate,mNerd,mSpeak;
    private MediaPlayer _mp = null;
    private PowerManager _pm = null;
    
    private String _modes [];
    private int mode = Activity.MODE_PRIVATE;
    
    SharedPreferences mySharedPreferences; 
    database_adapter _db = new database_adapter(this); 
    AudioManager mAudioManager = null;
    Display_Records my_records = null;
    
    public void onCreate(Bundle savedInstanceState) {
    	
    	setVolumeControlStream(AudioManager.STREAM_MUSIC); // We want the user to be able to turn us down..
    	super.onCreate(savedInstanceState);
    	setTitle(getString(R.string.alarmtitle));
    	mySpeak2=getString(R.string.alarmtext2);
    	
    	//http://stackoverflow.com/questions/628659/how-can-i-manage-audio-volumes-sanely-in-my-android-app/674207#674207
        // see http://getablogger.blogspot.com/2008/01/android-pass-data-to-activity.html
        // http://www.androidcompetencycenter.com/2009/03/tutorial-how-to-start-a-new-activity/
        
    	setContentView(R.layout.alarm_wake);
        
        textToSpeech = new TextToSpeech(this, this);
        
        loadDetails();
        
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                checkMedia();
            	Toast.makeText(AlarmActivity.this, "Snooze on", Toast.LENGTH_SHORT).show();
                
            }
        });
        
        
   }
    
    @Override
    protected void onPause(){
        super.onPause();
        cancelWakeLock();
        rescheduleAlarm(2);
        //checkMedia();
        //exitKeyguard(); 
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        cancelWakeLock();
        checkMedia();
        rescheduleAlarm(3);
        //exitKeyguard(); 
    }
    
    public void loadDetails (){
    	
    	Bundle extras = getIntent().getExtras(); 
        if(extras !=null)
          {mAlarmID = extras.getLong("AlarmID");}
        
        if (mAlarmID!=0) {
        	TextView mTtitleDisplay = (TextView) findViewById(R.id.wakeup);
        	
        	_db.open();
            Cursor a = _db.getAlarm(mAlarmID);	
            	
            	mySpeak1=a.getString(2); // Title 
	            mMode=a.getInt(5); 
	            mEnabled=a.getInt(4); 
	            mRepeat=a.getInt(3); 
	            mCounter=a.getInt(6);
	            mTest=a.getInt(7);
	            
	            if(mEnabled==0){finish();}
	            
	            String[] parts = a.getString(1).split(":",2);
	        	mHour = Integer.valueOf(parts[0]);
	        	mMinute = Integer.valueOf(parts[1]);
	        	
            mTtitleDisplay.setText(mySpeak1); 
                        
        }
        else
        {
        	finish();
        }
       
        //Let's get mode details..
        
        _modes=getResources().getStringArray(R.array.modes_array); 
        mySharedPreferences = getSharedPreferences(_modes[mMode],mode);
    	
        mySpeak3=mySharedPreferences.getString("greetingPref","");
    	mVibrate=mySharedPreferences.getBoolean("vibratePref",false);
    	mNerd=mySharedPreferences.getBoolean("nerdPref",false);
    	mSnooze=Integer.parseInt(mySharedPreferences.getString("snoozePref","5"));
    	
    	mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
   		mDefaultVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
   		
        setCharacteristics();
    }
    
    public void checkMedia(){
    	//if (_mp!=null){_mp.pause();
        //_mp.release();}
    	
    	 // DEALLOCATE ALL MEMORY
    	    if (_mp != null) {
    	        if (_mp.isPlaying()) {
    	            _mp.stop();
    	        }
    	        _mp.release();
    	        _mp= null;
    	    }

    	
    }
    public void handleSpeak(View view) {
        saySomething();
        //playSomething();
    }
    
    public void stopAlarm(View view) {
    	rescheduleAlarm(3);
    }
    private void saySomething() {
    	mySpeak3 = mySharedPreferences.getString("greetingPref", "");
    	textToSpeech.speak(mySpeak1, TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak(mySpeak3, TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(mySpeak2, TextToSpeech.QUEUE_ADD, null);
    }
    
    private void playSomething(int _play){
    
    //Useful : http://www.anddev.org/video-tut_-_playing_mediamp3_on_the_emulator-t156.html
    	_mp = new MediaPlayer();
    	
    	if(_play>0) {
			
						_mp = MediaPlayer.create(AlarmActivity.this,_play);
						try {_mp.prepare();} 
								catch (IllegalStateException e) {e.printStackTrace();} 
								catch (IOException e) {e.printStackTrace();}
		
					}
    	
    	else 		{
		    	 		try {_mp.setDataSource(this,Uri.parse(mySharedPreferences.getString("soundPref", "alarm_alert")));} 		    	 	
			    	 		catch (IllegalArgumentException e1) {e1.printStackTrace();} 
			    	 		catch (SecurityException e1) {e1.printStackTrace();} 
			    	 		catch (IllegalStateException e1) {e1.printStackTrace();} 
			    	 		catch (IOException e1) {e1.printStackTrace();}
		    	 
		    	 		 _mp.setAudioStreamType(AudioManager.STREAM_ALARM);
			    		 _mp.setLooping(false);
		    		 
			    		 try {_mp.prepare();} 
			    		 	 catch (IllegalStateException e) {e.printStackTrace();} 
				    		 catch (IOException e) {e.printStackTrace();}
    				}
		   
    	_mp.start();
	
    	 
    	_mp.setOnCompletionListener(new OnCompletionListener(){
         // @Override
         public void onCompletion(MediaPlayer arg0) {
        	 Toast.makeText(AlarmActivity.this,getString(R.string.sound_over), Toast.LENGTH_SHORT).show(); 
        	 rescheduleAlarm(mStatus);
         }
    	});
    	 	
    }
    

	public void onInit(int i) {
        //saySomething();
    }
    
    private void PowerUp() 
    { 
    	//http://groups.google.com/group/android-developers/browse_thread/thread/cc8d7ab760946ee4
    	_pm = (PowerManager) getSystemService (Context.POWER_SERVICE); 
        cancelWakeLock(); 
           
         //PowerManager.ON_AFTER_RELEASE          
           // and we keep it on for a bit after release - seem to actually stop the device powering up when 
           // not on charge 
            mWakeLock = _pm.newWakeLock( 
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
                           //saySomething(); 
                           // do the actions we wanted to do but had to defer till the user unlocked us 
                   } 
           } 
           else 
           { 
        	   //saySomething(); 
           } 
   } 

   public void onKeyguardExitResult(boolean success) 
   { 
           if( success ) 
           { 	
        	   	   //mKeyguardLock.reenableKeyguard(); 
                   mKeyguardLock = null; // don't want this any more 
                   //saySomething(); 
           } 
           else 
           { 
                   // user didn't dismiss keyguard, doing nothing 
                   if( mKeyguardLock != null ) // should never be null 
                           mKeyguardLock.reenableKeyguard(); 
           } 
   } 
  
   private void setCharacteristics(){
	   	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	   	mPlay=0;
	   	mSpeak=true;
	   	mStatus=0;
	   	
   	//Set characteristics..
       if (mMode==0){ 						//Bunny
	       	if (mCounter==0){
	       		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume / 3), AudioManager.FLAG_VIBRATE);
	       		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	       		mPlay=R.raw.b_birds;			
	       		mSpeak=false;
	       		mStatus=1;
	       		}
	       	else if (mCounter==1){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume / 2), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	   			mPlay=R.raw.b_b_birds;
	   			mSpeak=false;
	   			mStatus=1;
	       		}
	       	else if (mCounter==3){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume-2), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	       		}
	       	else if (mCounter==4){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume-1), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	       		}
	       	else if (mCounter>4){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	       		}
	       
	       }
       else if (mMode==1){}
       else if (mMode==2){}
       else if (mMode==3){}
       else if (mMode==4){}
       else if (mMode==5){}
   	
       PowerUp();
   		//UnlockKeyguard();
       if(mSpeak){saySomething();}
       playSomething(mPlay);
  	
   }
   
   private void rescheduleAlarm(int _status){	   
	   my_records = new Display_Records();
       my_records.SetMe(AlarmActivity.this); 
       
       int mHour, mMinute;
       String mTime;
       final Calendar c = Calendar.getInstance();
       mHour = c.get(Calendar.HOUR_OF_DAY);
       mMinute = c.get(Calendar.MINUTE);
       
       Toast.makeText(AlarmActivity.this,"status :" + _status, Toast.LENGTH_SHORT).show();
   	     
       if (_status==1) {
    	   // Just reload the alarm again in 15 mins.
    	   mNextAlarm = my_records.setTime((int)(mAlarmID),mHour,mMinute+2,0);//should be 15
       	   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();
       	   mCounter+=1;
       }
       else if (_status==2) {
    	   // Snooze
    	   mNextAlarm = my_records.setTime((int)(mAlarmID),0,mSnooze,0);//should be 15
       	   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();
       	   mCounter+=1;
       	}
       else if (_status==3) {
    	   // Reschedule
    	   if (mRepeat>0) {
		    	   mNextAlarm = my_records.setTime((int)(mAlarmID),mHour,mMinute,mRepeat);
		       	   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();}
    	   else
    		   mCounter=0;
    	   	   mEnabled=0;
    	   }
       
       /* Testing Alarm */
       mTime = mySpeak1 + "-" + mHour + ":" + mMinute;
        
       _db.updateStatistic(mAlarmID,mCounter,mEnabled,mTime + ":" + mMode);
       _db.close();
       //finish();
   }
   
}
