package dev.tinelix.irc.android;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CreateItemFragm extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_item,
                container, false);
        Button create_item_button = view.findViewById(R.id.create_item_button);
        create_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ((ConnectionManagerActivity) getActivity()).showEnterTextDialog();
                }
            }
        });

        return view;
    }
}
