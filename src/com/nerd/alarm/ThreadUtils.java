package com.nerd.alarm;

public class ThreadUtils {
	  public static void sleep(long sleeptime) {
	        try {
	            Thread.sleep(sleeptime);
	        } catch (InterruptedException e) {
	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	        }

	    }
}
