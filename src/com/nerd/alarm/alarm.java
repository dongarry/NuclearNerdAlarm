package com.nerd.alarm;

/* Alarm Class - Base class for all alarms */

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;

public class Alarm {
	
	private int mode = Activity.MODE_PRIVATE;
	private static final int nextAlarmTime = 0;
	private static final int nextSnooze = 1;
	
	private int volume;
	private String[] modes;
	private String status=" ";
	private boolean bolReturn;
	private boolean bolScheduled;
	private long interval=0;
	private boolean talk;
	private String nerdSummary;
	private String nerdDetails;
	
	private Context context;
	public AudioManager mAudioManager = null;
	private DatabaseAdapter db = null;
	MediaPlayer mp = null;	
	SharedPreferences mySharedPreferences; 	
	
	// Alarm
	private long alarmID; 			//	row_id
	private int alarmMode; 
	private String alarmTitle;
	private int repeat;
	private int counter;
	private int enabled; 
	private int testme;
	private int customSound;
	private String time;
	
	// Preferences
	private int vibrate;
	private int snooze;
	private int nerd;
	private String gap = "   ";
	private String greeting="..";
	private String talk1="..";
	private String talk2="..";
	private String sound;
	
	public Alarm(long ID, Context alarmContext) {
		
		alarmID=ID; 
		context=alarmContext;
		talk=true;
		bolScheduled=false;
		nerdSummary="";
		
		mAudioManager = (AudioManager) alarmContext.getSystemService(Context.AUDIO_SERVICE);
		db = new DatabaseAdapter(alarmContext);
		talk2 = alarmContext.getString(R.string.alarmtext2);
		
		if(alarmID!=0)	{
							if(!loadAlarm() || !loadPrefs() )	{
								status="Failed loading Alarm settings";
							}
		}
		else bolReturn=false;
	}
	
	public long getAlarmID() {return alarmID;}
	public void setAlarmID(long newValue) {alarmID = newValue;}

	public int getAlarmMode() {return alarmMode;}
	public void setAlarmMode(int newValue) {alarmMode = newValue;}

	public int getAlarmCounter() {return counter;}
	public void setAlarmCounter(int newValue) {counter = newValue;}

	public int getAlarmRepeat() {return repeat;}
	public void setAlarmRepeat(int newValue) {repeat = newValue;}

	public String getAlarmTime() {return time;}
	public void setAlarmTime(String newValue) {time = newValue;}

	public String getAlarmTitle() {return alarmTitle;}
	public void setAlarmTitle(String newValue) {alarmTitle = newValue;}
	
	public Boolean getAlarmTestMe() {return testme==1;}
	public void setAlarmTestMe(int newValue) {testme = newValue;}
	
	public boolean getAlarmEnabled() {return enabled==1;}
	public void setAlarmEnabled(int newValue) {enabled = newValue;}
	
	public int getAlarmVolume() {return volume;}
	public void setAlarmVolume(int newValue) {volume = newValue;}

	public int getCustomSound() {return customSound;}
	public void setCustomSound(int newValue) {customSound = newValue;}
	
	public boolean doTalk() {return talk;}
	public void setTalk(boolean newValue) {talk = newValue;}

	public String getAlarmStatus() {return status;}
	
	public String getSpeakLine() {return talk1;}
	public void setSpeakLine(String newValue) {
		if (newValue!=null){talk1 = newValue;}
	}
	
	public String getSpeakNextLine() {return talk2;}
	public void setSpeakNextLine(String newValue) {
		if (newValue!=null){talk2 = newValue;}
		}

	public void setInterval(long newValue) {interval = newValue;}
	
	public String getNerdDetails() {return nerdDetails;}
	
	public boolean doVibrate() {return vibrate==1;}
	public void setVibrate(int newValue) {vibrate = newValue;}
	
	public boolean doNerd() {return nerd==1;}
	public boolean isValid() {return bolReturn;}
	public Boolean isScheduled() {return bolScheduled;}
	
	public String getAlarmGreeting() {
		String returnString=gap;
		if (greeting!=null) {returnString=greeting + gap;}
		if (nerdSummary!=null) {returnString=greeting + gap + nerdSummary;}
		return returnString; 
	}
	
