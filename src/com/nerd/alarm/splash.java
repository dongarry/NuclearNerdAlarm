package com.nerd.alarm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

// See Splash tutorial ; http://www.droidnova.com/how-to-create-a-splash-screen,561.html

public class splash extends Activity { 
protected boolean mActive = true;
protected int mSplashTime = 5000; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    
/*
        ImageView jpgView = (ImageView)findViewById(R.id.splash_img);
        
        String myJpgPath = findViewById(R.id.NNA); //UPDATE WITH YOUR OWN JPG FILE
        
        
        BitmapFactory.Options options = new BitmapFactory.Options();
	  options.inSampleSize = 2;
	  Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
	  jpgView.setImageBitmap(bm); 
	*/        
        
     // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(mActive && (waited < mSplashTime)) {
                        sleep(100);
                        if(mActive) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    startActivity(new Intent("com.nerd.alarm"));
                    stop();
                }
            }
        };
        splashTread.start();
        
	}
	
	// On Touch event, let's move on..
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        mActive = false;
	    }
	    return true;
	}
}
