package mcr.apps.gridcoinremote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;


public class Alarm extends BroadcastReceiver {
    private static final String TAG = Alarm.class.getName();
    private final GridcoinRpcSettings gridcoinRpcSettings = GridcoinRpcSettings.getInstance();
    private final NotificationUpdater notificationUpdater = new NotificationUpdater();
    private static final GridcoinData gridcoinData = new GridcoinData();
    private boolean isSet = false;
    private final ReentrantLock lock = new ReentrantLock();

    public Alarm() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        //Toast.makeText(context, "onReceive:Toast" + System.currentTimeMillis(), Toast.LENGTH_LONG).show();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = Objects.requireNonNull(pm).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "gridcoinremote:alarmTag");
        wl.acquire(1000*60);

        if (isOnline(context)) {
            onTimerNotification(context);
        }
        else
        {
            Log.d(TAG, "gridcoinremote.isOffline");
            Toast.makeText(context, "GridcoinRemote is offline...", Toast.LENGTH_SHORT).show();
        }

        wl.release();
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //noinspection RedundantIfStatement
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    public void setAlarm(Context context) {
        this.lock.lock();
        try{
            if (!this.isSet) {
                this.isSet = true;
                Log.d(TAG, "setAlarm");
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, Alarm.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 1000 * 6 * 1, alarmIntent); // Millisecond * Second * Minute
                Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, 1000 * 6 * 5, alarmIntent);
                //Toast.makeText(context, "Time:" + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "setAlarmDone");
            }
        }
        finally{
            this.lock.unlock();
        }
    }

    public void cancelAlarm(Context context) {
        Log.d(TAG, "cancelAlarm");
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).cancel(sender);
    }

    private void onTimerNotification(Context context) {
        Log.d(TAG, "onTimerNotification");
        if (!this.gridcoinRpcSettings.isSet()) {
            Log.d(TAG, "gridcoinRpcSettings.Retrieve");
            this.gridcoinRpcSettings.Retrieve(context);
        }

        if (this.gridcoinRpcSettings.isSet()) {
            Log.d(TAG, "gridcoinRpcSettings.isSet");
            updateIsStaking();
            this.notificationUpdater.updateNotification(context, gridcoinData);
        }
    }

    private void updateIsStaking() {
        gridcoinData.ErrorInDataGathering = false;
        Thread t = new Thread(() -> {
            try {
                new GridcoinRpc().populateWalletInfo(gridcoinData);
            } catch (Exception e) {
                Log.d(TAG, "onTimerNotification:Thread ", e);
                gridcoinData.ErrorInDataGathering = true;
            }
        });
        try {
            t.start();
            t.join(1000 * 30);
        } catch (Exception e) {
            Log.d(TAG, "onTimerNotification:Thread start/join ", e);
            gridcoinData.ErrorInDataGathering = true;
        }
    }
}