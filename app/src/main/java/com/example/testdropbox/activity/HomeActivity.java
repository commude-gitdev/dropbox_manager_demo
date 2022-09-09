package com.example.testdropbox.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;


import com.example.testdropbox.R;
import com.example.testdropbox.adapter.VP2HomeAdapter;
import com.example.testdropbox.fragment.HomeFragment;
import com.example.testdropbox.fragment.SettingFragment;
import com.example.testdropbox.fragment.SyncFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        token = getIntent().getStringExtra("TOKEN");

        initToolbar();
        initView();
        initViewPager2();
        initBottomNav();


    }

    private void initBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    viewPager2.setCurrentItem(0);
                    break;
                case R.id.sync:
                    viewPager2.setCurrentItem(1);
                    break;
                case R.id.setting:
                    viewPager2.setCurrentItem(2);
                    break;
            }
            return true;
        });
    }

    private void initViewPager2() {
        Bundle bundle = new Bundle();
        bundle.putString("TOKEN", token);

        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setArguments(bundle);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        SyncFragment syncFragment = new SyncFragment();

        VP2HomeAdapter adapter = new VP2HomeAdapter(this, homeFragment, syncFragment, settingFragment);
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.sync).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.setting).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

    private void initView() {
        viewPager2 = findViewById(R.id.viewpager2);
        bottomNavigationView = findViewById(R.id.bottomNav);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }


}