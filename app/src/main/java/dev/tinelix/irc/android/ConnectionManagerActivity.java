package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.DialogFragment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
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
                            prefs.getString("server", ""), prefs.getInt("port", 0), false, false));
                }
                ;
            }
            ListView profilesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesList.setAdapter(profilesAdapter);
            profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(), ((TextView)view.findViewById(R.id.profile_item_label)).getText(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            FragmentManager fragmentManager = null;
            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = null;
            fragmentTransaction = fragmentManager.beginTransaction();
            CreateItemFragm ci_fragment = new CreateItemFragm();
            fragmentTransaction.add(R.id.create_item_layout2, ci_fragment);
            fragmentTransaction.commit();
        } else {
            LinearLayout add_item_ll = findViewById(R.id.add_item_ll);
            add_item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEnterTextDialog();
                }
            });
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connection_manager_menu, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DialogFragment enterTextDialogFragm = new EnterTextDialogFragm();
            enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
        } else {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.enter_text_activity, null);
            TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            dialog_title.setText(getString(R.string.enter_the_pfn_title));
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dialogView.setMinimumWidth(metrics.widthPixels);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText profile_name = dialogView.findViewById(R.id.profile_name_text);
                    profileNameOkClicked(profile_name.getText().toString());
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
            alertDialog.show();

            Button dialogButton;
            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.orange));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }
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
                            prefs.getString("server", ""), prefs.getInt("port", 0), false, false));
                }
                ;
            }
            final ListView profilesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesList.setAdapter(profilesAdapter);
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
        editor.putString("quit_message", getString(R.string.default_quit_msg));
        editor.commit();
        Intent intent = new Intent(this, ProfileSettingsActivity.class);
        intent.putExtra("profile_name", profile_name);
        intent.putExtra("package_name", getApplicationContext().getPackageName());
        startActivity(intent);
    }

    public void selectProfile(View v) {
    }

    public void connectProfile(int position) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ThreadActivity.class);
        Profile profile_item = null;
        profile_item = (Profile) profilesAdapter.getItem(position);
        intent.putExtra("profile_name", profile_item.name);
        startActivity(intent);
        finish();
    }

    public void editProfile(int position) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ProfileSettingsActivity.class);
        Profile profile_item = null;
        profile_item = (Profile) profilesAdapter.getItem(position);
        intent.putExtra("profile_name", profile_item.name);
        intent.putExtra("package_name", getApplicationContext().getPackageName());
        startActivity(intent);
    }

    public void deleteProfile(int position) {
        profilesAdapter = new ProfileAdapter(this, profilesList);
        String package_name = getApplicationContext().getPackageName();
        Profile profile_item = null;
        profile_item = (Profile) profilesAdapter.getItem(position);
        profilesArray.clear();
        profilesList.clear();
        String profile_path = "/data/data/" + package_name + "/shared_prefs/" + profile_item.name + ".xml";
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
                            prefs.getString("server", ""), prefs.getInt("port", 0), false, false));
                }
                ;
            }
            ListView profilesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesList.setAdapter(profilesAdapter);
            profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(), ((TextView)view.findViewById(R.id.profile_item_label)).getText(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    }
}
