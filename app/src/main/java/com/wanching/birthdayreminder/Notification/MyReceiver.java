package com.wanching.birthdayreminder.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by WanChing on 17/8/2017.
 */

/**
 * Class for handling BroadcastReceiver feature
 */

public class MyReceiver extends BroadcastReceiver {

    public MyReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, NotifyIntentService.class);
        intent1.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        context.startService(intent1);
    }
}
