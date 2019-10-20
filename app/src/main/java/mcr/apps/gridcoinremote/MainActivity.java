package mcr.apps.gridcoinremote;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.StrictMode;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private MenuDrawer menuDrawer;
    private GridcoinData gridcoinData = new GridcoinData();

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuDrawer = new MenuDrawer(this, new DrawerItemClickListener(), 0);

        if (!SignIn.SignInformationFilled) {
            SharedPreferences settings = getSharedPreferences("grcremote", MODE_PRIVATE);
            SignIn.ipFieldString = settings.getString("ip", "");
            SignIn.portFieldString = settings.getString("port", "");
            SignIn.UsernameFieldString = settings.getString("username", "");
            SignIn.PasswordFieldString = settings.getString("password", "");
            SignIn.RememberChecked = true;
            if (SignIn.ipFieldString.equals("") || SignIn.portFieldString.equals("") || SignIn.UsernameFieldString.equals("") || SignIn.PasswordFieldString.equals("")) {
                Intent login = new Intent(MainActivity.this, SignIn.class);
                startActivity(login);
            } else {
                SignIn.SignInformationFilled = true;
            }
        }
        if (SignIn.SignInformationFilled) {
            new LoadData().execute();
        }
    }

    private class LoadData extends AsyncTask<Void, String, Void> {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Loading Wallet Data...");
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                GridcoinRpc gridcoinRpc = new GridcoinRpc();
                gridcoinRpc.populateBalance(MainActivity.this.gridcoinData);
                //AddressString = populateAddress();
                gridcoinRpc.populateMiningInfo(MainActivity.this.gridcoinData);
                gridcoinRpc.populateInfo(MainActivity.this.gridcoinData);
                //populateMyMag();
                MainActivity.this.gridcoinData.debugOutput();
            } catch (Exception e) {
                gridcoinData.ErrorInDataGathering = true;
                Log.d(TAG, "doInBackground()", e);
            }
            return null;
        }

        protected void onProgressUpdate(String... params) {
            dialog.setMessage(params[0]);
        }

        protected void onPostExecute(Void result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (gridcoinData.ErrorInDataGathering) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Could not connect to wallet. Please verify that the wallet is running and that the server information is correct.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SignIn.EditMode = true;
                                Intent signin = new Intent(MainActivity.this, SignIn.class);
                                startActivity(signin);
                            }
                        });
                AlertDialog notify = builder.create();
                notify.show();

            } else {
                final TextView balanceText = findViewById(R.id.balance);
                final TextView addressText = findViewById(R.id.address);
                final TextView BlockText = findViewById(R.id.blocks);
                final TextView StakingText = findViewById(R.id.staking);
                final TextView PoRText = findViewById(R.id.PoRDifficulty);
                final TextView NetWeightText = findViewById(R.id.NetWeight);
                final TextView CPIDText = findViewById(R.id.CPIDText);
                final TextView GRCMagText = findViewById(R.id.MagUnit);
                final TextView MyMagText = findViewById(R.id.MyMag);
                final TextView ClientVersionText = findViewById(R.id.version);
                final TextView ConnectionsText = findViewById(R.id.Connections);
                balanceText.setText(String.format("Balance: %s GRC", gridcoinData.BalanceString));
                addressText.setText(gridcoinData.AddressString);
                BlockText.setText(String.format("Blocks: %s", gridcoinData.blocksString));
                StakingText.setText(String.format("Staking: %s", gridcoinData.stakingString));
                PoRText.setText(String.format("PoR Difficulty: %s", gridcoinData.PoRDiff));
                NetWeightText.setText(String.format("Net Weight: %s", gridcoinData.NetWeight));
                CPIDText.setText(String.format("CPID: %s", gridcoinData.CPIDString));
                GRCMagText.setText(String.format("GRC Mag Unit: %s", gridcoinData.GRCMagUnit));
                MyMagText.setText(String.format("My Magnitude: %s", gridcoinData.MyMag));
                ClientVersionText.setText(String.format("Client Version: %s", gridcoinData.ClientVersion));
                ConnectionsText.setText(String.format("Connections: %s", gridcoinData.NodeConnections));
            }
        }
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.menuDrawer.syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        if (this.menuDrawer.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.menuDrawer.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 1) //Settings
            {
                SignIn.EditMode = true;
                Intent signin = new Intent(MainActivity.this, SignIn.class);
                startActivity(signin);
            }
            if (position == 2) //About App
            {
                Intent about = new Intent(MainActivity.this, AboutApp.class);
                startActivity(about);
            }

            selectItem(position);
        }

    }

    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        this.menuDrawer.setItemChecked(position, true);
        this.menuDrawer.closeDrawer();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void RefreshData(MenuItem item) {
        new LoadData().execute();
    }
}
