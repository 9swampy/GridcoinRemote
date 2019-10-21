package mcr.apps.gridcoinremote;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GridcoinDataLoader extends AsyncTask<Void, String, Void> {
    final ProgressDialog dialog;
    final GridcoinData gridcoinData;
    final AppCompatActivity activity;

    private static final String TAG = GridcoinDataLoader.class.getName();

    public GridcoinDataLoader(GridcoinData gridcoinData, AppCompatActivity activity) {
        this.dialog = new ProgressDialog(activity);
        this.gridcoinData = gridcoinData;
        this.activity = activity;
    }

    protected void onPreExecute() {
        this.dialog.setMessage("Loading Wallet Data...");
        this.dialog.show();
        this.dialog.setCancelable(false);
        this.dialog.setCanceledOnTouchOutside(false);
        super.onPreExecute();
    }

    protected Void doInBackground(Void... params) {
        try {
            this.gridcoinData.ErrorInDataGathering = false;
            GridcoinRpc gridcoinRpc = new GridcoinRpc();
            gridcoinRpc.populateBalance(this.gridcoinData);
            //AddressString = populateAddress();
            gridcoinRpc.populateMiningInfo(this.gridcoinData);
            gridcoinRpc.populateInfo(this.gridcoinData);
            //populateMyMag();
            this.gridcoinData.debugOutput();
        } catch (Exception e) {
            this.gridcoinData.ErrorInDataGathering = true;
            Log.d(TAG, "doInBackground()", e);
        }
        return null;
    }

    protected void onProgressUpdate(String... params) {
        this.dialog.setMessage(params[0]);
    }

    protected void onPostExecute(Void result) {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (this.gridcoinData.ErrorInDataGathering) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setTitle("Error");
            builder.setMessage("Could not connect to wallet. Please verify that the wallet is running and that the server information is correct.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SignIn.EditMode = true;
                            Intent signin = new Intent(GridcoinDataLoader.this.activity, SignIn.class);
                            GridcoinDataLoader.this.activity.startActivity(signin);
                        }
                    });
            AlertDialog notify = builder.create();
            notify.show();
        } else {
            final TextView balanceText = GridcoinDataLoader.this.activity.findViewById(R.id.balance);
            final TextView addressText = GridcoinDataLoader.this.activity.findViewById(R.id.address);
            final TextView blockText = GridcoinDataLoader.this.activity.findViewById(R.id.blocks);
            final TextView stakingText = GridcoinDataLoader.this.activity.findViewById(R.id.staking);
            final TextView porText = GridcoinDataLoader.this.activity.findViewById(R.id.PoRDifficulty);
            final TextView netWeightText = GridcoinDataLoader.this.activity.findViewById(R.id.NetWeight);
            final TextView cpidText = GridcoinDataLoader.this.activity.findViewById(R.id.CPIDText);
            final TextView grcMagUnit = GridcoinDataLoader.this.activity.findViewById(R.id.MagUnit);
            final TextView myMagText = GridcoinDataLoader.this.activity.findViewById(R.id.MyMag);
            final TextView clientVersionText = GridcoinDataLoader.this.activity.findViewById(R.id.version);
            final TextView connectionsText = GridcoinDataLoader.this.activity.findViewById(R.id.Connections);
            balanceText.setText(String.format("Balance: %s GRC", this.gridcoinData.BalanceString));
            addressText.setText(this.gridcoinData.AddressString);
            blockText.setText(String.format("Blocks: %s", this.gridcoinData.blocksString));
            stakingText.setText(String.format("Staking: %s", this.gridcoinData.stakingString));
            porText.setText(String.format("PoR Difficulty: %s", this.gridcoinData.PoRDiff));
            netWeightText.setText(String.format("Net Weight: %s", this.gridcoinData.NetWeight));
            cpidText.setText(String.format("CPID: %s", this.gridcoinData.CPIDString));
            grcMagUnit.setText(String.format("GRC Mag Unit: %s", this.gridcoinData.GRCMagUnit));
            myMagText.setText(String.format("My Magnitude: %s", this.gridcoinData.MyMag));
            clientVersionText.setText(String.format("Client Version: %s", this.gridcoinData.ClientVersion));
            connectionsText.setText(String.format("Connections: %s", this.gridcoinData.NodeConnections));
        }
    }
}
