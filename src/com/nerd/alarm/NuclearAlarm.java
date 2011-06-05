package com.nerd.alarm;

import java.io.IOException;

import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class NuclearAlarm extends Alarm {

	
	public NuclearAlarm(long ID, Context alarmContext) {
		super(ID, alarmContext);
	}

	
	public void soundAlarm () {
		Log.i("NerdAlarm","Nuclear MediaPlayer -" + this.getAlarmCounter());
		
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		this.setAlarmVolume(maxVolume);    	 
		this.setInterval(AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    	
		if (this.getAlarmCounter()>0){
						Log.i("NerdAlarm","Nuclear MediaPlayer Set custom-");
						this.setCustomSound(R.raw.nuc); 			        	
		        }
		
		try { 
			this.mediaPlay();
			} 
		catch (IllegalStateException e) {
			Log.e("NerdAlarm","Nuclear MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
			e.printStackTrace();
			} 
		catch (IOException e) {
			Log.e("NerdAlarm","Nuclear MediaPlayer -" + this.getAlarmCounter() + " - " + e.getMessage());
			e.printStackTrace();
			}
	   }

}
