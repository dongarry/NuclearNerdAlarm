package com.nerd.alarm;

import java.io.IOException;

import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;

public class NuclearAlarm extends Alarm {

	
	public NuclearAlarm(long ID, Context alarmContext) {
		super(ID, alarmContext);
	}

	
	public void soundAlarm () {
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		this.setAlarmVolume(maxVolume);    	 
		this.setInterval(AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    	
		if (this.getAlarmCounter()>0){
						this.setCustomSound(R.raw.nuc); 			        	
		        }
		
		try { 
			this.mediaPlay();
			} 
		catch (IllegalStateException e) {
											e.printStackTrace();
										} 
		catch (IOException e) 			{
											e.printStackTrace();
										}
	   }

}
