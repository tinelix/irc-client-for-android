package dev.tinelix.android.irc.ui;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.tinelix.android.irc.ui.core.activities.MainActivity;

public class FragmentNavigator {
    private final Context ctx;

    public FragmentNavigator(Context ctx) {
        this.ctx = ctx;
    }

    public void navigateTo(String where) {
        if(ctx instanceof Activity) {
            if (ctx instanceof MainActivity activity) {
                FragmentManager fm = activity.getSupportFragmentManager();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.hide(activity.selectedFragment);
                if(where.equals("profiles")) {
                    activity.showFab(true);
                    ft.show(activity.profilesFragment);
                    activity.selectedFragment = activity.profilesFragment;
                } else if(where.equals("profile_editor")) {
                    activity.showFab(false);
                    activity.profileEditorFragment.setArguments(activity.newProfile);
                    activity.profileEditorFragment.checkArguments();
                    ft.show(activity.profileEditorFragment);
                    activity.selectedFragment = activity.profileEditorFragment;
                }
                ft.commit();
            }
        }
    }
}
