package com.nerd.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import java.util.Calendar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;


public class addalarm extends Activity {
	private Button mSaveAlarm;
	private TextView mSelectTime,mRepeatTime;
	private int mHour, mCurrHour, mEnabled=0, mCounter=0,mStat=0,mReset=0;
	private int mMinute, mCurrMin,selectedDays=0;
	private long mAlarmID=0;
	private String mAlarmtime;
	static final private int GET_REPEAT = 1;

	static final int TIME_DIALOG_ID = 0;
	
	database_adapter db = new database_adapter(this); 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.add));
        setContentView(R.layout.addalarm);
        
        final EditText edittext = (EditText) findViewById(R.id.edittext);
    	mRepeatTime = (TextView) findViewById(R.id.repeatLine);
        mSelectTime = (TextView) findViewById(R.id.secondLine);
        mSaveAlarm=(Button) findViewById(R.id.saveAlarm);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
	    
        //This will be the main screen to set the characteristics of the alarm.
       // General Mode preferences will be set on a separate screen..(might change this though..)
        		
        // Time widget..
        // capture our View elements
        //TextView mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        
        Bundle aBundle = this.getIntent().getExtras();
        mAlarmID = aBundle.getLong("Alarm");
        
        // We don't want the keyboard to pop up automatically..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        /*mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });*/ 
        
        // add a click listener to the button
        mSelectTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        
        mRepeatTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent dayIntent = new Intent(addalarm.this,days.class);
            	startActivityForResult (dayIntent,GET_REPEAT);
        	}
        });
           
        
        // get the current time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // display the current date
        updateDisplay();
        	
        //Edit Text box handle
		edittext.setOnKeyListener(new OnKeyListener() {
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
        
		//Handle CheckBox click
		final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
		checkbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
		                mEnabled=1;
					} else {
		                	mEnabled=0;
		                }
		            }
		        });
        
		//Handle Spinner
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	    		this, R.array.modes_array, android.R.layout.simple_spinner_item);
	        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	spinner.setAdapter(adapter);
	   
	        	
	    // Handle Save button
	    mSaveAlarm.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	            	db.open();        
	            
	            	int mMode = spinner.getSelectedItemPosition();
	                if (mMode<0) {mMode=0;}
	            	
	                //Save alarm details
	                if(mAlarmID>0){
	                	
	                	if (db.getEnabledAlarm(mAlarmID,mEnabled)){mReset=1;}
		            	
		                boolean bUpdate = db.updateAlarm(mAlarmID,
		    		                		mAlarmtime,
		    		                		edittext.getText().toString(),
		    		                		selectedDays,
		    		                		mEnabled,
		    		                		mCounter,
		    		                		mMode);
		                
		                if (bUpdate){Toast.makeText(addalarm.this, "Alarm:" + mAlarmID + " has been updated", Toast.LENGTH_SHORT).show();}
		                else{Toast.makeText(addalarm.this, "Alarm:" + mAlarmID + " UPDATE FAILED!", Toast.LENGTH_SHORT).show();}
	                	}
	                
	                else{
	                	mAlarmID = db.insertAlarm(
		                		mAlarmtime,
		                		edittext.getText().toString(),
		                		selectedDays,
		                		mEnabled,
		                		mCounter,
		                		mMode);//,mStat);     
	                }
		            
	                db.close();
	                if (mReset==1){Toast.makeText(addalarm.this, "Alarm: is reset!", Toast.LENGTH_SHORT).show();
	                	setTime((int)(mAlarmID));}
	                finish(); 
	            	}
	        });
	    /*
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	        //@Override
	        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	            int item = spinner.getSelectedItemPosition();
	            Toast.makeText(getBaseContext(), 
	                "You have selected the item: " + item, 
	                Toast.LENGTH_SHORT).show();
	        }


	       // @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	        }
	    });*/
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
        	} 
    	case 101:
        	{
        		mAlarmID=data.getIntExtra("AlarmID", 0);  
        		//mRepeatTime.setText(setDays(selectedDays));
        	} 
        default: {
        	//Toast.makeText(addalarm.this, "Repeat Failed:", Toast.LENGTH_SHORT).show();
        	}
    	}
    }

    private String setDays(int repeatSelect){
    	 String days = getString(R.string.enabled);
    	
    	 if (repeatSelect==0) {days = getString(R.string.norepeatset);}
    	 if ((repeatSelect & 2) == 2) {days += " " + getString(R.string.monday);}
    	 if ((repeatSelect & 4) == 4) {days += " " + getString(R.string.tuesday);}
    	 if ((repeatSelect & 8) == 8) {days += " " + getString(R.string.wednesday);}
    	 if ((repeatSelect & 16) == 16) {days += " " + getString(R.string.thursday);}
    	 if ((repeatSelect & 32) == 32) {days += " " + getString(R.string.friday);}
    	 if ((repeatSelect & 64) == 64) {days += " " + getString(R.string.saturday);}
    	 if ((repeatSelect & 128) == 128) {days += " " + getString(R.string.sunday);}
    	 if (repeatSelect ==254){days = getString(R.string.enabled) + " " + getString(R.string.everyday);}
     	if (repeatSelect ==62){days = getString(R.string.enabled) + " " + getString(R.string.weekdays);}
    	
    	return days;
    }
    
    private void setTime(int alarmID) {
    	 //TODO call the display records class ..
		 Intent alarmIntent = new Intent(this, AlarmActivity.class);
		      
		 Bundle params = new Bundle();
		 params.putLong("AlarmID",alarmID);   
		 alarmIntent.putExtras(params); //Pass the Alarm ID
	  	      
		 AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	  	 PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	  	 
	  	if (mEnabled==1){
		  	 long timeDiff =0; 
	  	     Calendar alarmTime = Calendar.getInstance();
	  	     mCurrHour = alarmTime.get(Calendar.HOUR_OF_DAY);
		     mCurrMin = alarmTime.get(Calendar.MINUTE);
		        
		     if (mCurrHour>mHour) {mHour+=24;}
		      
		     if (mCurrHour<mHour) {timeDiff+=((mHour-mCurrHour)*3600000);}
		      
		     if (mCurrMin>mMinute) {mMinute+=60;}
		    	  
		     if (mCurrMin<mMinute) {
		    	  if (mMinute>60) {timeDiff-=3600000;} // take off an hour
		    	  timeDiff+=(((mMinute-mCurrMin)*60000));} // Add minutes
		       
		     timeDiff-=10000; //Let's start up 10 Seconds before we're due.
		      
		     alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDiff, pendingIntent);
		     
		     String alarmDetails =  getString(R.string.alarm_enabled) + " ";
		     timeDiff=timeDiff/1000;
		     if (timeDiff>(24 * 3600)){
		    	  alarmDetails =  alarmDetails + (timeDiff / (24 * 3600)) + " " + getString(R.string.alarm_days) + (timeDiff/(3600)) + " " + getString(R.string.alarm_hours) + (timeDiff%(3600)/60) + " " + getString(R.string.alarm_minutes) + ".";}
		     else if (timeDiff>(3600)){
		    	  alarmDetails =  alarmDetails + (timeDiff/(3600)) + " " + getString(R.string.alarm_hours) + (timeDiff%(3600)/60) + " " + getString(R.string.alarm_minutes) + ".";}	    	  
			 else
				alarmDetails =  alarmDetails + (timeDiff%(3600)/60) + " " + getString(R.string.alarm_minutes) + ".";
	  		
		     Toast.makeText(addalarm.this, alarmDetails, Toast.LENGTH_SHORT).show();	

	  	}
	  	else
	  	{
	  			alarmManager.cancel(pendingIntent);	
	  			Toast.makeText(addalarm.this, getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();	
	  	}
		      return;
    };
  	    
    
    private void updateDisplay() {
    	StringBuilder sAlarmTime = new StringBuilder()
        .append(pad(mHour)).append(":")
        .append(pad(mMinute));
        
    	//mSelectTime.append(sAlarmTime);
    	mSelectTime.setText(getString(R.string.timesetto)+ " " + sAlarmTime);
    	mAlarmtime=sAlarmTime.toString();
    	        
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
 // the call back received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                updateDisplay();
            }
        };
        
        @Override
        protected Dialog onCreateDialog(int id) {
            switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            }
            return null;
        }
}

