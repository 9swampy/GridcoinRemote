package mcr.apps.gridcoinremote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChecker implements INetworkChecker {
    private static final String TAG = NetworkChecker.class.getName();

    public boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            try {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null
                        && networkInfo.isAvailable()
                        && networkInfo.isConnected()) {
                    return true;
                }
            } catch (Exception netError) {
                Log.d(TAG, "isOnline:getActiveNetworkInfo occurred ", netError);
            }
        } catch (Exception systemError) {
            Log.d(TAG, "isOnline:getSystemService occurred ", systemError);
        }

        return false;
    }
}
