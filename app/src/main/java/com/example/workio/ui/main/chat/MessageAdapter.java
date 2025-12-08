package com.example.workio.ui.main.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message.getSenderId() != null &&
                message.getSenderId().getId().equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {
        Message msg = messageList.get(position);

        // Format timestamp
        String formattedTime;
        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = inputFormat.parse(msg.getTimestamp());

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

            formattedTime = outputFormat.format(date);
        } catch (Exception e) {
            formattedTime = "--:--";
        }

        // Bind UI
        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;

            sentHolder.tvMessage.setText(msg.getContent());
            sentHolder.tvSenderName.setText("Bạn");
            sentHolder.tvTimestamp.setText(formattedTime);

        } else if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder recvHolder = (ReceivedViewHolder) holder;

            recvHolder.tvMessage.setText(msg.getContent());

            if (msg.getSenderId() != null && msg.getSenderId().getName() != null) {
                recvHolder.tvSenderName.setText(msg.getSenderId().getName());
            } else {
                recvHolder.tvSenderName.setText("Không rõ");
            }

            recvHolder.tvTimestamp.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    // ==================== VIEW HOLDERS ====================

    // Tin nhắn gửi
    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvSenderName, tvTimestamp;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }

    // Tin nhắn nhận
    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvSenderName, tvTimestamp;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
