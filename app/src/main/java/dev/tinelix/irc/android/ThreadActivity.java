package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import static java.lang.Thread.sleep;

public class ThreadActivity extends Activity {

    public Socket socket;
    public boolean isConnected;
    public InputStream input;
    Handler updateConversationHandler;
    public String profile_name;
    public String server;
    public String nicknames;
    public String hostname;
    public String realname;
    public StringBuilder msg;
    public int received_bytes;
    public int sended_bytes;
    public List<String> channelsArray = new ArrayList<String>();
    public List<String> outputMsgArray = new ArrayList<String>();
    public byte[] socket_data_bytes;
    public int port;
    private EditText socks_msg_text;
    public EditText output_msg_text;
    public byte[] socket_data = new byte[1<<12];
    public String socket_data_string;
    private Timer timer;
    private UpdateUITask updateUITask;
    public String state;
    public String encoding;
    public String channel;
    public String password;
    public String auth_method;
    public String hide_ip;
    public String quit_msg;
    public int sended_bytes_count;
    public int received_bytes_count;
    public String messageAuthor;
    public String messageBody;
    public boolean isMentioned;
    public String sendingMsgText;
    public AlertDialog connectionDialog;
    public Date dt;
    public boolean autoscroll_needed;
    public Menu thread_menu;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setCustomTheme(global_prefs);
        setContentView(R.layout.thread_activity);
        setColorStyle(global_prefs);
        dt = new Date(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        autoscroll_needed = true;
        socks_msg_text = findViewById(R.id.sock_msg_text);
        socks_msg_text.setKeyListener(null);
        socks_msg_text.setLongClickable(true);
        socks_msg_text.setTypeface(Typeface.MONOSPACE);
        socks_msg_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                            autoscroll_needed = false;
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                MenuItem go_down = thread_menu.findItem(R.id.go_down_item);
                                go_down.setVisible(true);
                            } else {
                                Button go_down_btn = findViewById(R.id.go_down_button);
                                go_down_btn.setVisibility(View.VISIBLE);
                            }
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            socks_msg_text.setTextIsSelectable(true);
        }
        if(global_prefs.getInt("font_size", 0) >= 12) {
            socks_msg_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, global_prefs.getInt("font_size", 0));
        }
        output_msg_text = findViewById(R.id.output_msg_text);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                profile_name = null;
            } else {
                profile_name = extras.getString("profile_name");
            }
        } else {
            profile_name = (String) savedInstanceState.getSerializable("profile_name");
        };
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(profile_name, 0);
        if (timer != null) {
            timer.cancel();
        }
        if(profile_name == null) {
            socket = null;
            finish();
            return;
        }

        updateUITask = new UpdateUITask();
        sendingMsgText = new String();

        final Context context = getApplicationContext();
        server = prefs.getString("server", "");
        port = prefs.getInt("port", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setSubtitle(server + ":" + port);
        } else {
            Log.i("Client", "\r\nProfile Info:\r\n\r\nPROFILE NAME: [" + profile_name + "]\r\nSERVER: [" + server + "]\r\nPORT: " + port);
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            TextView app_summary = findViewById(R.id.app_summary_text);
            app_summary.setText(server + ":" + port);
            final Button go_down_btn = findViewById(R.id.go_down_button);
            go_down_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    autoscroll_needed = true;
                    socks_msg_text.setSelection(socks_msg_text.getText().length());
                    go_down_btn.setVisibility(View.GONE);
                }
            });
        }
        nicknames = prefs.getString("nicknames", "");
        auth_method = prefs.getString("auth_method", "");
        password = prefs.getString("password", "");
        hostname = prefs.getString("hostname", "");
        realname = prefs.getString("realname", "");
        encoding = prefs.getString("encoding", "");
        hide_ip = prefs.getString("hide_ip", "");
        quit_msg = prefs.getString("quit_message", "");
        if(hostname.length() <= 2) {
            hostname = nicknames.split(", ")[0];
        }
        if(realname.length() <= 2) {
            realname = "Member";
        }

        TabHost tabHost = (TabHost) findViewById(R.id.thread_tabs_host);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("thread");

        tabSpec.setContent(R.id.thread_tab);
        tabSpec.setIndicator(getResources().getString(R.string.thread_category));
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            View view = tabHost.getTabWidget().getChildAt(0);
            view.setBackgroundResource(R.drawable.tabwidget);
            if (view != null) {
                Log.d("Client", "TabWidget View");
                tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = (int) (30 * getResources().getDisplayMetrics().density);
                View tabImage = view.findViewById(android.R.id.icon);
                if (tabImage != null) {
                    tabImage.setVisibility(View.GONE);
                    Log.d("Client", "TabIcon View");
                } else {
                    Log.e("Client", "TabImage View is null");
                }
                TextView tabTitle = (TextView) view.findViewById(android.R.id.title);
                if (tabTitle != null) {
                    Log.d("Client", "TabTitle View");
                    tabTitle.setGravity(Gravity.CENTER);
                    ViewGroup parent = (ViewGroup) tabTitle.getParent();
                    parent.removeView(tabTitle);
                    parent.addView(tabTitle);
                    ViewGroup.LayoutParams params = tabTitle.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    Log.e("Client", "TabTitle View is null");
                }
            } else {
                Log.e("Client", "TabWidget View is null");
            }
        }

        tabSpec = tabHost.newTabSpec("thread");

        new Thread(new ircThread()).start();
        ImageButton send_btn = findViewById(R.id.send_button);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (output_msg_text.getText().toString().length() > 0) {
                    EditText output_msg_text = findViewById(R.id.output_msg_text);
                    Thread send_msg_thread = new Thread(new SendSocketMsg());
                    new Thread(new SendSocketMsg()).start();
                    socks_msg_text.setText(socks_msg_text.getText() + "You: " + output_msg_text.getText() + "\r\n");
                    socks_msg_text.setSelection(socks_msg_text.getText().length());
                } else {
                    Toast emptyMessageAttempting = Toast.makeText(context, getString(R.string.empty_message_sending_attempt), Toast.LENGTH_SHORT);
                    emptyMessageAttempting.show();
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
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(this);
        } else {
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient_Light));
                } else {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient));
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient));
                } else {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient_Light));
                }
            }
        }
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.progress_activity, null);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dialogView.setMinimumWidth(metrics.widthPixels);
        }
        TextView progressText = dialogView.findViewById(R.id.progress_text);
        progressText.setText(getString(R.string.connection_progress, server + ":" + port));
        builder.setView(dialogView);
        connectionDialog = builder.create();
        connectionDialog.setCancelable(false);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            connectionDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        connectionDialog.show();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            Button dialogButton;
            dialogButton = connectionDialog.getButton(DialogInterface.BUTTON_POSITIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.orange));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = connectionDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = connectionDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }
    }

    private void setColorStyle(SharedPreferences global_prefs) {
        if (global_prefs.getString("theme", "Light").contains("Light")) {
            if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                EditText socks_msg_text = findViewById(R.id.sock_msg_text);
                socks_msg_text.setTextColor(getResources().getColor(R.color.black));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    LinearLayout app_title_bar = findViewById(R.id.app_title_bar);
                    app_title_bar.setBackgroundColor(getResources().getColor(R.color.white_75));
                    TextView app_title = findViewById(R.id.app_title_label);
                    app_title.setBackgroundColor(getResources().getColor(R.color.white_75));
                    ImageView app_icon = findViewById(R.id.app_icon_view);
                    app_icon.setBackgroundColor(getResources().getColor(R.color.white_75));
                    LinearLayout activity_ll = findViewById(R.id.activity_ll);
                    activity_ll.setBackgroundColor(getResources().getColor(R.color.white));
                    socks_msg_text.setBackgroundColor(getResources().getColor(R.color.white_75));
                    EditText sended_msg_area = findViewById(R.id.output_msg_text);
                    sended_msg_area.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(global_prefs.getInt("font_size", 0) >= 12) {
            socks_msg_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, global_prefs.getInt("font_size", 0));
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder;
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.quit_session_title));
            builder.setMessage(getResources().getString(R.string.quit_session_msg));
        } else {
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient_Light));
                    builder.setTitle(Html.fromHtml("<font color='#000000'>" + getResources().getString(R.string.quit_session_title) + "</b>"));
                    builder.setMessage(Html.fromHtml("<font color='#000000'>" + getResources().getString(R.string.quit_session_msg) + "</b>"));
                } else {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient));
                    builder.setTitle(Html.fromHtml("<font color='#ffffff'><b>" + getResources().getString(R.string.quit_session_title) + "</b>"));
                    builder.setMessage(Html.fromHtml("<font color='#ffffff'><b>" + getResources().getString(R.string.quit_session_msg) + "</b>"));
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient));
                    builder.setTitle(Html.fromHtml("<font color='#ffffff'><b>" + getResources().getString(R.string.quit_session_title) + "</b>"));
                    builder.setMessage(Html.fromHtml("<font color='#ffffff'><b>" + getResources().getString(R.string.quit_session_msg) + "</b>"));
                } else {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient_Light));
                    builder.setTitle(Html.fromHtml("<font color='#000000'><b>" + getResources().getString(R.string.quit_session_title) + "</b>"));
                    builder.setMessage(Html.fromHtml("<font color='#000000'><b>" + getResources().getString(R.string.quit_session_msg) + "</b>"));
                }
            }
        }
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendQuitMessage();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            Button dialogButton = null;
            customizeDialogStyle(dialogButton, global_prefs, alertDialog);
        }
    }

    private void sendQuitMessage() {
        output_msg_text.setText("/quit");
        sendingMsgText = "/quit";
        state = "sending_message";
        new Thread(new SendSocketMsg()).start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_menu, menu);
        thread_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statistics_item) {
            showStatisticsDialog();
            return true;
        } else if (id == R.id.about_application_item) {
            showAboutApplication();
        } else if (id == R.id.connection_manager_item) {
            showConnectionManager();
        } else if (id == R.id.settings_item) {
            showMainSettings();
        } else if(id == R.id.disconnect_item) {
            onBackPressed();
        } else if(id == R.id.go_down_item) {
            socks_msg_text.setSelection(socks_msg_text.getText().length());
            autoscroll_needed = true;
            MenuItem go_down = thread_menu.findItem(R.id.go_down_item);
            go_down.setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConnectionManager() {
        Intent intent = new Intent(ThreadActivity.this, ConnectionManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showAboutApplication() {
        Intent intent = new Intent(ThreadActivity.this, AboutApplicationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showStatisticsDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DialogFragment statsDialogFragm = new StatisticsFragm();
            statsDialogFragm.show(getFragmentManager(), "stats_dialog");
        } else {
            final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            AlertDialog.Builder builder;
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient_Light));
                } else {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient));
                }
            } else {
                if(global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient));
                } else {
                    builder = new AlertDialog.Builder(new ContextThemeWrapper(ThreadActivity.this, R.style.IRCClient_Light));
                }
            }
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.statistics_activity, null);
            TextView session_label = dialogView.findViewById(R.id.session_label);
            if (global_prefs.getString("theme", "Dark").contains("Light")) {
                if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    session_label.setTextColor(getResources().getColor(R.color.black));
                } else {
                    session_label.setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                    session_label.setTextColor(getResources().getColor(R.color.white));
                } else {
                    session_label.setTextColor(getResources().getColor(R.color.black));
                }
            }
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dialogView.setMinimumWidth(metrics.widthPixels);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            sended_bytes = getSendedBytes();
            received_bytes = getReceivedBytes();
            int total_bytes = sended_bytes + received_bytes;
            TextView sended_bytes_label = dialogView.findViewById(R.id.sended_label2);
            TextView received_bytes_label = dialogView.findViewById(R.id.received_label2);
            TextView total_bytes_label = dialogView.findViewById(R.id.total_label2);
            TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            dialog_title.setText(getString(R.string.statistics_item));
            if (sended_bytes > 1073741824) {
                sended_bytes_label.setText(getString(R.string.gbytes_stats, String.format("%.2f", (float)(sended_bytes / 1073741824))));
            } else if(sended_bytes > 1048576) {
                sended_bytes_label.setText(getString(R.string.mbytes_stats, String.format("%.2f", (float)(sended_bytes / 1048576))));
            } else if(sended_bytes > 1024) {
                sended_bytes_label.setText(getString(R.string.kbytes_stats, String.format("%.2f", (float)(sended_bytes / 1024))));
            } else {
                sended_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(sended_bytes)));
            }
            if (received_bytes > 1073741824) {
                received_bytes_label.setText(getString(R.string.gbytes_stats, String.format("%.2f", (float)(received_bytes / 1073741824))));
            } else if(received_bytes > 1048576) {
                received_bytes_label.setText(getString(R.string.mbytes_stats, String.format("%.2f", (float)(received_bytes / 1048576))));
            } else if(received_bytes > 1024) {
                received_bytes_label.setText(getString(R.string.kbytes_stats, String.format("%.2f", (float)(received_bytes / 1024))));
            } else {
                received_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(received_bytes)));
            }
            if (total_bytes > 1073741824) {
                total_bytes_label.setText(getString(R.string.gbytes_stats, String.format("%.2f", (double)(total_bytes / 1073741824))));
            } else if(total_bytes > 1048576) {
                total_bytes_label.setText(getString(R.string.mbytes_stats, String.format("%.2f", (double)(total_bytes / 1048576))));
            } else if(total_bytes > 1024) {
                total_bytes_label.setText(getString(R.string.kbytes_stats, String.format("%.2f", (double)(total_bytes / 1024))));
            } else {
                total_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(total_bytes)));
            }
            AlertDialog statisticsDlg = builder.create();
            statisticsDlg.getWindow().setGravity(Gravity.BOTTOM);
            statisticsDlg.show();

            Button dialogButton = null;
            customizeDialogStyle(dialogButton, global_prefs, statisticsDlg);
        }
    }

    private void setCustomTheme(SharedPreferences global_prefs) {
        if(global_prefs.getString("language", "OS dependent").contains("Russian")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LocaleList localeList = new LocaleList(locale);
                    LocaleList.setDefault(localeList);
                    config.setLocales(localeList);
                    config.setLayoutDirection(locale);
                } else {
                    config.locale = locale;
                }
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LocaleList localeList = new LocaleList(locale);
                    LocaleList.setDefault(localeList);
                    config.setLocales(localeList);
                    config.setLayoutDirection(locale);
                } else {
                    config.locale = locale;
                }
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            }
        } else if (global_prefs.getString("language", "OS dependent").contains("English")) {
            if(global_prefs.getBoolean("language_requires_restart", false) == false) {
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LocaleList localeList = new LocaleList(locale);
                    LocaleList.setDefault(localeList);
                    config.setLocales(localeList);
                    config.setLayoutDirection(locale);
                } else {
                    config.locale = locale;
                }
                getApplicationContext().getResources().updateConfiguration(config,
                        getApplicationContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("ru");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LocaleList localeList = new LocaleList(locale);
                    LocaleList.setDefault(localeList);
                    config.setLocales(localeList);
                    config.setLayoutDirection(locale);
                } else {
                    config.locale = locale;
                }
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

    private void showMainSettings() {
        Intent intent = new Intent(ThreadActivity.this, MainSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if(profile_name != null) {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("connected", false);
            editor.commit();
            SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = global_prefs.edit();
            editor.putBoolean("connected", false);
            editor.commit();
        }
        super.onDestroy();
    }

    public int getSendedBytes() {
        return sended_bytes_count;
    }

    public int getReceivedBytes() {
        return received_bytes_count;
    }

    class ircThread implements Runnable {
        @Override
        public void run() {
            try {
                socket = new Socket();
                state = "connecting";
                Log.d("Client", "Getting IP address from " + server + ":" + port + "...");
                InetAddress serverAddr = InetAddress.getByName(server);
                SocketAddress socketAddress = new InetSocketAddress(serverAddr, port);
                Log.d("Client", "Connecting to " + server + ":" + port + "...");
                socket.connect(socketAddress, 30000);
                while(state == "connecting") {
                    if (socket.isConnected()) {
                        updateUITask.run();
                        sleep(50);
                    }
                }
                input = socket.getInputStream();
                socket.getOutputStream().write(("USER " + nicknames.split(", ")[0] + " " +
                        hostname + " " + nicknames.split(", ")[0] + " :" +
                        realname + "\r\n").getBytes(encoding));
                socket.getOutputStream().flush();
                sended_bytes_count += ("USER " + nicknames.split(", ")[0] + " " +
                        hostname + " " + nicknames.split(", ")[0] + " :" +
                        realname + "\r\n").getBytes(encoding).length;
                socket.getOutputStream().write(("NICK " + nicknames.split(", ")[0] + "\r\n").getBytes(encoding));
                socket.getOutputStream().flush();
                sended_bytes_count += ("NICK " + nicknames.split(", ")[0] + "\r\n").getBytes(encoding).length;
                if(password.length() > 0 && auth_method.startsWith("NickServ")) {
                    socket.getOutputStream().write(("NICKSERV identify " + password + "\r\n").getBytes(encoding));
                    socket.getOutputStream().flush();
                    sended_bytes_count += ("NICKSERV identify " + password + "\r\n").getBytes(encoding).length;
                }
                if(hide_ip.startsWith("Enabled")) {
                    socket.getOutputStream().write(("MODE " + nicknames.split(", ")[0] + " +x\r\n").getBytes(encoding));
                    socket.getOutputStream().flush();
                    sended_bytes_count += ("MODE " + nicknames.split(", ")[0] + " +x\r\n").getBytes(encoding).length;
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
                String response;
                messageAuthor = new String();
                messageBody = new String();
                messageBody = "";
                String nick = nicknames.split(", ")[0];
                IRCParser parser = new IRCParser();
                msg = new StringBuilder();
                while(socket.isConnected() == true) {
                    if(in.ready() == true) {
                        sleep(10);
                        response = in.readLine();
                        received_bytes_count += response.length();
                        if (response.startsWith("PING")) {
                            socket.getOutputStream().write(("PONG " + response.split(" ")[1]).getBytes(encoding));
                            sended_bytes_count += ("PONG " + response.split(" ")[1]).getBytes(encoding).length;
                        }
                        if(response != null) {
                            String parsedString = parser.parseString(response, true);
                            messageBody = parser.getMessageBody(response);
                            messageAuthor = parser.getMessageAuthor(response);
                            if(parsedString.length() > 0) {
                                msg.append(parsedString).append("\n");
                                socket_data_string = msg.toString();
                                msg.setLength(0);
                                if(messageBody.contains(nicknames.split(", ")[0])) {
                                    state = "getting_data_with_mention";
                                } else {
                                    state = "getting_data";
                                }
                                updateUITask.run();
                            };
                        }
                    };
                }
                socket.close();
                socket = null;
                state = "connection_lost";
                updateUITask.run();
            } catch (UnknownHostException uhEx) {
                Log.e("Socket", "UnknownHostException");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "no_connection";
                updateUITask.run();
            } catch(SocketTimeoutException timeoutEx) {
                Log.e("Socket", "SocketTimeoutException");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "timeout";
                updateUITask.run();
            } catch (IllegalBlockingModeException ibmEx) {
                Log.e("Socket", "IllegalBlockingModeException");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "no_connection";
                updateUITask.run();
            } catch (IllegalArgumentException iaEx) {
                Log.e("Socket", "IllegalArgumentException");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "no_connection";
                updateUITask.run();
            } catch (ConnectException Ex) {
                Log.e("Socket", "ConnectException");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "no_connection";
                updateUITask.run();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            } catch (Exception ex) {
                try {
                    if(socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "disconnected";
                updateUITask.run();
            }
        }
    }

    class SendSocketMsg implements Runnable {
        @Override
        public void run() {
            if (socket != null) {
                state = "sending_message";
                while(state == "sending_message") {
                    updateUITask.run();
                    try {
                        sleep(50);
                        state = "finishing_sending_message";
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(sendingMsgText.length() > 0) {
                    try {
                        socket.getOutputStream().write((sendingMsgText).getBytes(encoding));
                        socket.getOutputStream().flush();
                        Log.i("Client", "\r\nSended message!");
                        if(sendingMsgText.startsWith("QUIT")) {
                            socket.close();
                            socket = null;
                            finish();
                        }
                        state = "sended_message";
                        updateUITask.run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("Socket", "Socket not created");
            }
        }
    }

    class UpdateUITask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(state == "getting_data") {
                        if(socket_data_string.length() > 0) {
                            if(global_prefs.getBoolean("save_msg_history", false) == true) {
                                File directory;
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "Tinelix");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Tinelix", "IRC Client");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }
                                    directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Tinelix/IRC Client", "Messages Logs");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }
                                } else {
                                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Tinelix");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tinelix", "IRC Client");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }
                                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tinelix/IRC Client", "Messages Logs");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }
                                }

                                try {
                                    Log.d("App", "Attempting creating log file...");
                                    File file = new File(directory, "LOG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(dt) + ".log");
                                    if (!file.exists()) {
                                        file.createNewFile();
                                    }
                                    Log.d("App", "Log file created!");
                                    FileWriter writer = new FileWriter(file);
                                    writer.append(socks_msg_text.getText() + socket_data_string);
                                    writer.flush();
                                    writer.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            socks_msg_text.setText(socks_msg_text.getText() + socket_data_string);
                            if(autoscroll_needed == true) {
                                socks_msg_text.setSelection(socks_msg_text.getText().length());
                            }
                            socket_data_string = "";
                        }
                    } else if(state == "getting_data_with_mention") {
                        if(socket_data_string.length() > 0) {
                            socks_msg_text.setText(socks_msg_text.getText() + socket_data_string);
                            socks_msg_text.setSelection(socks_msg_text.getText().length());
                            socket_data_string = "";
                            Context context = getApplicationContext();
                            if(messageBody.length() > 0 && nicknames.split(", ")[0].length() > 0) {
                                if(global_prefs.getBoolean("save_msg_history", false) == true) {
                                    File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Tinelix");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tinelix", "IRC Client");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }
                                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tinelix/IRC Client", "App Logs");
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                    }

                                    try {
                                        Log.d("App", "Attempting creating log file...");
                                        File file = new File(directory, "LOG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(dt) + ".log");
                                        if (!file.exists()) {
                                            file.createNewFile();
                                        }
                                        Log.d("App", "Log file created!");
                                        FileWriter writer = new FileWriter(file);
                                        writer.append(socks_msg_text.getText() + socket_data_string);
                                        writer.flush();
                                        writer.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    Notification.Builder notificationBuilder = null;
                                    notificationBuilder = new Notification.Builder(context);
                                    notificationBuilder
                                            .setSmallIcon(R.drawable.ic_notification_icon)
                                            .setWhen(System.currentTimeMillis())
                                            .setContentTitle(getString(R.string.mention_notification_title, messageAuthor))
                                            .setContentText(messageBody);
                                    notificationManager.notify(1, notificationBuilder.build());
                                } else {
                                    Notification notification = new Notification(R.drawable.ic_notification_icon, getString(R.string.mention_notification_title, messageAuthor), System.currentTimeMillis());
                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_activity);
                                    contentView.setTextViewText(R.id.notification_title, getString(R.string.mention_notification_title, messageAuthor));
                                    contentView.setTextViewText(R.id.notification_text, messageBody);
                                    DisplayMetrics metrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                    notification.contentView = contentView;
                                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                    List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                                    ActivityManager.RunningTaskInfo task = tasks.get(0); // Should be my task
                                    Intent notificationIntent = new Intent(context, ThreadActivity.class);
                                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    notificationIntent.setAction(Intent.ACTION_MAIN);
                                    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
                                    notification.contentIntent = contentIntent;
                                    notificationManager.notify(R.layout.notification_activity, notification);
                                }
                            }
                        }
                    } else if(state == "disconnected") {
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        connectionDialog.cancel();
                        finish();
                    } else if(state == "connection_lost") {
                        Toast.makeText(getApplicationContext(), R.string.connection_lost_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        connectionDialog.cancel();
                        finish();
                    } else if(state == "timeout") {
                        Toast.makeText(getApplicationContext(), R.string.connection_timeout_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        connectionDialog.cancel();
                        finish();
                    } else if(state == "no_connection") {
                        Toast.makeText(getApplicationContext(), R.string.no_connection_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        connectionDialog.cancel();
                        finish();
                    } else if(state == "sending_message") {
                        if (socket != null) {
                            EditText output_msg_text = findViewById(R.id.output_msg_text);
                            outputMsgArray = new LinkedList<String>(Arrays.asList(output_msg_text.getText().toString().split(" ")));
                            Log.i("Client", "\r\nSending message...\r\n\r\nMESSAGE: " + output_msg_text.getText().toString());
                            if (outputMsgArray.get(0).startsWith("/join") && outputMsgArray.size() > 1 && outputMsgArray.get(1).startsWith("#")) {
                                try {
                                    channelsArray.add(outputMsgArray.get(1));
                                    sendingMsgText = ("JOIN " + outputMsgArray.get(1) + "\r\n");
                                    sended_bytes_count += ("JOIN " + outputMsgArray.get(1) + "\r\n").getBytes(encoding).length;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (outputMsgArray.get(0).startsWith("/join") && outputMsgArray.size() > 1 && !outputMsgArray.get(1).startsWith("#")) {
                                try {
                                    channelsArray.add("#" + outputMsgArray.get(1));
                                    sendingMsgText = ("JOIN #" + outputMsgArray.get(1) + "\r\n");
                                    sended_bytes_count += ("JOIN #" + outputMsgArray.get(1) + "\r\n").getBytes(encoding).length;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (outputMsgArray.get(0).startsWith("/mode") && outputMsgArray.size() > 1) {
                                try {
                                    StringBuilder message_sb = new StringBuilder();
                                    for (int i = 1; i < outputMsgArray.size(); i++) {
                                        if (i < outputMsgArray.size() - 1 && outputMsgArray.get(i).length() > 0) {
                                            message_sb.append(outputMsgArray.get(i)).append(" ");
                                        } else if (outputMsgArray.get(i).length() > 0) {
                                            message_sb.append(outputMsgArray.get(i));
                                        }
                                    }
                                    sendingMsgText = ("MODE " + message_sb.toString() + "\r\n");
                                    sended_bytes_count += ("MODE " + message_sb.toString() + "\r\n").getBytes(encoding).length;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (outputMsgArray.get(0).startsWith("/nick") && outputMsgArray.size() == 1) {
                                try {
                                    sendingMsgText = ("NICK " + outputMsgArray.get(1) + "\r\n");
                                    sended_bytes_count += ("NICK " + outputMsgArray.get(1) + "\r\n").getBytes(encoding).length;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (outputMsgArray.get(0).startsWith("/quit")) {
                                try {
                                    sendingMsgText = ("QUIT :" + quit_msg + "\r\n");
                                    sended_bytes_count += ("QUIT :" + quit_msg + "\r\n").getBytes(encoding).length;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (outputMsgArray.get(0).startsWith("/")) {
                                try {
                                    StringBuilder message_sb = new StringBuilder();
                                    for (int i = 0; i < outputMsgArray.size(); i++) {
                                        if (i < outputMsgArray.size() - 1 && outputMsgArray.get(i).length() > 0) {
                                            message_sb.append(outputMsgArray.get(i)).append(" ");
                                        } else if (outputMsgArray.get(i).length() > 0) {
                                            message_sb.append(outputMsgArray.get(i));
                                        }
                                    }
                                    sendingMsgText = (message_sb.toString().substring(1) + "\r\n");
                                    sended_bytes_count += (message_sb.toString().substring(1) + "\r\n").getBytes(encoding).length;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if (channelsArray.size() > 0) {
                                        sendingMsgText = ("PRIVMSG " + channelsArray.get(channelsArray.size() - 1) + " :" + output_msg_text.getText().toString() + "\r\n");
                                        sended_bytes_count += ("PRIVMSG " + channelsArray.get(channelsArray.size() - 1) + " :" + output_msg_text.getText().toString() + "\r\n").getBytes(encoding).length;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            output_msg_text.setText("");
                        } else {
                            Log.e("Socket", "Socket not created");
                        }
                    } else if(state == "connecting") {
                        connectionDialog.cancel();
                        state = "connected";
                    }
                }
            });
        }
    }
}
