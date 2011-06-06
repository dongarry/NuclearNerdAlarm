package com.nerd.alarm;


import android.content.Context;
import android.os.Vibrator;

public class NinjaAlarm extends Alarm{

private Context context;

	public NinjaAlarm (long ID, Context alarmContext) {
			super(ID, alarmContext);
			context=alarmContext;
	}

		
		public void soundAlarm () {
			Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			this.setAlarmTestMe(0);
			v.vibrate(1000);
			this.setTalk(false);
			this.setVibrate(0);
	   	   	this.setInterval(0); 
		   }

}
