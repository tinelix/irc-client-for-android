package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

@SuppressLint("NewApi")
public class EnterTextDialogFragm extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.enter_text_activity, null);
        builder.setView(view);
        builder.setTitle(R.string.enter_the_pfn_title);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText profile_name = view.findViewById(R.id.profile_name_text);
                ((ConnectionManagerActivity) getActivity()).profileNameOkClicked(profile_name.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        return builder.create();
    }
}
