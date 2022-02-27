package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.DialogFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ConnectionManagerActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public List<String> profilesArray = new ArrayList<String>();
    ArrayList<Profile> profilesList = new ArrayList<Profile>();
    ProfileAdapter profilesAdapter;
    public String selected_profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_manager);
        profilesAdapter = new ProfileAdapter(this, profilesList);
        String package_name = getApplicationContext().getPackageName();
        String profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        profilesArray = new LinkedList<String>();
        String file_extension;
        Context context = getApplicationContext();
        try {
            for (int i = 0; i < prefs_files.length; i++) {
                SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                if (file_extension.contains(".xml") && file_extension.length() == 4) {
                    profilesList.add(new Profile(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)),
                            prefs.getString("server", ""), prefs.getInt("port", 0), false));
                }
                ;
            }
            ListView profilesList = findViewById(R.id.profile_list);
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesList.setAdapter(profilesAdapter);
            profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(context, ((TextView)view.findViewById(R.id.profile_item_label)).getText(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateItemFragm ci_fragment = new CreateItemFragm();
        fragmentTransaction.add(R.id.create_item_layout, ci_fragment);
        fragmentTransaction.commit();
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connection_manager_menu, menu);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        } else if(id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean showEnterTextDialog() {
        DialogFragment enterTextDialogFragm = new EnterTextDialogFragm();
        enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
        return false;
    };

    @Override
    protected void onResume() {
        super.onResume();
        profilesAdapter = new ProfileAdapter(this, profilesList);
        String package_name = getApplicationContext().getPackageName();
        String profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        profilesList.clear();
        profilesArray.clear();
        String file_extension;
        Context context = getApplicationContext();
        try {
            for (int i = 0; i < prefs_files.length; i++) {
                SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                if (file_extension.contains(".xml") && file_extension.length() == 4) {
                    profilesList.add(new Profile(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)),
                            prefs.getString("server", ""), prefs.getInt("port", 0), false));
                }
                ;
            }
            ListView profilesList = findViewById(R.id.profile_list);
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesList.setAdapter(profilesAdapter);
            profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(context, ((TextView)view.findViewById(R.id.profile_item_label)).getText(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void profileNameOkClicked(String profile_name) {
        profilesArray.clear();
        profilesList.clear();
        profilesAdapter = new ProfileAdapter(this, profilesList);
        String package_name = getApplicationContext().getPackageName();
        String profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(profile_name, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", profile_name);
        editor.putString("auth_method", "Disabled");
        editor.putString("hide_ip", "Disabled");
        editor.commit();
        Intent intent = new Intent(this, ProfileSettingsActivity.class);
        intent.putExtra("profile_name", profile_name);
        intent.putExtra("package_name", getApplicationContext().getPackageName());
        startActivity(intent);
    }

    public void selectProfile(View v) {
    }

    public void connectProfile(View v) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ThreadActivity.class);
        TextView profile_name = v.getRootView().findViewById(R.id.profile_item_label);
        intent.putExtra("profile_name", profile_name.getText());
        startActivity(intent);
        finish();
    }

    public void editProfile(View v) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ProfileSettingsActivity.class);
        TextView profile_name = v.getRootView().findViewById(R.id.profile_item_label);
        intent.putExtra("profile_name", profile_name.getText());
        intent.putExtra("package_name", getApplicationContext().getPackageName());
        startActivity(intent);
    }

    public void deleteProfile(View v) {
        profilesArray.clear();
        profilesList.clear();
        profilesAdapter = new ProfileAdapter(this, profilesList);
        String package_name = getApplicationContext().getPackageName();
        TextView profile_name = v.getRootView().findViewById(R.id.profile_item_label);
        TextView server_text = v.getRootView().findViewById(R.id.profile_server_label);
        String profile_path = "/data/data/" + package_name + "/shared_prefs/" + profile_name.getText() + ".xml";
        File file = new File(profile_path);
        file.delete();
        profilesAdapter = new ProfileAdapter(this, profilesList);
        package_name = getApplicationContext().getPackageName();
        profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        profilesList.clear();
        profilesArray.clear();
        String file_extension;
        Context context = getApplicationContext();
        try {
            for (int i = 0; i < prefs_files.length; i++) {
                SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                if (file_extension.contains(".xml") && file_extension.length() == 4) {
                    profilesList.add(new Profile(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)),
                            prefs.getString("server", ""), prefs.getInt("port", 0), false));
                }
                ;
            }
            ListView profilesList = findViewById(R.id.profile_list);
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesList.setAdapter(profilesAdapter);
            profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(context, ((TextView)view.findViewById(R.id.profile_item_label)).getText(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    }
}
