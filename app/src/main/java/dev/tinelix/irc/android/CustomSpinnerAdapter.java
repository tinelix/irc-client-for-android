package dev.tinelix.irc.android;

import android.content.Context;
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
        return view;
    }

    public class ViewHolder {
        public TextView profile_name;
        public TextView server;
    }


}
