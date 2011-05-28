package com.nerd.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

/* 
 * Nerd Alarm - A class to hold re-used functions
 * TODO Move setting of the alarm from add_alarm to display_record - currently this is the same code duplicated!.
 * 
 */
 
public class DisplayRecords extends Activity{
	private Context context;
	private int mCurrMin, mCurrHour,mDay;
	
	
	public void SetMe(Context context) {
	    this.context = context;
	  }
	
	public String setDays(int repeatSelect){
	String days = context.getString(R.string.enabled);
	
   	 if (repeatSelect==0) {days = context.getString(R.string.norepeatset);}
	 if ((repeatSelect & 4) == 4) {days += " " + context.getString(R.string.monday);}
   	 if ((repeatSelect & 8) == 8) {days += " " + context.getString(R.string.tuesday);}
   	 if ((repeatSelect & 16) == 16) {days += " " + context.getString(R.string.wednesday);}
   	 if ((repeatSelect & 32) == 32) {days += " " + context.getString(R.string.thursday);}
   	 if ((repeatSelect & 64) == 64) {days += " " + context.getString(R.string.friday);}
   	 if ((repeatSelect & 128) == 128) {days += " " + context.getString(R.string.saturday);}
   	 if ((repeatSelect & 2) == 2) {days += " " + context.getString(R.string.sunday);}
   	 if (repeatSelect ==254){days = context.getString(R.string.enabled) + " " + context.getString(R.string.everyday);}
     if (repeatSelect ==124){days = context.getString(R.string.enabled) + " " + context.getString(R.string.weekdays);}
   	
   	return days;
   }
	
	public boolean testMe(){
		return true;
	}
	
	public String setTime(int alarmID, int mHour, int mMinute,int repeat, int mode) {
	      Intent alarmIntent = new Intent(context, AlarmActivity.class);
	            
	      Bundle params = new Bundle();
	      params.putLong("AlarmID",alarmID);   
	      alarmIntent.putExtras(params); //Pass the Alarm ID
	      
	      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	      PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	      int _addHours=0;
	      
	      long timeDiff =0;
	       
	     Calendar alarmTime = Calendar.getInstance();
	     mCurrHour = alarmTime.get(Calendar.HOUR_OF_DAY);
	     mCurrMin = alarmTime.get(Calendar.MINUTE);
	     
		      if (repeat>0){
		    	 
		    	  mDay=alarmTime.get(Calendar.DAY_OF_WEEK); 
	              int day_power=1;
	              
	              
	              /* See above, all our days have a power associated with them
	               * Here we locate which power is associated with today
	               */
	              while(mDay>0){ 
	    			  day_power=day_power*2;
	    			  mDay-=1;}
	    		  	      
	              /* We now go through each day to see if our
	               * find when is our next alarm day
	               */
		    	  while (day_power>0){
		    		  if (day_power==128){day_power=1;} // End of week
		    		  day_power=day_power*2;
		     	      
		    		  if ((repeat & day_power)== day_power){day_power=0;}
		    		  else{_addHours+=24;}
		    		  
		    	  	}
		      	}
		       	  
	      if (mCurrHour>mHour) {mHour+=24;}
	      
	      if ((mCurrMin>mMinute) && (mCurrHour==mHour)) {mHour+=24;}
	      else if ((mCurrMin==mMinute) && (mCurrHour==mHour)) {mHour+=24;}
	      
	      if (mCurrHour<mHour) {timeDiff+=((mHour-mCurrHour)*3600000);}
	      
	      if (mCurrMin>mMinute) {mMinute+=60;}
	    	  
	      if (mCurrMin<mMinute) {
	    	  if (mMinute>60) {timeDiff-=3600000;} 		// reduce an hour
	    	  timeDiff+=(((mMinute-mCurrMin)*60000));} // Add minutes
	      
	      // For Bunny Mode, we wake 30 mins early, we will only do this for alarms > 5 hours away
	      if((mode<=0) && (timeDiff>18000000)){timeDiff-=1800000;} 
		  else{timeDiff-=10000;} 
		  
	      // Add in our next repeat day
	      if(_addHours>0){timeDiff+=((_addHours)*3600000);}
	      
	      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDiff, pendingIntent);
	      
	      
	      // Update the user 
	      
	      String alarmDetails =  context.getString(R.string.alarm_enabled) + " ";
	      timeDiff=timeDiff/1000;
	      
	      if (timeDiff>(24 * 3600)){
		    	  alarmDetails =  alarmDetails + (timeDiff / (24 * 3600)) + " " + context.getString(R.string.alarm_days) + " " + (timeDiff%(24 * 3600)/(3600)) + " " + context.getString(R.string.alarm_hours) + " " + (timeDiff%(3600)/60) + " " + context.getString(R.string.alarm_minutes) + ".";}
		  
	      else if (timeDiff>(3600)){
		    	  alarmDetails =  alarmDetails + (timeDiff/(3600)) + " " + context.getString(R.string.alarm_hours) + " " + (timeDiff%(3600)/60) + " " + context.getString(R.string.alarm_minutes) + ".";}	    	  
		  
		  else alarmDetails =  alarmDetails + (timeDiff%(3600)/60) + " " + context.getString(R.string.alarm_minutes) + ".";
	      			
	      return alarmDetails;
  }


}
