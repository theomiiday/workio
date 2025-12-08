package com.example.workio.ui.main.shift;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.workio.R;

import java.util.ArrayList;
import java.util.List;

public class ShiftListAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> availableShifts;
    private final List<String> selectedShifts;
    private final List<String> initialRegisteredShifts;

    public ShiftListAdapter(Context context, List<String> availableShifts, List<String> currentRegisteredShifts) {
        this.context = context;
        this.availableShifts = availableShifts;
        this.selectedShifts = new ArrayList<>(currentRegisteredShifts);
        this.initialRegisteredShifts = new ArrayList<>(currentRegisteredShifts);
    }

    @Override
    public int getCount() {
        return availableShifts.size();
    }

    @Override
    public Object getItem(int position) {
        return availableShifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // ⭐ Giả định layout file là shift_item_layout
            convertView = LayoutInflater.from(context).inflate(R.layout.shift_item_layout, parent, false);
        }

        // ⭐ Khắc phục lỗi: Kiểm tra lại ID của TextView này trong shift_item_layout.xml
        // Tôi giữ tên là shiftName theo code cũ, nhưng bạn PHẢI đảm bảo nó khớp
        // với id trong XML của bạn (ví dụ: android:id="@+id/shiftName")
        TextView shiftName = convertView.findViewById(R.id.shiftName);
        String shift = availableShifts.get(position);
        shiftName.setText(shift);

        // --- Logic hiển thị đã chỉnh sửa để tránh lỗi màu ---
        if (initialRegisteredShifts.contains(shift)) {
            // Ca đã đăng ký ban đầu (Ca KHÔNG được hủy chọn)
            shiftName.setTextColor(Color.parseColor("#6B6B6B")); // Màu xám đậm
            // Sử dụng màu android.R.color.holo_green_light để highlight đã đăng ký
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        } else if (selectedShifts.contains(shift)) {
            // Ca MỚI được chọn
            shiftName.setTextColor(Color.parseColor("#007B83"));
            // Sử dụng màu primary mặc định của theme
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedItemBackground));
        } else {
            // Ca chưa được chọn
            shiftName.setTextColor(Color.BLACK);
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    public void toggleSelection(int position) {
        String shift = availableShifts.get(position);

        if (initialRegisteredShifts.contains(shift)) {
            Toast.makeText(context, "Ca này đã được đăng ký. Vui lòng hủy ở giao diện chính.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedShifts.contains(shift)) {
            selectedShifts.remove(shift);
        } else {
            selectedShifts.add(shift);
        }
    }

    public List<String> getSelectedShifts() {
        return selectedShifts;
    }
}