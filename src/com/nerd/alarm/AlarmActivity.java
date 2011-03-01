package com.nerd.alarm;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;


public class AlarmActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;

    public void onCreate(Bundle savedInstanceState) {
//        Debug.waitForDebugger();
    	Toast.makeText(AlarmActivity.this, "Created Activity", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_wake);
        textToSpeech = new TextToSpeech(this, this);
    }

    public void handleSpeak(View view) {
        saySomething();
    }

    private void saySomething() {
        String myText1 = "This is an alarm";
        String myText2 = "This is an alarm which should wake you up, it's 3 degrees outside and you have appointments.... Yeah.";
        textToSpeech.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak(myText2, TextToSpeech.QUEUE_ADD, null);
    }

    public void onInit(int i) {
        saySomething();
    }
}
