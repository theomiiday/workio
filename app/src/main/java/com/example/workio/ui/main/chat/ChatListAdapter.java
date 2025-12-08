package com.example.workio.ui.main.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.model.Conversation;
import com.example.workio.ui.main.MainActivity;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final List<Conversation> list;
    private final Context context;

    public ChatListAdapter(List<Conversation> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conv = list.get(position);

        if (conv.isGroup()) {
            // 👉 Hội thoại nhóm
            holder.tvName.setText("💬 Nhóm chi nhánh");
            holder.tvLastMessage.setText("Bấm để xem tin nhắn nhóm");

            holder.itemView.setOnClickListener(v -> {
                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isGroup", true);
                chatFragment.setArguments(bundle);

                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_main, chatFragment)
                        .addToBackStack(null)
                        .commit();

                ((MainActivity) context).setBottomNavVisibility(false);
            });

        } else {
            // 👉 Hội thoại cá nhân
            holder.tvName.setText(conv.getUser().getName());

            if (conv.getLastMessage() != null)
                holder.tvLastMessage.setText(conv.getLastMessage().getContent());
            else
                holder.tvLastMessage.setText("Chưa có tin nhắn");

            holder.itemView.setOnClickListener(v -> {
                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("receiverId", conv.getUser().getId());
                bundle.putString("receiverName", conv.getUser().getName());
                bundle.putBoolean("isGroup", false);
                chatFragment.setArguments(bundle);

                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_main, chatFragment)
                        .addToBackStack(null)
                        .commit();

                ((MainActivity) context).setBottomNavVisibility(false);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }
    }
}
