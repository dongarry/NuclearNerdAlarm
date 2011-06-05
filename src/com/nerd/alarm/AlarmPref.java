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
import android.util.Log;
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

public class AlarmPref extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private EditTextPreference greetingPref;
	private CheckBoxPreference vibratePref,nerdPref;
	private ListPreference snoozePref,modePref;
	private RingtonePreference soundPref;
	private SharedPreferences.Editor editor;
	private String sound;
	private int mode = Activity.MODE_PRIVATE;
	private int snooze;
	
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
        Log.i("NerdAlarm","Loading Prefs.." + getString(R.string.mode_default));
		getPrefs(getString(R.string.mode_default)); //start on the default..
		Log.i("NerdAlarm","LoadingPref - after");
    	
        
        soundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
    		 public boolean onPreferenceChange(Preference preference, Object newValue) {
    			 		 	
    			 	mySharedPreferences = getSharedPreferences(modePref.getValue(), mode);
    	    		editor = mySharedPreferences.edit();
    	    		sound=newValue.toString();
    	    		editor.putString("soundPref", sound);
    	    		editor.commit();
    	    		
    	    		setSoundPref(sound); 
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
        	Log.i("NerdAlarm","Applying to snooze:" + snoozePref.getValue());
        	editor.putInt("snoozePref", Integer.parseInt(snoozePref.getValue()));		     
			snoozePref.setSummary(getString(R.string.snooze_summary) + " : " + snoozePref.getValue());}
        else if (key.equals("vibratePref")) {
    		editor.putInt("vibratePref",(int)(vibratePref.isChecked()?1:0));}
        else if (key.equals("nerdPref")) {
        	editor.putInt("nerdPref",(int)(nerdPref.isChecked()?1:0));} 
    	
    	
    	if (!key.equals("modePref")){ editor.commit();}
    	}

    private void getPrefs(String _pref){
    	
    	mySharedPreferences = getSharedPreferences(_pref,mode);
    	modePref.setTitle(getString(R.string.mode)+ " : " + _pref);
    	Log.i("NerdAlarm","SnoozePref - set title done");
    	
    	greetingPref.setText(mySharedPreferences.getString("greetingPref",""));
    	vibratePref.setChecked(mySharedPreferences.getInt("vibratePref",0) == 1);
    	nerdPref.setChecked(mySharedPreferences.getInt("nerdPref",0) == 1);
    	Log.i("NerdAlarm","SnoozePref - next");
    	snoozePref.setValue(String.valueOf(mySharedPreferences.getInt("snoozePref",5)));
    	Log.i("NerdAlarm","SnoozePref - after");
    	
    	try {
	    	soundPref.setPersistent(true); 
	    	soundPref.setDefaultValue((Object)(mySharedPreferences.getString("soundPref","alarm_alert")));
	    	setSoundPref(mySharedPreferences.getString("soundPref","alarm_alert"));
    		}
    	catch(Exception e){Log.e("NerdAlarm","Another ringtone issue: getPrefs :" + e.getMessage());}
    	
    	greetingPref.setSummary(getString(R.string.greeting_summary) + " : " +  mySharedPreferences.getString("greetingPref",""));
    	Log.i("NerdAlarm","SnoozePref - set summary next");
    	snoozePref.setSummary(getString(R.string.snooze_summary) + " : " + String.valueOf(mySharedPreferences.getInt("snoozePref",5)));        
		}
    
    private void setSoundPref(String _sound){
    	try {
	    	RingtoneManager rm = new RingtoneManager(this);
	    	rm.setType(RingtoneManager.TYPE_ALARM);
	    	//Ringtone ringtone = rm.getRingtone(this, Uri.parse(mySharedPreferences.getString("soundPref","alarm_alert")));
	    	Ringtone ringtone = rm.getRingtone(this, Uri.parse(mySharedPreferences.getString("soundPref","")));
	    	soundPref.setSummary(getString(R.string.ringtone_summary) + " : " + ringtone.getTitle(this));        
    	}
    	catch(Exception e){Log.e("NerdAlarm","Another ringtone issue: setSoundPref :" + e.getMessage());}
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
		    	Toast.makeText(AlarmPref.this,getString(R.string.err_greeting) + item.getItemId(), Toast.LENGTH_SHORT).show();
		    	return true;
		     }
		}
	 
}
