package com.nerd.alarm;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;


public class AngryAlarm extends Alarm {

private Context context;

		public AngryAlarm(long ID, Context alarmContext) {
			super(ID, alarmContext);
			context=alarmContext;
		}

		
		public void soundAlarm () {
			
			int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			
			this.setAlarmVolume(maxVolume);    	   	
	    	
			int angryStuff = (int)(Math.random() * (9 - 0));	
	    	String[] angry=context.getResources().getStringArray(R.array.andy_array); 
	    	
	    	this.setInterval(1);        	
		    	   
		    this.setSpeakNextLine(angry[angryStuff]);
		        	
		    if (this.getAlarmCounter()==1){this.setSpeakNextLine(context.getResources().getString(R.string.angry_cough)+ "...");}
		    if (this.getAlarmCounter()>6){this.setInterval(0);} 
			
			try { this.mediaPlay();} 
			catch (IllegalStateException e) {
				Log.e("NerdAlarm","Angry MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
				e.printStackTrace();
				} 
			catch (IOException e) {
				Log.e("NerdAlarm","Angry MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
				e.printStackTrace();
				}
		   }

}
