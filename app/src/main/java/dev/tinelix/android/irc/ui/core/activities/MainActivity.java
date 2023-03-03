package dev.tinelix.android.irc.ui.core.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import dev.tinelix.android.irc.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
}