	public int getAlarmSnooze() {return snooze;}
	
	public void updateAlarm()	{
		try {
			db.open();
			if (alarmID!=0)
				{
				bolReturn = db.updateAlarm(alarmID,
				        		time,
				        		alarmTitle,
				        		repeat,
				        		enabled,
				        		counter,
				        		alarmMode,
				        		testme);
				
				}
			else
				{
				alarmID = db.insertAlarm(
                			time,
                			alarmTitle,
                			repeat,
                			enabled,
                			counter,
                			alarmMode,
                			testme);
				
				bolReturn = true;

				}
				db.close();
				
			rescheduleAlarm(nextAlarmTime);
			}
		
		catch(Exception e)	{
			bolReturn=false;
			}
	}
	
	public boolean snoozeAlarm() {
		try {
				rescheduleAlarm(nextSnooze);
			}
		catch(Exception e) {
			//status="Error setting alarm snooze"
			bolReturn=false;
			return bolReturn;
		}
		bolReturn=true;
		return bolReturn;
	}

	public boolean resetAlarm() {
		try {
				rescheduleAlarm(nextAlarmTime);
			}
		catch(Exception e) {
			//status="Error re-setting alarm"
			bolReturn=false;
			return bolReturn;
		}
		bolReturn=true;
		return bolReturn;
	}
	
