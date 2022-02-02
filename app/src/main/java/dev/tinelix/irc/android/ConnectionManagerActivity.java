package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.DialogFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ConnectionManagerActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_manager);
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connection_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_item) {
            return showEnterTextDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean showEnterTextDialog() {
        DialogFragment enterTextDialogFragm = new EnterTextDialogFragm();
        enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
        return false;
    };

    public void profileNameOkClicked(String profile_name) {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(profile_name, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", profile_name);
        editor.commit();
        Intent intent = new Intent(this, ProfileSettingsActivity.class);
        intent.putExtra("profile_name", profile_name);
        intent.putExtra("package_name", getApplicationContext().getPackageName());
        startActivity(intent);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    }
}
