package com.nerd.alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;
//import com.nerd.alarm.RingtonePref;

public class alarm_pref extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private EditTextPreference greetingPref;
	private CheckBoxPreference vibratePref,nerdPref;
	private ListPreference snoozePref,modePref;
	private RingtonePreference soundPref;
	private SharedPreferences.Editor editor;
	
	SharedPreferences mySharedPreferences; 
    private String _sound;
	
	private int mode = Activity.MODE_PRIVATE;
	/** Called when the activity is first created. */
	//http://stackoverflow.com/questions/4593552/android-get-set-media-volumenot-ringone-volume
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        modePref = (ListPreference) findPreference("modePref");
        snoozePref = (ListPreference) findPreference("snoozePref");
        soundPref = (RingtonePreference) findPreference("soundPref");
        greetingPref = (EditTextPreference)findPreference("greetingPref");
        vibratePref=(CheckBoxPreference)findPreference("vibratePref");
        nerdPref=(CheckBoxPreference)findPreference("nerdPref");
        
        
        // We don't want the keyboard to pop up automatically..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toast.makeText(getBaseContext(),"Default : " + getString(R.string.mode_default),Toast.LENGTH_LONG).show();  	
		
        modePref.setValue(getString(R.string.mode_default));
        getPrefs(getString(R.string.mode_default)); //start on the default..
        
        soundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
    		 public boolean onPreferenceChange(Preference preference, Object newValue) {
    			 		 	
    			 	mySharedPreferences = getSharedPreferences(modePref.getValue(), mode);
    	    		editor = mySharedPreferences.edit();
    	    		_sound=newValue.toString();
    	    		editor.putString("soundPref", _sound);
    	    		editor.commit();
    	    		
    	    		setSoundPref(_sound); 
    	    		return true;
    		           }
         });  
    } 

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }
    
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Let's do something a preference value changes
    	
      	 if (!key.equals("modePref")){ 
    		mySharedPreferences = getSharedPreferences(modePref.getValue(), mode);
    		// Retrieve an editor to modify the shared preferences.
    		editor = mySharedPreferences.edit();
    	}

    	if (key.equals("modePref")) {
			getPrefs(modePref.getValue());}
		else if (key.equals("greetingPref")) {
			editor.putString("greetingPref", greetingPref.getText());		     
			greetingPref.setSummary(getString(R.string.greeting_summary) + " : " +  greetingPref.getText());}
        else if (key.equals("snoozePref")) {
        	editor.putString("snoozePref", snoozePref.getValue());		     
			snoozePref.setSummary(getString(R.string.snooze_summary) + " : " + snoozePref.getValue());}
        else if (key.equals("vibratePref")) {
    		editor.putBoolean("vibratePref",vibratePref.isChecked());}
        else if (key.equals("nerdPref")) {
        	editor.putBoolean("nerdPref",nerdPref.isChecked());} 
    	
    	if (!key.equals("modePref")){ editor.commit();}

    	}

    private void getPrefs(String _pref){
    	//We set new default values depending on the mode selected.
    	
    	mySharedPreferences = getSharedPreferences(_pref,mode);
    	modePref.setTitle(getString(R.string.mode)+ " : " + _pref);
 		
    	greetingPref.setText(mySharedPreferences.getString("greetingPref",""));
    	vibratePref.setChecked(mySharedPreferences.getBoolean("vibratePref",false));
    	nerdPref.setChecked(mySharedPreferences.getBoolean("nerdPref",false));
    	snoozePref.setValue(mySharedPreferences.getString("snoozePref","5"));
    	
    	soundPref.setPersistent(true); 
    	soundPref.setDefaultValue((Object)(mySharedPreferences.getString("soundPref","alarm_alert")));
    	setSoundPref(mySharedPreferences.getString("soundPref","alarm_alert"));
    	greetingPref.setSummary(getString(R.string.greeting_summary) + " : " +  mySharedPreferences.getString("greetingPref",""));
		snoozePref.setSummary(getString(R.string.snooze_summary) + " : " + mySharedPreferences.getString("snoozePref","5"));        
		
		if (mode==3){
				soundPref.setEnabled(false);
				vibratePref.setEnabled(false);
				greetingPref.setEnabled(false);
				snoozePref.setEnabled(false);}
		else {
				soundPref.setEnabled(true);
				vibratePref.setEnabled(true);
				greetingPref.setEnabled(true);
				snoozePref.setEnabled(true);}			
		}
    
    private void setSoundPref(String _sound){
    	//A super help!
    	//http://stackoverflow.com/questions/2834079/setting-ringtone-for-a-specific-application-only
    	
    	RingtoneManager rm = new RingtoneManager(this);
    	rm.setType(RingtoneManager.TYPE_ALARM);
    	Ringtone ringtone = rm.getRingtone(this, Uri.parse(mySharedPreferences.getString("soundPref","alarm_alert")));
    	soundPref.setSummary(getString(R.string.ringtone_summary) + " : " + ringtone.getTitle(this));        

    }
}
