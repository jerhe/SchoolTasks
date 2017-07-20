package com.edu.schooltask.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import server.api.SchoolTask;

public class PollService extends Service {
    PollBinder poolBinder = new PollBinder();
    public PollService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SchoolTask.poll();
            }
        }).run();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int delay = 5 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + delay;
        Intent i = new Intent(this, PollService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i ,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            }
            else{
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return poolBinder;
    }


}
