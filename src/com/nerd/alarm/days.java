package com.nerd.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class days extends Activity {
	private ListView lView;
	private Intent resultIntent;

	//http://www.androidpeople.com/android-listview-multiple-choice-example
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.days);
		String[] days = getResources().getStringArray(R.array.days_array);
		
		lView = (ListView) findViewById(R.id.daylist);
		//Set option as Multiple Choice. So that user can able to select more the one option from list
		lView.setAdapter(new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_multiple_choice, days));
		lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	
	}
	
	@Override
	protected void onPause(){
        super.onPause();
       //setResult(22);
        //finish();
	}
	
	public void daySave(View view){
		String book = "Return data";
		Intent returnIntent = new Intent();
		returnIntent.putExtra("SelectedBook",book);
		setResult(RESULT_OK,returnIntent);        
	    finish(); 
		}
}	
