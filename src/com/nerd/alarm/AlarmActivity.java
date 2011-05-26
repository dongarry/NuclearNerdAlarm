package com.nerd.alarm;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
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
    private String mySpeak1,mySpeak2,mySpeak3, mNextAlarm, mSound;
    
    private PowerManager.WakeLock mWakeLock;
    private Timer mPowerTimer;
    private KeyguardLock mKeyguardLock; 
    
    private int mCounter=0,mRepeat=0, mMode=0,mEnabled=0, mHour,mMinute;
    private int mTest=0,mSnooze=0, mDefaultVolume=0,mStatus=0;
    private boolean mVibrate,mNerd,mMovingMode;
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
    	
    	mMovingMode = false;
    	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
    	
    	// TODO
    	/* Do we need to consider language locales: mTts.isLanguageAvailable(Locale.UK))
			mTts.isLanguageAvailable(Locale.FRANCE))
    	*/
    	
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
            	Toast.makeText(AlarmActivity.this, getString(R.string.snooze), Toast.LENGTH_SHORT).show();
            	mStatus=3;
            	rescheduleAlarm(2);
            }
        });
        
        
   }
    
    @Override
    protected void onPause(){
        super.onPause();
        cancelWakeLock();
        if (!mMovingMode) {rescheduleAlarm(3);}
        //checkMedia();
        //exitKeyguard(); 
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        cancelWakeLock();
        checkMedia();
        
        if (!mMovingMode){rescheduleAlarm(3);}
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
            	if (a!=null){ 
		            	mySpeak1=a.getString(2); // Title 
			            mMode=a.getInt(5); 
			            mEnabled=a.getInt(4); 
			            mRepeat=a.getInt(3); 
			            mCounter=a.getInt(6);
			            mTest=a.getInt(7);
			            
			            if(mEnabled==0){finish(); Toast.makeText(AlarmActivity.this, "Finished 1", Toast.LENGTH_SHORT).show();}
			            
			            String[] parts = a.getString(1).split(":",2);
			        	mHour = Integer.valueOf(parts[0]);
			        	mMinute = Integer.valueOf(parts[1]);
            			}	
	        _db.close();
	            	
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
    	mSound=mySharedPreferences.getString("soundPref", "alarm_alert");
    	
    	Log.i("NerdAlarm","Mode " + _modes[mMode] + " Sound Preference: " + mSound + " and Snooze :" + mSnooze);
	 	
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
    	        
    	        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mDefaultVolume), AudioManager.FLAG_VIBRATE);
    	        _mp.release();
    	        _mp= null;
    	    }
    	    textToSpeech.shutdown();
    }
    public void handleSpeak(View view) {
        saySomething();
        //playSomething();
    }
    
    public void stopAlarm(View view) {
    	mMovingMode=true;
    	rescheduleAlarm(3);
    }
    private void saySomething() {
    	Log.i("NerdAlarm","Speaking..:" + mySpeak3);
    	//Lets keep everything on the AudioManager.STREAM_ALARM 
    	/*
    	HashMap<String, String> myHashAlarm = new HashMap();
    	myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
    	        String.valueOf(AudioManager.STREAM_ALARM));
    	textToSpeech.speak(mySpeak1, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        textToSpeech.speak(mySpeak3, TextToSpeech.QUEUE_ADD, myHashAlarm);
        textToSpeech.speak(mySpeak2, TextToSpeech.QUEUE_ADD, myHashAlarm);*/
        
        textToSpeech.speak(mySpeak1, TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak(mySpeak3, TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(mySpeak2, TextToSpeech.QUEUE_ADD, null);
        
    }
    
    private void playSomething(int _play){
    
    //Useful : http://www.anddev.org/video-tut_-_playing_mediamp3_on_the_emulator-t156.html
    	_mp = new MediaPlayer();
    	
    	if(_play>0) {
			
						_mp = MediaPlayer.create(AlarmActivity.this,_play);
						Log.i("NerdAlarm","setting this stream might not work!");
						//_mp.setAudioStreamType(AudioManager.STREAM_ALARM);
						try {_mp.prepare();} 
								catch (IllegalStateException e) {e.printStackTrace();} 
								catch (IOException e) {e.printStackTrace();}
		
					}
    	
    	else 		{
    					Log.i("NerdAlarm","MSound set to :" + mSound);
    					try {_mp.setDataSource(this,Uri.parse(mySharedPreferences.getString("soundPref", "alarm_alert")));} 		    	 	
			    	 		catch (IllegalArgumentException e1) {e1.printStackTrace();} 
			    	 		catch (SecurityException e1) {e1.printStackTrace();} 
			    	 		catch (IllegalStateException e1) {e1.printStackTrace();} 
			    	 		catch (IOException e1) {e1.printStackTrace();}
		    	 
		    	 		 _mp.setAudioStreamType(AudioManager.STREAM_ALARM);
			    		 _mp.setLooping(true);
		    		 
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
                            		 Log.i("NerdAlarm","Calling canel wake alert");
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
	   	boolean _Speak=true,_Lock=true,_Wake=true,_Play=true;
	   	int _playThis=0;
	   	mStatus=0;
	   	
	   	Log.i("NerdAlarm","Mode!" + mMode);
 	   
   	//Set characteristics..
       if (mMode==0){ 						//Bunny
	       	if (mCounter==0){
	       		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume - 5), AudioManager.FLAG_VIBRATE);
	       		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	       		_playThis=R.raw.b_birds;			
	       		_Speak=false;
	       		_Lock=false;
	       		mStatus=1;
	       		}
	       	else if (mCounter==1){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume - 3), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	   			_playThis=R.raw.b_b_birds;
	   			_Speak=false;
	   			_Lock=false;
	       		mStatus=2;
	       		}
	       	else if (mCounter==2){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume-2), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	   			mStatus=2;
	       		}
	       	else if (mCounter==3){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume-1), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	   			mStatus=2;
	       		}
	       	else if (mCounter>4){
	   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume), AudioManager.FLAG_VIBRATE);
	   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	   			mStatus=3;
	       		}
	       
	       }
       else if (mMode==1){
    	   											//NERD
    	   Log.i("NerdAlarm","Nerd!");
    	   int _nerdFact = (int)(Math.random() * (9 - 0));	
    	   String[] _facts=getResources().getStringArray(R.array.nerd_array); 
    	   mySpeak3 = _facts[_nerdFact];
    	   Log.i("NerdAlarm","Fact!:" + mySpeak3);
    	   if (mCounter==1){mySpeak1 = " cough ";}
    	   mStatus=2;	
       		}
       else if (mMode==2){
													//Nuclear
    	    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume), AudioManager.FLAG_VIBRATE);
  			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
  		 	_Speak=true;
  		 	if (mCounter>1) {_playThis=R.raw.nuc;} 			
  		 	mStatus=2;
       		}
       else if (mMode==3){
    	   											//Ninja
	   	   	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	   	   	v.vibrate(300);
	   	   	_Speak=false;
	   	   	_Lock=false;
	   	   	_Wake=false;
	   	   	_Play=false;
	   	   	mStatus=3;
       		}
	   	
       else if (mMode==4){
    	   mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume), AudioManager.FLAG_VIBRATE);
 		   mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
 		   int _angryStuff = (int)(Math.random() * (9 - 0));	
    	   String[] _andy=getResources().getStringArray(R.array.andy_array); 
    	   mySpeak3 = _andy[_angryStuff];
    	   Log.i("NerdAlarm","Andy!:" + mySpeak3);
    	   if (mCounter==1){mySpeak1 = "ah ah ah ah";}
    	   mStatus=2;	
       		}
       else if (mMode==5){
		   mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume-1), AudioManager.FLAG_VIBRATE);
		   mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		   int _happyStuff = (int)(Math.random() * (9 - 0));	
		   String[] _happy=getResources().getStringArray(R.array.helen_array); 
		   mySpeak3 = _happy[_happyStuff];
		   Log.i("NerdAlarm","Helen!:" + mySpeak3);
		   if (mCounter==1){mySpeak1 = "la la la";}
		   mStatus=2;	
       		}
       
       if(_Wake){PowerUp();}
   	   if(_Lock){UnlockKeyguard();}
   	   
   	   if(mVibrate){
   		   		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
   		   		v.vibrate(100);}
	   
   	   if(mNerd){mySpeak1=getNerdDetails();}
   	   if(_Speak){saySomething();}
       if(_Play){playSomething(_playThis);}
       	
   }
   
   private void rescheduleAlarm(int _status){	   
	   my_records = new Display_Records();
       my_records.SetMe(AlarmActivity.this); 
       
       int mCurrHour, mCurrMinute;
       String mTime;
       final Calendar c = Calendar.getInstance();
       mCurrHour = c.get(Calendar.HOUR_OF_DAY);
       mCurrMinute = c.get(Calendar.MINUTE);
       
       _db.open();
        
       if (_status==1) {
    	   // Just reload the alarm again in 15 mins.
    	   mNextAlarm = my_records.setTime((int)(mAlarmID),mCurrHour,mCurrMinute+1,0);//should be 15
    	   Log.i("NerdAlarm","Setting reload alarm again");
		   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();
       	   mCounter+=1;
       }
       else if (_status==2) {
    	   // Snooze
    	   mNextAlarm = my_records.setTime((int)(mAlarmID),mCurrHour,mCurrMinute+1,0); //Todo Snooze
    	   Log.i("NerdAlarm","Setting reload alarm again in snooze time");
		   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();
       	   mCounter+=1;
       	}
       else if (_status==3) {
    	   // Reschedule for next time..
    	   mCounter=0; 
    	   if (mRepeat>0) {
    		   		Log.i("NerdAlarm","Set alarm to repeat whenever");
				 	mNextAlarm = my_records.setTime((int)(mAlarmID),mHour,mMinute,mRepeat);
		       	   Toast.makeText(AlarmActivity.this,"Next time:" + mNextAlarm, Toast.LENGTH_SHORT).show();}
    	   else
    		   mCounter=0;
    	   	   mEnabled=0;
    	   	  Toast.makeText(AlarmActivity.this, getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();
    	   }
       
       /* Testing Alarm */
       mTime = mySpeak1 + "-" + mHour + ":" + mMinute;
   	   
       Log.i("NerdAlarm","Set alarm " + mAlarmID + " to Counter: " + mCounter + " and Enabled :" + mEnabled);
	 	 
       _db.updateStatistic(mAlarmID,mCounter,mEnabled,mTime + ":" + mMode);
       _db.close();

       mMovingMode = true;
       finish();
   }
   
   private String getNerdDetails(){
	   return "Get something";
   }
   
}
