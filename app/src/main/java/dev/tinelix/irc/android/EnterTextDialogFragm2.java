package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.File;

public class EnterTextDialogFragm2 extends DialogFragment {

    public String current_parameter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        current_parameter = ((ProfileSettingsActivity) getActivity()).getCurrentParameter();
        String current_value;
        current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "changing_profile_name") {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.enter_text_activity, null);
            builder.setView(view);
            builder.setTitle(R.string.enter_the_pfn_title);
            builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText profile_name = view.findViewById(R.id.profile_name_text);
                        ((ProfileSettingsActivity) getActivity()).onChangingValues(current_parameter, profile_name.getText().toString());
                    }
            });
            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
            });
        } else if(current_parameter == "changing_auth_method") {
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            String[] auth_methods = getResources().getStringArray(R.array.auth_method);
            builder.setTitle(R.string.auth_method);
            if(current_value == "Disabled") {
                builder.setSingleChoiceItems(auth_methods, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String value;
                            if(item == 0) {
                                value = "Disabled";
                            } else {
                                value = "NickServ";
                            }
                            ((ProfileSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                        }
                });
            } else if(current_value == "NickServ") {
                builder.setSingleChoiceItems(auth_methods, 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                String value;
                                if(item == 0) {
                                    value = "Disabled";
                                } else {
                                    value = "NickServ";
                                }
                                ((ProfileSettingsActivity) getActivity()).onChangingValues(current_parameter, value);
                            }
                });
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
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
}
