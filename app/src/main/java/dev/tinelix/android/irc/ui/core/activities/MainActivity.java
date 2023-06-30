package dev.tinelix.android.irc.ui.core.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dev.tinelix.android.irc.R;
import dev.tinelix.android.irc.ui.FragmentNavigator;
import dev.tinelix.android.irc.ui.core.fragments.ProfileEditorFragment;
import dev.tinelix.android.irc.ui.core.fragments.ProfilesFragment;

public class MainActivity extends AppCompatActivity {
    public Fragment selectedFragment;
    public ProfilesFragment profilesFragment;
    public ProfileEditorFragment profileEditorFragment;
    private FragmentNavigator fn;

    public Bundle newProfile;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchIRCProfiles();
        setUiListeners();
        createFragments();
    }

    private void createFragments() {
        fn = new FragmentNavigator(this);
        profilesFragment = new ProfilesFragment();
        profileEditorFragment = new ProfileEditorFragment();
        FragmentManager fm = getSupportFragmentManager();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, profilesFragment, "profiles");
        ft.add(R.id.fragment, profileEditorFragment, "profile_editor");
        ft.hide(profilesFragment);
        ft.hide(profileEditorFragment);
        ft.show(profilesFragment);
        selectedFragment = profilesFragment;
        ft.commit();
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
        View edit_text = View.inflate(this, R.layout.dialog_edit_text, null);
        TextInputEditText editText = edit_text.findViewById(R.id.input_edit);
        builder.setView(edit_text);
        builder.setNegativeButton(getResources().getString(android.R.string.cancel), null);
        builder.setPositiveButton(getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newProfile = new Bundle();
                        newProfile.putString("profile_name",
                                Objects.requireNonNull(editText.getText()).toString());
                        fn.navigateTo("profile_editor");
                    }
        });
        ((TextInputLayout) edit_text.findViewById(R.id.input_layout)).setHint(getResources().getString(R.string.profile_name));
        builder.show();
    }

    private void searchIRCProfiles() {
    }

    public void ircConnect(int position) {
    }

    public void showFab(boolean value) {
        if(value) {
            findViewById(R.id.fab).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.fab).setVisibility(View.GONE);
        }
    }
}
