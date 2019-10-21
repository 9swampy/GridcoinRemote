package mcr.apps.gridcoinremote;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.locks.ReentrantLock;

public class Alarm extends BroadcastReceiver {
    private static final String CHANNEL_ID = "Gridcoin Remote";
    private static final String TAG = Alarm.class.getName();
    private static final Alarm ourInstance = new Alarm();
    private final GridcoinRpcSettings gridcoinRpcSettings = GridcoinRpcSettings.getInstance();
    private static final GridcoinData gridcoinData = new GridcoinData();
    private boolean isSet = false;
    private ReentrantLock lock = new ReentrantLock();

    private Alarm() {
    }

    public static Alarm getInstance() {
        return ourInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, String.format("onReceive"));
        //Toast.makeText(context, "onReceive:Toast" + System.currentTimeMillis(), Toast.LENGTH_LONG).show();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "gridcoinremote:alarmTag");
        wl.acquire();

        createNotificationChannel(context);
        onTimerNotification(context);

        wl.release();
    }

    public void setAlarm(Context context) {
        lock.lock();
        try{
            if (!this.isSet) {
                this.isSet = true;
                Log.d(TAG, String.format("setAlarm"));
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, Alarm.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 1000 * 6 * 1, alarmIntent); // Millisec * Second * Minute
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, 1000 * 6 * 5, alarmIntent);
                //Toast.makeText(context, "Time:" + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                Log.d(TAG, String.format("setAlarmDone"));
            }
        }
        finally{
            lock.unlock();
        }
    }

    public void cancelAlarm(Context context) {
        Log.d(TAG, String.format("cancelAlarm"));
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private void createNotificationChannel(Context context) {
        Log.d(TAG, String.format("createNotificationChannel"));
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void onTimerNotification(Context context) {
        Log.d(TAG, String.format("onTimerNotification"));
        if (!this.gridcoinRpcSettings.isSet()) {
            Log.d(TAG, String.format("gridcoinRpcSettings.Retrieve"));
            this.gridcoinRpcSettings.Retrieve(context);
        }

        if (this.gridcoinRpcSettings.isSet()) {
            Log.d(TAG, String.format("gridcoinRpcSettings.isSet"));

            gridcoinData.ErrorInDataGathering = false;
            Thread t = new Thread(() -> {
                try {
                    new GridcoinRpc().populateMiningInfo(gridcoinData);
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

            Log.d(TAG, String.format("gridcoinData.stakingString: %s", this.gridcoinData.stakingString));
            Log.d(TAG, String.format("gridcoinData.ErrorInDataGathering: %s", this.gridcoinData.ErrorInDataGathering));
            if (this.gridcoinData.ErrorInDataGathering) {
                Toast.makeText(context, "Check GridcoinRemote connection", Toast.LENGTH_LONG).show();
            } else if (this.gridcoinData.stakingString != "true") {
                Log.d(TAG, String.format("Raise notification to unlock Gridcoin wallet..."));
                Toast.makeText(context, "Unlock Gridcoin wallet!" + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.gridcoin_logo)
                        .setContentTitle("Gridcoin Wallet")
                        .setContentText("Log in to your wallet to unlock for staking...")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(this.gridcoinRpcSettings.getNextNotificationId(context), builder.build());
            } else {
                try {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(this.gridcoinRpcSettings.getNextNotificationId(context));
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onTimerNotification:Notification cancel ", e);
                }
            }
        }
    }
}