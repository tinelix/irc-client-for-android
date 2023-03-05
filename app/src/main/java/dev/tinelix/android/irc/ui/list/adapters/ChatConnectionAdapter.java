package dev.tinelix.android.irc.ui.list.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import dev.tinelix.android.irc.R;
import dev.tinelix.android.irc.ui.core.activities.MainActivity;
import dev.tinelix.android.irc.ui.list.items.ChatProfile;

public class ChatConnectionAdapter extends RecyclerView.Adapter<ChatConnectionAdapter.Holder>  {

    private Context ctx;
    private ArrayList<ChatProfile> profiles;

    public ChatConnectionAdapter(Context ctx, ArrayList<ChatProfile> profiles) {
        this.ctx = ctx;
        this.profiles = profiles;
    }
    @NonNull
    @Override
    public ChatConnectionAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(ctx).inflate(R.layout.chat_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatConnectionAdapter.Holder holder, int position) {
        holder.bind(position);
    }

    public ChatProfile getItem(int position) {
        return profiles.get(position);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    @Override
    public void onViewRecycled(@NonNull Holder holder) {
        super.onViewRecycled(holder);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView profile_name;
        private final TextView profile_server;
        private final MaterialButton connect_btn;
        private final MaterialButton edit_btn;
        public Holder(@NonNull View view) {
            super(view);
            profile_name = view.findViewById(android.R.id.title);
            profile_server = view.findViewById(android.R.id.text1);
            connect_btn = view.findViewById(R.id.connect_btn);
            edit_btn = view.findViewById(R.id.edit_btn);
        }

        public void bind(int position) {
            final ChatProfile item = getItem(position);
            profile_name.setText(item.profile_name);
            profile_server.setText(item.server);
            connect_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ctx instanceof MainActivity) {
                        ((MainActivity) ctx).ircConnect(position);
                    }
                }
            });
        }
    }
}
