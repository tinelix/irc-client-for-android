package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.io.File;

public class ProfileSettingsActivity extends PreferenceActivity
{
    public String old_profile_name;
    public String package_name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.profile_settings);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                old_profile_name = null;
                package_name = null;
            } else {
                old_profile_name = extras.getString("profile_name");
                package_name = extras.getString("package_name");
            }
        } else {
            old_profile_name = (String) savedInstanceState.getSerializable("profile_name");
            package_name = (String) savedInstanceState.getSerializable("package_name");
        }
        Preference prof_name = (Preference) findPreference("prof_name");
        prof_name.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                return true;
            }
        });
    }
    public void profileNameOkClicked(String profile_name) {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(profile_name, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", profile_name);
        editor.commit();
        String profile_path = "/data/data/" + package_name + "/shared_prefs/" + old_profile_name + ".xml";
        File file= new File(profile_path);
        file.delete();
        old_profile_name = profile_name;
    }
}