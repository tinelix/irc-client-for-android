package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    public String quitmsg_string;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.profile_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                    enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.enter_text_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText profile_name = dialogView.findViewById(R.id.profile_name_text);
                            onChangingValues(current_parameter, profile_name.getText().toString());
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    dialog_title.setText(getString(R.string.enter_the_pfn_title));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                    enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    String current_value = getCurrentValue(current_parameter);
                    String[] auth_methods = getResources().getStringArray(R.array.auth_method);
                    dialogBuilder.setTitle(R.string.auth_method);
                    if(current_value.contains("NickServ")) {
                        dialogBuilder.setSingleChoiceItems(auth_methods, 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        String value;
                                        if(item == 0) {
                                            value = "Disabled";
                                        } else {
                                            value = "NickServ";
                                        }
                                        onChangingValues(current_parameter, value);
                                    }
                                });
                    } else {
                        dialogBuilder.setSingleChoiceItems(auth_methods, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        String value;
                                        if(item == 0) {
                                            value = "Disabled";
                                        } else {
                                            value = "NickServ";
                                        }
                                        onChangingValues(current_parameter, value);
                                    }
                                });
                    }
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment enterPasswordDialogFragm = new EnterPasswordDialogFragm();
                    enterPasswordDialogFragm.show(getFragmentManager(), "enter_password_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.enter_password_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText profile_name = dialogView.findViewById(R.id.password_text);
                            onChangingValues(current_parameter, profile_name.getText().toString());
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    dialog_title.setText(getString(R.string.enter_the_password_title));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment serverSettingsDialogFragm = new ServerSettingsDialogFragm();
                    serverSettingsDialogFragm.show(getFragmentManager(), "server_settings_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.enter_server_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    dialogBuilder.setView(dialogView);
                    final Spinner encoding_spinner = dialogView.findViewById(R.id.encoding_spinner);
                    final EditText server_name = dialogView.findViewById(R.id.server_text);
                    final EditText port_number = dialogView.findViewById(R.id.port_numb);
                    final CheckBox hide_ip_cb = dialogView.findViewById(R.id.hide_ip_checkbox);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] encoding_array = getResources().getStringArray(R.array.encoding_array);
                            String encoding = new String();
                            if(encoding_spinner.getSelectedItemPosition() == 0) {
                                encoding = "utf-8";
                            } else if(encoding_spinner.getSelectedItemPosition() == 1) {
                                encoding = "cp866";
                            } else if(encoding_spinner.getSelectedItemPosition() == 2) {
                                encoding = "windows-1251";
                            } else if(encoding_spinner.getSelectedItemPosition() == 3) {
                                encoding = "koi8_r";
                            } else if(encoding_spinner.getSelectedItemPosition() == 4) {
                                encoding = "koi8_u";
                            };
                            String hide_ip = new String();
                            if(hide_ip_cb.isChecked() == true) {
                                hide_ip = "Enabled";
                            } else {
                                hide_ip = "Disabled";
                            }
                            onSettingServer(server_name.getText().toString(),
                                    port_number.getText().toString(), encoding, hide_ip);
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    String[] encoding_array = getResources().getStringArray(R.array.encoding_array);
                    String server_parameter = new String();
                    String current_value = new String();
                    server_parameter = "changing_server";
                    current_value = getCurrentValue(server_parameter);
                    EditText server_text = dialogView.findViewById(R.id.server_text);
                    server_text.setText(current_value);
                    server_parameter = "changing_port";
                    current_value = getCurrentValue(server_parameter);
                    EditText port_numb = dialogView.findViewById(R.id.port_numb);
                    port_numb.setText(current_value);
                    server_parameter = "changing_encoding";
                    current_value = getCurrentValue(server_parameter);
                    if(current_value.contains("utf-8")) {
                        encoding_spinner.setSelection(0);
                    } else if(current_value.contains("cp866")) {
                        encoding_spinner.setSelection(1);
                    } else if(current_value.contains("windows-1251")) {
                        encoding_spinner.setSelection(2);
                    } else if(current_value.contains("koi8_r")) {
                        encoding_spinner.setSelection(3);
                    } else if(current_value.contains("koi8_u")) {
                        encoding_spinner.setSelection(4);
                    }
                    server_parameter = "hide_ip";
                    current_value = getCurrentValue(server_parameter);
                    if(current_value.contains("Disabled")) {
                        hide_ip_cb.setChecked(false);
                    } else {
                        hide_ip_cb.setChecked(true);
                    }
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    dialog_title.setText(getString(R.string.server_settings));
                    Spinner spinner = dialogView.findViewById(R.id.encoding_spinner);

                    ArrayList<CustomSpinnerItem> spinnerArray = new ArrayList<CustomSpinnerItem>();
                    spinnerArray.clear();
                    for (int i = 0; i < 5; i++) {
                        spinnerArray.add(new CustomSpinnerItem(getResources().getStringArray(R.array.encoding_array)[i]));
                    }
                    CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(dialogView.getContext(), spinnerArray);
                    Log.d("Client", "Count: " + spinnerArray.size());
                    spinner.setAdapter(customSpinnerAdapter);
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
                Context context = getApplicationContext();
                SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
                realname_string = prefs.getString("realname", "");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                    enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.enter_text_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText profile_name = dialogView.findViewById(R.id.profile_name_text);
                            onChangingValues(current_parameter, profile_name.getText().toString());
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    TextView textAreaTitle = dialogView.findViewById(R.id.profile_name_label);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    EditText dialog_value = dialogView.findViewById(R.id.profile_name_text);
                    dialog_value.setText(realname_string);
                    dialog_title.setText(getString(R.string.enter_the_realname_title));
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
                Context context = getApplicationContext();
                SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
                hostname_string = prefs.getString("hostname", "");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                    enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.enter_text_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText profile_name = dialogView.findViewById(R.id.profile_name_text);
                            onChangingValues(current_parameter, profile_name.getText().toString());
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    TextView textAreaTitle = dialogView.findViewById(R.id.profile_name_label);
                    textAreaTitle.setText(R.string.hostname);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    dialog_title.setText(getString(R.string.enter_the_hostname_title));
                    EditText dialog_value = dialogView.findViewById(R.id.profile_name_text);
                    dialog_value.setText(hostname_string);
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
                return true;
            }
        });
        Preference quit_msg = (Preference) findPreference("quit_message");
        quit_msg.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {
                setResult(Activity.RESULT_OK);
                current_parameter = "changing_quitmsg";
                Context context = getApplicationContext();
                SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
                quitmsg_string = prefs.getString("quit_message", "");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment enterTextDialogFragm = new EnterTextDialogFragm2();
                    enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
                } else {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileSettingsActivity.this, R.style.IRCClient));
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.enter_text_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText profile_name = dialogView.findViewById(R.id.profile_name_text);
                            onChangingValues(current_parameter, profile_name.getText().toString());
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    TextView textAreaTitle = dialogView.findViewById(R.id.profile_name_label);
                    textAreaTitle.setText(R.string.quit_message);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    dialog_title.setText(getString(R.string.enter_the_quiting_message));
                    EditText dialog_value = dialogView.findViewById(R.id.profile_name_text);
                    dialog_value.setText(quitmsg_string);
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
                return true;
            }
        });
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
        auth_method_string = prefs.getString("auth_method", "");
        if(auth_method_string.contains("NickServ")) {
            auth_method.setSummary("NickServ");
            password.setEnabled(true);
        } else {
            String[] auth_methods = getResources().getStringArray(R.array.auth_method);
            auth_method.setSummary(auth_methods[0]);
            password.setEnabled(false);
        };
        server_name = prefs.getString("server", "");
        server_port = prefs.getInt("port", 0);
        if(server_name.length() > 0 && server_port > 0) {
            server_settings.setSummary(server_name + ":" + Integer.toString(server_port));
        };
        realname_string = prefs.getString("realname", "");
        hostname_string = prefs.getString("hostname", "");
        quitmsg_string = prefs.getString("quit_message", "");
        realname.setSummary(realname_string);
        hostname.setSummary(hostname_string);
        quit_msg.setSummary(quitmsg_string);
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
            Preference password = (Preference) findPreference("password");
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            String[] auth_methods = getResources().getStringArray(R.array.auth_method);
            editor.putString("auth_method", value);
            if(value == "Disabled") {
                auth_method.setSummary(auth_methods[0]);
                password.setEnabled(false);
            } else {
                auth_method.setSummary(value);
                password.setEnabled(true);
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
        } else if (parameter == "changing_quitmsg") {
            Preference quit_msg = (Preference) findPreference("quit_message");
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("quit_message", value);
            editor.commit();
            quit_msg.setSummary(value);
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
        } else if(parameter == "changing_quitmsg") {
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            value = prefs.getString("quit_message", "");
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
        if(server.length() > 0 && port.length() > 0) {
            server_settings.setSummary(server + ":" + port);
        }
        editor.putString("encoding", encoding);
        editor.putString("hide_ip", hide_ip);
        editor.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}