package dev.tinelix.irc.android;

import android.app.PendingIntent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class IRCParser {
    public String parseString(String raw, boolean showTimestamp) {
        String[] array = raw.split(" ");
        String[] member_msgs_array = array[0].split("!");
        StringBuilder stringBuilder = new StringBuilder();
        String parsed = new String();
        if(raw.startsWith("PING")) {
            Log.w("Tinelix IRC Parser", "PING messages ignored.");
            parsed = "";
        } else if(array[1].startsWith("372")) {
            for(int index = 3; index < array.length; index++) {
                if(index == 3) {
                    stringBuilder.append(array[index].replace(":",""));
                } else {
                    stringBuilder.append(" " + array[index]);
                }
            }
            parsed = "MOTD: " + stringBuilder.toString();
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("371")) {
            for(int index = 3; index < array.length; index++) {
                if(index == 3) {
                    stringBuilder.append(array[index].replace(":",""));
                } else {
                    stringBuilder.append(" " + array[index]);
                }
            }
            parsed = "Info: " + stringBuilder.toString();
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("671")) {
            parsed = array[3] + " using a TLS/SSL connection.";
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("318")) {
            Log.w("Tinelix IRC Parser", "Messages with \"318\" code ignored.");
            parsed = "";
        } else if(array[1].startsWith("321")) {
            Log.w("Tinelix IRC Parser", "Messages with \"321\" code ignored.");
            parsed = "";
        } else if(array[1].startsWith("374")) {
            Log.w("Tinelix IRC Parser", "Messages with \"374\" code ignored.");
            parsed = "";
        } else if(array[1].startsWith("374")) {
            Log.w("Tinelix IRC Parser", "Messages with \"374\" code ignored.");
            parsed = "";
        } else if(array[1].startsWith("366")) {
            Log.w("Tinelix IRC Parser", "Messages with \"366\" code ignored.");
            parsed = "";
        } else if(array[1].startsWith("376")) {
            Log.w("Tinelix IRC Parser", "Messages with \"376\" code ignored.");
            parsed = "";
        } else if(array[1].startsWith("JOIN")) {
            parsed = member_msgs_array[0].replace(":", "") + " joined on the " + array[2].replace(":", "") + " channel.";
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("PART")) {
            parsed = member_msgs_array[0].replace(":", "") + " left the " + array[2].replace(":", "") + " channel.";
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("QUIT")) {
            if(array.length > 3) {
                for(int index = 2; index < array.length; index++) {
                    if(index == 2) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(" " + array[index]);
                    }
                }
                parsed = member_msgs_array[0].replace(":", "") + " quited with reason: " + stringBuilder;
            } else {
                parsed = member_msgs_array[0].replace(":", "") + " quited.";
            }
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("PRIVMSG")) {
            for(int index = 3; index < array.length; index++) {
                if(index == 3) {
                    stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                            .replace("https//", "https://").replace("ftp//", "ftp://"));
                } else {
                    stringBuilder.append(" " + array[index]);
                }
            }
            parsed = member_msgs_array[0].replace(":", "") + ": " + stringBuilder.toString();
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("NOTICE")) {
            if(array.length > 4) {
                for(int index = 3; index < array.length; index++) {
                    if(index == 3) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(" " + array[index]);
                    }
                }
                parsed = member_msgs_array[0].replace(":", "") + " sent a notification: " + stringBuilder;
            }
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("NICK")) {
            parsed = member_msgs_array[0].replace(":", "") + " changed nickname to " + array[2].replace(":", "");
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("353")) {
            if(array.length > 5) {
                for (int index = 5; index < array.length; index++) {
                    if (index == 5) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(", " + array[index]);
                    }
                }
                parsed = "Members (" + (int)(array.length - 5) + "): " + stringBuilder;
                Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
            }
        } else if(array[1].startsWith("TOPIC")) {
            if(array.length > 3) {
                for (int index = 3; index < array.length; index++) {
                    if (index == 3) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(" " + array[index]);
                    }
                }
                parsed = "Topic: " + stringBuilder;
                Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
            }
        } else if(array[1].startsWith("311")) {
            if(array.length > 7) {
                for (int index = 7; index < array.length; index++) {
                    if (index == 7) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(" " + array[index].replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    }
                }
                parsed = array[3] + " (" + array[4] + "@" + array[5] + ")\r\nReal name: " + stringBuilder + "\r\n------------------------------";
                Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
            }
        } else if(array[1].startsWith("319")) {
            if(array.length > 4) {
                for (int index = 4; index < array.length; index++) {
                    if (index == 4) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(" " + array[index].replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    }
                }
                parsed = "Mutual channels: " + stringBuilder;
                Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
            }
        } else if(array[1].startsWith("317")) {
            if(array.length > 4) {
                try {
                    Date idle_time = new Date(TimeUnit.SECONDS.toMillis(Integer.parseInt(array[4])));
                    Date logon_time = new Date(TimeUnit.SECONDS.toMillis(Integer.parseInt(array[5])));
                    parsed = new SimpleDateFormat("HH:mm:ss").format(idle_time) + " idle, last logon time - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logon_time);
                } catch(Exception e) {
                    e.printStackTrace();
                    parsed = "";
                }
                Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]\r\nDate: " + System.currentTimeMillis());
            }
        } else if(array[1].startsWith("MODE")) {
            if(array.length > 3) {
                for (int index = 3; index < array.length; index++) {
                    if (index == 3) {
                        stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                .replace("https//", "https://").replace("ftp//", "ftp://"));
                    } else {
                        stringBuilder.append(" " + array[index]);
                    }
                }
                parsed = "Enabled user modes for " + member_msgs_array[0].substring(1) + ": " + stringBuilder;
                Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
            }
        } else {
            try {
                if(array.length > 3) {
                    for (int index = 3; index < array.length; index++) {
                        if (index == 3) {
                            stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                                    .replace("https//", "https://").replace("ftp//", "ftp://"));
                        } else {
                            stringBuilder.append(" " + array[index]);
                        }
                    }
                    parsed = "Code " + Integer.parseInt(array[1]) + ": " + stringBuilder.toString();
                }
            } catch(NumberFormatException nfe) {
                parsed = raw;
            }
        }
        if(showTimestamp == true && parsed.length() > 0 && array[1].startsWith("317") == false &&
                array[1].startsWith("319") == false && array[1].startsWith("311") == false && array[1].startsWith("TOPIC") == false) {
            Date time = new java.util.Date(System.currentTimeMillis());
            parsed = parsed + " (" + new SimpleDateFormat("HH:mm:ss").format(time) + ")";
        }
        return parsed;
    }



    public String getMessageBody(String raw) {
       String[] array = raw.split(" ");
       String[] member_msgs_array = array[0].split("!");
       StringBuilder stringBuilder = new StringBuilder();
       String parsed = new String();
       if(array[1].startsWith("PRIVMSG")) {
          for(int index = 3; index < array.length; index++) {
               if(index == 3) {
                  stringBuilder.append(array[index].substring(1).replace("http//", "http://")
                       .replace("https//", "https://").replace("ftp//", "ftp://"));
               } else {
                  stringBuilder.append(" " + array[index]);
               }
          }
          parsed = stringBuilder.toString();
          Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
       } else {
           parsed = "";
       }
       return parsed;
    }

    public String getMessageAuthor(String raw) {
        String[] array = raw.split(" ");
        String[] member_msgs_array = array[0].split("!");
        StringBuilder stringBuilder = new StringBuilder();
        String parsed = new String();
        if(array[1].startsWith("PRIVMSG")) {
            parsed = member_msgs_array[0].substring(1);
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else {
            parsed = "";
        }
        return parsed;
    }

    public String getChannel(String raw) {
        String[] array = raw.split(" ");
        String[] member_msgs_array = array[0].split("!");
        StringBuilder stringBuilder = new StringBuilder();
        String parsed = new String();
        if(array[1].startsWith("JOIN")) {
            parsed = array[2].substring(1);
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("PRIVMSG")) {
            parsed = array[2];
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("PART")) {
            parsed = array[2];
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else {
            parsed = "";
        }
        return parsed;
    }
}
