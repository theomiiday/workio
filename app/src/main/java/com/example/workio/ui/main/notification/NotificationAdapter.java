package com.example.workio.ui.main.notification;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.Notification;
import com.example.workio.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> list;

    public NotificationAdapter(List<Notification> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification item = list.get(position);

        // Hiển thị dữ liệu cơ bản
        holder.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "(Không có tiêu đề)");
        holder.tvMessage.setText(item.getMessage() != null ? item.getMessage() : "");
        holder.tvTime.setText(item.getCreatedAt() != null ? item.getCreatedAt() : "");

        // Màu nền
        if ("unread".equalsIgnoreCase(item.getStatus())) {
            holder.card.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.light_blue_bg));
        } else {
            holder.card.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.white));
        }

        // Icon tạm
        String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";
        if (title.contains("đăng ký ca làm thành công") || title.contains("approved")) {
            holder.imgIcon.setImageResource(R.drawable.ic_check_noti);
        } else if (title.contains("tin nhắn") || title.contains("message")) {
            holder.imgIcon.setImageResource(R.drawable.ic_message_noti);
        } else if (title.contains("nhắc") || title.contains("reminder")) {
            holder.imgIcon.setImageResource(R.drawable.ic_warning_noti);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_warning_noti);
        }

        // Khi bấm vào thông báo → gọi API markAsRead
        holder.itemView.setOnClickListener(v -> {
            if ("unread".equalsIgnoreCase(item.getStatus())) {
                markAsRead(v, item);
                // Cập nhật giao diện tạm thời
                holder.card.setCardBackgroundColor(v.getResources().getColor(R.color.white));
                item.setStatus("read");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void markAsRead(View v, Notification item) {
        ApiService api = RetrofitClient.getInstance(v.getContext()).getApiService();
        SessionManager session = new SessionManager(v.getContext());
        String token = "Bearer " + session.getAccessToken();

        String id = item.getId();
        Log.d("NotificationAdapter", "🟢 markAsRead() called for ID=" + id);
        Log.d("NotificationAdapter", "🟢 Full token=" + token);
        Log.d("NotificationAdapter", "🟢 Title=" + item.getTitle());
        Log.d("NotificationAdapter", "🟢 Status before=" + item.getStatus());

        if (id == null || id.isEmpty()) {
            Log.e("NotificationAdapter", "❌ Notification ID is null or empty, cannot call API!");
            Toast.makeText(v.getContext(), "Không có ID thông báo!", Toast.LENGTH_SHORT).show();
            return;
        }

        api.markAsRead(token, id).enqueue(new Callback<ApiResponse<Notification>>() {
            @Override
            public void onResponse(Call<ApiResponse<Notification>> call, Response<ApiResponse<Notification>> response) {
                if (response.isSuccessful()) {
                    Log.d("NotificationAdapter", "✅ Marked as read successfully for ID=" + id);
                } else {
                    Log.e("NotificationAdapter", "⚠️ MarkAsRead failed: " + response.code());
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("NotificationAdapter", "📩 Error body: " + err);
                    } catch (Exception e) {
                        Log.e("NotificationAdapter", "⚠️ Could not parse errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Notification>> call, Throwable t) {
                Log.e("NotificationAdapter", "❌ Network error in markAsRead(): " + t.getMessage(), t);
                Toast.makeText(v.getContext(), "Lỗi mạng khi đánh dấu đọc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView imgIcon;
        TextView tvTitle, tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardNotification);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
