package com.example.workio.ui.auth;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.LoginResponse;
import com.example.workio.data.model.VerifyEmailRequest;
import com.example.workio.data.model.ForgotPasswordRequest;
import com.example.workio.data.model.VerifyResetOtpRequest;
import com.example.workio.ui.onboarding.SelectBranchActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private PinView pinView;
    private Button btnConfirm;
    private TextView tvResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);

        pinView = findViewById(R.id.pinView);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvResend = findViewById(R.id.tvResend);

        // Khi nhập đủ 6 số thì bật nút
        pinView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(s.length() == 6);
                btnConfirm.setAlpha(s.length() == 6 ? 1f : 0.5f);
            }
        });

        //  Nhấn "Xác nhận" → gọi API verify-email
        btnConfirm.setOnClickListener(v -> {
            String otp = pinView.getText().toString().trim();
            String email = getIntent().getStringExtra("email");

            if (otp.isEmpty()) {
                return;
            }

            ApiService api = RetrofitClient.getInstance(this).getApiService();
            VerifyResetOtpRequest request = new VerifyResetOtpRequest(email, otp);

            api.verifyResetOtp(request).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    // In mã HTTP trả về
                    Log.d("VerifyEmail", ">>> Response code: " + response.code());

                    if (response.isSuccessful()) {
                        Intent intent = new Intent(OtpActivity.this, ResetPassword.class);
                        intent.putExtra("email", email);
                        intent.putExtra("otp", otp);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            // In chi tiết lỗi trả về từ server
                            if (response.errorBody() != null) {
                                String error = response.errorBody().string();
                                Log.e("VerifyEmail", " Error body: " + error);
                            }
                        } catch (Exception e) {
                            Log.e("VerifyEmail", " Không đọc được error body", e);
                        }

                        Toast.makeText(OtpActivity.this,
                                " OTP không hợp lệ hoặc đã hết hạn (" + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Log.e("VerifyEmail", " Lỗi mạng khi xác minh OTP", t);
                    Toast.makeText(OtpActivity.this, " Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        // ✅ Nhấn "Gửi lại OTP"
        tvResend.setOnClickListener(v -> {
            String email = getIntent().getStringExtra("email");
            ApiService api = RetrofitClient.getInstance(this).getApiService();
            ForgotPasswordRequest request = new ForgotPasswordRequest(email);

            api.forgotPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(OtpActivity.this, " Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
