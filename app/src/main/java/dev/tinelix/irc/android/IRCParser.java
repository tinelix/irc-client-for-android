package dev.tinelix.irc.android;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IRCParser {
    public String parseString(String raw, boolean getTimestamp) {
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
        } else if(array[1].startsWith("JOIN")) {
            parsed = member_msgs_array[0] + " joined on the " + array[2] + " channel.";
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else if(array[1].startsWith("PRIVMSG")) {
            for(int index = 3; index < array.length; index++) {
                if(index == 3) {
                    stringBuilder.append(array[index].replace(":",""));
                } else {
                    stringBuilder.append(" " + array[index]);
                }
            }
            parsed = member_msgs_array[0] + ": " + stringBuilder.toString();
            Log.i("Tinelix IRC Parser", "\r\nDone!\r\n\r\nOriginal string: [" + raw + "]\r\nCode: [" + array[1] + "]");
        } else {
            parsed = raw;
        }
        if(getTimestamp == true) {
            Date time = new java.util.Date(System.currentTimeMillis());
            parsed = parsed + " (" + new SimpleDateFormat("HH:mm:ss").format(time) + ")";
        }
        return parsed;
    }
}