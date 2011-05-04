package com.nerd.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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

public class CustomSqlCursorAdapter extends SimpleCursorAdapter{
	private Context context; 
    private int layout;
    private Cursor myCursor;
    
	public CustomSqlCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);  
        this.context = context; 
        this.layout = layout; 
        this.myCursor=cursor;
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
	       //String mMode = this.myCursor.getString(this.myCursor.getColumnIndex("mode"));
	       TextView aTitle = (TextView) v.findViewById(R.id.firstLine);
	       //aTitle.setText(mTitle + ":" + mMode);
	       
	       String mCounter = this.myCursor.getString(this.myCursor.getColumnIndex("enabled"));
	       TextView aCounter = (TextView) v.findViewById(R.id.secondLine);
	       aCounter.setText(mCounter);
	       
	       CheckBox cb=(CheckBox)v.findViewById(R.id.enable_cbx);
	       if  (myCursor.getLong(myCursor.getColumnIndex("enabled"))>0) {
	       		cb.setChecked(true);}
	       
	       return(v);
	}
	
	public void onClick(View v) {
		CheckBox cBox = (CheckBox) v;
		String itemText = (String) cBox.getTag();

		Log.d("debug", "message from click listener");
		/*
		if (cBox.isChecked()) {
			if (!this.selectedItems.contains(itemText))
				this.selectedItems.add(itemText);
		} else {
			if (this.selectedItems.contains(itemText))
				this.selectedItems.remove(itemText);
		}

		SaveSelections();
		*/
	}
	
	
@Override   
public void bindView(View view, Context context, Cursor cursor) {
    CheckBox cb=(CheckBox)view.findViewById(R.id.enable_cbx);
    if  (cursor.getLong(cursor.getColumnIndex("enabled"))>0) {
    		cb.setChecked(true);}
    
    cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {            
	            if(cb.isChecked()) {                    
	            	//stuff
	            }
	            else if(isChecked==false) {
	                // action
	            	}
	        	}           
	    	}
    	);
	}
} 