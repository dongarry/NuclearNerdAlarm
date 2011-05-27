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

import android.util.Log;
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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;


public class addalarm extends Activity {
	TextView mSelectTime,mRepeatTime;
	Spinner mode_spinner;
	EditText title_edittext; 
	CheckBox enabled_cb,test_cb;
	
	private int mHour, mCurrHour, mEnabled=0, mCounter=0,mStat=0,mBuffer=0;
	private int mMinute, mCurrMin,selectedDays=0,mReset=0,mTest=0;
	private long mAlarmID=0;
	private String mAlarmtime;
	static final private int GET_REPEAT = 1;

	static final int TIME_DIALOG_ID = 0;
	
	database_adapter _db = new database_adapter(this); 
	
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
		
        //This will be the main screen to set the characteristics of the alarm.
       // General Mode preferences will be set on a separate screen..(might change this though..)
        		
        Bundle aBundle = this.getIntent().getExtras();
        mAlarmID = aBundle.getLong("Alarm");
        
     // get the current time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	    		this, R.array.modes_array, android.R.layout.simple_spinner_item);
	        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	mode_spinner.setAdapter(adapter);
	   
        
        if(mAlarmID>0){
        	_db.open();
        	Cursor _a = _db.getAlarm(mAlarmID);
            LoadExistingAlarm(_a);
        	_db.close();}
        	else{
        		// display the current date
                updateDisplay();                
        	}
        
        // We don't want the keyboard to pop up automatically..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        // add a click listener to the button
        mSelectTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        
        mRepeatTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Bundle aBundle = new Bundle();
            	aBundle.putLong("Alarm",mAlarmID);
            	Intent dayIntent = new Intent(addalarm.this,days.class);
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
        
