package dev.tinelix.android.irc.ui.core.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import dev.tinelix.android.irc.R;

public class ProfileEditorFragment extends PreferenceFragmentCompat {
    private SharedPreferences global_prefs;
    private SharedPreferences profile_prefs;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.profile_preferences);
        global_prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if(getArguments() != null && getArguments().containsKey("profile_name")) {
            String profile_name = getArguments().getString("profile_name");
            profile_prefs = requireContext().getSharedPreferences(profile_name, 0);
            Preference profile_name_pref = findPreference("profile_name");
            Objects.requireNonNull(profile_name_pref).setSummary(profile_name);
        }
    }

    public void checkArguments() {
        if(getArguments() != null && getArguments().containsKey("profile_name")) {
            String profile_name = getArguments().getString("profile_name");
            profile_prefs = requireContext().getSharedPreferences(profile_name, 0);
            Preference profile_name_pref = findPreference("profile_name");
            Objects.requireNonNull(profile_name_pref).setSummary(profile_name);
        }
    }
}
