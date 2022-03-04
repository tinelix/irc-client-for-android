package dev.tinelix.irc.android;

import android.app.Activity;
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
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

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
    public int sended_bytes_count;
    public int received_bytes_count;
    public String messageAuthor;
    public String messageBody;
    public boolean isMentioned;
    public String sendingMsgText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        socks_msg_text = findViewById(R.id.sock_msg_text);
        socks_msg_text.setKeyListener(null);
        final EditText output_msg_text = findViewById(R.id.output_msg_text);
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
        if (timer != null) {
            timer.cancel();
        }

        updateUITask = new UpdateUITask();
        sendingMsgText = new String();

        final Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(profile_name, 0);
        server = prefs.getString("server", "");
        port = prefs.getInt("port", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setSubtitle(server + ":" + port);
        }
        nicknames = prefs.getString("nicknames", "");
        auth_method = prefs.getString("auth_method", "");
        password = prefs.getString("password", "");
        hostname = prefs.getString("hostname", "");
        realname = prefs.getString("realname", "");
        encoding = prefs.getString("encoding", "");
        hide_ip = prefs.getString("hide_ip", "");
        if(hostname.length() <= 2) {
            hostname = nicknames.split(", ")[0];
        }
        if(hostname.length() <= 2) {
            realname = "Member";
        }
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
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(this);
        } else {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.IRCClient));
        }
        builder.setTitle(R.string.quit_session_title);
        builder.setMessage(R.string.quit_session_msg);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                finish();
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
            Button dialogButton;
            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.orange));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_menu, menu);
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
        } else if(id == R.id.disconnect_item) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutApplication() {
        Intent intent = new Intent(this, AboutApplicationActivity.class);
        startActivity(intent);
    }

    private void showStatisticsDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DialogFragment statsDialogFragm = new StatisticsFragm();
            statsDialogFragm.show(getFragmentManager(), "stats_dialog");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.IRCClient));
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.statistics_activity, null);
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

            Button dialogButton;
            dialogButton = statisticsDlg.getButton(DialogInterface.BUTTON_POSITIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.orange));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = statisticsDlg.getButton(DialogInterface.BUTTON_NEGATIVE);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

            dialogButton = statisticsDlg.getButton(DialogInterface.BUTTON_NEUTRAL);

            if(dialogButton != null) {
                dialogButton.setBackgroundColor(getResources().getColor(R.color.title_v11_full_transparent));
                dialogButton.setTextColor(getResources().getColor(R.color.white));
                dialogButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }
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
                Log.d("Client", "Getting IP address from " + server + ":" + port + "...");
                InetAddress serverAddr = InetAddress.getByName(server);
                SocketAddress socketAddress = new InetSocketAddress(serverAddr, port);
                Log.d("Client", "Connecting to " + server + ":" + port + "...");
                socket.connect(socketAddress);
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
            } catch (IllegalArgumentException iaEx) {
                Log.e("Socket", "IllegalArgumentException");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            } catch (IOException ioEx) {
                Log.e("Socket", "IOException");
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
                }
                if(sendingMsgText.length() > 0) {
                    try {
                        socket.getOutputStream().write((sendingMsgText).getBytes(encoding));
                        socket.getOutputStream().flush();
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
                    if(state == "getting_data") {
                        if(socket_data_string.length() > 0) {
                            socks_msg_text.setText(socks_msg_text.getText() + socket_data_string);
                            socks_msg_text.setSelection(socks_msg_text.getText().length());
                            socket_data_string = "";
                        }
                    } else if(state == "getting_data_with_mention") {
                        if(socket_data_string.length() > 0) {
                            socks_msg_text.setText(socks_msg_text.getText() + socket_data_string);
                            socks_msg_text.setSelection(socks_msg_text.getText().length());
                            socket_data_string = "";
                            Context context = getApplicationContext();
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
                                Toast.makeText(context, getString(R.string.mention_notification_title, messageAuthor) + ":" + messageBody, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else if(state == "disconnected") {
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    } else if(state == "connection_lost") {
                        Toast.makeText(getApplicationContext(), R.string.connection_lost_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    }else if(state == "timeout") {
                        Toast.makeText(getApplicationContext(), R.string.connection_timeout_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    } else if(state == "no_connection") {
                        Toast.makeText(getApplicationContext(), R.string.no_connection_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    } else if(state == "sending_message") {
                        if (socket != null) {
                            EditText output_msg_text = findViewById(R.id.output_msg_text);
                            outputMsgArray = new LinkedList<String>(Arrays.asList(output_msg_text.getText().toString().split(" ")));
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
                            Log.i("Client", "Message: [" + sendingMsgText + "]");
                            state = "finishing_sending_message";
                        } else {
                            Log.e("Socket", "Socket not created");
                        }
                    }
                }
            });
        }
    }
}
