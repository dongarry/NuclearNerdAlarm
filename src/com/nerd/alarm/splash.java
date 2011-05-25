package com.nerd.alarm;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.content.Context;

// See Splash tutorial ; http://www.droidnova.com/how-to-create-a-splash-screen,561.html

public class splash extends Activity { 
protected boolean mActive = true;
protected int mSplashTime = 5000; 
private final int mMode = Activity.MODE_PRIVATE;
SharedPreferences mySharedPreferences = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        //Let's load default Preferences here..
        SetDefaultPrefs();
        
        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(mActive && (waited < mSplashTime)) {
                        sleep(100);
                        if(mActive) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    startActivity(new Intent("com.nerd.alarm"));
                    stop();
                }
            }
        };
        splashTread.start();
        
	}
	
	// On Touch event, let's move on..
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        mActive = false;
	    }
	    return true;
	}
	
	private void AddCustomRingtones(){
		// useful!
		// http://coderzheaven.com/index.php/2010/10/how-to-set-ringtone-in-android/
		
		Uri path = Uri.parse("android.resource://com.nerd.alarm/" + R.raw.nerd);
		//String filepath ="/sdcard/myring.mp3";
		File ringtoneFile = new File(path.toString());
		ContentValues content = new ContentValues();
		content.put(MediaStore.MediaColumns.DATA,ringtoneFile.getAbsolutePath());
		content.put(MediaStore.MediaColumns.TITLE, "nerd_1");
		content.put(MediaStore.MediaColumns.SIZE, 999);
		content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
		content.put(MediaStore.Audio.Media.ARTIST, "artist");
		content.put(MediaStore.Audio.Media.DURATION, 50);
		content.put(MediaStore.Audio.Media.IS_RINGTONE, false);
		content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		content.put(MediaStore.Audio.Media.IS_ALARM, true);
		content.put(MediaStore.Audio.Media.IS_MUSIC, false);

		//Log.i(TAG, "the absolute path of the file is :"+ringtoneFile.getAbsolutePath());
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());
		Uri newUri = (getBaseContext()).getContentResolver().insert(uri, content);
		
		//ringtoneUri = newUri;
		//Log.i(TAG,"the ringtone uri is :"+ringtoneUri);
//			RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_RINGTONE,newUri);
		
	}
	
	private void SetDefaultPrefs(){
		String modeArr [];
		boolean _vibrate=false,_nerd=false,h_vibrate,h_nerd;
		String _greeting=null,_sound=null,h_greeting=null,h_sound=null,_snooze="5",h_snooze;

		
		modeArr=getResources().getStringArray(R.array.modes_array);
		
		for (int i = 0; i < modeArr.length; ++i) {
			
				mySharedPreferences = getSharedPreferences(modeArr[i], mMode);
				//Toast.makeText(getBaseContext(),modeArr[i],Toast.LENGTH_SHORT).show();
				//Nerdy Facts : http://bobado.com/joke/nerdy-facts
			    
				switch (i){
		    	case 0: 	
		    				_snooze="15";
		    				_greeting=getString(R.string.bunny_greeting);
		    				_sound="alarm_alert";
		    				break;		    
		    	case 1: 
		    				_snooze="10";
		    				_nerd=true;
		    				_greeting=getString(R.string.nerd_greeting);
		    				_sound="alarm_alert";
		    				break; 
		    	case 2: 
				    		_snooze="5";
		    				_nerd=false;
		    				_vibrate=true;
		    				_greeting=getString(R.string.nuclear_greeting);
		    				_sound="alarm_alert";
		    				break; 
		    	case 3: 
				    		_snooze="10";
		    				_greeting=getString(R.string.ninja_greeting);
		    				_sound="alarm_alert";
		    				break; 
		    	case 4: 
				    		_snooze="5";
				    		_greeting=getString(R.string.andy_greeting);
		    				_sound="alarm_alert";
		    				break; 
		    	case 5: 
				    		_snooze="10";
				    		_vibrate=false;
		    				_greeting=getString(R.string.helen_greeting);
		    				_sound="alarm_alert";
		    				break; 
 		    		}  	
				
				// Retrieve an editor to modify the shared preferences.
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				
				//Slightly awkward as each mode has different preferences
				//so we ensure the defaults are set for each one
				//we couldn't simply use getDefaultSharedPreferences
				
				h_greeting = mySharedPreferences.getString("greetingPref", _greeting);
				h_vibrate = mySharedPreferences.getBoolean("vibratePref", _vibrate);
	    	    h_snooze = mySharedPreferences.getString("snoozePref", _snooze);
	    	    h_nerd = mySharedPreferences.getBoolean("nerdPref", _nerd);
	    	    h_sound = mySharedPreferences.getString("soundPref", _sound);
	    	    	
				editor.putString("greetingPref", h_greeting);
				editor.putBoolean("vibratePref", h_vibrate);
				editor.putString("snoozePref", h_snooze);
				editor.putBoolean("nerdPref", h_nerd);
				editor.putString("soundPref", h_sound);
				
				editor.commit();
		}
	}
}