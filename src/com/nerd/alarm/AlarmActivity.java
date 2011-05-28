package com.nerd.alarm;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.app.Activity;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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
 *  This Class is like spaghetti junction at the moment and needs serious refactoring,
 *  This is on my TODO list..(as well as)
 *   Add more functionality to the Test me option - currently this just keeps the keyguard on
 * 
 */

public class AlarmActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private long mAlarmID =0;
    private String mySpeak1="_",mySpeak2="_",mySpeak3="_"; 
    private String mNextAlarm, mSound;
    
    private PowerManager.WakeLock mWakeLock;
    private KeyguardManager mKeyguardMan; 
    private KeyguardLock mKeyguardLock=null; 
    
    private int mCounter=0,mRepeat=0, mMode=0,mEnabled=0, mHour,mMinute;
    private int mTest=0,mSnooze=0, mDefaultVolume=0,mStatus=0;
    private boolean mVibrate,mNerd,mMovingMode,mSpeak=true;
   
    private String _modes [];
    private int mode = Activity.MODE_PRIVATE;
    private MediaPlayer _mp = null;
    private PowerManager _pm = null;
    
    TextView mNerdDetails;
    SharedPreferences mySharedPreferences; 
    DatabaseAdapter _db = new DatabaseAdapter(this); 
    AudioManager mAudioManager = null;
    DisplayRecords my_records = null;
    
    public void onCreate(Bundle savedInstanceState) {
   	
    	setVolumeControlStream(AudioManager.STREAM_MUSIC); // We want the user to be able to turn us down..
    	super.onCreate(savedInstanceState);
    	setTitle(getString(R.string.alarmtitle));
    	mySpeak2=getString(R.string.alarmtext2);
    	
    	mMovingMode = false;
    	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
    	
    	mKeyguardMan = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);     	
    	PowerUp();
  
    	setContentView(R.layout.alarm_wake);
    	
    	mNerdDetails = (TextView) findViewById(R.id.nerd_info);
    	
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
        super.onPause();}
    
    @Override
    protected void onStop(){
        super.onStop();
        checkMedia();
        
        if (!mMovingMode){rescheduleAlarm(3);}
        			
        exitKeyguard(); 
        cancelWakeLock();

    }
    
    public void loadDetails (){
    	
    	Bundle extras = getIntent().getExtras(); 
        if(extras !=null)
          {mAlarmID = extras.getLong("AlarmID");}
        
        if (mAlarmID!=0) {
        	TextView mTtitleDisplay = (TextView) findViewById(R.id.wakeup);
        	
        	_db.open();
            Cursor a = _db.getAlarm(mAlarmID);	
            _db.close();
            
            if (a!=null){ 
		            	mySpeak1=a.getString(2); // Title 
			            mMode=a.getInt(5); 
			            mEnabled=a.getInt(4); 
			            mRepeat=a.getInt(3); 
			            mCounter=a.getInt(6);
			            mTest=a.getInt(7);
			            //Log.i("NerdAlarm",":"+mySpeak1+":"+mMode+":"+mEnabled+":"+mRepeat+":"+mCounter+":"+mTest);
			            if(mEnabled==0){finish(); Toast.makeText(AlarmActivity.this, "Finished 1", Toast.LENGTH_SHORT).show();}
			            
			            String[] parts = a.getString(1).split(":",2);
			        	mHour = Integer.valueOf(parts[0]);
			        	mMinute = Integer.valueOf(parts[1]);
            			}	
	        
	            	
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
    	 // DEALLOCATE MEMORY
    	    if (_mp != null) {
    	        if (_mp.isPlaying()) {
    	            _mp.stop();
    	        }
    	        
    	        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mDefaultVolume), AudioManager.FLAG_VIBRATE);
    	        _mp.release();
    	        _mp= null;
    	    }
    	    
    	    if (textToSpeech != null) {
    	    	textToSpeech.stop();
    	    	textToSpeech.shutdown();}
    	}
    
    public void handleSpeak(View view) {saySomething();}
    
    public void stopAlarm(View view) {rescheduleAlarm(3);}
    
    private void saySomething() {
        textToSpeech.speak(mySpeak1, TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak(mySpeak3, TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(mySpeak2, TextToSpeech.QUEUE_ADD, null);        
    	}
    
    private void playSomething(int _play) throws IllegalStateException, IOException
    {	
    	_mp = new MediaPlayer();
    	
    	_mp.setOnErrorListener(
				new MediaPlayer.OnErrorListener() {
			        public boolean onError(MediaPlayer mp, int arg, int argx) {
			        	Log.e("NerdAlarm", "Error in MediaPlayer: (" + arg + ") with extra (" + argx +")" );
			    		return false;}
			    });
    
    	if(_play>0) {
    					_mp = MediaPlayer.create(AlarmActivity.this,_play);}
			    		//_mp.prepare(); Tut tut!

    	else 		{
    					_mp.setDataSource(this,Uri.parse(mySharedPreferences.getString("soundPref", "alarm_alert"))); 		    	 		    	 	
			    	 	_mp.setAudioStreamType(AudioManager.STREAM_ALARM);
			    	 	_mp.setLooping(true);
			    	 	_mp.prepare();    					 	 
    	}
    	
    	_mp.start();
    	   	
    	_mp.setOnCompletionListener(new OnCompletionListener(){
         // @Override
         public void onCompletion(MediaPlayer arg0) {
        	rescheduleAlarm(mStatus);}
    	});
    	 	
    }
    
    public void onInit(int i) {
		  if (i == TextToSpeech.SUCCESS) {
	            //TODO Consider doing something with locales?
			  	 if(mSpeak){saySomething();}
	        } 
		  else {Log.e("NerdAlarm", "Could not initialise TextToSpeech.");}
	}
    
    private void PowerUp() { 
    	_pm = (PowerManager) getSystemService (Context.POWER_SERVICE); 
         
    	mWakeLock = _pm.newWakeLock( 
                         PowerManager.FULL_WAKE_LOCK |  // could use bright instead 
                         PowerManager.ACQUIRE_CAUSES_WAKEUP  // so we actually wake the device  
                         , "Nerd Alarm"); 
             
            mWakeLock.acquire(); 
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
	   				Log.i("NerdAlarm","Disabling KeyGuard");
	   				mKeyguardLock.disableKeyguard(); 
           		} 
           else 
           		{ mKeyguardLock = null; } 
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
   
   private void setCharacteristics(){
	   	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	   	boolean _Lock=true,_Play=true;
	   	int _playThis=0;
	   	mStatus=0;
	   	
   	   if (mMode==0){ 						//Bunny
		       	if (mCounter==0){
		       		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume - 5), AudioManager.FLAG_VIBRATE);
		       		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		       		_playThis=R.raw.b_birds;			
		       		mSpeak=false;
		       		_Lock=false;
		       		mStatus=1;
		       		}
		       	else if (mCounter==1){
		   			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume - 3), AudioManager.FLAG_VIBRATE);
		   			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		   			_playThis=R.raw.b_b_birds;
		   			mSpeak=false;
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
    	   mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume - 2), AudioManager.FLAG_VIBRATE);
      	   mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
      	   int _nerdFact = (int)(Math.random() * (9 - 0));	
    	   String[] _facts=getResources().getStringArray(R.array.nerd_array); 
    	   mySpeak3 = _facts[_nerdFact];
    	   if (mCounter==1){mySpeak1 = " cough ";}
    	   mStatus=2;	
    	   if (mCounter>6){mStatus=3;} 
       		}
       
       else if (mMode==2){
													//Nuclear
    	    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume), AudioManager.FLAG_VIBRATE);
  			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
  		 	if (mCounter>0) {_playThis=R.raw.nuc;} 			
  		 	mStatus=2;
       		}
       
       else if (mMode==3){
    	   											//Ninja
	   	   	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	   	   	v.vibrate(700);
	   	   	mSpeak=false;
	   	   	_Lock=false;
	   	   	_Play=false;
	   	   	mStatus=3;
       		}
	   	
       else if (mMode==4){
    	   											//Angry
    	   mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume), AudioManager.FLAG_VIBRATE);
 		   mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
 		   int _angryStuff = (int)(Math.random() * (9 - 0));	
    	   String[] _andy=getResources().getStringArray(R.array.andy_array); 
    	   mySpeak3 = _andy[_angryStuff];
    	   if (mCounter==1){mySpeak1 = " aha ";}
    	   mStatus=2;	
       		}
       
       else if (mMode==5){
    	   											//Happy
    	   mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume-1), AudioManager.FLAG_VIBRATE);
		   mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		   int _happyStuff = (int)(Math.random() * (9 - 0));	
		   String[] _happy=getResources().getStringArray(R.array.helen_array); 
		   mySpeak3 = _happy[_happyStuff];
		   if (mCounter==1){mySpeak1 = " oh la la ";}
		   mStatus=2;	
       		}
       
       if(_Lock && (mTest==0)){UnlockKeyguard();} // For test me - make it harder to get to the alarm if keyguard is on.
   	   
       if(mVibrate){
   		   		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
   		   		v.vibrate(200);}
	   
   	   if(mNerd){mySpeak1=getNerdDetails();}
   	   if(_Play){
   		   			try {
   		   					playSomething(_playThis);} 
   		   			catch (IllegalStateException e) {
   		   					Log.e("NerdAlarm","MediaPlayer -" + _Play + " - " + e.getMessage());
   		   					e.printStackTrace();} 
   		   			catch (IOException e) {
   		   					Log.e("NerdAlarm","MediaPlayer -" + _Play + " - " + e.getMessage());
   		   					e.printStackTrace();}}
   		   			       	
   }
   
   private void rescheduleAlarm(int _status){	   
	   my_records = new DisplayRecords();
       my_records.SetMe(AlarmActivity.this); 
       
       int mCurrHour, mCurrMinute;
       String mTime;
       final Calendar c = Calendar.getInstance();
       mCurrHour = c.get(Calendar.HOUR_OF_DAY);
       mCurrMinute = c.get(Calendar.MINUTE);
        
       if (_status==1) {
    	   // Just reload the alarm again in 15 mins.
    	   mNextAlarm = my_records.setTime((int)(mAlarmID),mCurrHour,mCurrMinute+15,0,mMode);
    	   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();
       	   mCounter+=1;
       }
       else if (_status==2) {
    	   // Snooze
    	   mNextAlarm = my_records.setTime((int)(mAlarmID),mCurrHour,mCurrMinute+mSnooze,0,mMode); 
    	   Toast.makeText(AlarmActivity.this,mNextAlarm, Toast.LENGTH_SHORT).show();
       	   mCounter+=1;
       	}
       else if (_status==3) {
    	   // Reschedule for next time..
    	   mCounter=0; 
    	   if (mRepeat>0) {
	    		   	mNextAlarm = my_records.setTime((int)(mAlarmID),mHour,mMinute,mRepeat,mMode);
			       	Toast.makeText(AlarmActivity.this, mNextAlarm, Toast.LENGTH_SHORT).show();
			       	mEnabled=1;}
    	   else
    	   	   		mEnabled=0;
    	   	  		Toast.makeText(AlarmActivity.this, getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();
    	   }
       
	       /* Testing Alarm */
	       mTime = mySpeak1 + "-" + mHour + ":" + mMinute;
	        
	       Log.i("NerdAlarm", "Resetting Alarm " + mAlarmID + ",Enabled:" + mEnabled);
	       _db.open();
	       _db.updateStatistic(mAlarmID,mCounter,mEnabled,mTime + ":" + mMode);
	       _db.close();
	
	       mMovingMode = true;
	       finish();
   }
   
   private String getNerdDetails()
   { 
	  	
	try {   
			LocationManager locationManager;
		  	String context = Context.LOCATION_SERVICE;
		 	locationManager = (LocationManager)getSystemService(context);
		 	String provider = LocationManager.NETWORK_PROVIDER;
		 	Location location = locationManager.getLastKnownLocation(provider);
		 	Log.i("NerdAlarm","Location: " + location.getLongitude()  + ":" + location.getLatitude());

		 	//We now have coordinates - let's get the weather
		 	//http://www.google.com/ig/api?weather=,,,4550000,-7358300
		 	//These coordinates need to be formatted
		 	String weatherString = "http://www.google.com/ig/api?weather=,,,";
		 	String _lat= location.getLatitude() + ""; 
		 	String _lon = "" + location.getLongitude();
		 	_lat=f_Coordinates(_lat) + "," + f_Coordinates(_lon); 	
		 	weatherString = weatherString + _lat;
		 	URL url;	 	
			url = new URL(weatherString); 
		 	
			//Return should be XML so deal with it:
	 		SAXParserFactory spf = SAXParserFactory.newInstance();
	 		SAXParser sp;
        	sp = spf.newSAXParser(); 
        	XmlHandler _xmlHandler = new XmlHandler();
        	XMLReader xr = null;
        	xr = sp.getXMLReader();
        	xr.setContentHandler(_xmlHandler);
		
        	// We cannot be waiting long for this..
        	URLConnection conn = url.openConnection();
        	conn.setConnectTimeout(3000);
        	conn.setReadTimeout(2000);
        	xr.parse(new InputSource(conn.getInputStream())); 
	
			// xmlHandler provides the parsed data
			Log.i("NerdAlarm","URL:" + url);
	        ParsedXmlSet _parsedXmlSet = _xmlHandler.getParsedData();
	        Log.i("NerdAlarm",_parsedXmlSet.toString());
	        
	        String _nerdDetails = "No";
	        
	        if (_parsedXmlSet.getWind().length()>4){
	        		mNerdDetails.setText(_parsedXmlSet.toString() + getString(R.string.temp));
	        		mNerdDetails.setTypeface(null,Typeface.BOLD);
	        		_nerdDetails = _parsedXmlSet.getCondition();
	        		}
	        
	        return _nerdDetails;
	}

	catch (Exception e) {
			Log.e("NerdAlarm", "Error getting Weather info:" + e.getMessage());
        	return "No"; }
	  }
   
   private String f_Coordinates(String _c) 
   {	// Ensure Coordinates are in the correct format..
	   _c=_c.replace(".", "");   
	   if(_c.length()>8){_c=_c.substring(0, 8);}
	   else if(_c.length()<8){_c=_c.format("%-" + 8 + "s", _c).replace(' ', '0');}
	   
	   return _c;
	    
   }
      
}
