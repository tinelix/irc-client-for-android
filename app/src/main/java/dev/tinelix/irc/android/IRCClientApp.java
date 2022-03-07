package dev.tinelix.irc.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class IRCClientApp extends Application {
    ArrayList<Profile> profilesList = new ArrayList<Profile>();
    @Override
    public void onCreate() {
        String package_name = getApplicationContext().getPackageName();
        String profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        String file_extension;
        Context context = getApplicationContext();
        for (int i = 0; i < prefs_files.length; i++) {
            if(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)).startsWith(getApplicationInfo().packageName + "_preferences"))
            {
            } else {
                SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                if (file_extension.contains(".xml") && file_extension.length() == 4) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("connected", false);
                    editor.commit();
                }
            }
        }
        SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = global_prefs.edit();
        editor.putBoolean("connected", false);
        editor.putBoolean("theme_requires_restart", false);
        editor.putBoolean("language_requires_restart", false);
        editor.commit();
        super.onCreate();
    }
}
