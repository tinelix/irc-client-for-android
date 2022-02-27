package dev.tinelix.irc.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ServerSettingsDialogFragm extends DialogFragment {

    public String current_parameter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        current_parameter = ((ProfileSettingsActivity) getActivity()).getCurrentParameter();
        String current_value;
        String server_parameter;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "setting_server") {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.enter_server_activity, null);
            Spinner encoding_spinner = view.findViewById(R.id.encoding_spinner);
            builder.setView(view);
            builder.setTitle(R.string.server_settings);
            builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText server_name = view.findViewById(R.id.server_text);
                        EditText port_number = view.findViewById(R.id.port_numb);
                        CheckBox hide_ip_cb = view.findViewById(R.id.hide_ip_checkbox);
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
                            hide_ip = "Disabled";
                        } else {
                            hide_ip = "Enabled";
                        }
                       ((ProfileSettingsActivity) getActivity()).onSettingServer(server_name.getText().toString(),
                               port_number.getText().toString(), encoding, hide_ip);
                    }
            });
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
            });
            String[] encoding_array = getResources().getStringArray(R.array.encoding_array);
            server_parameter = "changing_server";
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(server_parameter);
            EditText server_text = view.findViewById(R.id.server_text);
            server_text.setText(current_value);
            server_parameter = "changing_port";
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(server_parameter);
            EditText port_numb = view.findViewById(R.id.port_numb);
            port_numb.setText(current_value);
            ArrayAdapter<?> encoding_adapter =
                    ArrayAdapter.createFromResource(view.getContext(), R.array.encoding_array,
                            android.R.layout.simple_spinner_item);
            encoding_spinner.setAdapter(encoding_adapter);
            server_parameter = "changing_encoding";
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(server_parameter);
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
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(server_parameter);
            CheckBox hide_ip_cb = view.findViewById(R.id.hide_ip_checkbox);
            if(current_value.contains("Disabled")) {
                hide_ip_cb.setChecked(false);
            } else {
                hide_ip_cb.setChecked(true);
            }
        };
        return builder.create();
    }
}
