package com.example.workio.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.ForgotPasswordRequest;
import com.example.workio.data.model.LoginRequest;
import com.example.workio.data.model.LoginResponse;
import com.example.workio.ui.main.MainActivity;
import com.example.workio.ui.onboarding.SelectBranchActivity;
import com.example.workio.utils.SessionDAO;
import com.example.workio.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private ImageView showPasswordIcon;
    private CheckBox checkBoxRemember;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private SessionManager sessionManager;

//    Dùng Share Preferences
    @Override
    protected void onStart() {
        super.onStart();
        sessionManager = new SessionManager(this);

        // 🔹 Nếu người dùng đã tick “Ghi nhớ đăng nhập” → vào thẳng app
        boolean remember = sessionManager.isRememberLogin();
        String token = sessionManager.getAccessToken();

        if (remember && token != null && !token.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.editText_username);
        passwordField = findViewById(R.id.editText_password);
        showPasswordIcon = findViewById(R.id.imageView_show_password);
        tvForgotPassword = findViewById(R.id.textView_forgot_password);
        btnLogin = findViewById(R.id.button_login);
        checkBoxRemember = findViewById(R.id.checkBox2);

        sessionManager = new SessionManager(this);

        // Theo dõi nhập liệu
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { validateInputs(); }
        };
        usernameField.addTextChangedListener(textWatcher);
        passwordField.addTextChangedListener(textWatcher);
        validateInputs();

        btnLogin.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            callLoginApi(username, password);
        });

        setupForgotPassword();
    }

    // 🔹 Gọi API đăng nhập
    private void callLoginApi(String username, String password) {
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();
        LoginRequest request = new LoginRequest(username, password);

        btnLogin.setEnabled(false);
        btnLogin.setAlpha(0.6f);


        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setAlpha(1.0f);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse.Data data = response.body().getData();

                    if (data != null && data.getAccessToken() != null) {
                        String token = data.getAccessToken();
                        String role = data.getUser().getRole();
                        String branchId = data.getUser().getBranchId();

                        if ("employee".equalsIgnoreCase(role)) {
                            //  Lưu token + branchId + user info
                            sessionManager.saveSession(token, null, data.getUser());
                            sessionManager.updateBranchId(branchId);
                            sessionManager.setRememberLogin(checkBoxRemember.isChecked());

                            Log.d("LoginActivity", " Token: " + token);
                            Log.d("LoginActivity", " BranchId: " + branchId);

                            // Lưu user info bằng SharedPreferences (giữ nguyên)
                            sessionManager.saveSession(token, null, data.getUser());
                            sessionManager.updateBranchId(branchId);

                            Intent intent = new Intent(LoginActivity.this, SelectBranchActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showErrorDialog("Tài khoản không thuộc vai trò nhân viên!");
                        }
                    } else {
                        showErrorDialog("Không nhận được token hợp lệ từ máy chủ!");
                    }
                } else {
                    showErrorDialog("Tên đăng nhập hoặc mật khẩu không đúng.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setAlpha(1.0f);
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void callForgotPasswordApi(String email) {
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);



        apiService.forgotPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đã gửi email đặt lại mật khẩu!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Không tìm thấy email này!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showErrorDialog(String message) {
        new MaterialAlertDialogBuilder(LoginActivity.this)
                .setTitle("Đăng nhập thất bại")
                .setMessage(message)
                .setPositiveButton("OK", (d, which) -> d.dismiss())
                .show();
    }

    private void validateInputs() {
        String usernameInput = usernameField.getText().toString().trim();
        String passwordInput = passwordField.getText().toString().trim();
        btnLogin.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
        btnLogin.setAlpha(btnLogin.isEnabled() ? 1.0f : 0.5f);
    }

    private void setupForgotPassword() {
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Ẩn bàn phím khi bấm ra ngoài
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