		enabled_cb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {mEnabled=1;}
					 else {mEnabled=0;}		                
		            }
		        });
        
		test_cb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {mTest=1;}
					 else {mTest=0;}		                
		            }
		        });
		
	        	
	    mSaveAlarm.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	            	_db.open();        
	            
	            	int _mode = mode_spinner.getSelectedItemPosition();
	                if (_mode<0) {_mode=0;}
	            	
	                //Save alarm details
	                if(mAlarmID>0){
	                	mReset=1;
		                boolean bUpdate = _db.updateAlarm(mAlarmID,
		    		                		mAlarmtime,
		    		                		title_edittext.getText().toString(),
		    		                		selectedDays,
		    		                		mEnabled,
		    		                		mCounter,
		    		                		_mode,
		    		                		mTest);
		                
		                if (bUpdate){Log.i("NerdAlarm","Added Alarm " + mAlarmID + " to DB");}
		                else{Log.i("NerdAlarm","FAILED adding Alarm " + mAlarmID + " to DB");
		                	Toast.makeText(addalarm.this, "ERROR! : Alarm was not saved!", Toast.LENGTH_SHORT).show();}
	                	}
	                
	                else{
	                	mAlarmID = _db.insertAlarm(
		                		mAlarmtime,
		                		title_edittext.getText().toString(),
		                		selectedDays,
		                		mEnabled,
		                		mCounter,
		                		_mode,
		                		mTest);//,mStat);     
	                }
		            
	                _db.close();
	                
	                //if (mReset==1){Toast.makeText(addalarm.this, "Alarm: is reset!", Toast.LENGTH_SHORT).show();
	                setTime((int)(mAlarmID),mReset);
	                finish(); 
	                mReset=0;
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
    	 if ((repeatSelect & 2) == 4) {_days += " " + getString(R.string.monday);}
    	 if ((repeatSelect & 4) == 8) {_days += " " + getString(R.string.tuesday);}
    	 if ((repeatSelect & 8) == 16) {_days += " " + getString(R.string.wednesday);}
    	 if ((repeatSelect & 16) == 32) {_days += " " + getString(R.string.thursday);}
    	 if ((repeatSelect & 32) == 64) {_days += " " + getString(R.string.friday);}
    	 if ((repeatSelect & 64) == 128) {_days += " " + getString(R.string.saturday);}
    	 if ((repeatSelect & 128) == 2) {_days += " " + getString(R.string.sunday);}
    	 if (repeatSelect ==254){_days = getString(R.string.enabled) + " " + getString(R.string.everyday);}
     	 if (repeatSelect ==124){_days = getString(R.string.enabled) + " " + getString(R.string.weekdays);}
    	
    	return _days;
    }
    
    public void setTime(int alarmID, int reset) {
    	 //TODO call the display records class ..
		 Intent alarmIntent = new Intent(this, AlarmActivity.class);
		      
		 Bundle params = new Bundle();
		 params.putLong("AlarmID",alarmID);   
		 alarmIntent.putExtras(params); //Pass the Alarm ID
	  	      
		 AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	  	 PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	  	 
	  	if (mEnabled==1){
	  		 //If an alarm has been previously set with the same intent,
	  		 // alarm Manager will automatically cancel it and set the new one.
	  		 	 
	  		 long _timeDiff =0; 
	  	     Calendar alarmTime = Calendar.getInstance();
	  	     mCurrHour = alarmTime.get(Calendar.HOUR_OF_DAY);
		     mCurrMin = alarmTime.get(Calendar.MINUTE);
		     
		     if (mCurrHour>mHour) {mHour+=24;}
		      
		     if (mCurrHour<mHour) {_timeDiff+=((mHour-mCurrHour)*3600000);}
		      
		     if (mCurrMin>mMinute) {mMinute+=60;}
		    	  
		     if (mCurrMin<mMinute) {
		    	  if (mMinute>60) {_timeDiff-=3600000;} // take off an hour
		    	  _timeDiff+=(((mMinute-mCurrMin)*60000));} // Add minutes
		       
		     _timeDiff-=10000; //Let's start up 10 Seconds before we're due.
		      //TODO - Switch this back
		     //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + _timeDiff, pendingIntent);
		     alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2000, pendingIntent);
		     
		     String alarmDetails =  getString(R.string.alarm_enabled) + " ";
		     _timeDiff=_timeDiff/1000;
		     if (_timeDiff>(24 * 3600)){
		    	  alarmDetails =  alarmDetails + (_timeDiff / (24 * 3600)) + " " + getString(R.string.alarm_days) + (_timeDiff/(3600)) + " " + getString(R.string.alarm_hours) + (_timeDiff%(3600)/60) + " " + getString(R.string.alarm_minutes) + ".";}
		     else if (_timeDiff>(3600)){
		    	  alarmDetails =  alarmDetails + (_timeDiff/(3600)) + " " + getString(R.string.alarm_hours) + (_timeDiff%(3600)/60) + " " + getString(R.string.alarm_minutes) + ".";}	    	  
			 else
				alarmDetails =  alarmDetails + (_timeDiff%(3600)/60) + " " + getString(R.string.alarm_minutes) + ".";
	  		
		     Toast.makeText(addalarm.this, alarmDetails, Toast.LENGTH_SHORT).show();	

	  	}
	  	else
	  	{
	  		  if (reset==1) { // Cancel Intent if it exists
	  			Toast.makeText(addalarm.this, "Cancelled previous alarm", Toast.LENGTH_SHORT).show();
	  			alarmManager.cancel(pendingIntent);	
	  			Toast.makeText(addalarm.this, getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();	
	  		  }
	  	}
		      return;
    };
  	    

    private void LoadExistingAlarm(Cursor alarmDetails) {
    	   mAlarmtime = alarmDetails.getString(alarmDetails.getColumnIndex("time"));
    	   mSelectTime.setText(getString(R.string.timesetto)+ " " + mAlarmtime);
	       
	       String _title = alarmDetails.getString(alarmDetails.getColumnIndex("title"));
	       //EditText aTitle = (EditText) findViewById(R.id.edittext);
	       title_edittext.setText(_title);
	       
	       int _mode = alarmDetails.getInt(alarmDetails.getColumnIndex("mode"));
	       //Spinner mSpinner = (Spinner) findViewById(R.id.mode_spinner);
	       Log.i("NerdAlarm","Mode returned:"+_mode);
	       mode_spinner.setSelection(_mode);
	       //mode_spinner.
	       selectedDays = alarmDetails.getInt(alarmDetails.getColumnIndex("repeat"));
	       mRepeatTime.setText(setDays(selectedDays));
	       
	       int _counter = alarmDetails.getInt(alarmDetails.getColumnIndex("enabled"));
	       //CheckBox cb=(CheckBox) findViewById(R.id.checkbox);
	       mEnabled=_counter;
	       if  (_counter>0) {
	       		enabled_cb.setChecked(true);}
	       else enabled_cb.setChecked(false);
	       
	       
	       int _test = alarmDetails.getInt(alarmDetails.getColumnIndex("test"));
	       if  (_test>0) {
	       		test_cb.setChecked(true);}
	       else test_cb.setChecked(false);
	       
    }

    private void updateDisplay() {
    	StringBuilder _alarmTime = new StringBuilder()
        .append(pad(mHour)).append(":")
        .append(pad(mMinute));
        
    	//mSelectTime.append(sAlarmTime);
    	mSelectTime.setText(getString(R.string.timesetto)+ " " + _alarmTime);
    	mAlarmtime=_alarmTime.toString();
    	        
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
                mHour = hourOfDay;
                mMinute = minute;
                updateDisplay();
            }
        };
        
        @Override
        protected Dialog onCreateDialog(int _id) {
            switch (_id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            }
            return null;
        }
}

