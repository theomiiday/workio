package com.example.workio.ui.main.more;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.ChangePasswordRequest;
import com.example.workio.data.model.ForgotPasswordRequest;
import com.example.workio.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private TextView tvForgotPassword;
    private SessionManager sessionManager;
    private ApiService api;

    private static final String TAG = "ChangePassword";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnSave);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        sessionManager = new SessionManager(requireContext());
        api = RetrofitClient.getInstance(requireContext()).getApiService();

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        // ⚙️ Giữ nguyên logic đổi mật khẩu như cũ
    }

    // 🟢 Gọi API forgot-password khi bấm "Quên mật khẩu"

}
