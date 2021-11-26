package dev.tinelix.irc;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Dmitry on 21.11.2021.
 */

public class MainPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_settings);
    }
}
