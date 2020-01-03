package mcr.apps.gridcoinremote;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class CheckConnection extends AppCompatActivity {
    private MenuDrawer menuDrawer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_connection);

        this.menuDrawer = new MenuDrawer(this,2);
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
}