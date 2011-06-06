package com.nerd.alarm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Some Credits:
 *  http://www.anddev.org/tutorial_modal_dialogs-t4325.html
 *  http://stackoverflow.com/questions/6142308/android-dialog-keep-dialog-open-when-button-is-pressed
 *  
 * Used for test me custom dialog box functionality on the alarm
 */

class myCustomDialog extends Dialog {
	
	
	private Button okButton;
	private EditText answerEditText;
	TextView mTestMeQ = null;
	TextView mTitle = null;
	private Context myContext;
	private String answer;
	private int count=0;
	
	public myCustomDialog(Context context) {
		super(context);
		myContext=context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		setContentView(R.layout.test);
		super.onCreate(savedInstanceState);	
		
		okButton = (Button) findViewById(R.id.ok_button);
		answerEditText = (EditText) findViewById(R.id.title_edittext);
		
		int qNo = (int)(Math.random() * (6 - 0));	
    	String[] questions=myContext.getResources().getStringArray(R.array.testme_array); 
    	String[] answers=myContext.getResources().getStringArray(R.array.testme_answers); 
    	answer=answers[qNo];
    	
    	mTitle = (TextView) findViewById(R.id.window_title);
    	mTestMeQ = (TextView) findViewById(R.id.testme_question);
    	mTestMeQ.setText(questions[qNo]);
        	
		okButton.setOnClickListener(new OKListener());
	}
	
	 private class OKListener implements android.view.View.OnClickListener {
		 
         @Override
         public void onClick(View v) {
        	 count+=1;
        	if(answer.equalsIgnoreCase("ok") || count>2)	{
         		Toast.makeText(myContext, myContext.getString(R.string.testme_move_on), Toast.LENGTH_SHORT).show();
         		dismiss();
         	}
         	else if(answer.equalsIgnoreCase(answerEditText.getText().toString().trim()))	{
			         			Toast.makeText(myContext, myContext.getString(R.string.testme_ok), Toast.LENGTH_SHORT).show();
			         			dismiss();
			        }
			else	{
			         			Toast.makeText(myContext, myContext.getString(R.string.testme_incorrect), Toast.LENGTH_SHORT).show();
			         			mTitle.setText(myContext.getString(R.string.testme_incorrect));
			     				answerEditText.setText("");
         			}
         	}
	 }
}
