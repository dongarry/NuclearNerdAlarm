package com.nerd.alarm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class alarm_pref extends Activity {
	
	/** Called when the activity is first created. */
	//http://stackoverflow.com/questions/4593552/android-get-set-media-volumenot-ringone-volume
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(alarm_pref.this, "load_prefs", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.alarm_preferences); 
    
    }
	
}
