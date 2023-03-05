package dev.tinelix.android.irc.ui.core.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dev.tinelix.android.irc.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        searchIRCProfiles();
        setUiListeners();
    }

    private void setUiListeners() {
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfile();
            }
        });
    }

    private void createProfile() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View edit_text = View.inflate(this, R.layout.edit_text_dlg, null);
        builder.setView(edit_text);
        builder.setNegativeButton(getResources().getString(android.R.string.cancel), null);
        builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.not_implemented_yet), Toast.LENGTH_LONG).show();
            }
        });
        ((TextInputLayout) edit_text.findViewById(R.id.input_layout)).setHint(getResources().getString(R.string.profile_name));
        builder.show();
    }

    private void searchIRCProfiles() {
    }

    public void ircConnect(int position) {
    }
}
