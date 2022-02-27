package dev.tinelix.irc.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class StatisticsFragm extends DialogFragment {
    public int sended_bytes;
    public int received_bytes;
    public int total_bytes;
    public Timer statsTimer;
    public TextView sended_bytes_label;
    public TextView received_bytes_label;
    public TextView total_bytes_label;
    public View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sended_bytes = ((ThreadActivity) getActivity()).getSendedBytes();
        received_bytes = ((ThreadActivity) getActivity()).getReceivedBytes();
        total_bytes = sended_bytes + received_bytes;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.statistics_item);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.statistics_activity, null);
        builder.setView(view);
        sended_bytes_label = view.findViewById(R.id.sended_label2);
        received_bytes_label = view.findViewById(R.id.received_label2);
        total_bytes_label = view.findViewById(R.id.total_label2);
        if (sended_bytes > 1073741824) {
            sended_bytes_label.setText(getString(R.string.gbytes_stats, String.format("%.2f", (double)(sended_bytes / 1073741824))));
        } else if(sended_bytes > 1048576) {
            sended_bytes_label.setText(getString(R.string.mbytes_stats, String.format("%.2f", (double)(sended_bytes / 1048576))));
        } else if(sended_bytes > 1024) {
            sended_bytes_label.setText(getString(R.string.kbytes_stats, String.format("%.2f", (double)(sended_bytes / 1024))));
        } else {
            sended_bytes_label.setText(getString(R.string.bytes_stats, Integer.toString(sended_bytes)));
        }
        if (received_bytes > 1073741824) {
            received_bytes_label.setText(getString(R.string.gbytes_stats, String.format("%.2f", (double)(received_bytes / 1073741824))));
        } else if(received_bytes > 1048576) {
            received_bytes_label.setText(getString(R.string.mbytes_stats, String.format("%.2f", (double)(received_bytes / 1048576))));
        } else if(received_bytes > 1024) {
            received_bytes_label.setText(getString(R.string.kbytes_stats, String.format("%.2f", (double)(received_bytes / 1024))));
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
        Handler updateHandler = new Handler();
        statsTimer = new Timer();
        statsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sended_bytes = ((ThreadActivity) getActivity()).getSendedBytes();
                received_bytes = ((ThreadActivity) getActivity()).getReceivedBytes();
                total_bytes = sended_bytes + received_bytes;
                updateHandler.post(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
            }
        }, 1000, 1000);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statsTimer.cancel();
                statsTimer = null;
                return;
            }
        });
        return builder.create();
    }

}
