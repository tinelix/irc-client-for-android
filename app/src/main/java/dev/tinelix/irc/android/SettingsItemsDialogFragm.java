package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import java.util.Locale;

@SuppressLint("NewApi")
public class SettingsItemsDialogFragm extends DialogFragment {
    public String current_parameter;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        current_parameter = ((MainSettingsActivity) getActivity()).getCurrentParameter();
        final String current_value;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "setting_language") {
            current_value = ((MainSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            final String[] languages = getResources().getStringArray(R.array.ui_language);
            builder.setTitle(R.string.interface_language_item);
            if(current_value.contains("OS dependent")) {
                builder.setSingleChoiceItems(languages, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                String value;
                                if(item == 0) {
                                    value = "OS dependent";
                                } else if(item == 1) {
                                    value = "English";
                                } else {
                                    value = "Russian";
                                }
                                ((MainSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                            }
                        });
            } else if(current_value.contains("English")) {
                builder.setSingleChoiceItems(languages, 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                String value;
                                if(item == 0) {
                                    value = "OS dependent";
                                } else if(item == 1) {
                                    value = "English";
                                } else {
                                    value = "Russian";
                                }
                                ((MainSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                            }
                        });
            } else {
                builder.setSingleChoiceItems(languages, 2,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                String value;
                                if(item == 0) {
                                    value = "OS dependent";
                                } else if(item == 1) {
                                    value = "English";
                                } else {
                                    value = "Russian";
                                }
                                ((MainSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                            }
                        });
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    final String[] requires_reboot_app = getResources().getStringArray(R.array.ui_language_app_restart_toast);
                    if(global_prefs.getBoolean("connected", false) == false) {
                        Intent mainActivity = new Intent(getActivity(), this.getClass());
                        int pendingIntentId = 1;
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), pendingIntentId, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                        System.exit(0);
                    } else {
                        if(current_value.contains("Russian")) {
                            Toast.makeText(getActivity(), requires_reboot_app[1], Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), requires_reboot_app[0], Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        } else if(current_parameter == "setting_theme") {
            current_value = ((MainSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            String[] themes = getResources().getStringArray(R.array.themes);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            builder.setTitle(getString(R.string.interface_theme_item));
            if(current_value.contains("Dark")) {
                builder.setSingleChoiceItems(themes, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                String value;
                                if(item == 0) {
                                    value = "Dark";
                                } else {
                                    value = "Light";
                                }
                                ((MainSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                            }
                        });
            } else {
                builder.setSingleChoiceItems(themes, 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                String value;
                                if(item == 0) {
                                    value = "Dark";
                                } else {
                                    value = "Light";
                                }
                                ((MainSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                            }
                        });
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    final String[] requires_reboot_app = getResources().getStringArray(R.array.ui_language_app_restart_toast);
                    if(global_prefs.getBoolean("connected", false) == false) {
                        Intent mainActivity = new Intent(getActivity(), this.getClass());
                        int pendingIntentId = 1;
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), pendingIntentId, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                        System.exit(0);
                    } else {
                        if(current_value.contains("Russian")) {
                            Toast.makeText(getActivity(), requires_reboot_app[0], Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), requires_reboot_app[1], Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        return builder.create();
    }

    private void setLocale(SharedPreferences global_prefs) {
        if(global_prefs.getString("language", "OS dependent").contains("Russian")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getActivity().getResources().updateConfiguration(config,
                        getActivity().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getActivity().getResources().updateConfiguration(config,
                        getActivity().getResources().getDisplayMetrics());
            }
        } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getActivity().getResources().updateConfiguration(config,
                        getActivity().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getActivity().getResources().updateConfiguration(config,
                        getActivity().getResources().getDisplayMetrics());
            }
        }
    }
}
