package com.nerd.alarm;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.speech.tts.TextToSpeech;
import android.widget.ListView;

import android.widget.Toast;

/* Credits
 * Android Tutorial on TextToSpeech
 * http://kahdev.wordpress.com/2010/09/27/android-using-the-sqlite-database-with-listview/	   
 * 
 * Nerd Alarm - Main screen to display current alarms and allow each switching on/off
 * custom menu to access preferences and delete
 * TODO Facility to delete just one alarm - maybe play on long click
 *  Cancel all enabled alarms when Delete all is selected, currently these are just dismissed when fired
 * 
 */
public class ViewAlarm extends ListActivity {
    private long m_alarmID;
	private static final int MY_DATA_CHECK_CODE = 1;
	DatabaseAdapter db = null; 
	private CustomSqlCursorAdapter dataSource;
	private static final String fields[] = { "time", "title","enabled","counter","mode",BaseColumns._ID };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
   
        if (this.db == null) {this.db = new DatabaseAdapter(this);}
        
        db.open();
        
        setContentView(R.layout.main); 
        setTitle(getString(R.string.app_name));
	    
        loadAlarms();
        
		Intent checkIntent = new Intent();
	    checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	    startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    
    } 
    
    @Override
        protected void onRestart() {
            super.onRestart();}

    @Override
        protected void onPause() {
            super.onPause();
        }
    
    @Override
    protected void onResume(){
        super.onResume();
        Log.i("NerdAlarm","ViewAlarm - > Requery!");
        dataSource.getCursor().requery();
  }
    
    protected void onClose(){
        //super.onClose();
    	this.db.close();
    }
     
    // Edit existing alarms
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
 
        //Intent intent = new Intent(Intent.ACTION_CALL);
        Cursor _cursor = (Cursor) dataSource.getItem(position);
        m_alarmID = _cursor.getLong(_cursor.getColumnIndex(BaseColumns._ID));
    	
        if(m_alarmID>0){
        	Bundle aBundle = new Bundle();
        	aBundle.putLong("Alarm",m_alarmID);
        	Intent editIntent = new Intent(this,AddAlarm.class); 
        	editIntent.putExtras(aBundle);       
		    startActivityForResult(editIntent,0);
        }
    }
    
    //Check Text to Speech..
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case MY_DATA_CHECK_CODE: 	{
    	    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
    	    }
    	    else 	{
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);	}
        	}
        default: {}
        }
    }
   
    public void loadAlarms(){
    	Cursor _a = db.getAlarms();
        DisplayAlarm(_a);
        //db.close();
        }
    
    public void addalarm(View view) {
    	Bundle aBundle = new Bundle();
    	aBundle.putLong("Alarm",0);
		Intent addIntent = new Intent(this,AddAlarm.class); 
		addIntent.putExtras(aBundle);       
	    startActivityForResult(addIntent,0);		
    }

	// Display our own custom menu when menu is selected.
	 @Override
	
	public boolean onCreateOptionsMenu(Menu mymenu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alarm_menu, mymenu);
		return true;
		}
	 
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		    case R.id.preferences:
		      	Intent prefIntent = new Intent(this,AlarmPref.class); 
				startActivity(prefIntent);
		      	return true;
		    case R.id.clear_all:
		    	Toast.makeText(ViewAlarm.this, getString(R.string.delete_all), Toast.LENGTH_SHORT).show();
		    	/* TODO Ensure we cancel all enabled alarms when doing this */
		    	boolean _del = db.deleteAllAlarms();
		    	dataSource.getCursor().requery();
		    	return (_del);
		    	
			 default:
		    	Toast.makeText(ViewAlarm.this,getString(R.string.err_greeting) + item.getItemId(), Toast.LENGTH_SHORT).show();
		    	return true;
		     }
		}
	   
	   public void DisplayAlarm(Cursor _data){ 
		   dataSource = new CustomSqlCursorAdapter(this, 
	    			 R.layout.alarm_list, _data, 
                   fields, new int[] {R.id.alarmTime,R.id.firstLine, R.id.secondLine},db);
	    	 setListAdapter(dataSource);
	   
		   }

	   
}
