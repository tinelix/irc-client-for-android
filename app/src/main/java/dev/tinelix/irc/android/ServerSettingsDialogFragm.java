package dev.tinelix.irc.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ServerSettingsDialogFragm extends DialogFragment {

    public String current_parameter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        current_parameter = ((ProfileSettingsActivity) getActivity()).getCurrentParameter();
        String current_value;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "setting_server") {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.enter_server_activity, null);
            builder.setView(view);
            builder.setTitle(R.string.server_settings);
            builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText server_name = view.findViewById(R.id.server_text);
                        EditText port_number = view.findViewById(R.id.port_numb);
                       ((ProfileSettingsActivity) getActivity()).onSettingServer(server_name.getText().toString(), port_number.getText().toString());
                    }
            });
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
            });
        };
        return builder.create();
    }
}
