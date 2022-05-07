package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class StatisticsFragm extends DialogFragment {
    public int sended_bytes;
    public int received_bytes;
    public int total_bytes;
    public Timer statsTimer;
    public TextView sended_bytes_label;
    public TextView received_bytes_label;
    public TextView total_bytes_label;
    public View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setLocale(global_prefs);
        try {
            sended_bytes = ((ThreadActivity) getActivity()).getSendedBytes();
            received_bytes = ((ThreadActivity) getActivity()).getReceivedBytes();
            total_bytes = sended_bytes + received_bytes;
        } catch (NullPointerException npe) {
            sended_bytes = -1;
            received_bytes = -1;
            total_bytes = -1;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.statistics_item);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.statistics_activity, null);
        builder.setView(view);
        sended_bytes_label = view.findViewById(R.id.sended_label2);
        received_bytes_label = view.findViewById(R.id.received_label2);
        total_bytes_label = view.findViewById(R.id.total_label2);
        TextView session_label = view.findViewById(R.id.session_label);
        if (global_prefs.getString("theme", "Dark").contains("Light")) {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                session_label.setTextColor(getResources().getColor(R.color.black));
            } else {
                session_label.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                session_label.setTextColor(getResources().getColor(R.color.white));
            } else {
                session_label.setTextColor(getResources().getColor(R.color.black));
            }
        }
        DecimalFormat dF = new DecimalFormat("#.##");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            dF.setRoundingMode(RoundingMode.DOWN);
        }
        if (sended_bytes > 1073741824) {
            String sended_bytes_rounded = dF.format((float)(sended_bytes / 1073741824));
            sended_bytes_label.setText(getString(R.string.gbytes_stats, sended_bytes_rounded));
        } else if(sended_bytes > 1048576) {
            String sended_bytes_rounded = dF.format((float)(sended_bytes / 1048576));
            sended_bytes_label.setText(getString(R.string.mbytes_stats, sended_bytes_rounded));
        } else if(sended_bytes > 1024) {
            String sended_bytes_rounded = dF.format((float)(sended_bytes / 1024));
            sended_bytes_label.setText(getString(R.string.kbytes_stats, sended_bytes_rounded));
        } else if(sended_bytes >= 0) {
            sended_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(sended_bytes)));
        } else {
            sended_bytes_label.setText(R.string.unknown);
        }
        if (received_bytes > 1073741824) {
            String received_bytes_rounded = dF.format((float)(received_bytes / 1073741824));
            received_bytes_label.setText(getString(R.string.gbytes_stats, received_bytes_rounded));
        } else if(received_bytes > 1048576) {
            String received_bytes_rounded = dF.format((float)(received_bytes / 1048576));
            received_bytes_label.setText(getString(R.string.mbytes_stats, received_bytes_rounded));
        } else if(received_bytes > 1024) {
            String received_bytes_rounded = dF.format((float)(received_bytes / 1024));
            received_bytes_label.setText(getString(R.string.kbytes_stats, received_bytes_rounded));
        } else if (received_bytes >= 0) {
            received_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(received_bytes)));
        } else {
            received_bytes_label.setText(R.string.unknown);
        }
        if (total_bytes > 1073741824) {
            String total_bytes_rounded = dF.format((float)(total_bytes / 1073741824));
            total_bytes_label.setText(getString(R.string.gbytes_stats, total_bytes_rounded));
        } else if(total_bytes > 1048576) {
            String total_bytes_rounded = dF.format((float)(total_bytes / 1048576));
            total_bytes_label.setText(getString(R.string.mbytes_stats, total_bytes_rounded));
        } else if(total_bytes > 1024) {
            String total_bytes_rounded = dF.format((float)(total_bytes / 1024));
            total_bytes_label.setText(getString(R.string.kbytes_stats, total_bytes_rounded));
        } else if (total_bytes >= 0) {
            total_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(total_bytes)));
        } else {
            total_bytes_label.setText(R.string.unknown);
        }
        final Handler updateHandler = new Handler();
        statsTimer = new Timer();
        statsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sended_bytes = ((ThreadActivity) getActivity()).getSendedBytes();
                    received_bytes = ((ThreadActivity) getActivity()).getReceivedBytes();
                    total_bytes = sended_bytes + received_bytes;
                } catch (NullPointerException npe) {
                    sended_bytes = -1;
                    received_bytes = -1;
                    total_bytes = -1;
                }
                updateHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DecimalFormat dF = new DecimalFormat("#.##");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                            dF.setRoundingMode(RoundingMode.DOWN);
                        }
                        if (sended_bytes > 1073741824) {
                            String sended_bytes_rounded = dF.format((float)(sended_bytes / 1073741824));
                            sended_bytes_label.setText(getString(R.string.gbytes_stats, sended_bytes_rounded));
                        } else if(sended_bytes > 1048576) {
                            String sended_bytes_rounded = dF.format((float)(sended_bytes / 1048576));
                            sended_bytes_label.setText(getString(R.string.mbytes_stats, sended_bytes_rounded));
                        } else if(sended_bytes > 1024) {
                            String sended_bytes_rounded = dF.format((float)(sended_bytes / 1024));
                            sended_bytes_label.setText(getString(R.string.kbytes_stats, sended_bytes_rounded));
                        } else if(sended_bytes >= 0) {
                            sended_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(sended_bytes)));
                        } else {
                            sended_bytes_label.setText(R.string.unknown);
                        }
                        if (received_bytes > 1073741824) {
                            String received_bytes_rounded = dF.format((float)(received_bytes / 1073741824));
                            received_bytes_label.setText(getString(R.string.gbytes_stats, received_bytes_rounded));
                        } else if(received_bytes > 1048576) {
                            String received_bytes_rounded = dF.format((float)(received_bytes / 1048576));
                            received_bytes_label.setText(getString(R.string.mbytes_stats, received_bytes_rounded));
                        } else if(received_bytes > 1024) {
                            String received_bytes_rounded = dF.format((float)(received_bytes / 1024));
                            received_bytes_label.setText(getString(R.string.kbytes_stats, received_bytes_rounded));
                        } else if(received_bytes >= 0) {
                            received_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(received_bytes)));
                        } else {
                            received_bytes_label.setText(R.string.unknown);
                        }
                        if (total_bytes > 1073741824) {
                            String total_bytes_rounded = dF.format((float)(total_bytes / 1073741824));
                            total_bytes_label.setText(getString(R.string.gbytes_stats, total_bytes_rounded));
                        } else if(total_bytes > 1048576) {
                            String total_bytes_rounded = dF.format((float)(total_bytes / 1048576));
                            total_bytes_label.setText(getString(R.string.mbytes_stats, total_bytes_rounded));
                        } else if(total_bytes > 1024) {
                            String total_bytes_rounded = dF.format((float)(total_bytes / 1024));
                            total_bytes_label.setText(getString(R.string.kbytes_stats, total_bytes_rounded));
                        } else if(total_bytes >= 0) {
                            total_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(total_bytes)));
                        } else {
                            total_bytes_label.setText(R.string.unknown);
                        }
                    }
                });
            }
        }, 1000, 1000);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statsTimer.cancel();
                statsTimer = null;
                return;
            }
        });
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
