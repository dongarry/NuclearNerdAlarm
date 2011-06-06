package com.nerd.alarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import java.util.Calendar;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnKeyListener;

/* 
 * Nerd Alarm - Add update alarms
 */

public class AddAlarm extends Activity {
	
	TextView mSelectTime;
	TextView mRepeatTime;
	Spinner mode_spinner;
	EditText title_edittext; 
	CheckBox enabled_cb,test_cb;
	
	private long mAlarmID=0;
	private String mAlarmtime;
	private int reset=0;
	
	private int selectedDays=0;
	private Alarm oAlarm=null;
	private int alarmMinute;
	private int alarmHour; 
	
	static final private int GET_REPEAT = 1;
	static final int TIME_DIALOG_ID = 0;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.add));
        setContentView(R.layout.addalarm);
        
        title_edittext = (EditText) findViewById(R.id.title_edittext);
        mRepeatTime = (TextView) findViewById(R.id.repeatLine);
        mSelectTime = (TextView) findViewById(R.id.secondLine);
        Button mSaveAlarm=(Button) findViewById(R.id.saveAlarm);
        mode_spinner = (Spinner) findViewById(R.id.mode_spinner);
        enabled_cb = (CheckBox) findViewById(R.id.enabled_checkbox);
		test_cb=(CheckBox) findViewById(R.id.testme_checkbox);
		
        //Mode Characteristics are set on the Preferences screen
		
        Bundle aBundle = this.getIntent().getExtras();
        mAlarmID = aBundle.getLong("Alarm");
        
        // get the current time
        
        final Calendar c = Calendar.getInstance();
        alarmHour = c.get(Calendar.HOUR_OF_DAY);
        alarmMinute = c.get(Calendar.MINUTE);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	    		this, R.array.modes_array, android.R.layout.simple_spinner_item);
	        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	mode_spinner.setAdapter(adapter);
	        	
	    oAlarm = new Alarm(mAlarmID,AddAlarm.this);
	    
	    
        if(oAlarm.isValid())	{
						        	LoadExistingAlarm(oAlarm);
        						}		
        
        else					{
        							updateDisplay(); // display the current time           
        						}     
        	
        
        // We don't want the keyboard to pop up automatically..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        
        mSelectTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        
        mRepeatTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Bundle aBundle = new Bundle();
            	aBundle.putLong("Alarm",mAlarmID);
            	Intent dayIntent = new Intent(AddAlarm.this,RepeatDays.class);
            	dayIntent.putExtras(aBundle);
            	startActivityForResult (dayIntent,GET_REPEAT);
        	}
        });
              	
        title_edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
		        			// Perform action on key press
		        			return true;
		                	}
		                	return false;
		            	}
		        });
        
		
	        	
	    mSaveAlarm.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	            
	            	int mode = mode_spinner.getSelectedItemPosition();
	                if (mode<0) {mode=0;}
	            	
	                reset=(int)(oAlarm.getAlarmEnabled()?1:0); // previous alarm value
	                
	                oAlarm.setAlarmTime(mAlarmtime);
                	oAlarm.setAlarmTitle(title_edittext.getText().toString());
                	oAlarm.setAlarmEnabled((int)(enabled_cb.isChecked()?1:0));
                	oAlarm.setAlarmMode(mode);
                	oAlarm.setAlarmTestMe((int)(test_cb.isChecked()?1:0));
                	oAlarm.setAlarmRepeat(selectedDays);
         	       
	                oAlarm.updateAlarm();
	                
	                if (reset==1 && enabled_cb.isChecked()== false)	{
	                		oAlarm.cancelAlarm();
	                }
	                
	                if (oAlarm.isValid()==false) {}
	                
	                else	{
		                if(oAlarm.getAlarmStatus().length()>1){
		                	Toast.makeText(AddAlarm.this, oAlarm.getAlarmStatus(), Toast.LENGTH_SHORT).show();	
	                	}
	                }
	                
	                finish(); 
	        }
	   });
 
    } //OnCreate
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
    // Take back the days selected for repeat
   	
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (resultCode){
    	case RESULT_OK: 
    	{
        	selectedDays=data.getIntExtra("SelectedDay", 0);  
        	mRepeatTime.setText(setDays(selectedDays));
    		mAlarmID=data.getLongExtra("Alarm", 0);
 	    } 
    	case 101:
        	{
        		mAlarmID=data.getLongExtra("Alarm", 0);  
        		//mRepeatTime.setText(setDays(selectedDays));
        	} 
        default: {}
    	}
    }

    private String setDays(int repeatSelect){
    	 String _days = getString(R.string.enabled);
    	
    	 if (repeatSelect==0) {_days = getString(R.string.norepeatset);}
    	 if ((repeatSelect & 4) == 4) {_days += " " + getString(R.string.monday);}
    	 if ((repeatSelect & 8) == 8) {_days += " " + getString(R.string.tuesday);}
    	 if ((repeatSelect & 16) == 16) {_days += " " + getString(R.string.wednesday);}
    	 if ((repeatSelect & 32) == 32) {_days += " " + getString(R.string.thursday);}
    	 if ((repeatSelect & 64) == 64) {_days += " " + getString(R.string.friday);}
    	 if ((repeatSelect & 128) == 128) {_days += " " + getString(R.string.saturday);}
    	 if ((repeatSelect & 2) == 2) {_days += " " + getString(R.string.sunday);}
    	 if (repeatSelect ==254){_days = getString(R.string.enabled) + " " + getString(R.string.everyday);}
     	 if (repeatSelect ==124){_days = getString(R.string.enabled) + " " + getString(R.string.weekdays);}
    	
    	return _days;
    }
      	    

    private void LoadExistingAlarm(Alarm alarm) 	{
    	
    	   mAlarmtime = alarm.getAlarmTime();
    	   mSelectTime.setText(getString(R.string.timesetto)+ " " + mAlarmtime);
	       
	       title_edittext.setText(alarm.getAlarmTitle());
	       
	       
	       mode_spinner.setSelection(alarm.getAlarmMode());
	       
	       selectedDays = alarm.getAlarmRepeat();
	       mRepeatTime.setText(setDays(alarm.getAlarmRepeat()));
	      
	       enabled_cb.setChecked(alarm.getAlarmEnabled());
	       
	       test_cb.setChecked(alarm.getAlarmTestMe());
	       
	}

    private void updateDisplay() {
    	StringBuilder alarmTime = new StringBuilder()
        .append(pad(alarmHour)).append(":")
        .append(pad(alarmMinute));
        
    	mSelectTime.setText(getString(R.string.timesetto)+ " " + alarmTime);
    	mAlarmtime=alarmTime.toString();
    	        
    }

    private static String pad(int _c) {
        if (_c >= 10)
            return String.valueOf(_c);
        else
            return "0" + String.valueOf(_c);
    }
    
 // the call back received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarmHour = hourOfDay;
                alarmMinute = minute;
                updateDisplay();
            }
        };
        
        @Override
        protected Dialog onCreateDialog(int _id) {
            switch (_id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, alarmHour, alarmMinute, false);
            }
            return null;
        }
}

