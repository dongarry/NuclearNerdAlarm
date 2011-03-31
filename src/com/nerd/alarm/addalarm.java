package com.nerd.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
//import android.view.Window;
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
	private TextView mTimeDisplay;
	private Button mPickTime;
	private Button mSaveAlarm;
	private TextView mSelectTime;
	//private byte mAlarmOk = 0;   
	private int mHour;
	private int mMinute;
	private String mAlarmtime;
	private String mAlarmTitle;
	
	
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
        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mSelectTime = (TextView) findViewById(R.id.secondLine);
        //mPickTime = (Button) findViewById(R.id.pickTime);
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
        	
		        //icon Button handle
		        final Button button = (Button) findViewById(R.id.button);
		        button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		                // Perform action on clicks
		                Toast.makeText(addalarm.this, "Beep Bop", Toast.LENGTH_SHORT).show();
		            }
		        });
        
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
		                // Perform action on clicks, depending on whether it's now checked
		                if (((CheckBox) v).isChecked()) {
		                    Toast.makeText(addalarm.this, "Selected", Toast.LENGTH_SHORT).show();
		                } else {
		                    Toast.makeText(addalarm.this, "Not selected", Toast.LENGTH_SHORT).show();
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
	                long id;
	                Toast.makeText(addalarm.this,mAlarmtime, Toast.LENGTH_SHORT).show();;
	                //Toast.makeText(addalarm.this,edittext.getText().toString(), Toast.LENGTH_SHORT).show();;
	                id = db.insertAlarm(
	                		mAlarmtime,
	                		edittext.getText().toString(),
	                		0);     
	                
	                
	                db.close();
	                Toast.makeText(addalarm.this,"Saved" + id, Toast.LENGTH_SHORT).show();;
	
	            }
	        });
    	
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
	