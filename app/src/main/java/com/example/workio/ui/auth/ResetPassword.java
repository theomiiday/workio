package com.example.workio.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.ResetPasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText newPasswordEditText;
    private com.google.android.material.textfield.TextInputEditText confirmPasswordEditText;
    private Button confirmButton;
    private final int ACTIVE_COLOR = Color.parseColor("#509AC8");
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        confirmButton = findViewById(R.id.confirmButton);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        confirmButton.setBackgroundColor(ACTIVE_COLOR);
        updateConfirmButtonState(false);

        newPasswordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);

        confirmButton.setOnClickListener(v -> {
            String newPass = newPasswordEditText.getText().toString().trim();
            String confirmPass = confirmPasswordEditText.getText().toString().trim();
            String email = getIntent().getStringExtra("email");
            String code = getIntent().getStringExtra("otp");

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getInstance(this).getApiService();
            ResetPasswordRequest req = new ResetPasswordRequest(email, code, newPass, confirmPass);

            api.resetPassword(req).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> res) {
                    if (res.isSuccessful()) {
                        Toast.makeText(ResetPassword.this, " Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPassword.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ResetPassword.this, " Không thể đặt lại mật khẩu (" + res.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(ResetPassword.this, " Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { checkInputValidity(); }
        @Override public void afterTextChanged(Editable s) {}
    };

    private void checkInputValidity() {
        boolean isValid = newPasswordEditText.getText().length() >= MIN_PASSWORD_LENGTH
                && confirmPasswordEditText.getText().length() >= MIN_PASSWORD_LENGTH;
        updateConfirmButtonState(isValid);
    }

    private void updateConfirmButtonState(boolean isEnabled) {
        confirmButton.setEnabled(isEnabled);
        confirmButton.setAlpha(isEnabled ? 1f : 0.6f);
    }
}
