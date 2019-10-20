package mcr.apps.gridcoinremote;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by cardo on 10/30/2016.
 */

public class AboutApp extends AppCompatActivity {
    private MenuDrawer menuDrawer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutapp);

        this.menuDrawer = new MenuDrawer(this, new AboutApp.DrawerItemClickListener(),2);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (this.menuDrawer.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        this.menuDrawer.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        this.menuDrawer.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) //Wallet
            {
                Intent wallet = new Intent(AboutApp.this, MainActivity.class);
                startActivity(wallet);
            }
            if (position == 1) //Settings
            {
                SignIn.EditMode = true;
                Intent signin = new Intent(AboutApp.this, SignIn.class);
                startActivity(signin);
            }

            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        this.menuDrawer.setItemChecked(position, true);
        this.menuDrawer.closeDrawer();
    }
}
