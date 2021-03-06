package com.wanching.birthdayreminder.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.wanching.birthdayreminder.Activities.TodayBirthdayActivity;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;

/**
 * Created by WanChing on 17/8/2017.
 */

/**
 * Class for handling notification service
 * which will notify user about the today birthday list
 */

public class NotifyIntentService extends IntentService {

    private static final int NOTIFICTION_ID = 1;

    private BirthdayDbQueries dbq;

    public NotifyIntentService(){
        super("NotifyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));

        // Notification is sent only when there is at least one birthday record
        if(dbq.retrieveTodayBirthday().getCount() > 0) {
            startNotificationService();
        }
    }

    /**
     * Start notification service
     */
    private void startNotificationService(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.notification_content_title))
        .setContentText(String.format(getString(R.string.notification_content_text), dbq.retrieveTodayBirthday().getCount()))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.launcher)
        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        Log.v("notification started", "notification");

        Intent listBirthdayIntent = new Intent(this, TodayBirthdayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, listBirthdayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICTION_ID, notification);

        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
    }
}
