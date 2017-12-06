package com.arturagapov.easymathforgirls;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Artur Agapov on 09.11.2016.
 */
public class NotificationPollReceiver extends BroadcastReceiver {
    //private static final int PERIOD=  1000*60*10;

    @Override
    public void onReceive(Context ctxt, Intent i) {
        scheduleAlarms(ctxt);
    }

    static void scheduleAlarms(Context ctxt) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 8);

        AlarmManager mgr= (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(ctxt, NotificationScheduledService.class);
        PendingIntent pi=PendingIntent.getService(ctxt, 0, i, 0);

        //mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + PERIOD, PERIOD, pi);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pi);
    }
}
