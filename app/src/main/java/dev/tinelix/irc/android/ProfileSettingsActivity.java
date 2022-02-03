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
    public String current_parameter;
    public String auth_method_string;

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
        prof_name.setSummary(old_profile_name);
        prof_name.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "changing_profile_name";
                DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                return true;
            }
        });
        Preference auth_method = (Preference) findPreference("auth_method");
        auth_method.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "changing_auth_method";
                DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                return true;
            }
        });
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
        auth_method_string = prefs.getString("auth_method", "");
        if(auth_method_string == "NickServ") {
            auth_method.setSummary("NickServ");
        } else {
            String[] auth_methods = getResources().getStringArray(R.array.auth_method);
            auth_method.setSummary(auth_methods[0]);
        };
    }
    public void onChangingValues(String parameter, String value) {
        Preference prof_name = (Preference) findPreference("prof_name");
        Preference nicknames = (Preference) findPreference("nicknames");
        if (parameter == "changing_profile_name") {
            Context context = getApplicationContext();
            String profile_path = "/data/data/" + package_name + "/shared_prefs/" + old_profile_name + ".xml";
            File file = new File(profile_path);
            file.delete();
            SharedPreferences prefs = context.getSharedPreferences(value, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", value);
            editor.commit();
            old_profile_name = value;
            prof_name.setSummary(old_profile_name);
        } else if(parameter == "changing_auth_method") {
            Preference auth_method = (Preference) findPreference("auth_method");
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            String[] auth_methods = getResources().getStringArray(R.array.auth_method);
            editor.putString("auth_method", value);
            if(value == "Disabled") {
                auth_method.setSummary(auth_methods[0]);
            } else {
                auth_method.setSummary(value);
            }
            editor.commit();
        } else if (parameter == "changing_nicknames") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(value, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nicknames", value);
            editor.commit();
            nicknames.setSummary(value);
        }
    }

    public String getCurrentParameter() {
        return current_parameter;
    }

    public String getCurrentValue(String parameter) {
        String value;
        if(parameter == "changing_auth_method") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("auth_method", "");
        } else {
            value = "";
        };
        return value;
    }
}