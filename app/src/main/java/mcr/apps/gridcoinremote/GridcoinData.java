package mcr.apps.gridcoinremote;

import android.util.Log;

public class GridcoinData {
    String BalanceString = "N/A";
    String AddressString = "Address Unknown";
    String stakingString = "0";
    String blocksString = "0";
    String PoRDiff = "0";
    String NetWeight = "0";
    String CPIDString = "N/A";
    String GRCMagUnit = "0";
    String ClientVersion = "0.0.0.0";
    String NodeConnections = "0";
    String MyMag = "0";
    boolean ErrorInDataGathering = false;

    private static final String TAG = GridcoinData.class.getSimpleName();

    public void debugOutput() {
        Log.d(TAG, "DebugOutput()");
        Log.d(TAG, String.format("BalanceString: %s", this.BalanceString));
        Log.d(TAG, String.format("AddressString: %s", this.AddressString));
        Log.d(TAG, String.format("blocksString: %s", this.blocksString));
        Log.d(TAG, String.format("stakingString: %s", this.stakingString));
        Log.d(TAG, String.format("PoRDiff: %s", this.PoRDiff));
        Log.d(TAG, String.format("NetWeight: %s", this.NetWeight));
        Log.d(TAG, String.format("CPIDString: %s", this.CPIDString));
        Log.d(TAG, String.format("GRCMagUnit: %s", this.GRCMagUnit));
        Log.d(TAG, String.format("ClientVersion: %s", this.ClientVersion));
        Log.d(TAG, String.format("NodeConnections: %s", this.NodeConnections));
        Log.d(TAG, String.format("MyMag: %s", this.MyMag));
    }
}
