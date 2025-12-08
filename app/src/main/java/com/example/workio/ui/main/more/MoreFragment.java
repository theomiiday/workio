package com.example.workio.ui.main.more;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workio.R;
import com.example.workio.ui.auth.LoginActivity;
import com.example.workio.utils.SessionDAO;
import com.example.workio.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MoreFragment extends Fragment {

    private SessionManager sessionManager;



    public MoreFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        TextView tvLogout = view.findViewById(R.id.tvLogout);
        sessionManager = new SessionManager(requireContext());
        sessionManager = new SessionManager(requireContext());
        String employeeId = sessionManager.getEmployeeId();
        LinearLayout layoutViolationHistory = view.findViewById(R.id.layoutViolationHistory);

        layoutViolationHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViolationHistoryActivity.class);
            intent.putExtra("employeeId", employeeId);
            startActivity(intent);
        });

        tvLogout.setOnClickListener(v -> showLogoutConfirmDialog());
        TextView btnPersonal = view.findViewById(R.id.tvPersonal);
        btnPersonal.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_main, new PersonalFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;

    }

    private void showLogoutConfirmDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void logout() {
        sessionManager = new SessionManager(requireContext());
        sessionManager.setCheckedIn(false);
        sessionManager.saveCheckInTime(null);
        sessionManager.saveCheckOutTime(null);
        sessionManager.saveAttendanceId(null);
        sessionManager.saveTodayRegistrationId(null);

        sessionManager.clearSession();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);

        requireActivity().finishAffinity(); // xoá toàn bộ stack nhưng KHÔNG kill app
    }


}
