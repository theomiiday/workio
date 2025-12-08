package com.example.workio.ui.main.more;

import android.os.Bundle;
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
import com.example.workio.data.model.UpdateProfileRequest;
import com.example.workio.data.model.UserProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePhone extends AppCompatActivity {

    private EditText edtPhone;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_phone);

        // Bind UI
        edtPhone = findViewById(R.id.etPhone);
        btnSave  = findViewById(R.id.btnSave);

        // Nhận giá trị cũ từ PersonalFragment
        String currentPhone = getIntent().getStringExtra("currentPhone");
        if (currentPhone != null) {
            edtPhone.setText(currentPhone);
        }

        // Bấm nút lưu
        btnSave.setOnClickListener(v -> updatePhone());
    }

    private void updatePhone() {

        String newPhone = edtPhone.getText().toString().trim();

        if (newPhone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        ApiService api = RetrofitClient.getInstance(this).getApiService();

        // Chỉ cập nhật PHONE -> Name và Email để null
        UpdateProfileRequest request = new UpdateProfileRequest(
                null,       // name
                newPhone,   // phone
                null        // email
        );

        Call<ApiResponse<UserProfile>> call = api.updateProfile(request);

        call.enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdatePhone.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdatePhone.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API_UPDATE", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e("API_UPDATE", "Error: " + t.getMessage());
                Toast.makeText(UpdatePhone.this, "Call API lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
