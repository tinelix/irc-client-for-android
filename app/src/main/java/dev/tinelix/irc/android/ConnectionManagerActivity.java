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
import android.content.res.Configuration;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ConnectionManagerActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public List<String> profilesArray = new ArrayList<String>();
    ArrayList<Profile> profilesList = new ArrayList<Profile>();
    ProfileAdapter profilesAdapter;
    public String selected_profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setCustomTheme(global_prefs);
        setContentView(R.layout.activity_connection_manager);
        setColorStyle(global_prefs);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ImageButton menu_button = findViewById(R.id.menu_button);
            menu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionsMenu();
                }
            });
        }
        profilesAdapter = new ProfileAdapter(this, profilesList);
        String package_name = getApplicationContext().getPackageName();
        String profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        ListView profilesListView = findViewById(R.id.profiles_list);
        LinearLayout profilesLinearLayout = findViewById(R.id.empty_profiles_list_layout);
        profilesArray = new LinkedList<String>();
        String file_extension;
        Context context = getApplicationContext();
        try {
            for (int i = 0; i < prefs_files.length; i++) {
                if(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)).startsWith(getApplicationInfo().packageName + "_preferences"))
                {
                } else {
                    SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                    file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                    if (file_extension.contains(".xml") && file_extension.length() == 4) {
                        profilesList.add(new Profile(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)),
                                prefs.getString("server", ""), prefs.getInt("port", 0), false, false));
                    }
                }
            }
            if(prefs_files == null || profilesList.size() == 0) {
                profilesListView.setVisibility(View.GONE);
                profilesLinearLayout.setVisibility(View.VISIBLE);
            } else {
                profilesListView.setVisibility(View.VISIBLE);
                profilesLinearLayout.setVisibility(View.GONE);
            }
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesListView.setAdapter(profilesAdapter);
            profilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(), ((TextView)view.findViewById(R.id.profile_item_label)).getText(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
            final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            customizeDialogStyle(dialogButton, global_prefs, alertDialog);
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
        ListView profilesListView = findViewById(R.id.profiles_list);
        LinearLayout profilesLinearLayout = findViewById(R.id.empty_profiles_list_layout);
        try {
            for (int i = 0; i < prefs_files.length; i++) {
                if(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)).startsWith(getApplicationInfo().packageName + "_preferences"))
                {
                } else {
                    SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                    file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                    if (file_extension.contains(".xml") && file_extension.length() == 4) {
                        profilesList.add(new Profile(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)),
                                prefs.getString("server", ""), prefs.getInt("port", 0), false, false));
                    }
                }
            }
            if(prefs_files == null || profilesList.size() == 0) {
                profilesListView.setVisibility(View.GONE);
                profilesLinearLayout.setVisibility(View.VISIBLE);
            } else {
                profilesListView.setVisibility(View.VISIBLE);
                profilesLinearLayout.setVisibility(View.GONE);
            }
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesListView.setAdapter(profilesAdapter);
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
        editor.putBoolean("connected", false);
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
        Intent parentIntent = new Intent();
        Profile profile_item = null;
        profile_item = (Profile) profilesAdapter.getItem(position);
        intent.putExtra("profile_name", profile_item.name);
        SharedPreferences prefs = context.getSharedPreferences(profile_item.name, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("connected", true);
        editor.commit();
        SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(global_prefs.getBoolean("connected", false) == false) {
            editor = global_prefs.edit();
            editor.putBoolean("connected", true);
            editor.commit();
            setResult(RESULT_OK, parentIntent);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        }
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
        ListView profilesListView = findViewById(R.id.profiles_list);
        LinearLayout profilesLinearLayout = findViewById(R.id.empty_profiles_list_layout);
        try {
            for (int i = 0; i < prefs_files.length; i++) {
                if(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)).startsWith(getApplicationInfo().packageName + "_preferences"))
                {
                } else {
                    SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                    file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                    if (file_extension.contains(".xml") && file_extension.length() == 4) {
                        profilesList.add(new Profile(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)),
                                prefs.getString("server", ""), prefs.getInt("port", 0), false, false));
                    }
                }
            }
            if(prefs_files == null || profilesList.size() == 0) {
                profilesListView.setVisibility(View.GONE);
                profilesLinearLayout.setVisibility(View.VISIBLE);
            } else {
                profilesListView.setVisibility(View.VISIBLE);
                profilesLinearLayout.setVisibility(View.GONE);
            }
            ArrayAdapter<String> profilesAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, profilesArray);
            profilesListView.setAdapter(profilesAdapter);
            profilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private void setColorStyle(SharedPreferences global_prefs) {
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                }
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        }
    }

    private void setCustomTheme(SharedPreferences global_prefs) {
        if(global_prefs.getString("language", "OS dependent").contains("Russian")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            }
        } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            }
        }
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient_Light);
            } else {
                setTheme(R.style.IRCClient);
            }
        } else {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient);
            } else {
                setTheme(R.style.IRCClient_Light);
            }
        }
    }
    private void customizeDialogStyle(Button dialogButton, SharedPreferences global_prefs, AlertDialog alertDialog) {
        if(global_prefs.getString("theme", "Dark").contains("Light")) {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            } else {
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        } else {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            } else {
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        }
    }
}
