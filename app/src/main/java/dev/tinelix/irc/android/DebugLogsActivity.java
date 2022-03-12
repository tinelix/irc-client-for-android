package dev.tinelix.irc.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DebugLogsActivity extends Activity {

    public EditText debug_log_text;
    public String log_text;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setCustomTheme(global_prefs);
        setContentView(R.layout.debug_log_activity);
        setColorStyle(global_prefs);
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            final EditText debug_log_text = findViewById(R.id.debug_log_text);
            log_text = log.toString();
            debug_log_text.setText(log_text);
            debug_log_text.setKeyListener(null);
            debug_log_text.setTypeface(Typeface.MONOSPACE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                debug_log_text.setTextIsSelectable(true);
            }
            if(global_prefs.getInt("font_size", 0) >= 12) {
                debug_log_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, global_prefs.getInt("font_size", 0));
            }
        } catch (IOException e) {
            // Handle Exception
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        Button save_log_btn = findViewById(R.id.save_log_btn);
        save_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "Tinelix");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + "/Tinelix", "IRC Client");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)  + "/Tinelix/IRC Client", "App Logs");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        try {
                            Date dt = new Date(System.currentTimeMillis());
                            Log.d("App", "Attempting creating log file...");
                            File file = new File(directory, "LOG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(dt) + ".log");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            Log.d("App", "Log file created!");
                            FileWriter writer = new FileWriter(file);
                            writer.append(log_text);
                            writer.flush();
                            writer.close();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved_log, "Documents/Tinelix/IRC Client/App Logs"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("App", "Could not save log to file: " + e.getMessage());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                showMissingPermssionDialog();
                            }
                        }
                } else {
                    File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Tinelix");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tinelix", "IRC Client");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tinelix/IRC Client", "App Logs");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    try {
                        Date dt = new Date(System.currentTimeMillis());
                        Log.d("App", "Attempting creating log file...");
                        File file = new File(directory, "LOG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(dt) + ".log");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        Log.d("App", "Log file created!");
                        FileWriter writer = new FileWriter(file);
                        writer.append(log_text);
                        writer.flush();
                        writer.close();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved_log, "Tinelix/IRC Client/App Logs"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("App", "Could not save log to file: " + e.getMessage());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            showMissingPermssionDialog();
                        }
                    }
                }
            }
        });
    }

    private void showMissingPermssionDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.allow_permisssion_in_storage_title));
        builder.setMessage(getResources().getString(R.string.allow_permisssion_in_storage));
        builder.setPositiveButton(getResources().getString(R.string.open_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        dialog = builder.create();
        dialog.show();
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
                    ListView main_menu = findViewById(R.id.mainmenu);
                    debug_log_text = findViewById(R.id.debug_log_text);
                    debug_log_text.setBackgroundColor(getResources().getColor(R.color.white_90));
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ListView main_menu = findViewById(R.id.mainmenu);
                    debug_log_text = findViewById(R.id.debug_log_text);
                    debug_log_text.setBackgroundColor(getResources().getColor(R.color.gray_v2));
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
                    debug_log_text = findViewById(R.id.debug_log_text);
                    debug_log_text.setBackgroundColor(getResources().getColor(R.color.gray_v2));
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    debug_log_text = findViewById(R.id.debug_log_text);
                    debug_log_text.setBackgroundColor(getResources().getColor(R.color.white_90));
                }
            }
        }
    }
}
