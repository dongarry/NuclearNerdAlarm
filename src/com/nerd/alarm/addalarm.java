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
	private TextView mSelectTime,mRepeatTime;
	private int mHour, mCurrHour, mEnabled=0;
	private int mMinute, mCurrMin;
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
        
        //This will be the main screen to set the characteristics of the alarm.
       // General Mode preferences will be set on a separate screen..(might change this though..)
        		
        // Time widget..
        // capture our View elements
        //TextView mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mRepeatTime = (TextView) findViewById(R.id.repeatLine);
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
		final EditText edittext = (EditText) findViewById(R.id.edittext);
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
	                		0,
	                		mEnabled);     
	                db.close();
	                setTime((int)(id));
	                finish(); // We're done with this.
	            	}
	        });
    	
    } //OnCreate
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
    // You can use the requestCode to select between multiple child
    // activities you may have started.  Here there is only one thing
    // we launch.
    	Toast.makeText(addalarm.this, "Repeat Result:", Toast.LENGTH_SHORT).show();
  	  
    	super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        	Toast.makeText(addalarm.this, "Repeat :" + data.getStringExtra("SelectedBook"), Toast.LENGTH_SHORT).show();
    	    
        // This is a standard resultCode that is sent back if the
        // activity doesn't supply an explicit result.  It will also
        // be returned if the activity failed to launch.
        //if (resultCode == RESULT_CANCELED) {
        //    Toast.makeText(addalarm.this, "Repeat Selection Cancelled" + data.getStringExtra("SelectedBook"), Toast.LENGTH_SHORT).show();
	    	
        // Our protocol with the sending activity is that it will send
        // text in 'data' as its result.
        } 
        else {
        	Toast.makeText(addalarm.this, "Repeat :" + data.getStringExtra("SelectedBook"), Toast.LENGTH_SHORT).show();
	    	
        }

    //}
}

    
    private void setTime(int alarmID) {
	      Intent alarmIntent = new Intent(this, AlarmActivity.class);
	      
	      Bundle params = new Bundle();
	      params.putLong("AlarmID",alarmID);   
	      alarmIntent.putExtras(params); //Pass the Alarm ID
  	      
	      AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
  	      PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
  	      
  	      long timeDiff =0;
  	      int mDayFlag=0;
  	      
  	     Calendar alarmTime = Calendar.getInstance();
  	     mCurrHour = alarmTime.get(Calendar.HOUR_OF_DAY);
	      mCurrMin = alarmTime.get(Calendar.MINUTE);
	      
	      Toast.makeText(addalarm.this, "Set : " + mCurrHour + ":" + mCurrMin + " - " + mHour + ":" + mMinute, Toast.LENGTH_SHORT).show();
	      
	      
	      if (mCurrHour>mHour) {
	    	  timeDiff+=86400000; //Set for next day
	    	  mDayFlag=1;
	    	  Toast.makeText(addalarm.this, "1 Hour > " + timeDiff, Toast.LENGTH_SHORT).show();		      
	      };
	      
	      if (mCurrHour<mHour) {
	    	  timeDiff+=((mHour-mCurrHour)*3600000);
	    	  Toast.makeText(addalarm.this, "2 Hour < " + timeDiff, Toast.LENGTH_SHORT).show();

	      };
	      
	      if (mCurrMin>mMinute) {
	    	  mMinute+=60;
	    	 // if (mDayFlag==1){
	    		//  	timeDiff-=3600000; //Take off an hour
	  	    	//  Toast.makeText(addalarm.this, "3 Min > " + timeDiff, Toast.LENGTH_SHORT).show();

	    	  //}
	    	  //else {
	    	//	  timeDiff+=86400000;  
	    	  //		}
	    	  };
	    	  
	      if (mCurrMin<mMinute) {
	    	  if (mMinute>60) {
	    		  timeDiff-=3600000; // take off an hour
	    	  }
	    	  Toast.makeText(addalarm.this, "3 Min <" + timeDiff, Toast.LENGTH_SHORT).show();
	    	  timeDiff+=(((mMinute-mCurrMin)*60000)); // Add minutes
	    	  Toast.makeText(addalarm.this, "4 Min <" + timeDiff, Toast.LENGTH_SHORT).show();

	      };
	      
	      timeDiff-=10000; //Let's start up 10 Seconds before we're due.
	      
	      if (mEnabled==1){timeDiff=3000;
	      	alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDiff, pendingIntent);
	      timeDiff=20000;
	      }; // For test purposes
  	      
	      Toast.makeText(addalarm.this, "Set : " + timeDiff, Toast.LENGTH_SHORT).show();
	      //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDiff, pendingIntent);
    	
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
	