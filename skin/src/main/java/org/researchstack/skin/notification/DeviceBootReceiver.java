package org.researchstack.skin.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DeviceBootReceiver", "onReceive()");
        context.sendBroadcast(new Intent(TaskAlertReceiver.ALERT_CREATE_ALL));
    }
}
