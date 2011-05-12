package com.nerd.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
 
public class Display_Records extends Activity{
	private Context context;
	private int mCurrMin, mCurrHour;
	
	
	public void SetMe(Context context) {
	    this.context = context;
	  }
	
	public String setDays(int repeatSelect){
	String days = context.getString(R.string.enabled);
	
   	 if (repeatSelect==0) {days = context.getString(R.string.norepeatset);}
	 if ((repeatSelect & 2) == 2) {days += " " + getString(R.string.monday);}
   	 if ((repeatSelect & 4) == 4) {days += " " + getString(R.string.tuesday);}
   	 if ((repeatSelect & 8) == 8) {days += " " + getString(R.string.wednesday);}
   	 if ((repeatSelect & 16) == 16) {days += " " + getString(R.string.thursday);}
   	 if ((repeatSelect & 32) == 32) {days += " " + getString(R.string.friday);}
   	 if ((repeatSelect & 64) == 64) {days += " " + getString(R.string.saturday);}
   	 if ((repeatSelect & 128) == 128) {days += " " + getString(R.string.sunday);}
   	 if (repeatSelect ==254){days = getString(R.string.enabled) + " " + getString(R.string.everyday);}
    	if (repeatSelect ==62){days = getString(R.string.enabled) + " " + getString(R.string.weekdays);}
   	
   	return days;
   }
    
	public boolean testMe(){
		return true;
	}
	public String setTime(int alarmID, int mHour, int mMinute) {
	      Intent alarmIntent = new Intent(context, AlarmActivity.class);
	            
	      Bundle params = new Bundle();
	      params.putLong("AlarmID",alarmID);   
	      alarmIntent.putExtras(params); //Pass the Alarm ID
	      
	      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	      PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	      
	      long timeDiff =0;
	      
	     Calendar alarmTime = Calendar.getInstance();
	     mCurrHour = alarmTime.get(Calendar.HOUR_OF_DAY);
	     mCurrMin = alarmTime.get(Calendar.MINUTE);
	      
	      
	      if (mCurrHour>mHour) {mHour+=24;}
	      
	      if (mCurrHour<mHour) {timeDiff+=((mHour-mCurrHour)*3600000);}
	      
	      if (mCurrMin>mMinute) {mMinute+=60;}
	    	  
	      if (mCurrMin<mMinute) {
	    	  if (mMinute>60) {timeDiff-=3600000;} // take off an hour
	    	  timeDiff+=(((mMinute-mCurrMin)*60000));} // Add minutes
	      
	      timeDiff-=10000; //Let's start up 10 Seconds before we're due.
	      
	      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDiff, pendingIntent);
	      
	      //Update the user..
	      String alarmDetails =  context.getString(R.string.alarm_enabled) + " ";
	      timeDiff=timeDiff/1000;
	      if (timeDiff>(24 * 3600)){
	    	  alarmDetails =  alarmDetails + (timeDiff / (24 * 3600)) + " days and " + (timeDiff/(3600)) + " hours and " + (timeDiff%(3600)/60) + " minutes.";}
	      else if (timeDiff>(3600)){
	    	  alarmDetails =  alarmDetails + (timeDiff/(3600)) + " hours and " + (timeDiff%(3600)/60) + " minutes.";}	    	  
		else
			alarmDetails =  alarmDetails + (timeDiff%(3600)/60) + " minutes.";

	      return alarmDetails;
  }


}
