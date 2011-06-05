package com.nerd.alarm;


import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class NinjaAlarm extends Alarm{

private Context context;

	public NinjaAlarm (long ID, Context alarmContext) {
			super(ID, alarmContext);
			context=alarmContext;
	}

		
		public void soundAlarm () {
			Log.i("NerdAlarm","Ninja MediaPlayer -" + this.getAlarmCounter());
			
			Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			Log.i("NerdAlarm","Ninja Vibrate");
			v.vibrate(1000);
			Log.i("NerdAlarm","Ninja Vibrate2");
			this.setTalk(false);
			this.setVibrate(0);
	   	   	this.setInterval(0); 
		   }

}
