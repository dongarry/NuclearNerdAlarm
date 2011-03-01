package com.nerd.alarm;

import android.app.IntentService;
import android.content.Intent;
import com.nerd.alarm.ThreadUtils;

public class AlarmService extends IntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        for (int i = 0; i < 20; i++) {
            System.out.println("counter = " + i);
            ThreadUtils.sleep(1000);
        }

    }
}