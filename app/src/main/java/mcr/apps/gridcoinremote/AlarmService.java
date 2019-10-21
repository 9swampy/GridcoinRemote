package mcr.apps.gridcoinremote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getName();

    public AlarmService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(TAG, String.format("AlarmService"));
    }

    public void onCreate() {
        Log.d(TAG, String.format("onCreate"));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, String.format("onStartCommand"));
        Alarm.getInstance().setAlarm(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, String.format("onBind"));
        return null;
    }
}