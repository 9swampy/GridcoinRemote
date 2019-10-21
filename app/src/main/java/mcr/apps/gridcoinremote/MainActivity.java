package mcr.apps.gridcoinremote;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MenuDrawer menuDrawer;

    private final GridcoinData gridcoinData = new GridcoinData();
    private final GridcoinRpcSettings gridcoinRpcSettings = GridcoinRpcSettings.getInstance();

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.menuDrawer = new MenuDrawer(this, 0);
        Alarm.getInstance().setAlarm(this);

        if (!SignIn.SignInformationFilled) {
            this.gridcoinRpcSettings.Retrieve(this);
            this.gridcoinRpcSettings.RememberChecked = true;
            if (!this.gridcoinRpcSettings.isSet()) {
                Intent login = new Intent(MainActivity.this, SignIn.class);
                startActivity(login);
            } else {
                SignIn.SignInformationFilled = true;
            }
        }

        if (SignIn.SignInformationFilled) {
            new GridcoinDataLoader(this.gridcoinData, this).execute();
        }
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.menuDrawer.syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return (this.menuDrawer.onOptionsItemSelected(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.menuDrawer.onConfigurationChanged(newConfig);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void RefreshData(MenuItem item) {
        new GridcoinDataLoader(this.gridcoinData, this).execute();
    }
}
