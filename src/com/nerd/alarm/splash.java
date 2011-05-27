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
*  Nerdy Facts : http://bobado.com/joke/nerdy-facts
*  
*  Nerd Alarm - Splash screen 	        
*/

public class splash extends Activity { 
protected boolean mActive = true;
protected int mSplashTime = 3000; 
private final int mMode = Activity.MODE_PRIVATE;
SharedPreferences mySharedPreferences = null;
	
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
                    startActivity(new Intent("com.nerd.alarm"));
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
			
	private void SetDefaultPrefs(){
		String modeArr [];
		boolean _vibrate=false,_nerd=false,h_vibrate,h_nerd;
		String _greeting=null,_sound=null,h_greeting=null,h_sound=null,_snooze="5",h_snooze;
        
		modeArr=getResources().getStringArray(R.array.modes_array);
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); 
		 
		for (int i = 0; i < modeArr.length; ++i) {
				Log.i("NerdAlarm","Set up Preferences for " + modeArr[i]);
				mySharedPreferences = getSharedPreferences(modeArr[i], mMode);
				
				switch (i){
			    	case 0: 	
			    				_snooze="15";
			    				_greeting=getString(R.string.bunny_greeting);
			    				_sound=alert.toString();
			    				break;		    
			    	case 1: 
			    				_snooze="10";
			    				_sound=alert.toString();
			    				_nerd=true;
			    				_greeting=getString(R.string.nerd_greeting);
			    				break; 
			    	case 2: 
					    		_snooze="5";
			    				_nerd=false;
			    				_vibrate=true;
			    				_greeting=getString(R.string.nuclear_greeting);
			    				_sound=alert.toString();
			    				break; 
			    	case 3: 
					    		_snooze="10";
			    				_greeting=getString(R.string.ninja_greeting);
			    				_sound=alert.toString();
			    				break; 
			    	case 4: 
					    		_snooze="5";
					    		_greeting=getString(R.string.andy_greeting);
			    				_sound=alert.toString();
			    				break; 
			    	case 5: 
			    	    		_snooze="10";
					    		_vibrate=false;
			    				_greeting=getString(R.string.helen_greeting);
			    				_sound=alert.toString();
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