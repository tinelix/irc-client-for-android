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

    public String version = "0.4.1 Beta";
    public String build_date = "2022-05-07";

    @Override
    public void onCreate() {
        String package_name = getApplicationContext().getPackageName();
        String profile_path = "/data/data/" + package_name + "/shared_prefs";
        File prefs_directory = new File(profile_path);
        File[] prefs_files = prefs_directory.listFiles();
        SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = global_prefs.edit();
        if(prefs_files != null) {
            String file_extension;
            Context context = getApplicationContext();
            for (int i = 0; i < prefs_files.length; i++) {
                if (prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)).startsWith(getApplicationInfo().packageName + "_preferences")) {
                } else {
                    SharedPreferences prefs = context.getSharedPreferences(prefs_files[i].getName().substring(0, (int) (prefs_files[i].getName().length() - 4)), 0);
                    file_extension = prefs_files[i].getName().substring((int) (prefs_files[i].getName().length() - 4));
                    if (file_extension.contains(".xml") && file_extension.length() == 4) {
                        editor = prefs.edit();
                        editor.putBoolean("connected", false);
                        editor.commit();
                    }
                }
            }
        }
        editor = global_prefs.edit();
        editor.putBoolean("connected", false);
        editor.putBoolean("theme_requires_restart", false);
        editor.putBoolean("language_requires_restart", false);
        if(global_prefs.contains("theme") == false) {
            editor.putString("theme", "Dark");
        }
        if(global_prefs.contains("language") == false) {
            editor.putString("language", "OS dependent");
        }
        if(global_prefs.contains("show_msg_timestamps") == false) {
            editor.putBoolean("show_msg_timestamps", false);
        }
        editor.commit();
        super.onCreate();
    }
}
