package mcr.apps.gridcoinremote;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

class MenuDrawer {
    private final AppCompatActivity appCompatActivity;
    private final DrawerLayout drawerLayout;
    private final ListView drawerList;
    private final ActionBarDrawerToggle drawerToggle;

    public MenuDrawer(AppCompatActivity appCompatActivity, int position) {
        this.appCompatActivity = appCompatActivity;
        drawerList = this.appCompatActivity.findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(appCompatActivity, R.layout.drawerlistbox, this.appCompatActivity.getResources().getStringArray(R.array.drawerMainActivityList)));
        drawerLayout = this.appCompatActivity.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(appCompatActivity, drawerLayout, 0, 0) {
            public void onDrawerClosed(View view) {
                MenuDrawer.this.appCompatActivity.getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
            }

            public void onDrawerOpened(View drawerView) {
                MenuDrawer.this.appCompatActivity.invalidateOptionsMenu();
                drawerLayout.bringToFront();
            }

            public void onDrawerSlide(View drawerView, float offset) {
                if (offset != 0)
                    drawerLayout.bringToFront();
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        this.appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        this.appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerList.setItemChecked(position, true);
        drawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        this.drawerToggle.onConfigurationChanged(newConfig);
    }

    public void syncState() {
        this.drawerToggle.syncState();
    }


    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.drawerToggle.onOptionsItemSelected(item);
    }

    public void setItemChecked(int position, boolean value) {
        this.drawerList.setItemChecked(position, value);
    }

    public void closeDrawer() {
        this.drawerLayout.closeDrawer(this.drawerList);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) //Wallet
            {
                Intent wallet = new Intent(MenuDrawer.this.appCompatActivity, MainActivity.class);
                MenuDrawer.this.appCompatActivity.startActivity(wallet);
            }
            if (position == 1) //Settings
            {
                SignIn.EditMode = true;
                Intent signin = new Intent(MenuDrawer.this.appCompatActivity, SignIn.class);
                MenuDrawer.this.appCompatActivity.startActivity(signin);
            }
            if (position == 2) //About App
            {
                Intent about = new Intent(MenuDrawer.this.appCompatActivity, AboutApp.class);
                MenuDrawer.this.appCompatActivity.startActivity(about);
            }

            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        this.setItemChecked(position, true);
        this.closeDrawer();
    }
}
