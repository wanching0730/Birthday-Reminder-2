package com.wanching.birthdayreminder.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.wanching.birthdayreminder.Activities.TodayBirthdayActivity;
import com.wanching.birthdayreminder.R;

/**
 * Created by WanChing on 17/8/2017.
 */

public class NotifyIntentService extends IntentService {

    private static final int NOTIFICTION_ID = 1;

    public NotifyIntentService(){
        super("NotifyIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.notification_content_title));
        builder.setContentText(getResources().getString(R.string.notification_content_text));
        builder.setSmallIcon(R.drawable.cake);

        Intent listBirthdayIntent = new Intent(this, TodayBirthdayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, listBirthdayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICTION_ID, notification);
    }
}
