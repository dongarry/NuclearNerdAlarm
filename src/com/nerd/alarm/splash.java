package com.nerd.alarm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/* Credits
*  http://www.droidnova.com/how-to-create-a-splash-screen,561.html
*  http://www.anddev.org/error_while_writing_on_sdcard-t2997.html   [testing on emulator]
*  Nerdy Facts : http://bobado.com/joke/nerdy-facts
*  
*  Nerd Alarm - Splash screen 	        
*/

public class Splash extends Activity { 
protected boolean mActive = true;
protected int mSplashTime = 3000; 
private final int mMode = Activity.MODE_PRIVATE;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        //Let's load default Preferences here..
        SetDefaultPrefs();
        
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
                    startActivity(new Intent("com.nerd.ViewAlarm"));
         	        stop();
                }
            }
        };
        splashTread.start();
        
	}
	
	@Override // On Touch event, let's move on..
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        mActive = false;
	    }
	    return true;
	}
			
	private void SetDefaultPrefs()	{
		
		String modeArr [];
		
		int nerd = 0;
		String sound = "";
		int vibrate = 0;
		int snooze = 5;
		String greeting = "";
		
		int holdVibrate;
		int holdNerd;
		String holdGreeting;
		String holdSound;
		int holdSnooze;
        
		modeArr=getResources().getStringArray(R.array.modes_array);
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); 
        
		if(alert == null){  
			// Emulator is teaching me there is no such thing as a default alarm tone 
            // use default ringtone instead..
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
            Log.i("NerdAlarm","No default Alarm sounds");
		} 
		
		SharedPreferences mySharedPreferences = null;

		for (int i = 0; i < modeArr.length; ++i) {
				Log.i("NerdAlarm","Set up Preferences for " + modeArr[i]);
				mySharedPreferences = getSharedPreferences(modeArr[i], mMode);
				
				switch (i){
			    	case 0: 	
			    				snooze=15;
			    				greeting=getString(R.string.bunny_greeting);
			    				sound=alert.toString();
			    				break;		    
			    	case 1: 
			    				snooze=10;
			    				sound=alert.toString();
			    				nerd=1;
			    				greeting=getString(R.string.nerd_greeting);
			    				break; 
			    	case 2: 
					    		snooze=5;
			    				nerd=0;
			    				vibrate=1;
			    				greeting=getString(R.string.nuclear_greeting);
			    				sound=alert.toString();
			    				break; 
			    	case 3: 
					    		snooze=10;
			    				greeting=getString(R.string.ninja_greeting);
			    				sound=alert.toString();
			    				break; 
			    	case 4: 
					    		snooze=5;
					    		greeting=getString(R.string.andy_greeting);
			    				sound=alert.toString();
			    				break; 
			    	case 5: 
			    	    		snooze=10;
					    		vibrate=0;
			    				greeting=getString(R.string.helen_greeting);
			    				sound=alert.toString();
			    				break; 
 		    			  }  	
				
				// Retrieve an editor to modify the shared preferences.
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				
				//Slightly awkward as each mode has different preferences
				//so we ensure the defaults are set for each one
				//we couldn't simply use getDefaultSharedPreferences
				
				holdGreeting = mySharedPreferences.getString("greetingPref", greeting);
				holdVibrate = mySharedPreferences.getInt("vibratePref", vibrate);
	    	    holdSnooze = mySharedPreferences.getInt("snoozePref", snooze);
	    	    holdNerd = mySharedPreferences.getInt("nerdPref", nerd);
	    	    holdSound = mySharedPreferences.getString("soundPref", sound);
	    	    	
				editor.putString("greetingPref", holdGreeting);
				editor.putInt("vibratePref", holdVibrate);
				editor.putInt("snoozePref", holdSnooze);
				editor.putInt("nerdPref", holdNerd);
				editor.putString("soundPref", holdSound);
				editor.commit();
				
		}
	}
}