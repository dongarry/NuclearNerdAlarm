package com.nerd.alarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.view.WindowManager;
import android.widget.Toast;

public class alarm_pref extends PreferenceActivity  {
	private EditTextPreference greetingPref;
	private CheckBoxPreference vibratePref,nerdPref;
	private ListPreference snoozePref,modePref;
	private RingtonePreference soundPref;

	SharedPreferences mySharedPreferences; 
    private String _modes [];
	
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
		 	
        getPrefs(getString(R.string.mode_default)); //start on the default..
        
        modePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        		 public boolean onPreferenceChange(Preference preference, Object newValue) {
        			 		
        			 		getPrefs(newValue.toString());
        			 		
        			 		return true;
        		           }
        });
        
        snoozePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
   		 public boolean onPreferenceChange(Preference preference, Object newValue) {
   			 	Toast.makeText(getBaseContext(),"selected: " + snoozePref.getEntry() + " and " + snoozePref.getValue() + " : " + newValue,Toast.LENGTH_LONG).show();  	
   			 		return true;
   		           }
        });
        
        soundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      		 public boolean onPreferenceChange(Preference preference, Object newValue) {
      			 	Toast.makeText(getBaseContext(),"selected: " + newValue,Toast.LENGTH_LONG).show();  	
      			 		return true;
      		           }
           });
        /*
        modePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		     public boolean onPreferenceChange(Preference preference, Object newValue) {
		    	 //Toast.makeText(getBaseContext(),"The mode preference has been selected : " + newValue,Toast.LENGTH_LONG).show();
		    	 //modePref.setTitle(newValue.toString());
		    	 //getPrefs(newValue.toString());
		    	 return true;}
		      });
		*/
        
        //notifyChanged()
        
        /*
        mySharedPreferences = getSharedPreferences(_modes[position],mode);
	    modePref.setTitle(getString(R.string.mode) + " : " + _modes[position]);
	    greetPref.setDefaultValue(mySharedPreferences.getString("greetingPref", ""));
	    */
        
	    /*
	    	       float lastFloat = mySharedPreferences.getFloat("lastFloat", 0f);
	    	       int wholeNumber = mySharedPreferences.getInt("wholeNumber", 1);
	    	       long aNumber = mySharedPreferences.getLong("aNumber", 0);
	    	       String stringPreference = mySharedPreferences.getString("textEntryValue", "");
	    			*/
	    	//DG }

	    	//TODO Provide reset feature..
	    	// getDefaultSharedPreferences(Context)

        /*	    
	    mSaveMode.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Toast.makeText(alarm_pref.this, "let's update!", Toast.LENGTH_SHORT).show();
	    	*/    		    	
	    	     	    		//assign m_mode;
	    	     	    	   /*
	    	     	    		SharedPreferences mySharedPreferences = getSharedPreferences(m_mode, mode);
	    	     	    	     
	    	     	    		  // Retrieve an editor to modify the shared preferences.
	    	     	    	     SharedPreferences.Editor editor = mySharedPreferences.edit();
	    	     	    	     // Store new primitive types in the shared preferences object.
	    	     	    	     editor.putBoolean("isTrue", true);
	    	     	    	     editor.putFloat("lastFloat", 1f);
	    	     	    	     editor.putInt("wholeNumber", 2);
	    	     	    	     editor.putLong("aNumber", 3l);
	    	     	    	     editor.putString("textEntryValue", "Not Empty");
	    	     	    	     // Commit the changes.
	    	     	    	     editor.commit();
								*/
	    	     	    		
	    	  /*   	    		
	    	     	  }
	    	   });*/

    } 


    private void getPrefs(String _pref){
    	//We set new default values depending on the mode selected.
    	
    	mySharedPreferences = getSharedPreferences(_pref,mode);
    	modePref.setTitle(getString(R.string.mode)+ " : " + _pref);
 		
    	//Toast.makeText(getBaseContext(),"snooze" + mySharedPreferences.getString("snoozePref","5"),Toast.LENGTH_LONG).show();
   	 
    	greetingPref.setText(mySharedPreferences.getString("greetingPref",""));
    	vibratePref.setChecked(mySharedPreferences.getBoolean("vibratePref",false));
    	nerdPref.setChecked(mySharedPreferences.getBoolean("nerdPref",false));
    	snoozePref.setValue(mySharedPreferences.getString("snoozePref","5"));
    	
    }
        
}