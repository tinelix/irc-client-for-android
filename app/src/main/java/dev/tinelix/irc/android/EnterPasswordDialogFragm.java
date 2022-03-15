package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

@SuppressLint("NewApi")
public class EnterPasswordDialogFragm extends DialogFragment {

    public String current_parameter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        current_parameter = ((ProfileSettingsActivity) getActivity()).getCurrentParameter();
        String current_value;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "setting_password") {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.enter_password_activity, null);
            builder.setView(view);
            builder.setTitle(R.string.enter_the_password_title);
            builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText profile_name = view.findViewById(R.id.password_text);
                        ((ProfileSettingsActivity) getActivity()).onChangingValues(current_parameter, profile_name.getText().toString());
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
