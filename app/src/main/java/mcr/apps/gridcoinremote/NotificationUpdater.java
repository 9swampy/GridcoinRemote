package mcr.apps.gridcoinremote;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

class NotificationUpdater {

    private static final String CHANNEL_ID = "Gridcoin Remote";
    private static final String TAG = NotificationUpdater.class.getName();

    private int getNextNotificationId(Context context) {
        return 1;
        //SharedPreferences settings = getSharedPreferences(context);
        //int id = settings.getInt("notification_id_key", 0);
        //settings.edit().putInt("notification_id_key", (id + 1) % Integer.MAX_VALUE).apply();
        //return id;
    }

    private void createNotificationChannel(Context context, GridcoinData gridcoinData) {
        try {
            Log.d(TAG, "createNotificationChannel");
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
        } catch (Exception e) {
            Log.d(TAG, "createNotificationChannel", e);
            gridcoinData.ErrorInDataGathering = true;
        }
    }

    public void updateNotification(Context context, GridcoinData gridcoinData) {
        InitialiseForToasting();
        createNotificationChannel(context, gridcoinData);
        Log.d(TAG, String.format("gridcoinData.stakingString: %s", gridcoinData.stakingString));
        Log.d(TAG, String.format("gridcoinData.ErrorInDataGathering: %s", gridcoinData.ErrorInDataGathering));
        if (gridcoinData.ErrorInDataGathering) {
            updateNotification(context,R.drawable.gridcoin_comms_logo,"Raise notification to check connection to Gridcoin wallet...","Check connection to Gridcoin wallet!","Check connection to wallet...", SignIn.class);
        } else if (gridcoinData.stakingString != "true") {
            updateNotification(context,R.drawable.gridcoin_detail_borderless_logo,"Raise notification to unlock Gridcoin wallet...","Unlock Gridcoin wallet!","Log in to your wallet to unlock for staking...", MainActivity.class);
        } else {
            try {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(this.getNextNotificationId(context));
            }
            catch (Exception e)
            {
                Log.d(TAG, "onTimerNotification:Notification cancel ", e);
            }
        }
    }

    private void InitialiseForToasting() {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

    private void updateNotification(Context context, int icon, String logMessage, String toastMessage, String notificationMessage, Class intentDestination) {
        Log.d(TAG, logMessage);
        Toast.makeText(context, toastMessage + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, intentDestination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle("Gridcoin Wallet")
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(this.getNextNotificationId(context), builder.build());
    }
}
