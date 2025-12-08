package com.example.workio.ui.main.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.Notification;
import com.example.workio.data.model.NotificationResponse;
import com.example.workio.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = RetrofitClient.getInstance(requireContext()).getApiService();
        sessionManager = new SessionManager(requireContext());

        // Nút quay lại
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Gọi API thật để lấy danh sách thông báo
        loadNotifications();
    }

    private void loadNotifications() {
        String token = "Bearer " + sessionManager.getAccessToken();
        apiService.getNotifications(token, 1, 20, null).enqueue(new Callback<ApiResponse<NotificationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<NotificationResponse>> call, Response<ApiResponse<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Notification> list = response.body().getData().getNotifications();
                    adapter = new NotificationAdapter(list);
                    recyclerView.setAdapter(adapter);
                    Log.d("NotificationFragment", "📬 Loaded " + list.size() + " notifications");
                } else {
                    Toast.makeText(requireContext(), "⚠️ Không thể tải thông báo", Toast.LENGTH_SHORT).show();
                    Log.e("NotificationFragment", "❌ Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NotificationResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "❌ Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NotificationFragment", "❌ API error: ", t);
            }
        });
    }
}
