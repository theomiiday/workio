package com.example.workio.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.ForgotPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateEmailActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_email);

        etEmail = findViewById(R.id.etEmail);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setEnabled(false);
        btnConfirm.setAlpha(0.5f);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                btnConfirm.setEnabled(!email.isEmpty());
                btnConfirm.setAlpha(!email.isEmpty() ? 1f : 0.5f);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            callForgotPasswordApi(email);
        });
    }

    private void callForgotPasswordApi(String email) {
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        btnConfirm.setEnabled(false);
        btnConfirm.setAlpha(0.5f);
        Toast.makeText(this, "Đang gửi mã OTP...", Toast.LENGTH_SHORT).show();

        apiService.forgotPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setAlpha(1f);
//                Log.d("ForgotPassword", ">>> Response code: " + response.code());
//                Log.d("ForgotPassword", ">>> Response body: " + response.body());
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateEmailActivity.this,
                            " Mã OTP đã được gửi đến email của bạn!",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(UpdateEmailActivity.this, OtpActivity.class);
                    intent.putExtra("otp_purpose", "reset_password");
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateEmailActivity.this,
                            " Không tìm thấy email này hoặc gửi OTP thất bại.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setAlpha(1f);
                Toast.makeText(UpdateEmailActivity.this,
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
