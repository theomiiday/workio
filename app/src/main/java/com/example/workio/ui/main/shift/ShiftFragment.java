package com.example.workio.ui.main.shift;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.Attendance;
import com.example.workio.data.model.Shift;
import com.example.workio.data.model.ShiftRegistration;
import com.example.workio.utils.SessionManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftFragment extends Fragment {

    private CompactCalendarView calendarView;
    private TextView textViewSelectedDate, textViewMonth;
    private LinearLayout shiftButtonContainer;

    private final Map<String, List<ShiftRegistration>> shiftMap = new HashMap<>();
    private String currentSelectedDate = "";

    private final SimpleDateFormat dateFormatDisplay = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.US);
    private List<Attendance> attendanceList = new ArrayList<>();

    public ShiftFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shift, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        textViewMonth = view.findViewById(R.id.textViewMonth);
        shiftButtonContainer = view.findViewById(R.id.shiftButtonContainer);
        Button buttonAddShift = view.findViewById(R.id.buttonAddShift);

        // Khởi tạo lịch
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setLocale(TimeZone.getDefault(), Locale.getDefault());
        updateMonthText(new Date());

        String[] weekdays = {"H", "B", "T", "N", "S", "B", "C"};
        calendarView.setDayColumnNames(weekdays);

        // Cập nhật ngày hiện tại lần đầu
        Date today = new Date();
        currentSelectedDate = dateFormatDisplay.format(today);
        textViewSelectedDate.setText("Ngày " + currentSelectedDate + ":");

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                currentSelectedDate = dateFormatDisplay.format(dateClicked);
                textViewSelectedDate.setText("Ngày " + currentSelectedDate + ":");
                updateRegisteredShiftUI(currentSelectedDate);
                Log.d("ShiftFragment", "🟢 Người dùng click vào ngày: " + currentSelectedDate);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateMonthText(firstDayOfNewMonth);
            }
        });

        // Tải dữ liệu API
        loadShiftRegistrationsFromApi();
        loadAttendance();

        buttonAddShift.setOnClickListener(v -> {
            if (!currentSelectedDate.isEmpty())
                showShiftSelectionDialog(currentSelectedDate);
            else
                Toast.makeText(requireContext(), "Vui lòng chọn ngày trên lịch.", Toast.LENGTH_SHORT).show();
        });

        logCurrentTime();
    }

    private void updateMonthText(Date date) {
        textViewMonth.setText("Tháng " + monthFormat.format(date));
    }

    private void logCurrentTime() {
        Date now = new Date();

        SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        localFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Log.d("ShiftFragment", "🕒 Local (VN): " + localFormat.format(now));
        Log.d("ShiftFragment", "🌍 UTC: " + utcFormat.format(now));
    }

    // --- Load dữ liệu từ API ---
    private void loadShiftRegistrationsFromApi() {
        SessionManager session = new SessionManager(requireContext());
        String token = session.getAccessToken();
        if (token == null) return;

        ApiService api = RetrofitClient.getInstance(requireContext()).getApiService();

        api.getAllShiftRegistrations("Bearer " + token).enqueue(new Callback<ApiResponse<List<ShiftRegistration>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShiftRegistration>>> call,
                                   Response<ApiResponse<List<ShiftRegistration>>> res) {

                if (!res.isSuccessful() || res.body() == null) {
                    Log.e("ShiftFragment", "API response not successful: " + res.code());
                    return;
                }

                shiftMap.clear();
                List<ShiftRegistration> regs = res.body().getData();
                if (regs == null) return;

                Log.d("ShiftFragment", "📦 Tổng số ca nhận được: " + regs.size());

                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                SimpleDateFormat vnFormatDisplay = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                vnFormatDisplay.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

                for (ShiftRegistration r : regs) {
                    try {
                        if (r.getDate() == null) continue;
                        Date utcDate = utcFormat.parse(r.getDate());
                        String vnDate = vnFormatDisplay.format(utcDate);

                        Log.d("ShiftFragment", "📅 Server UTC: " + r.getDate() + " → VN Date: " + vnDate);
                        shiftMap.computeIfAbsent(vnDate, k -> new ArrayList<>()).add(r);

                    } catch (Exception e) {
                        Log.e("ShiftFragment", "⚠️ Parse date error: " + r.getDate(), e);
                    }
                }

                updateCalendarEvents();
                updateRegisteredShiftUI(currentSelectedDate);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShiftRegistration>>> call, Throwable t) {
                Log.e("ShiftFragment", "❌ load error", t);
                Toast.makeText(requireContext(), "Lỗi mạng khi tải ca làm.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Cập nhật event lên lịch ---
    private void updateCalendarEvents() {
        calendarView.removeAllEvents();
        for (String dateKey : shiftMap.keySet()) {
            try {
                Date d = dateFormatDisplay.parse(dateKey);
                if (d != null) {
                    calendarView.addEvent(new Event(Color.GREEN, d.getTime(), shiftMap.get(dateKey).size()));
                }
            } catch (ParseException ignored) {}
        }
    }

    // --- Hiển thị danh sách ca ---
    private void updateRegisteredShiftUI(String dateDisplay) {
        shiftButtonContainer.removeAllViews();

        List<ShiftRegistration> regs = shiftMap.get(dateDisplay);
        if (regs == null || regs.isEmpty()) {
            textViewSelectedDate.setText("Ngày " + dateDisplay + ": (Không có ca)");
            return;
        }

        textViewSelectedDate.setText("Ngày " + dateDisplay + " có " + regs.size() + " ca:");

        for (ShiftRegistration r : regs) {

            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_shift_row, shiftButtonContainer, false);

            TextView tvShiftInfo = row.findViewById(R.id.textShiftInfo);
            TextView tvAttendanceTime = row.findViewById(R.id.tv_attendance_time);
            ImageView iconShiftStatus = row.findViewById(R.id.iconShiftStatus);
            Button btnCancel = row.findViewById(R.id.btnCancelShift);

            // Tên ca
            String shiftText = r.getShiftName() + " (" + r.getShiftTimeRange() + ")";
            tvShiftInfo.setText(shiftText);

            // Mặc định ẩn giờ
            tvAttendanceTime.setVisibility(View.GONE);

            // -- TÌM attendance TƯƠNG ỨNG --
            Attendance matched = null;
            for (Attendance a : attendanceList) {
                if (a.getRegistrationId() != null &&
                        a.getRegistrationId().getId().equals(r.getId())) {
                    matched = a;
                    break;
                }
            }

            // CASE 1: CHƯA CHECK-IN → hiện icon + nút huỷ, ẩn giờ
            if (matched == null || matched.getCheckInTime() == null) {

                tvAttendanceTime.setVisibility(View.GONE);   // ẨN GIỜ
                iconShiftStatus.setVisibility(View.VISIBLE); // HIỆN ICON
                btnCancel.setVisibility(View.VISIBLE);       // HIỆN HUỶ

                // Icon theo status ca làm
                String status = r.getStatus() != null ? r.getStatus().toLowerCase() : "";
                switch (status) {
                    case "approved":
                        iconShiftStatus.setImageResource(R.drawable.ic_check_circle);
                        iconShiftStatus.setColorFilter(Color.parseColor("#4CAF50"));
                        break;
                    case "pending":
                        iconShiftStatus.setImageResource(R.drawable.ic_hourglass_empty);
                        iconShiftStatus.setColorFilter(Color.parseColor("#FFC107"));
                        break;
                    case "rejected":
                        iconShiftStatus.setImageResource(R.drawable.ic_cancel);
                        iconShiftStatus.setColorFilter(Color.parseColor("#F44336"));
                        btnCancel.setVisibility(View.GONE); // rejected thì không được huỷ
                        break;
                    default:
                        iconShiftStatus.setImageResource(0);
                        break;
                }
            }

            // CASE 2: ĐÃ CHECK-IN hoặc CHECK-OUT → hiện giờ, ẩn icon + nút huỷ
            else {

                String ci = matched.getCheckInTime() != null ? formatTime(matched.getCheckInTime()) : "--:--";
                String co = matched.getCheckOutTime() != null ? formatTime(matched.getCheckOutTime()) : "--:--";

                tvAttendanceTime.setText(ci + " - " + co);
                tvAttendanceTime.setVisibility(View.VISIBLE);

                // ẨN icon + huỷ
                iconShiftStatus.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

                // ------- 🔥 TÍNH TRỄ / ĐÚNG GIỜ 🔥 -------

                try {
                    // --- 1) Parse giờ bắt đầu ca (HH:mm) ---
                    String[] times = r.getShiftTimeRange().split("-");
                    String shiftStartStr = times[0].trim(); // "19:05"

                    // --- 2) Parse ngày ca làm (dd-MM-yyyy → yyyy-MM-dd) ---
                    SimpleDateFormat dfDisplay = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    SimpleDateFormat dfStore = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                    Date parsedDate = dfDisplay.parse(currentSelectedDate);
                    String dayString = dfStore.format(parsedDate); // "2025-02-28"

                    // --- 3) Combine thành full datetime của CA LÀM ---
                    SimpleDateFormat fullFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                    Date shiftStart = fullFmt.parse(dayString + " " + shiftStartStr);

                    // --- 4) Parse CHECK-IN full UTC → full VN datetime ---
                    SimpleDateFormat utcFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                    utcFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date ciUTC = utcFmt.parse(matched.getCheckInTime());

                    SimpleDateFormat vnFullFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                    vnFullFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                    Date ciVN = vnFullFmt.parse(vnFullFmt.format(ciUTC));

                    // --- 5) So sánh trễ ---
                    if (ciVN.after(shiftStart)) {
                        // Đi trễ
                        tvAttendanceTime.setTextColor(Color.parseColor("#F44336"));
                    } else {
                        // Đúng giờ hoặc sớm
                        tvAttendanceTime.setTextColor(Color.parseColor("#4CAF50"));
                    }

                } catch (Exception e) {
                    tvAttendanceTime.setTextColor(Color.parseColor("#444444"));
                }
            }


            // Gán sự kiện huỷ ca
            btnCancel.setOnClickListener(v -> cancelShiftRegistration(r, dateDisplay));

            shiftButtonContainer.addView(row);
        }
    }


    // --- Huỷ ca ---
    private void cancelShiftRegistration(ShiftRegistration reg, String date) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận huỷ ca làm")
                .setMessage("Bạn có chắc muốn huỷ ca \"" + reg.getShiftName()
                        + "\" vào ngày " + date + " không?")
                .setPositiveButton("Huỷ ca", (dialog, which) -> {
                    performCancelShiftApi(reg, date);
                    dialog.dismiss();
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void performCancelShiftApi(ShiftRegistration reg, String date) {
        SessionManager session = new SessionManager(requireContext());
        String token = session.getAccessToken();
        if (token == null) return;

        ApiService api = RetrofitClient.getInstance(requireContext()).getApiService();

        api.cancelShiftRegistration("Bearer " + token, reg.getId())
                .enqueue(new Callback<ApiResponse<Void>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(),
                                    "Đã huỷ ca: " + reg.getShiftName(),
                                    Toast.LENGTH_SHORT).show();

                            List<ShiftRegistration> regs = shiftMap.get(date);
                            if (regs != null) {
                                regs.remove(reg);
                                if (regs.isEmpty()) shiftMap.remove(date);
                            }

                            updateRegisteredShiftUI(date);
                            updateCalendarEvents();

                        } else {
                            Toast.makeText(requireContext(),
                                    "Không thể huỷ ca (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                "Lỗi mạng khi huỷ ca.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- Đăng ký nhiều ca ---
    private void registerMultipleShifts(List<Shift> selectedShifts, String date) {

        SessionManager sm = new SessionManager(requireContext());
        String token = sm.getAccessToken();
        String empId = sm.getEmployeeId();

        ApiService api = RetrofitClient.getInstance(requireContext()).getApiService();

        for (Shift s : selectedShifts) {

            Map<String, Object> body = new HashMap<>();
            body.put("shiftId", s.getId());
            body.put("note", "Tôi muốn đăng ký ca này");

            try {
                SimpleDateFormat vnDateParse = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Date selectedDate = vnDateParse.parse(date);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                body.put("date", df.format(selectedDate)); // giống Postman

            } catch (ParseException e) {
                Log.e("ShiftFragment", "⚠️ Parse date error", e);
            }

            api.registerShift("Bearer " + token, body)
                    .enqueue(new Callback<ApiResponse<ShiftRegistration>>() {

                        @Override
                        public void onResponse(Call<ApiResponse<ShiftRegistration>> call,
                                               Response<ApiResponse<ShiftRegistration>> res) {

                            if (res.isSuccessful() && res.body() != null) {
                                loadShiftRegistrationsFromApi();
                                Toast.makeText(requireContext(),
                                        "Đăng ký: " + s.getShiftName(),
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(requireContext(),
                                        "Lỗi đăng ký ca: " + s.getShiftName()
                                                + " (" + res.message() + ")",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ShiftRegistration>> call, Throwable t) {
                            Toast.makeText(requireContext(),
                                    "Lỗi mạng khi đăng ký.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // --- Hiển thị danh sách ca ---
    private void showShiftSelectionDialog(String date) {

        SessionManager session = new SessionManager(requireContext());
        String token = session.getAccessToken();

        if (token == null) {
            Toast.makeText(requireContext(), "Thiếu token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getInstance(requireContext()).getApiService();

        api.getShifts().enqueue(new Callback<ApiResponse<List<Shift>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<Shift>>> call,
                                   Response<ApiResponse<List<Shift>>> res) {

                if (res.isSuccessful() && res.body() != null) {

                    List<Shift> allShifts = res.body().getData();
                    if (allShifts == null || allShifts.isEmpty()) {
                        Toast.makeText(requireContext(), "Không có ca làm nào.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String empBranch = session.getBranchId();
                    List<Shift> availableShifts = new ArrayList<>();

                    for (Shift s : allShifts) {
                        if (empBranch != null && empBranch.equals(s.getBranchIdString())) {
                            availableShifts.add(s);
                        }
                    }

                    showShiftListDialog(availableShifts, date);

                } else {
                    Toast.makeText(requireContext(),
                            "Không thể tải danh sách ca làm.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Shift>>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showShiftListDialog(List<Shift> shifts, String date) {

        List<ShiftRegistration> regsOfDate = shiftMap.getOrDefault(date, new ArrayList<>());

        List<String> registeredShiftIds = new ArrayList<>();
        for (ShiftRegistration reg : regsOfDate) {
            if (!"rejected".equalsIgnoreCase(reg.getStatus()))
                registeredShiftIds.add(reg.getShiftId());
        }

        List<Shift> availableShifts = new ArrayList<>();
        for (Shift s : shifts) {
            if (!registeredShiftIds.contains(s.getId()))
                availableShifts.add(s);
        }

        if (availableShifts.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Tất cả ca trong ngày này đã được đăng ký hoặc đang chờ duyệt/đã duyệt.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String[] shiftNames = new String[availableShifts.size()];
        for (int i = 0; i < availableShifts.size(); i++) {
            Shift s = availableShifts.get(i);
            shiftNames[i] =
                    s.getShiftName() + " (" + s.getStartTime() + " - " + s.getEndTime() + ")";
        }

        List<Shift> selectedShifts = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn ca làm cho ngày " + date);

        builder.setMultiChoiceItems(shiftNames, null, (dialog, which, isChecked) -> {
            Shift s = availableShifts.get(which);
            if (isChecked) selectedShifts.add(s);
            else selectedShifts.remove(s);
        });

        builder.setPositiveButton("Đăng ký", (dialog, which) -> {
            if (selectedShifts.isEmpty()) {
                Toast.makeText(requireContext(), "Bạn chưa chọn ca nào!", Toast.LENGTH_SHORT).show();
                return;
            }
            registerMultipleShifts(selectedShifts, date);
            dialog.dismiss();
        });

        builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void loadAttendance() {
        SessionManager sm = new SessionManager(requireContext());
        String token = sm.getAccessToken();

        ApiService api = RetrofitClient.getInstance(requireContext()).getApiService();
        api.getAllAttendance(1, 100).enqueue(new Callback<ApiResponse<List<Attendance>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Attendance>>> call, Response<ApiResponse<List<Attendance>>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    attendanceList = res.body().getData();
                    updateRegisteredShiftUI(currentSelectedDate); // load lại giao diện
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Attendance>>> call, Throwable t) {
                Log.e("ShiftFragment", "loadAttendance lỗi: " + t.getMessage());
            }
        });
    }
    private String formatTime(String utcString) {
        if (utcString == null) return "--:--";
        try {
            SimpleDateFormat utcFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
            utcFmt.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date d = utcFmt.parse(utcString);

            SimpleDateFormat vnFmt = new SimpleDateFormat("HH:mm", Locale.US);
            vnFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            return vnFmt.format(d);
        } catch (Exception e) {
            return "--:--";
        }
    }
}
