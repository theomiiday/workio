package com.example.workio.ui.main.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.Attendance;
import com.example.workio.data.model.CheckInRequest;
import com.example.workio.data.model.CheckInResponse;
import com.example.workio.data.model.CheckOutRequest;
import com.example.workio.data.model.CheckOutResponse;
import com.example.workio.data.model.CreateGoalRequest;
import com.example.workio.data.model.CurrentGoalData;
import com.example.workio.data.model.SalaryGoal;
import com.example.workio.data.model.ShiftRegistration;
import com.example.workio.ui.main.notification.NotificationFragment;
import com.example.workio.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Button btnStartShift;
    private TextView tvShiftInfo;
    private ApiService apiService;
    private SessionManager sessionManager;

    private FusedLocationProviderClient fusedLocationClient;

    // Trạng thái
    private boolean isCheckedIn = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView tv_progress_value;
    private ProgressBar progress_circle;
    private TextView tv_current_income;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStartShift = view.findViewById(R.id.btn_start_shift);
        tvShiftInfo = view.findViewById(R.id.tv_shift_info);
        ImageView btnNotification = view.findViewById(R.id.btn_notification);

        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getInstance(requireContext()).getApiService();
        tv_progress_value = view.findViewById(R.id.tv_progress_value);
        progress_circle = view.findViewById(R.id.progress_circle);
        tv_current_income = view.findViewById(R.id.tv_current_income);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        logGpsOnEnter();

        //  Đồng bộ trạng thái từ SharedPreferences
        isCheckedIn = sessionManager.isCheckedIn();

        updateButtonStateDynamic();
        loadAttendancesAndFilterShifts();

        btnStartShift.setOnClickListener(v -> {
            if (!isCheckedIn) doCheckIn();
            else doCheckOut();
        });
        View card_btn_goals = view.findViewById(R.id.card_btn_goals);
        card_btn_goals.setOnClickListener(v -> showSetGoalDialog());

        View cardViewShifts = view.findViewById(R.id.card_view_shifts);
        View cardBtnRegister = view.findViewById(R.id.card_btn_register);
        View.OnClickListener navigateToShiftTab = v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_shift);
        };
        cardViewShifts.setOnClickListener(navigateToShiftTab);
        cardBtnRegister.setOnClickListener(navigateToShiftTab);

        handler.post(refreshRunnable);

        btnNotification.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_main, new NotificationFragment())
                    .addToBackStack(null)
                    .commit();
        });

    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            updateButtonStateDynamic();
            handler.postDelayed(this, 10000);
        }
    };

    private void doCheckIn() {
        String token = "Bearer " + sessionManager.getAccessToken();
        String registrationId = sessionManager.getTodayRegistrationId();

        if (registrationId == null || registrationId.isEmpty()) {
            Toast.makeText(requireContext(), " Không tìm thấy ca làm hôm nay để check-in", Toast.LENGTH_SHORT).show();
            return;
        }
        // 🕒 Log thời gian hiện tại
//        TimeZone vnTz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
//        SimpleDateFormat dfVN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//        dfVN.setTimeZone(vnTz);
//        Log.d("HomeFragment", "🕒 Giờ Việt Nam: " + dfVN.format(new Date()));

        // 📍 Kiểm tra quyền vị trí
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        btnStartShift.setEnabled(false);
        btnStartShift.setAlpha(0.6f);

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        Log.e("HomeFragment", " Location null");
                        Toast.makeText(requireContext(),
                                "Không lấy được vị trí",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //  KIỂM TRA FAKE GPS
                    if (isFakeGPS(location)) {
                        Log.e("HomeFragment", " PHÁT HIỆN FAKE GPS");

                        Toast.makeText(requireContext(),
                                "Phát hiện vị trí giả mạo. Không thể chấm công!",
                                Toast.LENGTH_LONG).show();

                        btnStartShift.setEnabled(true);
                        btnStartShift.setAlpha(1f);
                        return;
                    }

                    //  GPS THẬT → CHO CHECK-IN
                    Log.e("HomeFragment",
                            " GPS REAL → lat=" + location.getLatitude()
                                    + ", lng=" + location.getLongitude());

                    CheckInRequest body = new CheckInRequest(
                            registrationId,
                            location.getLatitude(),
                            location.getLongitude()
                    );

                    apiService.checkIn(token, body).enqueue(new Callback<ApiResponse<CheckInResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<CheckInResponse>> call, Response<ApiResponse<CheckInResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                isCheckedIn = true;
                                sessionManager.setCheckedIn(true);

                                sessionManager.saveCheckInTime(String.valueOf(System.currentTimeMillis()));
                                sessionManager.saveAttendanceId(response.body().getData().get_id());

                                Toast.makeText(requireContext(), " Check-in thành công!", Toast.LENGTH_SHORT).show();
                            }
                            updateButtonStateDynamic();
                            btnStartShift.setEnabled(true);
                            btnStartShift.setAlpha(1f);
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<CheckInResponse>> call, Throwable t) {
                            updateButtonStateDynamic();
                        }
                    });
                });
    }

    private void doCheckOut() {
        String token = "Bearer " + sessionManager.getAccessToken();
        String attendanceId = sessionManager.getAttendanceId();

        if (attendanceId == null || attendanceId.isEmpty()) {
            Toast.makeText(requireContext(), " Không tìm thấy mã attendance", Toast.LENGTH_SHORT).show();
            return;
        }

        btnStartShift.setEnabled(false);
        btnStartShift.setAlpha(0.6f);

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        Toast.makeText(requireContext(), " Không thể lấy vị trí", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CheckOutRequest body = new CheckOutRequest(attendanceId, location.getLatitude(), location.getLongitude());

                    apiService.checkOut(token, body).enqueue(new Callback<ApiResponse<CheckOutResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<CheckOutResponse>> call, Response<ApiResponse<CheckOutResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                isCheckedIn = false;
                                sessionManager.setCheckedIn(false);

                                sessionManager.saveCheckOutTime(String.valueOf(System.currentTimeMillis()));
                                sessionManager.saveTodayRegistrationId(null);

                                Toast.makeText(requireContext(), " Check-out thành công!", Toast.LENGTH_SHORT).show();

                                loadAttendancesAndFilterShifts();
                            }
                            updateButtonStateDynamic();
                            btnStartShift.setEnabled(true);
                            btnStartShift.setAlpha(1f);
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<CheckOutResponse>> call, Throwable t) {
                            updateButtonStateDynamic();
                        }
                    });
                });
    }

    private void loadAttendancesAndFilterShifts() {

        apiService.getAllAttendance(1, 100).enqueue(new Callback<ApiResponse<List<Attendance>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Attendance>>> call, Response<ApiResponse<List<Attendance>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadApprovedShift(response.body().getData());
                } else {
                    loadApprovedShift(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Attendance>>> call, Throwable t) {
                loadApprovedShift(null);
            }
        });
    }

    private void loadApprovedShift(List<Attendance> attendances) {
        String token = "Bearer " + sessionManager.getAccessToken();

        apiService.getAllShiftRegistrations(token).enqueue(new Callback<ApiResponse<List<ShiftRegistration>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShiftRegistration>>> call, Response<ApiResponse<List<ShiftRegistration>>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvShiftInfo.setText("Không thể tải ca sắp tới.");
                    return;
                }

                List<ShiftRegistration> regs = response.body().getData();
                if (regs == null || regs.isEmpty()) {
                    tvShiftInfo.setText("Không có ca nào được duyệt.");
                    return;
                }

                SimpleDateFormat utcFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                utcFmt.setTimeZone(TimeZone.getTimeZone("UTC"));

                SimpleDateFormat vnDateFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                vnDateFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

                SimpleDateFormat datetimeFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                datetimeFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

                Date now = new Date();
                ShiftRegistration nearest = null;
                long minDiff = Long.MAX_VALUE;
                String nearestDateStr = "";

                for (ShiftRegistration r : regs) {
                    if (!"approved".equalsIgnoreCase(r.getStatus())) continue;

                    // Bỏ ca đã checkout
                    if (attendances != null) {
                        boolean alreadyCheckedOut = false;
                        for (Attendance a : attendances) {
                            if (a.getRegistrationId() != null &&
                                    a.getRegistrationId().getId() != null &&
                                    a.getRegistrationId().getId().equals(r.getId()) &&
                                    "checked-out".equalsIgnoreCase(a.getStatus())) {
                                alreadyCheckedOut = true;
                                break;
                            }
                        }
                        if (alreadyCheckedOut) continue;
                    }

                    try {
                        Date utcDate = utcFmt.parse(r.getDate());
                        String vnDateStr = vnDateFmt.format(utcDate);
                        String[] times = r.getShiftTimeRange().split(" - ");
                        Date fullStart = datetimeFmt.parse(vnDateStr + " " + times[0].trim());
                        Date fullEnd = datetimeFmt.parse(vnDateStr + " " + times[1].trim());

                        if (fullStart != null && fullEnd != null && fullEnd.after(now)) {
                            long diff = Math.abs(fullStart.getTime() - now.getTime());
                            if (diff < minDiff) {
                                minDiff = diff;
                                nearest = r;
                                nearestDateStr = vnDateStr;
                                sessionManager.saveShiftTimes(fullStart.getTime(), fullEnd.getTime());
                            }
                        }
                    } catch (Exception ignored) {}
                }

                if (nearest != null) {
                    tvShiftInfo.setText(nearestDateStr + "\n" + nearest.getShiftName() + " (" + nearest.getShiftTimeRange() + ")");
                    sessionManager.saveTodayRegistrationId(nearest.getId());
                    updateButtonStateDynamic();
                } else {
                    tvShiftInfo.setText("Không có ca nào sắp diễn ra.");
                    sessionManager.saveTodayRegistrationId(null);
                    btnStartShift.setEnabled(false);
                    btnStartShift.setAlpha(0.5f);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShiftRegistration>>> call, Throwable t) {
                tvShiftInfo.setText("Lỗi tải ca sắp tới");
            }
        });
    }

    private void updateButtonStateDynamic() {
        Long startMillis = sessionManager.getShiftStartTime();
        Long endMillis = sessionManager.getShiftEndTime();

        if (startMillis == null || endMillis == null) {
            btnStartShift.setEnabled(false);
            btnStartShift.setAlpha(0.5f);
            return;
        }

        Date now = new Date();
        long diffToStart = startMillis - now.getTime();
        long diffToEnd = endMillis - now.getTime();

        //  CASE 1 — TRƯỚC GIỜ LÀM > 15 PHÚT
        if (diffToStart > 15 * 60 * 1000) {
            btnStartShift.setText("Bắt đầu");
            btnStartShift.setEnabled(false);
            btnStartShift.setAlpha(0.5f);
            return;
        }

        // CASE 2 — ĐANG TRONG CA (hoặc trước giờ làm ≤ 15 phút)
        if (diffToEnd > 0) {

            // *** CHƯA CHECK-IN ***
            if (!isCheckedIn) {
                btnStartShift.setText("Bắt đầu");
                btnStartShift.setEnabled(true);
                btnStartShift.setAlpha(1f);
                return;
            }

            // *** ĐÃ CHECK-IN ***
            btnStartShift.setText("Kết thúc");

            //  CHỈ BẬT KHI CÒN ≤5 PHÚT TRƯỚC GIỜ KẾT THÚC
            long fiveMinutesBeforeEnd = endMillis - (5 * 60 * 1000);

            if (now.getTime() >= fiveMinutesBeforeEnd) {
                btnStartShift.setEnabled(true);
                btnStartShift.setAlpha(1f);
            } else {
                btnStartShift.setEnabled(false);
                btnStartShift.setAlpha(0.5f);
            }

            return;
        }

        //  CASE 3 — HẾT GIỜ LÀM
        btnStartShift.setText("Ca đã kết thúc");
        btnStartShift.setEnabled(false);
        btnStartShift.setAlpha(0.5f);
    }

    private void showSetGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_set_goal, null);

        EditText etTarget = dialogView.findViewById(R.id.et_target_shifts);
        Button btnXacNhan = dialogView.findViewById(R.id.btn_xac_nhan);

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Đặt mục tiêu số ca")
                .show();

        btnXacNhan.setOnClickListener(v -> {
            String targetStr = etTarget.getText().toString().trim();
            if (!targetStr.isEmpty()) {
                int targetShifts = Integer.parseInt(targetStr);
                datMucTieu(targetShifts);
                dialog.dismiss();
            }
        });
    }
    private void datMucTieu(int targetShifts) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH bắt đầu từ 0
        int year = cal.get(Calendar.YEAR);
        CreateGoalRequest request = new CreateGoalRequest(targetShifts, month, year);
        Log.d("GOAL_REQUEST_JSON", new Gson().toJson(request));


        apiService.createOrUpdateGoal(request).enqueue(new Callback<ApiResponse<SalaryGoal>>() {
            @Override
            public void onResponse(Call<ApiResponse<SalaryGoal>> call,
                                   Response<ApiResponse<SalaryGoal>> response) {
                Log.e("DEBUG", "Raw JSON: " + new Gson().toJson(response.body()));


                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Đặt mục tiêu thành công!", Toast.LENGTH_SHORT).show();
                    loadTienDoHienTai();
                } else {
                    Toast.makeText(getContext(), "Đặt mục tiêu thất bại (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SalaryGoal>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTienDoHienTai() {
        apiService.getCurrentGoal().enqueue(new Callback<ApiResponse<CurrentGoalData>>() {
            @Override
            public void onResponse(Call<ApiResponse<CurrentGoalData>> call,
                                   Response<ApiResponse<CurrentGoalData>> response) {
                if (!isAdded()) return; // fragment đã detach

                if (!response.isSuccessful() || response.body() == null) {
                    // Không có goal cho tháng hiện tại hoặc lỗi server
                    tv_progress_value.setText("0/0");
                    progress_circle.setMax(1);
                    progress_circle.setProgress(0);
                    tv_current_income.setText("0");
                    return;
                }

                CurrentGoalData data = response.body().getData();
                if (data == null || data.getGoal() == null) {
                    tv_progress_value.setText("0/0");
                    progress_circle.setMax(1);
                    progress_circle.setProgress(0);
                    tv_current_income.setText("0");
                    return;
                }

                SalaryGoal goal = data.getGoal();

                int current = goal.getCurrentShifts();
                int target = goal.getTargetShifts();

                if (target <= 0) target = 1; // tránh max = 0

                tv_progress_value.setText(current + "/" + goal.getTargetShifts());
                progress_circle.setMax(target);
                progress_circle.setProgress(Math.min(current, target));
                tv_current_income.setText(formatTienVND(goal.getCurrentEarnings()));
            }

            @Override
            public void onFailure(Call<ApiResponse<CurrentGoalData>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("Goal", "loadTienDoHienTai onFailure", t);
                Toast.makeText(requireContext(),
                        "Lỗi tải mục tiêu: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTienVND(double amount) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return nf.format(amount);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadTienDoHienTai();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshRunnable);
    }
    private void logGpsOnEnter() {
        Log.e("HomeFragment", "📍 logGpsOnEnter() CALLED");

        // Kiểm tra quyền
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e("HomeFragment", "❌ CHƯA CÓ QUYỀN GPS");
            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location == null) {
                Log.e("HomeFragment", "❌ LOCATION = NULL");
                return;
            }

            Log.e("HomeFragment",
                    "  GPS ON ENTER → lat=" + location.getLatitude()
                            + ", lng=" + location.getLongitude());
        }).addOnFailureListener(e -> {
            Log.e("HomeFragment", "❌ GPS FAIL: " + e.getMessage());
        });
    }
    private boolean isFakeGPS(android.location.Location location) {
        if (location == null) return false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        }

        return false;
    }

}