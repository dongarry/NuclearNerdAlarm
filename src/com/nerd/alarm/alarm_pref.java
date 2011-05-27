package com.nerd.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/*
 * Credits
 * http://stackoverflow.com/questions/2834079/setting-ringtone-for-a-specific-application-only
 * http://stackoverflow.com/questions/4593552/android-get-set-media-volumenot-ringone-volume   	
 * http://androidideasblog.blogspot.com/2010/02/creating-custom-dialog-in-android.html
 * 
 * Nerd Alarm - Preferences screen - set characteristics for each mode
 * TODO Create a reset to defaults option (probably on a sub menu)
 *  Disable preferences for Ninja as this mode doesn't use them (Preference.setEnabled does not do this)
 */

public class alarm_pref extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private EditTextPreference greetingPref;
	private CheckBoxPreference vibratePref,nerdPref;
	private ListPreference snoozePref,modePref;
	private RingtonePreference soundPref;
	private SharedPreferences.Editor editor;
	private String _sound;
	private int mode = Activity.MODE_PRIVATE;
	
	static final private int ALARM_DETAIL = 1;

	SharedPreferences mySharedPreferences; 
    
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
    	    		return true;	}
         });  
    } 

	
	@Override // This is our information box..
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	        case ALARM_DETAIL:
	            LayoutInflater li = LayoutInflater.from(this);
	            View alarmDetailView = li.inflate(R.layout.info, null);
	            
	            AlertDialog.Builder alarmDetailBuilder = new AlertDialog.Builder(this);
	            alarmDetailBuilder.setView(alarmDetailView);
	            AlertDialog categoryDetail = alarmDetailBuilder.create();
	           
	            categoryDetail.setButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                return;
	            }}); 
	            
	            return categoryDetail;
	        default:
	            break;
	    }
	    return null;
	}
	
	
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);}
    
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);}
    
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        
      	 if (!key.equals("modePref")){ 
    		mySharedPreferences = getSharedPreferences(modePref.getValue(), mode);
    		editor = mySharedPreferences.edit();}

      	 
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
		}
    
    private void setSoundPref(String _sound){
    	RingtoneManager rm = new RingtoneManager(this);
    	rm.setType(RingtoneManager.TYPE_ALARM);
    	Ringtone ringtone = rm.getRingtone(this, Uri.parse(mySharedPreferences.getString("soundPref","alarm_alert")));
    	soundPref.setSummary(getString(R.string.ringtone_summary) + " : " + ringtone.getTitle(this));        
    }
    
	// Display our own custom menu when menu is selected.
	 @Override
	
	public boolean onCreateOptionsMenu(Menu mymenu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pref_menu, mymenu);
		return true;
		}
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		    case R.id.info:
		    	showDialog(ALARM_DETAIL);
		      	return true;
		     default:
		    	Toast.makeText(alarm_pref.this,getString(R.string.err_greeting) + item.getItemId(), Toast.LENGTH_SHORT).show();
		    	return true;
		     }
		}
	 
}