	public void cancelAlarm(){
		Bundle params = new Bundle();
		params.putLong("AlarmID",alarmID);   
		params.putInt("AlarmMode",alarmMode);
		      
		Intent alarmIntent = new Intent(context, AlarmActivity.class);
		alarmIntent.putExtras(params); //Pass the Alarm ID and Mode
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)(alarmID), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    alarmManager.cancel(pendingIntent);
	    status = context.getString(R.string.alarm_disabled);
	}
	
	
	public void rescheduleAlarm(long onDo){	   
		if (enabled == 0) {
			status=context.getString(R.string.alarm_disabled);
			counter=0;
		}
		else
		{
		    long interval = 0;
	       
		   Bundle params = new Bundle();
		   params.putLong("AlarmID",alarmID);   
		   params.putInt("AlarmMode",alarmMode);
		   
	       Intent alarmIntent = new Intent(context, AlarmActivity.class);
		   alarmIntent.putExtras(params); //Pass the Alarm ID and Mode
	       
		   AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	       PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)(alarmID), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	       
	       if (onDo==nextSnooze)	{				// Snooze  	    	   
	 	       counter+=1;
	    	   
			   interval=(snooze * 60000);
			   alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pendingIntent);
		   	}
	       
	       else if (onDo==nextAlarmTime)	{	
	    	   						// Reschedule for alarm time (will include repeats also)
	    	   					   interval=getNextInterval();			   
								   alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pendingIntent);  
	    	 }
	       
	       else	{						// OnDo is the next Interval
	       				
	 	       				   counter+=1;
	    	   				   interval = onDo;			   
							   alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pendingIntent);
	       	}
		        
	       setStatus(interval);
	    }   
	    
		bolScheduled=true;   
		
		db.open();
		db.updateStatistic(alarmID,counter,enabled,time + ":" + alarmMode);
		db.close();  
	}
	
	
    public void mediaPlay() throws IllegalStateException, IOException
    {	
    	mp = new MediaPlayer();
    	mp.setOnErrorListener(
				new MediaPlayer.OnErrorListener() {
			        public boolean onError(MediaPlayer mp, int arg, int argx) {
			        	return false;}
			    });
    
    	if(customSound>0) {
    					mp = MediaPlayer.create(context,customSound);
    					mp.setLooping(alarmMode==2); //Only loop on Nuclear for custom sounds.
    					//_mp.prepare(); Tut tut!
    	}
    	
    	else 		{
    					mp.setDataSource(context,Uri.parse(sound)); 		    	 		    	 	
			    	 	mp.setAudioStreamType(AudioManager.STREAM_ALARM);
			    	 	mp.setLooping(true);
			    	 	mp.prepare();    					 	 
    	}
    	
    	mp.start();
    	   	
    	mp.setOnCompletionListener(new OnCompletionListener(){
         // @Override
	         public void onCompletion(MediaPlayer arg0) {
	        	 rescheduleAlarm(AlarmManager.INTERVAL_FIFTEEN_MINUTES);
	        	 ((Activity) context).finish();
	         }
    	});
    	 	
    }
    
    public void ResetMedia(){
   	 // DEALLOCATE MEMORY
   	    if (mp != null) {
   	        if (mp.isPlaying()) {
   	            mp.stop();
   	        }
   	        
   	        mp.release();
   	        mp= null;
   	    }   	    
   	}
	
	private final boolean loadAlarm()	{
		try {
			
				db.open();
	        	Cursor data = db.getAlarm(alarmID);
	        	
	        	if (data!=null){
	        		bolReturn = true;
	        		time = data.getString(data.getColumnIndex("time"));	         	   
	     	        alarmTitle = data.getString(data.getColumnIndex("title"));
	     	        alarmMode = data.getInt(data.getColumnIndex("mode"));	     	       
	     	        repeat = data.getInt(data.getColumnIndex("repeat"));
	     	        enabled = data.getInt(data.getColumnIndex("enabled"));
	     	        testme = data.getInt(data.getColumnIndex("test"));
	     	        counter=data.getInt(data.getColumnIndex("counter"));
	        	}
	        	else {
	        		bolReturn = false;
	        	}
	        	
	        	db.close();
	        	return bolReturn;
		
			}
		
		catch(Exception e)	{
			return false;
			}
	}

	private final Boolean loadPrefs() {
		try {
	        
			
			modes= context.getResources().getStringArray(R.array.modes_array); 
	        mySharedPreferences = context.getSharedPreferences(modes[alarmMode],mode);
	    	
	        greeting=mySharedPreferences.getString("greetingPref","");
	    	vibrate=mySharedPreferences.getInt("vibratePref",0);
	    	nerd=mySharedPreferences.getInt("nerdPref",0);
	    	snooze=mySharedPreferences.getInt("snoozePref",5);
	    	sound=mySharedPreferences.getString("soundPref", "alarm_alert");
	    	
	    	if(nerd==1)	{
	    					getWeatherDetails();
	    				}
	    	
	    	// Overwrite greeting with Title after initial run
 	        if(counter==0){greeting=greeting + gap + alarmTitle;}
 	        else greeting=alarmTitle;
 	        
	    	bolReturn=true;
	    	
			return bolReturn;
			}
		
		catch (Exception e)	{
			bolReturn=false;
			return bolReturn;
			}
	}
	
	   
	private final long getNextInterval() {
	            
	     int addHours=0;	      
	     long timeDiff =0;
	       
	     Calendar alarmTime = Calendar.getInstance();
	     int currHour = alarmTime.get(Calendar.HOUR_OF_DAY);
	     int currMin = alarmTime.get(Calendar.MINUTE);
		 int day=alarmTime.get(Calendar.DAY_OF_WEEK); 
	     int day_power=1;
	                       
	     if (repeat>0)  {
		    	 
	    	 	 /* Handle our next repeat day 
			      * Here we locate which power is associated with today
			      */
			     
			     while(day>0)	{ 
			    			  	day_power=day_power*2;
			    			  	day-=1;
			    			  	}
			    		  	      
			     /* We now go through each day to see if our
			      * find when is our next alarm day
			      */
			     
				 while (day_power>0)	{
				    		  				if (day_power==128)	{
				    		  										day_power=1; // End of week
				    		  									} 
				    		  				day_power=day_power*2;
				     	      
				    		  				if ((repeat & day_power)== day_power)	{
				    		  															day_power=0;
				    		  														}
				    		  				else	{
				    		  							addHours+=24;
				    		  						}
				    	  				}
	     				}
	     
		 String[] parts = time.split(":",2);
     	 int alarmHour = Integer.valueOf(parts[0]);
     	 int alarmMinute = Integer.valueOf(parts[1]);     	    
     	 
	      if (currHour>alarmHour) 									{
	    	  															alarmHour+=24;
	    	  														}
	      
	      if ((currMin>alarmMinute) && (currHour==alarmHour)) 		{
	    	  															alarmHour+=24;
	    	  														}
	      
	      else if ((currMin==alarmMinute) && (currHour==alarmHour))	{
	    	  															alarmHour+=24;
	    	  														}
	      
	      if (currHour<alarmHour) 	{ // Add hours
	    	  								timeDiff+=((alarmHour-currHour)*3600000);
	    	  						}
	      
	      if (currMin>alarmMinute) 	{
	    	  							alarmMinute+=60;
	    	  						}
	    	  
	      if (currMin<alarmMinute) 	{   // Add minutes
	    	  							if (alarmMinute>59) 	{
	    	  														timeDiff-=3600000; // reduce an hour
	    	  													} 		
	    	  							timeDiff+=(((alarmMinute - currMin)*60000));
	    	  						} 
	      
	      timeDiff+=((addHours)*3600000); // Add in the next repeat day if exists
	      
	      if (alarmMode==0 && timeDiff > 18000000) {
	    	  timeDiff-=1800000; // Half an hour early
	      }
	      
	      return timeDiff;
	}   
	
	private final void setStatus(long interval) {
		  
	      status =  context.getString(R.string.alarm_enabled) + " ";
	      interval=interval/1000;
	      
	      if (interval>(24 * 3600))		{
	    	  	status =  status + (interval / (24 * 3600)) + " " + context.getString(R.string.alarm_days) + " " + (interval%(24 * 3600)/(3600)) + " " + context.getString(R.string.alarm_hours) + " " + (interval%(3600)/60) + " " + context.getString(R.string.alarm_minutes) + ".";
	    	  							}
		  
	      else if (interval>(3600))		{
		    	status =  status + (interval/(3600)) + " " + context.getString(R.string.alarm_hours) + " " + (interval%(3600)/60) + " " + context.getString(R.string.alarm_minutes) + ".";
		    	  						}	    	  
		  
		  else 
			  	status =  status + (interval%(3600)/60) + " " + context.getString(R.string.alarm_minutes) + ".";
	      
	}
	
	private void getWeatherDetails()
	{ 
		  	
		try {   
				LocationManager locationManager;
			  	String locService = Context.LOCATION_SERVICE;
			 	locationManager = (LocationManager)context.getSystemService(locService);
			 	String provider = LocationManager.NETWORK_PROVIDER;
			 	Location location = locationManager.getLastKnownLocation(provider);
			 	
			 	//We now have coordinates - let's get the weather
			 	//http://www.google.com/ig/api?weather=,,,4550000,-7358300
			 	//These coordinates need to be formatted
			 	String weatherString = "http://www.google.com/ig/api?weather=,,,";
			 	String lat= location.getLatitude() + ""; 
			 	String lon = "" + location.getLongitude();
			 	lat=f_Coordinates(lat) + "," + f_Coordinates(lon); 	
			 	weatherString = weatherString + lat;
			 	URL url;	 	
				url = new URL(weatherString); 
			 	
				//Return should be XML so deal with it:
		 		SAXParserFactory spf = SAXParserFactory.newInstance();
		 		SAXParser sp;
	        	sp = spf.newSAXParser(); 
	        	XmlHandler xmlHandler = new XmlHandler();
	        	XMLReader xr = null;
	        	xr = sp.getXMLReader();
	        	xr.setContentHandler(xmlHandler);
			
	        	// We cannot be waiting long for this..
	        	URLConnection conn = url.openConnection();
	        	conn.setConnectTimeout(3000);
	        	conn.setReadTimeout(2000);
	        	xr.parse(new InputSource(conn.getInputStream())); 
		
				// xmlHandler provides the parsed data
		        ParsedXmlSet parsedXmlSet = xmlHandler.getParsedData();
		        
		        if (parsedXmlSet.getWind().length()>4){
		        		nerdDetails = parsedXmlSet.toString();
		        		nerdSummary = parsedXmlSet.getCondition();
		        		}
		}

		catch (Exception e) {
				}
		  }
	   
	   private String f_Coordinates(String _c) 
	   {	// Ensure Coordinates are in the correct format..
		   _c=_c.replace(".", "");   
		   if(_c.length()>8){_c=_c.substring(0, 8);}
		   else if(_c.length()<8){_c=_c.format("%-" + 8 + "s", _c).replace(' ', '0');}
		   
		   return _c;
		    
	   }
	   
	   public void soundAlarm() {
		   //Implement in each sub class
	   }
	   
}

	




