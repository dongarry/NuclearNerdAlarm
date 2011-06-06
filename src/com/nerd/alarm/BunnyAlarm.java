package com.nerd.alarm;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;

public class BunnyAlarm extends Alarm {
	
	public BunnyAlarm(long ID, Context alarmContext) {
		super(ID, alarmContext);
	}
	
	
	public void soundAlarm () {
		
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		if (this.getAlarmCounter()==0){
		        	this.setAlarmVolume(maxVolume-5);    	   	
		        	this.setCustomSound(R.raw.b_b_birds);
		        	this.setAlarmTestMe(0);
		        	//this.setInterval(AlarmManager.INTERVAL_FIFTEEN_MINUTES);
		        	this.setTalk(false);	
		        }
		
		else if (this.getAlarmCounter()==1){
					this.setAlarmVolume(maxVolume-4);    	   	
		        	this.setCustomSound(R.raw.b_b_birds);
		        	this.setAlarmTestMe(0);
		        	//this.setInterval(AlarmManager.INTERVAL_FIFTEEN_MINUTES);
		        	this.setTalk(false);
		       		}
		else if (this.getAlarmCounter()==2){
					this.setAlarmVolume(maxVolume-3);    	   	
					this.setCustomSound(0);
					this.setInterval(1);
		        	this.setTalk(true);
		       		}
		else if (this.getAlarmCounter()==3){
					this.setAlarmVolume(maxVolume-2);    	   	
				    }
		else if (this.getAlarmCounter()>4){
					this.setAlarmVolume(maxVolume-1);    	   	
		    		}
		
		try { this.mediaPlay();} 
		catch (IllegalStateException e) {
			e.printStackTrace();
			} 
		catch (IOException e) {
			e.printStackTrace();
			}
	   }
	
}