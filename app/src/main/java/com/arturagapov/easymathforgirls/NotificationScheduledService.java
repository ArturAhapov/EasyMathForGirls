package com.arturagapov.easymathforgirls;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Artur Agapov on 09.11.2016.
 */
public class NotificationScheduledService extends IntentService {
    // An ID used to post the notification.
    private static final int NOTIFICATION_ID = 200003;
    public NotificationScheduledService() {
        super("NotificationScheduledService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "I ran!");

        Calendar calendar = Calendar.getInstance();
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 20 || calendar.get(Calendar.HOUR_OF_DAY) == 8) && calendar.get(Calendar.MINUTE) >= 0 && calendar.get(Calendar.MINUTE) <= 2) {

            String message;
            message = getResources().getString(R.string.its_time);

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent activityIntent = new Intent(this, Activity_1st.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.startlogo))
                    .setContentIntent(pendingIntent)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.its_time))
                    .setContentText(getResources().getString(R.string.its_time))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);


            Notification notification = builder.build();
        /*
        if (Build.VERSION.SDK_INT >= 16) {
            notification = new Notification.BigTextStyle(builder).bigText(message).build();
        } else {
            notification = builder.setContentText(message).build();
        }
        */
            nm.notify(NOTIFICATION_ID, notification);
        }
    }
}
