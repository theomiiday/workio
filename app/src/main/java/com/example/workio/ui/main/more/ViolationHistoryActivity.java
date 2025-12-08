package com.example.workio.ui.main.more;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.Violation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViolationHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ViolationHistoryAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_history);

        apiService = RetrofitClient.getInstance(this).getApiService();

        recyclerView = findViewById(R.id.recyclerViolations);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String employeeId = getIntent().getStringExtra("employeeId");

        if (employeeId == null) {
            Toast.makeText(this, "Không tìm thấy employeeId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadViolations(employeeId);
    }

    private void loadViolations(String employeeId) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getEmployeeViolations(employeeId)
                .enqueue(new Callback<ApiResponse<List<Violation>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Violation>>> call,
                                           Response<ApiResponse<List<Violation>>> response) {

                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("ViolationAPI", "❌ HTTP Error: " + response.code());
                            Toast.makeText(ViolationHistoryActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Violation> violations = response.body().getData();

                        if (violations == null || violations.isEmpty()) {
                            Toast.makeText(ViolationHistoryActivity.this, "Không có vi phạm nào", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        adapter = new ViolationHistoryAdapter(violations);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Violation>>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("ViolationAPI", "❌ Failure: " + t.getMessage());
                        Toast.makeText(ViolationHistoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
