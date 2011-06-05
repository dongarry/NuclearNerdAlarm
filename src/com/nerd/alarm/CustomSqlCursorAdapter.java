package com.nerd.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Credits:
 * http://automateddeveloper.blogspot.com/2010/09/everything-you-need-to-know-about.html
 * http://thinkandroid.wordpress.com/2010/01/11/custom-cursoradapters/
 * NerdAlarm - This is the adapter for the main alarm screen - it loads the alarms list view and
 * 	caters for switching the alarms on and off
 * TODO - CustomSqlCursorAdapter - instead of checkboxes, have icons for each mode
 * 	Incorporate colours into Preferences (maybe)	
 *  Remove dependancy on Display_Records class - this is not required..
 */

public class CustomSqlCursorAdapter extends SimpleCursorAdapter {
	private Context context; 
    private long m_alarmID;
	private Cursor myCursor;
    private String m_AlarmTime;
    private int mMode=0; 
    DatabaseAdapter c_db = null; 
	
	public CustomSqlCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to,DatabaseAdapter db) {
        super(context, layout, cursor, from, to);  
        this.context = context; 
        this.myCursor=cursor;
        this.c_db= db;
		}
	
	
	public View getView(int pos, View inView, ViewGroup parent) {
	       View v = inView;	
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.alarm_list, null);
	       }
	       this.myCursor.moveToPosition(pos);		
	       
	       String _time = this.myCursor.getString(this.myCursor.getColumnIndex("time"));
	       TextView aTime = (TextView) v.findViewById(R.id.alarmTime);
	       aTime.setText(_time);
	       
	       String _title = this.myCursor.getString(this.myCursor.getColumnIndex("title"));
	       mMode = this.myCursor.getInt(this.myCursor.getColumnIndex("mode"));
	       TextView aTitle = (TextView) v.findViewById(R.id.firstLine);
	       aTitle.setText(_title);
	       
	       /* TODO Incorporate colours into Preferences*/
	       if(mMode==0){aTime.setTextColor(context.getResources().getColor(R.color.forest_green));} 		//Bunny
	       else if(mMode==1){aTime.setTextColor(context.getResources().getColor(R.color.medium_orchid));}	//Nerd
	       else if(mMode==2){aTime.setTextColor(context.getResources().getColor(R.color.dark_orange));}		//Nuclear
	       else if(mMode==3){aTime.setTextColor(context.getResources().getColor(R.color.black));}			//Ninja
	       else if(mMode==4){aTime.setTextColor(context.getResources().getColor(R.color.firebrick));}		//Angry
	       else if(mMode==5){aTime.setTextColor(context.getResources().getColor(R.color.royal_blue));;}		//Happy
	       
	       
	       int enabled = this.myCursor.getInt(this.myCursor.getColumnIndex("enabled"));
	       int repeat = this.myCursor.getInt(this.myCursor.getColumnIndex("repeat"));
	       
	       DisplayRecords my_records = new DisplayRecords();
	       my_records.SetMe(context); 
	       String mRepeatDesc=my_records.setDays(repeat);
	       
	       TextView aCounter = (TextView) v.findViewById(R.id.secondLine);
	       aCounter.setText(mRepeatDesc);
	       
	       CheckBox cb=(CheckBox)v.findViewById(R.id.enable_cbx);
	       cb.setTag(new Integer(pos));
	       if  (enabled>0) {
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
						if(c_db.enableAlarm(m_alarmID,1)){Log.i("NerdAlarm","Enabled Alarm :" + m_alarmID);}
						//Toast.makeText(context, context.getString(R.string.alarm_enabled), Toast.LENGTH_SHORT).show();
		       			setalarm((int)(m_alarmID),true,m_AlarmTime);
					} 
					else if (!cBox.isChecked()) {
						if (c_db.enableAlarm(m_alarmID,0)){Log.i("NerdAlarm","Disabled Alarm :" + m_alarmID);}
						Toast.makeText(context, context.getString(R.string.alarm_disabled), Toast.LENGTH_SHORT).show();
		       			setalarm((int)(m_alarmID),false,"00:00");
					}
				}
			});

	       return(v);
	}

	 public void setalarm(int _id, boolean _enabled, String _alarmTime) {
	       
	      Intent alarmIntent = new Intent(context, AlarmActivity.class);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, _id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
	        if (_enabled){
	        	String[] parts = _alarmTime.split(":",2);
	        	int hours = Integer.valueOf(parts[0]);
	        	int minutes = Integer.valueOf(parts[1]);
	        	 
	        	DisplayRecords my_a_records = new DisplayRecords();
	        	my_a_records.SetMe(context); 
	        	_alarmTime = my_a_records.setTime(_id,hours,minutes,0,mMode);
	        	Toast.makeText(context, m_AlarmTime, Toast.LENGTH_SHORT).show();
       			//a_enabled = my_a_records.testMe();
	        	
	        }
	        
	        else {
	        	Log.i("NerdAlarm","Cancelling the intent:" + _id);
	        	alarmManager.cancel(pendingIntent);	//disable alarm
	        }
	    }
} 

