package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class AboutApplicationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setCustomTheme(global_prefs);
        setContentView(R.layout.about_application_activity);
        setColorStyle(global_prefs);
        Button repoButton = findViewById(R.id.repo_button);
        Button websiteButton = findViewById(R.id.website_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        repoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/tinelix/irc-client-for-android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://tinelix.downmail.ru/web1/pages/tinelix/irc-client_utf8.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        if (global_prefs.getString("language", "OS dependent").contains("English")) {
            websiteButton.setVisibility(View.GONE);
        }
        TextView license_label = (TextView) findViewById(R.id.license_label);
        license_label.setMovementMethod(LinkMovementMethod.getInstance());
        TextView version_label = (TextView) findViewById(R.id.version_label);
        version_label.setText(getResources().getString(R.string.version_str, ((IRCClientApp) getApplicationContext()).version, ((IRCClientApp) getApplicationContext()).build_date));
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setColorStyle(SharedPreferences global_prefs) {
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.activity_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.black));
                    ((TextView) findViewById(R.id.license_label)).setTextColor(Color.DKGRAY);
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.activity_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.white));
                    ((TextView) findViewById(R.id.license_label)).setTextColor(Color.LTGRAY);
                }
            }  else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.black));
                } else {
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.white));
                }
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.activity_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.white));
                    ((TextView) findViewById(R.id.license_label)).setTextColor(Color.LTGRAY);
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.activity_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.black));
                    ((TextView) findViewById(R.id.license_label)).setTextColor(Color.DKGRAY);
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.white));
                } else {
                    TextView app_title2 = findViewById(R.id.app_title_label);
                    app_title2.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
    }

    private void setCustomTheme(SharedPreferences global_prefs) {
        if (global_prefs.getString("language", "OS dependent").contains("Russian")) {
            if (global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                        config.setLocale(locale);
                    } else {
                        config.locale = locale;
                    }
                    config.setLayoutDirection(locale);
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    getApplicationContext().createConfigurationContext(config);
                } else {
                    getApplicationContext().getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                }
            } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        config.setLocale(locale);
                    } else {
                        config.locale = locale;
                    }
                    config.setLayoutDirection(locale);
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    getApplicationContext().createConfigurationContext(config);
                } else {
                    getApplicationContext().getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                }
            }
        } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
            if (global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.setLocale(locale);
                } else {
                    config.locale = locale;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getApplicationContext().createConfigurationContext(config);
                } else {
                    getApplicationContext().getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                }
            } else {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.setLocale(locale);
                } else {
                    config.locale = locale;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.setLayoutDirection(locale);
                    getApplicationContext().createConfigurationContext(config);
                } else {
                    getApplicationContext().getResources().updateConfiguration(config,
                            getApplicationContext().getResources().getDisplayMetrics());
                }
            }
        }
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient_Light);
            } else {
                setTheme(R.style.IRCClient);
            }
        } else {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                setTheme(R.style.IRCClient);
            } else {
                setTheme(R.style.IRCClient_Light);
            }
        }
    }
}
