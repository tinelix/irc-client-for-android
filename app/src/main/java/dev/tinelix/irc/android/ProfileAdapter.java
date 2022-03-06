package dev.tinelix.irc.android;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.profile_item, parent, false);
        }

        Profile p = getProfile(position);
        ((TextView) view.findViewById(R.id.profile_item_label)).setText(p.name);
        ((TextView) view.findViewById(R.id.profile_server_label)).setText(p.server + ":" + p.port);
        ImageButton edit_btn = view.findViewById(R.id.edit_btn);
        edit_btn.setTag(position);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int pos = 0; pos < getCount(); pos++) {
                    if(pos == position) {
                        ((Profile) getItem(pos)).isSelected = true;
                        ((ConnectionManagerActivity) ctx).editProfile(pos);
                    } else {
                        ((Profile) getItem(pos)).isSelected = false;
                    }
                }
            }
        });
        ImageButton delete_btn = view.findViewById(R.id.delete_btn);
        delete_btn.setTag(position);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int pos = 0; pos < getCount(); pos++) {
                    if(pos == position) {
                        ((Profile) getItem(pos)).isSelected = true;
                        ((ConnectionManagerActivity) ctx).deleteProfile(pos);
                    } else {
                        ((Profile) getItem(pos)).isSelected = false;
                    }
                }
            }
        });
        ImageButton connect_btn = view.findViewById(R.id.connect_btn);
        connect_btn.setTag(position);
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int pos = 0; pos < getCount(); pos++) {
                    if(pos == position) {
                        ((Profile) getItem(pos)).isSelected = true;
                        ((ConnectionManagerActivity) ctx).connectProfile(pos);
                    } else {
                        ((Profile) getItem(pos)).isSelected = false;
                    }
                }
            }
        });
        ((TextView) view.findViewById(R.id.profile_server_label)).setTag(position);
        return view;
    }

    public class ViewHolder {
        public TextView profile_name;
        public TextView server;
    }


}
