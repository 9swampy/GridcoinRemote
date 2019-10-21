package mcr.apps.gridcoinremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    private static final String TAG = AutoStart.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, String.format("onReceive"));
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Alarm.getInstance().setAlarm(context);
        }
    }
}