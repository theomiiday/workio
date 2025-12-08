package com.example.workio.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.Branch;
import com.example.workio.ui.main.MainActivity;
import com.example.workio.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectBranchActivity extends AppCompatActivity {

    private LinearLayout layoutBranchContainer;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_branch);

        layoutBranchContainer = findViewById(R.id.layoutBranchContainer);
        progressBar = findViewById(R.id.progressBar);

        apiService = RetrofitClient.getInstance(this).getApiService();
        sessionManager = new SessionManager(this);

        loadBranches();
    }

    private void loadBranches() {
        progressBar.setVisibility(View.VISIBLE);
        layoutBranchContainer.removeAllViews();

        String token = sessionManager.getAccessToken();
        String userBranchId = sessionManager.getBranchId();

        Log.d("SelectBranchActivity", "🔑 AccessToken: " + token);
        Log.d("SelectBranchActivity", "🏢 BranchId trong prefs: " + userBranchId);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Call<ApiResponse<List<Branch>>> call = apiService.getBranches("Bearer " + token);
        call.enqueue(new Callback<ApiResponse<List<Branch>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Branch>>> call,
                                   Response<ApiResponse<List<Branch>>> response) {
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SelectBranchActivity.this,
                            "Không tải được danh sách chi nhánh", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<List<Branch>> apiResponse = response.body();
                List<Branch> branches = apiResponse.getData();

                if (branches == null || branches.isEmpty()) {
                    Toast.makeText(SelectBranchActivity.this,
                            "Không có chi nhánh nào khả dụng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Lọc theo branchId của nhân viên
                List<Branch> filteredBranches = new ArrayList<>();
                for (Branch b : branches) {
                    Log.d("SelectBranchActivity", "📦 Branch từ API: " + b.getId());
                    if (b.getId() != null && b.getId().equals(userBranchId)) {
                        filteredBranches.add(b);
                    }
                }

                Log.d("SelectBranchActivity", "✅ Số chi nhánh sau lọc: " + filteredBranches.size());

                if (filteredBranches.isEmpty()) {
                    Toast.makeText(SelectBranchActivity.this,
                            "Không tìm thấy chi nhánh trùng với nhân viên", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBranchList(filteredBranches);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Branch>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SelectBranchActivity.this,
                        "Lỗi mạng: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBranchList(List<Branch> branches) {
        LayoutInflater inflater = LayoutInflater.from(this);
        layoutBranchContainer.removeAllViews();

        for (Branch branch : branches) {
            View branchCard = inflater.inflate(R.layout.item_branch_card, layoutBranchContainer, false);

            TextView tvBranchName = branchCard.findViewById(R.id.tvBranchName);
            TextView tvBranchAddress = branchCard.findViewById(R.id.tvBranchAddress);
            CardView card = branchCard.findViewById(R.id.cardBranchItem);

            tvBranchName.setText(branch.getBranchName());
            tvBranchAddress.setText(branch.getAddress());

            card.setOnClickListener(v -> selectBranch(branch.getId(), branch.getBranchName()));
            layoutBranchContainer.addView(branchCard);
        }
    }

    private void selectBranch(String branchId, String branchName) {
        sessionManager.updateBranchId(branchId);
        Toast.makeText(this, "Đã chọn chi nhánh: " + branchName, Toast.LENGTH_SHORT).show();
        Log.d("SelectBranchActivity", "✅ Lưu branchId: " + branchId);
        Log.d("SelectBranchActivity", "✅ Lưu vào prefs: " + sessionManager.getBranchId());

        Intent intent = new Intent(SelectBranchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
