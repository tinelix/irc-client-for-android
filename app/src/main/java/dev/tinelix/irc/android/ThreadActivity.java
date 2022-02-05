package dev.tinelix.irc.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
    public List<String> channelsArray = new ArrayList<String>();
    public List<String> outputMsgArray = new ArrayList<String>();
    public byte[] socket_data_bytes;
    public int port;
    private EditText socks_msg_text;
    private EditText output_msg_text;
    public byte[] socket_data = new byte[1<<12];
    public String socket_data_string;
    private Timer timer;
    private UpdateUITask updateUITask;
    public String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_activity);
        getActionBar().setHomeButtonEnabled(true);
        socks_msg_text = findViewById(R.id.sock_msg_text);
        socks_msg_text.setKeyListener(null);
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
        if (timer != null) {
            timer.cancel();
        }

        updateUITask = new UpdateUITask();

        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(profile_name, 0);
        server = prefs.getString("server", "");
        port = prefs.getInt("port", 0);
        nicknames = prefs.getString("nicknames", "");
        hostname = prefs.getString("hostname", "");
        realname = prefs.getString("realname", "");
        if(hostname.length() <= 2) {
            hostname = nicknames.split(", ")[0];
        }
        if(hostname.length() <= 2) {
            realname = "Member";
        }
        new Thread(new ircThread()).start();
        Button send_btn = findViewById(R.id.send_button);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(socket != null) {
                    outputMsgArray = new LinkedList<String>(Arrays.asList(output_msg_text.getText().toString().split(" ")));
                    if(outputMsgArray.get(0).startsWith("/join") && outputMsgArray.size() > 1 && outputMsgArray.get(1).startsWith("#")) {
                        try {
                            socket.getOutputStream().write(("JOIN " + outputMsgArray.get(1) + "\r\n").getBytes());
                            channelsArray.add(outputMsgArray.get(1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(outputMsgArray.get(0).startsWith("/join") && outputMsgArray.size() > 1 && !outputMsgArray.get(1).startsWith("#")) {
                        try {
                            socket.getOutputStream().write(("JOIN #" + outputMsgArray.get(1) + "\r\n").getBytes());
                            channelsArray.add("#" + outputMsgArray.get(1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(outputMsgArray.get(0).startsWith("/mode") && outputMsgArray.size() > 1) {
                        try {
                            StringBuilder message_sb = new StringBuilder();
                            for (int i = 1; i < outputMsgArray.size(); i++)
                            {
                                if(i < outputMsgArray.size() - 1 && outputMsgArray.get(i).length() > 0) {
                                    message_sb.append(outputMsgArray.get(i)).append(" ");
                                } else if(outputMsgArray.get(i).length() > 0) {
                                    message_sb.append(outputMsgArray.get(i));
                                }
                            };
                            socket.getOutputStream().write(("MODE " + message_sb.toString() + "\r\n").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(outputMsgArray.get(0).startsWith("/nick") && outputMsgArray.size() == 1) {
                        try {
                            socket.getOutputStream().write(("NICK " + outputMsgArray.get(1) + "\r\n").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(outputMsgArray.get(0).startsWith("/")) {
                        try {
                            StringBuilder message_sb = new StringBuilder();
                            for (int i = 0; i < outputMsgArray.size(); i++)
                            {
                                if(i < outputMsgArray.size() - 1 && outputMsgArray.get(i).length() > 0) {
                                    message_sb.append(outputMsgArray.get(i)).append(" ");
                                } else if(outputMsgArray.get(i).length() > 0) {
                                    message_sb.append(outputMsgArray.get(i));
                                }
                            };
                            socket.getOutputStream().write((message_sb.toString().substring(1) + "\r\n").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if(channelsArray.size() > 0) {
                                socket.getOutputStream().write(("PRIVMSG " + channelsArray.get(channelsArray.size() - 1) + " :" + output_msg_text.getText() + "\r\n").getBytes());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    socks_msg_text.setText(socks_msg_text.getText() + "\nYou: " + output_msg_text.getText());
                    socks_msg_text.setSelection(socks_msg_text.getText().length());
                    output_msg_text.setText("");
                }
            }
        });
    }

    class ircThread implements Runnable {
        @Override
        public void run() {
            try {
                socket = new Socket();
                InetAddress serverAddr = InetAddress.getByName(server);
                SocketAddress socketAddress = new InetSocketAddress(serverAddr, port);
                socket.connect(socketAddress);
                input = socket.getInputStream();
                socket.getOutputStream().write(("USER " + nicknames.split(", ")[0] + " " +
                        hostname + " " + nicknames.split(", ")[0] + " :" +
                        realname + "\r\n").getBytes());
                socket.getOutputStream().flush();
                socket.getOutputStream().write(("NICK " + nicknames.split(", ")[0] + "\r\n").getBytes());
                socket.getOutputStream().flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response;
                msg = new StringBuilder();
                while((received_bytes = socket.getInputStream().read()) != 1) {
                    if(in.ready() == true) {
                        response = in.readLine();
                        if (response.startsWith("PING")) {
                            socket.getOutputStream().write(("PONG " + response.split(" ")[1]).getBytes());
                        }
                        if(response != null) {
                            msg.append(response).append("\n");
                            socket_data_string = msg.toString();
                            state = "getting_data";
                            updateUITask.run();
                        }
                    };
                }
                socket.close();
                socket = null;
                state = "disconnected";
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
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                state = "disconnected";
                updateUITask.run();
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
                        socks_msg_text.setText(msg.toString());
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        socket_data_string = "";
                    } else if(state == "disconnected") {
                        Toast.makeText(getApplicationContext(), R.string.connection_lost_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    } else if(state == "timeout") {
                        Toast.makeText(getApplicationContext(), R.string.connection_timeout_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    } else if(state == "no_connection") {
                        Toast.makeText(getApplicationContext(), R.string.no_connection_msg, Toast.LENGTH_SHORT).show();
                        socks_msg_text.setSelection(socks_msg_text.getText().length());
                        finish();
                    }
                }
            });
        }
    }
}
