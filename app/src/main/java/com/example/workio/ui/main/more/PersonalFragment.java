package com.example.workio.ui.main.more;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.UserProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalFragment extends Fragment {

    private TextView tvName, tvEmail, tvPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind UI
        tvName  = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);

        // Nút back
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Mở màn đổi mật khẩu
        LinearLayout btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_main, new ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // Mở màn đổi số điện thoại
        tvPhone.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UpdatePhone.class);
            intent.putExtra("type", "phone");
            intent.putExtra("currentValue", tvPhone.getText().toString());
            startActivity(intent);
        });
        //mo man doi email
        tvEmail.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UpdatePhone.class);
            intent.putExtra("type", "email");
            intent.putExtra("currentValue", tvEmail.getText().toString());
            startActivity(intent);
        });


        // 🔥 GỌI API
        loadUserProfile();
    }

    private void loadUserProfile() {

        ApiService api = RetrofitClient.getInstance(requireContext()).getApiService();

        Call<ApiResponse<UserProfile>> call = api.getUserProfile();

        call.enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    UserProfile user = response.body().getData();

                    if (user == null) {
                        Log.e("API_PROFILE", "User = null");
                        return;
                    }

                    // SAFE: tránh crash vì null
                    tvName.setText(user.getName() != null ? user.getName() : "—");
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "—");
                    tvPhone.setText(user.getPhone() != null ? user.getPhone() : "—");

                } else {
                    Log.e("API_PROFILE", "Lỗi response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e("API_PROFILE", "Call thất bại: " + t.getMessage());
            }
        });

    }
}
