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
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class ProfileSettingsActivity extends PreferenceActivity
{
    public String old_profile_name;
    public String package_name;
    public String current_parameter;
    public String auth_method_string;
    public String server_name;
    public int server_port;
    public String realname_string;
    public String hostname_string;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.profile_settings);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
        Preference nicknames = (Preference) findPreference("nicknames");
        nicknames.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                showNicknamesActivity(old_profile_name);
                return true;
            }
        });
        Preference password = (Preference) findPreference("password");
        password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "setting_password";
                DialogFragment enterPasswordDialogFragm = new EnterPasswordDialogFragm();
                enterPasswordDialogFragm.show(getFragmentManager(), "enter_password_dlg");
                return true;
            }
        });
        Preference server_settings = (Preference) findPreference("server_settings");
        server_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "setting_server";
                DialogFragment serverSettingsDialogFragm = new ServerSettingsDialogFragm();
                serverSettingsDialogFragm.show(getFragmentManager(), "server_settings_dlg");
                return true;
            }
        });
        Preference realname = (Preference) findPreference("realname");
        realname.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "changing_realname";
                DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                return true;
            }
        });
        Preference hostname = (Preference) findPreference("hostname");
        hostname.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "changing_hostname";
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
        server_name = prefs.getString("server", "");
        server_port = prefs.getInt("port", 0);
        if(server_name.length() > 0 && server_port > 0) {
            server_settings.setSummary(server_name + ":" + Integer.toString(server_port));
        };
        realname_string = prefs.getString("realname", "");
        hostname_string = prefs.getString("hostname", "");
        realname.setSummary(realname_string);
        hostname.setSummary(hostname_string);
    }

    private void showNicknamesActivity(String profile_name) {
        Intent intent = new Intent(this, CustomNicknamesActivity.class);
        intent.putExtra("profile_name", profile_name);
        startActivity(intent);
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
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nicknames", value);
            editor.commit();
            nicknames.setSummary(value);
        } else if (parameter == "setting_password") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("password", value);
            editor.commit();
        } else if (parameter == "changing_realname") {
            Preference realname = (Preference) findPreference("realname");
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("realname", value);
            editor.commit();
            realname.setSummary(value);
        } else if (parameter == "changing_hostname") {
            Preference hostname = (Preference) findPreference("hostname");
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("hostname", value);
            editor.commit();
            hostname.setSummary(value);
        }
    }

    public String getCurrentParameter() {
        return current_parameter;
    }

    public String getCurrentValue(String parameter) {
        String value;
        if(current_parameter == "changing_profile_name") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("prof_name", "");
        } else if(parameter == "changing_auth_method") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("auth_method", "");
        } else if(parameter == "changing_realname") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("realname", "");
        } else if(parameter == "changing_hostname") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("hostname", "");
        } else if(parameter == "changing_server") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("server", "");
        } else if(parameter == "changing_port") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = "" + prefs.getInt("port", 0);
        } else if(parameter == "changing_encoding") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("encoding", "");
        } else if(parameter == "hide_ip") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("hide_ip", "");
        } else {
            value = "";
        };
        return value;
    }

    public void onSettingServer(String server, String port, String encoding, String hide_ip) {
        Preference server_settings = (Preference) findPreference("server_settings");
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("server", server);
        editor.putInt("port", Integer.parseInt(port));
        editor.commit();
        if(server.length() > 0 && port.length() > 0) {
            server_settings.setSummary(server + ":" + port);
        }
        editor.putString("encoding", encoding);
        editor.putString("hide_ip", hide_ip);
        editor.commit();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}