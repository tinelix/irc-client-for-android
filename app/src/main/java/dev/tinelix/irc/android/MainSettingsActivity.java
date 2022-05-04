package dev.tinelix.irc.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
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
import android.os.LocaleList;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Locale;

public class MainSettingsActivity  extends PreferenceActivity {

    public String package_name;
    public String current_parameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setCustomTheme(global_prefs);
        setColorStyle(global_prefs);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_settings);
        setContentView(R.layout.custom_preferences_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            TextView app_title = findViewById(R.id.app_title_label);
            app_title.setText(R.string.settings_title);
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Preference ui_language = findPreference("interface_language");
        ui_language.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setResult(Activity.RESULT_OK);
                current_parameter = "setting_language";
                final String current_value;
                final String[] value = {new String()};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment setFontSizeDialogFragm = new SettingsItemsDialogFragm();
                    setFontSizeDialogFragm.show(getFragmentManager(), "settings_items");
                } else {
                    current_value = getCurrentValue(current_parameter);
                    final String[] languages = getResources().getStringArray(R.array.ui_language);
                    final String[] requires_reboot_app = getResources().getStringArray(R.array.ui_language_app_restart_toast);
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final AlertDialog.Builder dialogBuilder;
                    if (global_prefs.getString("theme", "Dark").contains("Light")) {
                        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient_Light));
                        } else {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient));
                        }
                    } else {
                        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient));
                        } else {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient_Light));
                        }
                    }
                    if (global_prefs.getString("theme", "Dark").contains("Light")) {
                        if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#000000'><b>" + getString(R.string.interface_language_item) + "</b>"));
                        } else {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#ffffff'><b>" + getString(R.string.interface_language_item) + "</b>"));
                        }
                    } else {
                        if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#ffffff'><b>" + getString(R.string.interface_language_item) + "</b>"));
                        } else {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#000000'><b>" + getString(R.string.interface_language_item) + "</b>"));
                        }
                    }
                    if(current_value.contains("OS dependent")) {
                        dialogBuilder.setSingleChoiceItems(languages, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(item == 0) {
                                            value[0] = "OS dependent";
                                        } else if(item == 1) {
                                            value[0] = "English";
                                        } else {
                                            value[0] = "Russian";
                                        }
                                    }
                                });
                    } else if(current_value.contains("English")) {
                        dialogBuilder.setSingleChoiceItems(languages, 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(item == 0) {
                                            value[0] = "OS dependent";
                                        } else if(item == 1) {
                                            value[0] = "English";
                                        } else {
                                            value[0] = "Russian";
                                        }
                                    }
                                });
                    } else {
                        dialogBuilder.setSingleChoiceItems(languages, 2,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(item == 0) {
                                            value[0] = "OS dependent";
                                        } else if(item == 1) {
                                            value[0] = "English";
                                        } else {
                                            value[0] = "Russian";
                                        }
                                    }
                                });
                    }
                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            onChangingValues(current_parameter, value[0]);
                            SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if(global_prefs.getBoolean("connected", false) == false) {
                                Intent mainActivity = new Intent(getApplicationContext(), this.getClass());
                                int pendingIntentId = 1;
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), pendingIntentId, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                                System.exit(0);
                            } else {
                                if(current_value.contains("Russian")) {
                                    Toast.makeText(getApplicationContext(), requires_reboot_app[1], Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), requires_reboot_app[0], Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
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
                return true;
            }
        });
        String[] languages = getResources().getStringArray(R.array.ui_language);
            if (global_prefs.getBoolean("language_requires_restart", false) == false) {
                if (global_prefs.getString("language", "OS dependent").contains("English")) {
                    ui_language.setSummary(languages[1]);
                } else if (global_prefs.getString("language", "OS dependent").contains("Russian")) {
                    ui_language.setSummary(languages[2]);
                } else {
                    ui_language.setSummary(languages[0]);
                }
            } else {
                ui_language.setSummary(R.string.need_to_restart);
                ui_language.setEnabled(false);
            }
        final Preference app_theme = findPreference("interface_theme");
        final String[] value = {new String()};
        app_theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setResult(Activity.RESULT_OK);
                current_parameter = "setting_theme";
                final String current_value;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment setFontSizeDialogFragm = new SettingsItemsDialogFragm();
                    setFontSizeDialogFragm.show(getFragmentManager(), "settings_items");
                } else {
                    current_value = getCurrentValue(current_parameter);
                    final String[] requires_reboot_app = getResources().getStringArray(R.array.ui_language_app_restart_toast);
                    final String[] themes = getResources().getStringArray(R.array.themes);
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final AlertDialog.Builder dialogBuilder;
                    if (global_prefs.getString("theme", "Dark").contains("Light")) {
                        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient_Light));
                        } else {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient));
                        }
                    } else {
                        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient));
                        } else {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient_Light));
                        }
                    }
                    if (global_prefs.getString("theme", "Dark").contains("Light")) {
                        if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#000000'><b>" + getString(R.string.interface_theme_item) + "</b>"));
                        } else {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#ffffff'><b>" + getString(R.string.interface_theme_item) + "</b>"));
                        }
                    } else {
                        if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#ffffff'><b>" + getString(R.string.interface_theme_item) + "</b>"));
                        } else {
                            dialogBuilder.setTitle(Html.fromHtml("<font color='#000000'><b>" + getString(R.string.interface_theme_item) + "</b>"));
                        }
                    }
                    if(current_value.contains("Light")) {
                        dialogBuilder.setSingleChoiceItems(themes, 1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(item == 0) {
                                            value[0] = "Dark";
                                        } else {
                                            value[0] = "Light";
                                        }
                                    }
                                });
                    } else {
                        dialogBuilder.setSingleChoiceItems(themes, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(item == 0) {
                                            value[0] = "Dark";
                                        } else {
                                            value[0] = "Light";
                                        }
                                    }
                                });
                    }
                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            onChangingValues(current_parameter, value[0]);
                            if(global_prefs.getBoolean("connected", false) == false) {
                                Intent mainActivity = new Intent(getApplicationContext(), this.getClass());
                                int pendingIntentId = 1;
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), pendingIntentId, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                                System.exit(0);
                            } else {
                                if(current_value.contains("Russian")) {
                                    Toast.makeText(getApplicationContext(), requires_reboot_app[0], Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), requires_reboot_app[1], Toast.LENGTH_LONG).show();
                                }
                            }
                            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                                if (global_prefs.getString("theme", "Dark").contains("Light")) {
                                    app_theme.setSummary(themes[1]);
                                } else {
                                    app_theme.setSummary(themes[0]);
                                }
                            } else {
                                app_theme.setSummary(R.string.need_to_restart);
                                app_theme.setEnabled(false);
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
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
                return true;
            }
        });
        String[] themes = getResources().getStringArray(R.array.themes);
        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                app_theme.setSummary(themes[1]);
            } else {
                app_theme.setSummary(themes[0]);
            }
        } else {
            app_theme.setSummary(R.string.need_to_restart);
            app_theme.setEnabled(false);
        }

        Preference setFontSizePref = findPreference("font_size");
        setFontSizePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setResult(Activity.RESULT_OK);
                current_parameter = "setting_fontsize";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DialogFragment setFontSizeDialogFragm = new SetFontSizeDialogFragm();
                    setFontSizeDialogFragm.show(getFragmentManager(), "set_font_size");
                } else {
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final AlertDialog.Builder dialogBuilder;
                    if (global_prefs.getString("theme", "Dark").contains("Light")) {
                        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient_Light));
                        } else {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient));
                        }
                    } else {
                        if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient));
                        } else {
                            dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainSettingsActivity.this, R.style.IRCClient_Light));
                        }
                    }
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.set_font_size_activity, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    dialogView.setMinimumWidth(metrics.widthPixels);
                    final SeekBar font_size_seekbar = dialogView.findViewById(R.id.font_size_seekbar);
                    final EditText preview_text = dialogView.findViewById(R.id.preview_text);
                    final TextView value_label = dialogView.findViewById(R.id.value_label);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("font_size", font_size_seekbar.getProgress() + 12);
                            editor.commit();
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    preview_text.setKeyListener(null);
                    font_size_seekbar.setMax((60 - 12) / 1);
                    font_size_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            preview_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, i + 12);
                            value_label.setText(getString(R.string.value_in_px, i + 12));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    if(prefs.getInt("font_size", 0) < 12) {
                        float text_size_in_sp = (int) preview_text.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                        font_size_seekbar.setProgress((int) text_size_in_sp - 12);
                        value_label.setText(getString(R.string.value_in_px, (int)text_size_in_sp));
                        preview_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_size_in_sp);
                    } else {
                        font_size_seekbar.setProgress(prefs.getInt("font_size", 0) - 12);
                        value_label.setText(getString(R.string.value_in_px, prefs.getInt("font_size", 0)));
                        preview_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, prefs.getInt("font_size", 0));
                    }
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    dialog_title.setText(getString(R.string.changing_font_label));
                    dialogBuilder.setView(dialogView);
                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.getWindow().setGravity(Gravity.BOTTOM);

                    alertDialog.show();
                    Button dialogButton;
                    dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                    final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    customizeDialogStyle(dialogButton, global_prefs, alertDialog);
                }
                return true;
            }
        });
        setFontSizePref.setSummary(getString(R.string.value_in_px, prefs.getInt("font_size", 18)));

        final CheckBoxPreference save_msg_history = (CheckBoxPreference) findPreference("save_msg_history");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).canWrite() == true) {
                save_msg_history.setChecked(global_prefs.getBoolean("save_msg_history", false));
            } else {
                save_msg_history.setChecked(false);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    save_msg_history.setChecked(global_prefs.getBoolean("save_msg_history", false));
            } else {
                save_msg_history.setChecked(false);
            }
        } else {
            save_msg_history.setChecked(global_prefs.getBoolean("save_msg_history", false));
        }

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!directory.canWrite() && !directory.canRead()) {
                save_msg_history.setChecked(false);
                onChangingBooleanValues("setting_saving_msg_history", false);
                save_msg_history.setEnabled(false);
                save_msg_history.setSummary(R.string.device_memory_restricted);
            }
        }

        save_msg_history.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(save_msg_history.isChecked() == true) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        save_msg_history.setSummary(getResources().getString(R.string.saved_messages_history, "Documents/Tinelix/IRC Client/Messages Logs"));
                    } else {
                        save_msg_history.setSummary(getResources().getString(R.string.saved_messages_history, "Tinelix/IRC Client/Messages Logs"));
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).canWrite() == true) {
                        current_parameter = "setting_saving_msg_history";
                        onChangingBooleanValues(current_parameter, save_msg_history.isChecked());
                    } else {
                        showMissingPermssionDialog();
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        current_parameter = "setting_saving_msg_history";
                        onChangingBooleanValues(current_parameter, save_msg_history.isChecked());
                    } else {
                        showMissingPermssionDialog();
                    }
                } else {
                    current_parameter = "setting_saving_msg_history";
                    onChangingBooleanValues(current_parameter, save_msg_history.isChecked());
                }
                return false;
            }
        });
        Preference debug_logs = findPreference("debug_logs");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            debug_logs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDebugLogsActivity();
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        CheckBoxPreference save_msg_history = (CheckBoxPreference) findPreference("save_msg_history");
        SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(MainSettingsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).canWrite() == true) {
                save_msg_history.setChecked(global_prefs.getBoolean("save_msg_history", false));
                save_msg_history.setSummary(getResources().getString(R.string.saved_messages_history, "Documents/Tinelix/IRC Client/Messages Logs"));
            } else {
                save_msg_history.setChecked(false);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                save_msg_history.setChecked(global_prefs.getBoolean("save_msg_history", false));
                save_msg_history.setSummary(getResources().getString(R.string.saved_messages_history, "Tinelix/IRC Client/Messages Logs"));
            } else {
                save_msg_history.setChecked(false);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            if (!directory.canWrite() && !directory.canRead()) {
                save_msg_history.setChecked(false);
                save_msg_history.setEnabled(false);
                save_msg_history.setSummary(R.string.device_memory_restricted);
            } else {
                save_msg_history.setChecked(global_prefs.getBoolean("save_msg_history", false));
                save_msg_history.setSummary(getResources().getString(R.string.saved_messages_history, "Tinelix/IRC Client/Messages Logs"));
            }
        }
        super.onResume();
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
                CheckBoxPreference save_msg_history = (CheckBoxPreference) findPreference("save_msg_history");
                save_msg_history.setChecked(false);
                onChangingBooleanValues(current_parameter, save_msg_history.isChecked());
                return;
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void showDebugLogsActivity() {
        Intent intent = new Intent(this, DebugLogsActivity.class);
        startActivity(intent);
    }

    public String getCurrentParameter() {
        return current_parameter;
    }

    public String getCurrentValue(String current_parameter) {
        String value;
        if(current_parameter == "setting_language") {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            value = prefs.getString("language", "OS dependent");
        } else if(current_parameter == "setting_theme") {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            value = prefs.getString("theme", "Dark");
        } else {
            value = "";
        }
        return value;
    }

    public void onChangingValues(String current_parameter, String value) {
        if(current_parameter == "setting_language") {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("language", value);
            editor.putBoolean("language_requires_restart", true);
            editor.commit();
            Preference ui_language = findPreference("interface_language");
            final String[] languages = getResources().getStringArray(R.array.ui_language);
            if(prefs.getBoolean("language_requires_restart", false) == false) {
                if (prefs.getString("language", "OS dependent").contains("English")) {
                    ui_language.setSummary(languages[1]);
                } else if (prefs.getString("language", "OS dependent").contains("Russian")) {
                    ui_language.setSummary(languages[2]);
                } else {
                    ui_language.setSummary(languages[0]);
                }
            } else {
                ui_language.setSummary(R.string.need_to_restart);
                ui_language.setEnabled(false);
            }
        } else if(current_parameter == "setting_theme") {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme", value);
            editor.putBoolean("theme_requires_restart", true);
            editor.commit();
            Preference theme = findPreference("interface_theme");
            final String[] themes = getResources().getStringArray(R.array.themes);
            if(prefs.getBoolean("theme_requires_restart", false) == false) {
                if (prefs.getString("theme", "Dark").contains("Light")) {
                    theme.setSummary(themes[1]);
                } else {
                    theme.setSummary(themes[0]);
                }
            } else {
                theme.setSummary(R.string.need_to_restart);
                theme.setEnabled(false);
            }
        }
    }

    private void onChangingBooleanValues(String current_parameter, boolean value) {
        if(current_parameter == "setting_saving_msg_history") {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("save_msg_history", value);
            editor.commit();
        }
    }

    public void showDebugLogActivity() {
        Intent intent = new Intent(this, ConnectionManagerActivity.class);
        startActivity(intent);
    }

    public void setColorStyle(SharedPreferences global_prefs) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    getListView().setBackgroundColor(getResources().getColor(R.color.white));
                    getListView().setCacheColorHint(getResources().getColor(R.color.white));
                } else {
                    getListView().setBackgroundColor(getResources().getColor(R.color.black));
                    getListView().setCacheColorHint(getResources().getColor(R.color.black));
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    getListView().setBackgroundColor(getResources().getColor(R.color.black));
                    getListView().setCacheColorHint(getResources().getColor(R.color.black));
                } else {
                    getListView().setBackgroundColor(getResources().getColor(R.color.white));
                    getListView().setCacheColorHint(getResources().getColor(R.color.white));
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
                    getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                } else {
                    Locale locale = new Locale("en_US");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                }
        } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
                if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                    Locale locale = new Locale("en_US");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                } else {
                    Locale locale = new Locale("ru");
                    Locale.setDefault(locale);
                    Configuration config = getResources().getConfiguration();
                    config.locale = locale;
                    getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                }
        }
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient_Light);
            } else {
                setTheme(R.style.IRCClient);
            }
        } else {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
