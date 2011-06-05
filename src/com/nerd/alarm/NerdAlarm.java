package com.nerd.alarm;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class NerdAlarm extends Alarm{

private Context context;
	
	public NerdAlarm(long ID, Context alarmContext) {
		super(ID, alarmContext);
		context=alarmContext;
	}

	
	public void soundAlarm () {
		Log.i("NerdAlarm","Nerd MediaPlayer -" + this.getAlarmCounter());
		
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		this.setAlarmVolume(maxVolume);    	   	
    	
		if (this.getAlarmCounter()>0){
				this.setAlarmVolume(maxVolume-2);    	   	
	        	this.setInterval(1);        	
	      	   
	        	int nerdFact = (int)(Math.random() * (9 - 0));	
	        	String[] facts=context.getResources().getStringArray(R.array.nerd_array); 
	    	   
	        	this.setSpeakNextLine(facts[nerdFact]);
	        	
	        	if (this.getAlarmCounter()==1){this.setSpeakNextLine(context.getResources().getString(R.string.nerd_cough)+ "...");}
	    	    if (this.getAlarmCounter()>6){this.setInterval(0);} 
	 		 }
		
		try { this.mediaPlay();} 
		catch (IllegalStateException e) {
			Log.e("NerdAlarm","Nerd MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
			e.printStackTrace();
			} 
		catch (IOException e) {
			Log.e("NerdAlarm","Nerd MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
			e.printStackTrace();
			}
	   }

}
