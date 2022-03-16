package dev.tinelix.irc.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<CustomSpinnerItem> objects;

    CustomSpinnerAdapter(Context context, ArrayList<CustomSpinnerItem> products) {
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

    CustomSpinnerItem getProfile(int position) {
        return ((CustomSpinnerItem) getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        CustomSpinnerItem p = getProfile(position);
        ((TextView) view.findViewById(R.id.spinner_item_text)).setText(p.name);
        TextView item_name = view.findViewById(R.id.spinner_item_text);
        SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        if (global_prefs.getString("theme", "Dark").contains("Light")) {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                item_name.setTextColor(view.getResources().getColor(R.color.black));
            } else {
                item_name.setTextColor(view.getResources().getColor(R.color.white));
            }
        } else {
            if (global_prefs.getBoolean("theme_requires_restart", false) == false) {
                item_name.setTextColor(view.getResources().getColor(R.color.white));
            } else {
                item_name.setTextColor(view.getResources().getColor(R.color.black));
            }
        }
        return view;
    }

    public class ViewHolder {
        public TextView profile_name;
        public TextView server;
    }


}
