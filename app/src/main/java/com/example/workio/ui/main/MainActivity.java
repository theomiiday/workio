package com.example.workio.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.workio.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //  Mặc định mở HomeFragment khi vào app
        if (savedInstanceState == null) {
            loadFragment(new com.example.workio.ui.main.home.HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        //  Lắng nghe khi user chọn tab
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new com.example.workio.ui.main.home.HomeFragment();
            } else if (itemId == R.id.nav_shift) {
                selectedFragment = new com.example.workio.ui.main.shift.ShiftFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new com.example.workio.ui.main.chat.ChatListFragment();
            } else if (itemId == R.id.nav_more) {
                selectedFragment = new com.example.workio.ui.main.more.MoreFragment();
            }


            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    /**
     * 🧭 Hàm cho phép Fragment gọi để chuyển tab
     * (VD: từ HomeFragment → Ca làm)
     */
    public void navigateToTab(int menuItemId) {
        bottomNavigationView.setSelectedItemId(menuItemId);
    }

    /**
     * 🔄 Load fragment mới lên container
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_main, fragment)
                .commit();
    }
    public void setBottomNavVisibility(boolean visible) {
        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        if (navView != null) {
            navView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

}
