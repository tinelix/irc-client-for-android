package dev.tinelix.irc.android;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CreateItemFragm2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences global_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        View view = inflater.inflate(R.layout.create_item2,
                container, false);
        if(global_prefs.getString("theme", "Dark").contains("Light")) {
            TextView add_item_label = view.findViewById(R.id.add_item_label);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                add_item_label.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                add_item_label.setTextColor(getResources().getColor(R.color.orange));
            }
        }
        Button create_item_button = view.findViewById(R.id.create_item_button2);
        create_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ((CustomNicknamesActivity) getActivity()).showEnterTextDialog();
                }
            }
        });

        return view;
    }

}
