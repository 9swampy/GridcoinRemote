package mcr.apps.gridcoinremote;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;

public class GridcoinRpcSettings {
    private static final GridcoinRpcSettings ourInstance = new GridcoinRpcSettings();
    public String ipFieldString = null;
    public String portFieldString = null;
    public String UsernameFieldString = null;
    public String PasswordFieldString = null;
    public boolean RememberChecked = false;

    private GridcoinRpcSettings() {
    }

    public static GridcoinRpcSettings getInstance() {
        return ourInstance;
    }

    public boolean isSet() {
        return !(StringUtils.isBlank(this.ipFieldString) || StringUtils.isBlank(this.portFieldString) || StringUtils.isBlank(this.UsernameFieldString) || StringUtils.isBlank(this.PasswordFieldString));
    }

    public boolean isIpSet() {
        return !TextUtils.isEmpty(this.ipFieldString);
    }

    public boolean isPasswordSet() {
        return !TextUtils.isEmpty(this.PasswordFieldString);
    }

    public boolean isUsernameSet() {
        return !TextUtils.isEmpty(this.UsernameFieldString);
    }

    public boolean isPortSet() {
        return !TextUtils.isEmpty(this.portFieldString);
    }

    public void Retrieve(AppCompatActivity activity) {
        SharedPreferences settings = getSharedPreferences(activity);
        this.ipFieldString = settings.getString("ip", "");
        this.portFieldString = settings.getString("port", "");
        this.UsernameFieldString = settings.getString("username", "");
        this.PasswordFieldString = settings.getString("password", "");
    }

    private SharedPreferences getSharedPreferences(AppCompatActivity activity) {
        return activity.getSharedPreferences("grcremote", android.content.Context.MODE_PRIVATE);
    }

    public void Forget(AppCompatActivity activity) {
        try {
            SharedPreferences settings = getSharedPreferences(activity);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("ip");
            editor.remove("port");
            editor.remove("username");
            editor.remove("password");
            editor.apply();
        } catch (Exception e) {
            //nothing here
        }
    }

    public void Save(AppCompatActivity activity) {
        SharedPreferences settings = getSharedPreferences(activity);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ip", this.ipFieldString);
        editor.putString("port", this.portFieldString);
        editor.putString("username", this.UsernameFieldString);
        editor.putString("password", this.PasswordFieldString);
        editor.apply();
    }
}