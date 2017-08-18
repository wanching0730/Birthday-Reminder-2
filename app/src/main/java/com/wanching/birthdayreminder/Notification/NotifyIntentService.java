package com.wanching.birthdayreminder.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.wanching.birthdayreminder.Activities.TodayBirthdayActivity;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

import java.util.Calendar;

/**
 * Created by WanChing on 17/8/2017.
 */

public class NotifyIntentService extends IntentService {

    private static final int NOTIFICTION_ID = 1;
    private Cursor cursor;

    public NotifyIntentService(){
        super("NotifyIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(this));
        Calendar cal = Calendar.getInstance();
        String selection = "strftime('%m-%d'," + BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE + "/1000, 'unixepoch')" + " BETWEEN strftime('%m-%d',?/1000, 'unixepoch') AND strftime('%m-%d',?/1000, 'unixepoch')";
        String[] selectionArgs = {Long.toString(cal.getTimeInMillis()-86400000), Long.toString(cal.getTimeInMillis()-86400000) };
        Cursor cursor = dbq.read(DbColumns.columns, selection, selectionArgs, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");

        Log.v("cursor_count", cursor.getCount() + "");
        Log.v("now", cal.getTimeInMillis() + "");

        if(cursor.getCount() > 0) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle(getResources().getString(R.string.notification_content_title));
            builder.setContentText(getResources().getString(R.string.notification_content_text));
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.cake);

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
}
