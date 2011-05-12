package com.nerd.alarm;

import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

// With help from...
//http://thinkandroid.wordpress.com/2010/01/11/custom-cursoradapters/

public class CustomSqlCursorAdapter extends SimpleCursorAdapter {
	private Context context; 
    private int layout;
    private long m_alarmID;
	private Cursor myCursor;
    //private Display_Records my_records = new Display_Records();
    private String m_AlarmTime;
    database_adapter c_db = null; 
	
	public CustomSqlCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to,database_adapter db) {
        super(context, layout, cursor, from, to);  
        this.context = context; 
        this.layout = layout; 
        this.myCursor=cursor;
        this.c_db= db;
		}
	
	
	public View getView(int pos, View inView, ViewGroup parent) {
	       View v = inView;	
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.alarm_list, null);
	       }
	       //"time", "title","enabled","counter"
	       this.myCursor.moveToPosition(pos);		
	       
	       String mTime = this.myCursor.getString(this.myCursor.getColumnIndex("time"));
	       TextView aTime = (TextView) v.findViewById(R.id.alarmTime);
	       aTime.setText(mTime);
	       
	       String mTitle = this.myCursor.getString(this.myCursor.getColumnIndex("title"));
	       String mMode = this.myCursor.getString(this.myCursor.getColumnIndex("mode"));
	       TextView aTitle = (TextView) v.findViewById(R.id.firstLine);
	       aTitle.setText(mTitle + ":" + mMode);
	       
	       int mCounter = this.myCursor.getInt(this.myCursor.getColumnIndex("enabled"));
	       
	       Display_Records my_records = new Display_Records();
	       my_records.SetMe(context); 
	       String mCounterDesc=my_records.setDays(mCounter);
	     
	       TextView aCounter = (TextView) v.findViewById(R.id.secondLine);
	       aCounter.setText(mCounterDesc);
	       
	       CheckBox cb=(CheckBox)v.findViewById(R.id.enable_cbx);
	       cb.setTag(new Integer(pos)); // useful locator : Thanks: http://automateddeveloper.blogspot.com/2010/09/everything-you-need-to-know-about.html
	       if  (mCounter>0) {
	       		cb.setChecked(true);}
	       else cb.setChecked(false);
	       
	       cb.setOnClickListener(new OnClickListener() {  
				@Override
				public void onClick(View v) {
					
					CheckBox cBox = (CheckBox) v.findViewById(R.id.enable_cbx);
					Integer posSelected = (Integer)cBox.getTag();
					myCursor.moveToPosition(posSelected);
					
					m_alarmID = myCursor.getLong(myCursor.getColumnIndex(BaseColumns._ID));		    	
					m_AlarmTime = myCursor.getString(myCursor.getColumnIndex("time"));		    	
					
					if (cBox.isChecked()) {
						if(c_db.enableAlarm(m_alarmID,1)){   
						//Toast.makeText(context, "alarm:" + m_alarmID + " Updated!", Toast.LENGTH_SHORT).show();
						}
						//Toast.makeText(context, context.getString(R.string.alarm_enabled), Toast.LENGTH_SHORT).show();
		       			setalarm((int)(m_alarmID),true,m_AlarmTime);
					} 
					else if (!cBox.isChecked()) {
						if (c_db.enableAlarm(m_alarmID,0)){
		       			//Toast.makeText(context, "alarm:" + m_alarmID + " Updated!", Toast.LENGTH_SHORT).show();
						}
		       			Toast.makeText(context, context.getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();
		       			setalarm((int)(m_alarmID),false,"00:00");
					}
				}
			});

	       return(v);
	}

	 public void setalarm(int a_id, boolean a_enabled, String m_AlarmTime) {
	       
	      Intent alarmIntent = new Intent(context, AlarmActivity.class);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, a_id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
	        if (a_enabled){
	        	String[] parts = m_AlarmTime.split(":",2);
	        	int hours = Integer.valueOf(parts[0]);
	        	int minutes = Integer.valueOf(parts[1]);
	        	//Toast.makeText(context, "time :" + parts[0] + ":" + parts[1], Toast.LENGTH_SHORT).show();
	        	 
	        	Display_Records my_a_records = new Display_Records();
	        	my_a_records.SetMe(context); 
	        	m_AlarmTime = my_a_records.setTime(a_id,hours,minutes);
	        	Toast.makeText(context, m_AlarmTime, Toast.LENGTH_SHORT).show();
       			//a_enabled = my_a_records.testMe();
	        	
	        }
	        
	        else {
	        	//disable alarm
	        	alarmManager.cancel(pendingIntent);	
	        }
	    }
	
/*	
@Override   
public void bindView(View view, Context context, Cursor cursor) {
    CheckBox cb=(CheckBox)view.findViewById(R.id.enable_cbx);
    
    checker.setOnCheckedChangeListener(context.ac.this);
    checker.setTag(Long.valueOf(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))));
    // call super class for default binding
    super.bindView(view,context,cursor);
    
    if  (cursor.getLong(cursor.getColumnIndex("enabled"))>0) {
    		cb.setChecked(true);}
    
    cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {            
    		cb.setTag(Long.valueOf(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))));
    	     			
    				}           
	    				}  
    	);
    
    // call super class for default binding
    super.bindView(view,context,cursor);
    
	}
	*/
} 

