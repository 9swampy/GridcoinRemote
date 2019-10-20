package mcr.apps.gridcoinremote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by cardo on 10/29/2016.
 */

public class SignIn extends AppCompatActivity {

    private MenuDrawer menuDrawer;
    static boolean SignInformationFilled = false;
    static boolean EditMode = false;
    static GridcoinRpcSettings gridcoinRpcSettings = GridcoinRpcSettings.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        menuDrawer = new MenuDrawer(this, 1);

        final TextView welcomeText = findViewById(R.id.welcomeText);
        final TextView howToEnableRPCLink = findViewById(R.id.HowToEnableRPC);
        final EditText ipField = findViewById(R.id.IPAddressText);
        final EditText portField = findViewById(R.id.PortText);
        final EditText usernameField = findViewById(R.id.UsernameField);
        final EditText passwordField = findViewById(R.id.PasswordTextBox);
        final CheckBox rememberBox = findViewById(R.id.RememberCheckBox);
        final Button button = findViewById(R.id.SaveSignInButton);
        if (EditMode) {
            welcomeText.setText("Wallet Settings");
        } else {
            welcomeText.setText("Welcome!");
        }
        if (gridcoinRpcSettings.isIpSet()) {
            ipField.setText(gridcoinRpcSettings.ipFieldString);
        }
        if (gridcoinRpcSettings.isPortSet()) {
            portField.setText(gridcoinRpcSettings.portFieldString);
        }
        if (gridcoinRpcSettings.isUsernameSet()) {
            usernameField.setText(gridcoinRpcSettings.UsernameFieldString);
        }
        if (gridcoinRpcSettings.isPasswordSet()) {
            passwordField.setText(gridcoinRpcSettings.PasswordFieldString);
        }
        if (gridcoinRpcSettings.RememberChecked) {
            rememberBox.setChecked(true);
        } else {
            rememberBox.setChecked(false);
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isIpBlank()) {
                    gridcoinRpcSettings.ipFieldString = ipField.getText().toString();
                }
                if (!isPortBlank()) {
                    gridcoinRpcSettings.portFieldString = portField.getText().toString();
                }
                if (!isUsernameBlank()) {
                    gridcoinRpcSettings.UsernameFieldString = usernameField.getText().toString();
                }
                if (!isPasswordBlank()) {
                    gridcoinRpcSettings.PasswordFieldString = passwordField.getText().toString();
                }
                if (!gridcoinRpcSettings.isSet()) {
                    SignInformationFilled = false;
                    String message, messageTitle = "Error";
                    message = "Please fill the following fields to proceed:" + System.getProperty("line.separator");
                    if (isIpBlank())
                        message += "-IP Address Field" + System.getProperty("line.separator");
                    if (isPortBlank())
                        message += "-Port Field" + System.getProperty("line.separator");
                    if (isUsernameBlank())
                        message += "-Username" + System.getProperty("line.separator");
                    if (isPasswordBlank())
                        message += "-Password" + System.getProperty("line.separator");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                    builder.setTitle(messageTitle);
                    builder.setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog notify = builder.create();
                    notify.show();
                } else {
                    SignInformationFilled = true;
                    if (rememberBox.isChecked()) {
                        gridcoinRpcSettings.RememberChecked = true;
                        gridcoinRpcSettings.Save(SignIn.this);
                    } else {
                        gridcoinRpcSettings.RememberChecked = false;
                        gridcoinRpcSettings.Forget(SignIn.this);
                    }

                    Intent start = new Intent(SignIn.this, MainActivity.class);
                    startActivity(start);
                }
            }

            private boolean isPasswordBlank() {
                return isEditTextBlank(passwordField);
            }

            private boolean isUsernameBlank() {
                return isEditTextBlank(usernameField);
            }

            private boolean isPortBlank() {
                return isEditTextBlank(portField);
            }

            private boolean isIpBlank() {
                return isEditTextBlank(ipField);
            }

            private boolean isEditTextBlank(EditText editText) {
                return StringUtils.isBlank(editText.getText().toString());
            }

        });
        howToEnableRPCLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://moisescardona.me/enable-gridcoin-rpc"));
                startActivity(browserIntent);
            }
        });
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
