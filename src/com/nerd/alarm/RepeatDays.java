package com.nerd.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/* Credits
 * http://www.androidpeople.com/android-listview-multiple-choice-example	
 * NerdAlarm - Allow users set days for repeat
 * TODO Repeat Days screen - remove the need to repeat checkbox
 * TODO Repeat Days screen - load selected details when user goes back in
 */

public class RepeatDays extends Activity {
	private ListView lView;
	private int selectRepeat;
	private long[] selectedRepeat;
	private Long mAlarmID;
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.days);
		String[] days = getResources().getStringArray(R.array.days_array);
		
		Bundle aBundle = this.getIntent().getExtras();
        mAlarmID = aBundle.getLong("Alarm");
		
		lView = (ListView) findViewById(R.id.daylist);
		//Set option as Multiple Choice. So that user can able to select more the one option from list
		lView.setAdapter(new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_multiple_choice, days));
		lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	// Lets forget about using a button, let user select and go back
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	int selected = getSelected();
			Intent returnIntent = new Intent();
			returnIntent.putExtra("SelectedDay",selected);
			returnIntent.putExtra("Alarm",mAlarmID);
	 	    
	        setResult(RESULT_OK,returnIntent);        
		    finish();
	        //return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public int getSelected(){
		lView = (ListView) findViewById(R.id.daylist);
		selectedRepeat = lView.getCheckItemIds();
		selectRepeat=0;
		
		int size = selectedRepeat.length;
	    for (int i=0; i<size; i++)
	    {
	  	  if (selectedRepeat[0]!=0) {break;}
	    	
	    	switch ((int)(selectedRepeat[i])){
	    	case 1: selectRepeat+=4;break;		    
	    	case 2: selectRepeat+=8;break; 
	    	case 3: selectRepeat+=16;break; 
	    	case 4: selectRepeat+=32;break; 
	    	case 5: selectRepeat+=64;break; 
	    	case 6: selectRepeat+=128;break; 
	    	case 7: selectRepeat+=2;break; 
	    		}  	
	    	
	    }
	    
		return selectRepeat;
	}
}	
