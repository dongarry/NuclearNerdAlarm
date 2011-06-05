package com.nerd.alarm;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class HappyAlarm extends Alarm{

private Context context;

	public HappyAlarm(long ID, Context alarmContext) {
		super(ID, alarmContext);
		context=alarmContext;
	}

	public void soundAlarm () {
				   
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		Log.i("NerdAlarm","Happy MediaPlayer -" + this.getAlarmCounter());
		
		this.setAlarmVolume(maxVolume-2);    	   	
    	
		int happyStuff = (int)(Math.random() * (9 - 0));	
		String[] happy=context.getResources().getStringArray(R.array.helen_array); 
		
    	this.setInterval(1);        	
	    	   
	    this.setSpeakNextLine(happy[happyStuff]);
	        	
	    if (this.getAlarmCounter()==1){this.setSpeakLine(context.getResources().getString(R.string.happy_cough)+ "...");}
	    if (this.getAlarmCounter()>6){this.setInterval(0);} 
		
		try { this.mediaPlay();} 
		catch (IllegalStateException e) {
			Log.e("NerdAlarm","Happy MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
			e.printStackTrace();
			} 
		catch (IOException e) {
			Log.e("NerdAlarm","Happy MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
			e.printStackTrace();
			}
	   }

}
