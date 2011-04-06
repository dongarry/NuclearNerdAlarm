package com.nerd.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import java.util.Calendar;
import android.view.KeyEvent;
import android.view.View;
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


public class addalarm extends Activity {
	private Button mSaveAlarm;
	private TextView mSelectTime;
	private int mHour, mCurrHour, mEnabled=0;
	private int mMinute, mCurrMin;
	private String mAlarmtime;
    
	static final int TIME_DIALOG_ID = 0;
	
	database_adapter db = new database_adapter(this); 
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.add));
        setContentView(R.layout.addalarm);
        
        //This will be the main screen to set the characteristics of the alarm.
       // General Mode preferences will be set on a separate screen..(might change this though..)
        		
        // Time widget..
        // capture our View elements
        TextView mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mSelectTime = (TextView) findViewById(R.id.secondLine);
        mSaveAlarm=(Button) findViewById(R.id.saveAlarm);
        
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

        // get the current time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // display the current date
        updateDisplay();
        	
		        //Edit Text box handle
		        final EditText edittext = (EditText) findViewById(R.id.edittext);
		        edittext.setOnKeyListener(new OnKeyListener() {
		            public boolean onKey(View v, int keyCode, KeyEvent event) {
		                // If the event is a key-down event on the "enter" button
		                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
		                  // Perform action on key press
		                  Toast.makeText(addalarm.this, edittext.getText(), Toast.LENGTH_SHORT).show();
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
	        Spinner spinner = (Spinner) findViewById(R.id.spinner);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.modes_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        
	        // Handle Save button
	        mSaveAlarm.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	db.open();        
	            	
	                //Save alarm details
	                long id = db.insertAlarm(
	                		mAlarmtime,
	                		edittext.getText().toString(),
	                		0);     
	                
	                db.close();
	                
	                setTime(id);
	                finish(); // We're done with this.
	                
	            							}
	        });
    	
    }
    
    private void setTime(long alarmID) {
	      Intent alarmIntent = new Intent(this, AlarmActivity.class);
	      
	      Bundle params = new Bundle();
	      params.putLong("AlarmID",alarmID);   
	      alarmIntent.putExtras(params); //Pass the Alarm ID
  	      
	      AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
  	      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
  	      
  	      long timeDiff =0;
  	      int mDayFlag=0;
  	      
  	     Calendar alarmTime = Calendar.getInstance();
  	     mCurrHour = alarmTime.get(Calendar.HOUR_OF_DAY);
	      mCurrMin = alarmTime.get(Calendar.MINUTE);
	      
	      if (mCurrHour>mHour) {
	    	  timeDiff+=86400000; //Set for next day
	    	  mDayFlag=1;
	      };
	      
	      if (mCurrHour<mHour) {
	    	  timeDiff+=((mHour-mCurrHour)*3600000);
	      };
	      
	      if (mCurrMin>mMinute) {
	    	  mMinute+=60;
	    	  if (mDayFlag==1){
	    		  	timeDiff-=3600000; //Take off an hour
	    	  }
	    	  else {
	    		  timeDiff+=86400000;  
	    	  		}
	    	  };
	    	  
	      if (mCurrMin<mMinute) {
	    	  if (mDayFlag==1) {
	    		  timeDiff-=3600000;
	    	  }
	    	  timeDiff+=(((mMinute-mCurrMin)*60000)); // Add minutes
	      };
	      
	      timeDiff-=10000; //Let's start up 10 Seconds before we're due.
	      
	      if (mEnabled==1){timeDiff=3000;}; // For test purposes
  	      
	      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDiff, pendingIntent);
    	
    }
    
    private void updateDisplay() {
    	StringBuilder sAlarmTime = new StringBuilder()
        .append(pad(mHour)).append(":")
        .append(pad(mMinute));
        
    	//mSelectTime.append(sAlarmTime);
    	mSelectTime.setText(getString(R.string.timesetto)+ sAlarmTime);
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
	