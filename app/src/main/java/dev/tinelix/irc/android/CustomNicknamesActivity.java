package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CustomNicknamesActivity extends Activity {

    List<String> nicknamesArray = new ArrayList<String>();
    String nicknamesString;
    String old_profile_name;
    public String current_parameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setCustomTheme(global_prefs);
        setContentView(R.layout.custom_nicknames_activity);
        setColorStyle(global_prefs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        current_parameter = "creating_nickname";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                old_profile_name = null;
            } else {
                old_profile_name = extras.getString("profile_name");
            }
        } else {
            old_profile_name = (String) savedInstanceState.getSerializable("profile_name");
        };
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
        nicknamesArray = new LinkedList<String>(Arrays.asList(prefs.getString("nicknames", "").split(", ")));
        if(nicknamesArray.size() == 1 && nicknamesArray.get(0).length() == 0) {
            nicknamesArray.remove(0);
        }
        ListView nicknamesList = findViewById(R.id.nicknames_list);
        ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, nicknamesArray);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            LinearLayout add_item_ll = findViewById(R.id.create_item_layout2);
            add_item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEnterTextDialog();
                }
            });
        }

        nicknamesList.setAdapter(nicknamesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nicknames_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.add_nickname_item) {
            showEnterTextDialog();
        } else if (id == R.id.clear_nicknames_item) {
            nicknamesArray.clear();
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nicknames", "");
            editor.commit();
            ListView nicknamesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, nicknamesArray);
            nicknamesList.setAdapter(nicknamesAdapter);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreatingNicknames(String parameter, String value) {
        if(parameter == "creating_nickname") {
            StringBuilder nicknames_sb = new StringBuilder();
            if(value.length() > 0) {

                nicknamesArray.add(value);
            };
            for (int i = 0; i < nicknamesArray.size(); i++) {
                if(i < nicknamesArray.size() - 1 && nicknamesArray.get(i).length() > 0) {
                    nicknames_sb.append(nicknamesArray.get(i)).append(", ");
                } else if(nicknamesArray.get(i).length() > 0) {
                    nicknames_sb.append(nicknamesArray.get(i));
                }
            };
            ListView nicknamesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, nicknamesArray);
            nicknamesList.setAdapter(nicknamesAdapter);
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nicknames", nicknames_sb.toString());
            editor.commit();
        }
    }

    public String getCurrentParameter() {
        return current_parameter;
    }

    public boolean showEnterTextDialog() {
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DialogFragment enterTextDialogFragm = new EnterTextDialogFragm3();
            enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
        } else {
            AlertDialog.Builder dialogBuilder;
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CustomNicknamesActivity.this, R.style.IRCClient_Light));
                } else {
                    dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CustomNicknamesActivity.this, R.style.IRCClient));
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CustomNicknamesActivity.this, R.style.IRCClient));
                } else {
                    dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CustomNicknamesActivity.this, R.style.IRCClient_Light));
                }
            }
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.enter_text_activity, null);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dialogView.setMinimumWidth(metrics.widthPixels);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText profile_name = dialogView.findViewById(R.id.profile_name_text);
                    onCreatingNicknames(current_parameter, profile_name.getText().toString());
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            TextView textAreaTitle = dialogView.findViewById(R.id.profile_name_label);
            textAreaTitle.setText(R.string.your_nickname);
            TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            dialog_title.setText(getString(R.string.enter_the_nick_title));
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
            alertDialog.show();
            Button dialogButton;
            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

            customizeDialogStyle(dialogButton, global_prefs, alertDialog);
        }
        return false;
    };

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
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.title_v11_transparent));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
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
                } else {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
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
    private void customizeDialogStyle(Button dialogButton, SharedPreferences global_prefs, AlertDialog alertDialog) {
        if(global_prefs.getString("theme", "Dark").contains("Light")) {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            } else {
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        } else {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                    dialogButton.setTextColor(getResources().getColor(R.color.white));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            } else {
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.black));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (dialogButton != null) {
                    dialogButton.setBackgroundColor(getResources().getColor(R.color.white));
                    dialogButton.setTextColor(getResources().getColor(R.color.orange));
                    dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        }
    }
}
