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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomSqlCursorAdapter extends SimpleCursorAdapter {// implements OnClickListener {
	// Taken from http://appfulcrum.com/?p=351
	private Cursor c;
	private Context context;    

	public CustomSqlCursorAdapter(Context context, int layout, Cursor c,
	        String[] from, int[] to) {
	    super(context, layout, c, from, to);
	    this.c = c;
	        this.context = context;

	}

	public View getView(int pos, View inView, ViewGroup parent) {
	    View v = inView;
	    if (v == null) {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.alarm_list, null);
	    }
	    Log.i("pos = ..................", "pos = "+pos);
	    this.c.moveToPosition(pos); 
	    //this.c.moveToPosition(this.c.getInt(this.c.getColumnIndex("_id")));
	    TextView tBox = (TextView)v.findViewById(R.id.alarm_id);
	    tBox.setTag(this.c.getInt(this.c.getColumnIndex("enabled")));
	    
	    //CheckBox cBox = (CheckBox) v.findViewById(R.id.enable_cbx);
	    //cBox.setTag(this.c.getInt(this.c.getColumnIndex("enabled")));

	    /*
	     * when reloading the list, check for chkd status, this is broken.  Need to query db directly.
	     */
	    database_adapter mDbHelper = new database_adapter(context);
	    mDbHelper.open(); 

	    int idTag = (Integer) cBox.getTag();                
	    int checked = mDbHelper.selectChk(idTag);
	    
	    mDbHelper.close();
	    Log.i("results from selectChk.....................", ""+checked);
	    if (checked == 1) {
	        cBox.setChecked(true);          
	    } else {
	        cBox.setChecked(false);
	    }

	    /*
	     * Populate the list
	     */     
	    TextView txtdateTime = (TextView)v.findViewById(R.id.time);
	    txtdateTime.setText(this.c.getString(this.c.getColumnIndex("time")));   
	    TextView txtdateEvent = (TextView)v.findViewById(R.id.event);
	    txtdateEvent.setText(this.c.getString(this.c.getColumnIndex("event")));
	    TextView txtdateLocation = (TextView)v.findViewById(R.id.location);
	    txtdateLocation.setText(this.c.getString(this.c.getColumnIndex("location")));
	    ImageView arrow = (ImageView) v.findViewById(R.id.arrowId);
	    arrow.setImageResource(R.drawable.rightarrow);


	    Log.i("if chk in db is = 1 then set checked.........",this.c.getString(this.c.getColumnIndex("checked")) +" " +this.c.getString(this.c.getColumnIndex("time")));        


	    /*
	     * Controls action based on clicked list item (background)
	     */
	    View lv = v.getRootView(); 
	    lv.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View lv) {
	            CheckBox cBox = (CheckBox) lv.findViewById(R.id.bcheck);

	            // id holds the rowid of each event.  pass this to a new activity to query for description

	            // Call Event Detail
	            String id = cBox.getTag().toString();
	            Intent i = new Intent(context, EventDetail.class);
	            //i.putExtra("description", c.getString(c.getColumnIndex("description")));
	            i.putExtra("_id", id);
	            context.startActivity(i);

	        }

	    });

	    /*
	     * Begin - Controls action based on clicked Text only

	    txtdateEvent.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            CharSequence charseq = "Darth Vader is alive";
	            Toast.makeText(context, charseq, Toast.LENGTH_SHORT).show();
	        }

	    });

	    * End - Controls action based on clicked Text only
	    */


	    /*
	     * Controls action based on clicked checkbox 
	     */
	    cBox.setOnClickListener(new OnClickListener() {  
	        @Override
	        public void onClick(View v) {
	            EventDbAdapter mDbHelper = new EventDbAdapter(context);
	            mDbHelper.open(); 

	            CheckBox cBox = (CheckBox) v.findViewById(R.id.bcheck);
	            if (cBox.isChecked()) {
	                //cBox.setChecked(false);
	                CharSequence charseq = "Added to My Schedule";
	                Toast.makeText(context, charseq, Toast.LENGTH_SHORT).show();

	                // Update the database for each checked item
	                mDbHelper.updateChecked(cBox.getTag().toString(), "1");     
	                c.requery();

	                // Verify that the db was updated for debugging purposes
	                String event = c.getString(c.getColumnIndex("event"));                  
	                int id = (Integer) cBox.getTag();

	                Log.i("checked _id...........", "id= " + id + " " +c.getString(c.getColumnIndex("_id"))); 
	                Log.i("checked checked...........", ""+c.getString(c.getColumnIndex("checked")));

	            } else if (!cBox.isChecked()) {
	                //cBox.setChecked(true);
	                CharSequence charseq = "Removed from My Schedule";
	                Toast.makeText(context, charseq, Toast.LENGTH_SHORT).show();
	                // checkList.remove(cBox.getTag());
	                //checkList.add((Integer) cBox.getTag());
	                String event = c.getString(c.getColumnIndex("event"));
	                //int id = c.getInt(c.getColumnIndex("_id"));
	                int id = (Integer) cBox.getTag();
	                mDbHelper.updateChecked(cBox.getTag().toString(), "0"); 
	                c.requery();
	                //int sqlresult = mDbHelper.selectChk(id, event);                   
	                //Log.i("sqlresult checked value after update...........", ""+ sqlresult);                  
	                //Log.i("unchecked _id...........", ""+c.getString(c.getColumnIndex("_id"))); 
	                //Log.i("unchecked checked...........", ""+c.getString(c.getColumnIndex("checked")));
	            }
	            //mDbHelper.close();
	        }
	    });

	    return(v);
	}