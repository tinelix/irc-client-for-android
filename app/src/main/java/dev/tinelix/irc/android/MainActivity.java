package dev.tinelix.irc.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTitle(R.string.app_name);
        setCustomTheme(global_prefs);
        setContentView(R.layout.activity_main);
        setColorStyle(global_prefs);
        String[] mainMenu = getResources().getStringArray(R.array.main_menu_array);
        ListView mainMenuList = (ListView) findViewById(R.id.mainmenu);

        ArrayAdapter<String> mainMenuAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mainMenu);

        mainMenuList.setAdapter(mainMenuAdapter);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ImageButton menu_button = findViewById(R.id.menu_button);
            menu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionsMenu();
                }
            });
        }

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    showConnectionManager();
                } else if(i == 1) {
                    showMainSettings();
                } else if(i == 2) {
                    showAboutApplication();
                } else if(i == 3) {
                    finish();
                    System.exit(0);
                }
            }
        });
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ImageButton menu_button = findViewById(R.id.menu_button);
            menu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionsMenu();
                }
            });
        }

    }

    private void showMainSettings() {
        Intent intent = new Intent(this, MainSettingsActivity.class);
        startActivity(intent);
    }

    private void showAboutApplication() {
        Intent intent = new Intent(this, AboutApplicationActivity.class);
        startActivity(intent);
    }

    private void showConnectionManager() {
        Intent intent = new Intent(this, ConnectionManagerActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_application_item) {
            showAboutApplication();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setColorStyle(SharedPreferences global_prefs) {
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    ListView main_menu = findViewById(R.id.mainmenu);
                    main_menu.setBackgroundColor(getResources().getColor(R.color.white));
                    main_menu.setCacheColorHint(getResources().getColor(R.color.white));
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ListView main_menu = findViewById(R.id.mainmenu);
                    main_menu.setBackgroundColor(getResources().getColor(R.color.black));
                    main_menu.setCacheColorHint(getResources().getColor(R.color.black));
                }
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ListView main_menu = findViewById(R.id.mainmenu);
                    main_menu.setBackgroundColor(getResources().getColor(R.color.black));
                    main_menu.setCacheColorHint(getResources().getColor(R.color.black));
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    ListView main_menu = findViewById(R.id.mainmenu);
                    main_menu.setBackgroundColor(getResources().getColor(R.color.white));
                    main_menu.setCacheColorHint(getResources().getColor(R.color.white));
                }
            }
        }
    }

    private void setCustomTheme(SharedPreferences global_prefs) {
        if(global_prefs.getString("language", "OS dependent").contains("Russian")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            }
        } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            }
        }
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient_Light);
            } else {
                setTheme(R.style.IRCClient);
            }
        } else {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient);
            } else {
                setTheme(R.style.IRCClient_Light);
            }
        }
    }

}
