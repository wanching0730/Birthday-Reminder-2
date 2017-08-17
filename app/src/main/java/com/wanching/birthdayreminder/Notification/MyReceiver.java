package com.wanching.birthdayreminder.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by WanChing on 17/8/2017.
 */

public class MyReceiver extends BroadcastReceiver {

    public MyReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, NotifyIntentService.class);
        context.startService(intent1);
    }
}
