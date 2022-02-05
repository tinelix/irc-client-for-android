package dev.tinelix.irc.android;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Profile> objects;

    ProfileAdapter(Context context, ArrayList<Profile> products) {
        ctx = context;
        objects = products;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    Profile getProfile(int position) {
        return ((Profile) getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.profile_item, parent, false);
        }

        Profile p = getProfile(position);
        ((TextView) view.findViewById(R.id.profile_item_label)).setText(p.name);
        ((TextView) view.findViewById(R.id.profile_server_label)).setText(p.server + ":" + p.port);
        return view;
    }

    public class ViewHolder {
        public TextView profile_name;
        public TextView server;
    }


}
