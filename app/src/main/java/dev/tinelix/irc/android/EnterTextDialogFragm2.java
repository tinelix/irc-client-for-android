package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class EnterTextDialogFragm2 extends DialogFragment {

    public String current_parameter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        current_parameter = ((ProfileSettingsActivity) getActivity()).getCurrentParameter();
        String current_value;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(current_parameter == "changing_profile_name") {
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.enter_text_activity, null);
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
            EditText value_text = view.findViewById(R.id.profile_name_text);
            value_text.setText(current_value);
        } else if(current_parameter == "changing_auth_method") {
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            String[] auth_methods = getResources().getStringArray(R.array.auth_method);
            builder.setTitle(R.string.auth_method);
            if(current_value.contains("NickServ")) {
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
            } else {
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
        } else if(current_parameter == "changing_realname") {
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.enter_text_activity, null);
            builder.setView(view);
            builder.setTitle(R.string.enter_the_realname_title);
            TextView realname_label = view.findViewById(R.id.profile_name_label);
            realname_label.setText(R.string.realname);
            EditText realname_text = view.findViewById(R.id.profile_name_text);
            realname_text.setText(current_value);
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
        } else if(current_parameter == "changing_hostname") {
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.enter_text_activity, null);
            builder.setView(view);
            builder.setTitle(R.string.enter_the_hostname_title);
            TextView realname_label = view.findViewById(R.id.profile_name_label);
            realname_label.setText(R.string.hostname);
            EditText realname_text = view.findViewById(R.id.profile_name_text);
            realname_text.setText(current_value);
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
        } else if(current_parameter == "changing_quitmsg") {
            current_value = ((ProfileSettingsActivity) getActivity()).getCurrentValue(current_parameter);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.enter_text_activity, null);
            builder.setView(view);
            builder.setTitle(R.string.enter_the_quiting_message);
            TextView quitmsgname_label = view.findViewById(R.id.profile_name_label);
            quitmsgname_label.setText(R.string.quit_message);
            EditText quitmsgname_text = view.findViewById(R.id.profile_name_text);
            quitmsgname_text.setText(current_value);
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
        }
        return builder.create();
    }
}
