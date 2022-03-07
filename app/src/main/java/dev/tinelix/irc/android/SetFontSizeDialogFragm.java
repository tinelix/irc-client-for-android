package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

@SuppressLint("NewApi")
public class SetFontSizeDialogFragm extends DialogFragment {
    public String current_parameter;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setLocale(global_prefs);
        current_parameter = ((MainSettingsActivity) getActivity()).getCurrentParameter();
        String current_value;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "setting_fontsize") {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.set_font_size_activity, null);
            final SeekBar font_size_seekbar = view.findViewById(R.id.font_size_seekbar);
            final EditText preview_text = view.findViewById(R.id.preview_text);
            final TextView value_label = view.findViewById(R.id.value_label);
            if(global_prefs.getString("theme", "Dark").contains("Light")) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    preview_text.setTextColor(getResources().getColor(R.color.black));
                } else {
                    preview_text.setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    preview_text.setTextColor(getResources().getColor(R.color.white));
                } else {
                    preview_text.setTextColor(getResources().getColor(R.color.black));
                }
            }
            builder.setView(view);
            builder.setTitle(R.string.changing_font_label);
            builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("font_size", font_size_seekbar.getProgress() + 12);
                    editor.commit();
                }
            });
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
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
        };
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
