package com.nerd.alarm;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

public class display_records extends ListActivity {
	private CursorAdapter dataSource;
	private static final String fields[] = { "time", "title","repeat",BaseColumns._ID };
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setTitle(getString(R.string.app_name));
	        
	        database_adapter db = new database_adapter(this);
	    	db.open();
	    	Cursor data = db.getAlarms();
	    		
	    	        dataSource = new SimpleCursorAdapter(this,
	    	            R.layout.alarm_list, data, fields,
	    	            new int[] {R.id.alarmTime,R.id.firstLine, R.id.secondLine });
	    	        setListAdapter(dataSource);	 
	 }	
}
