package com.hoangt3k56.dropbox;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token=getIntent().getStringExtra("TOKEN");

        viewPager2=findViewById(R.id.viewpager2);
        Bundle bundle=new Bundle();
        bundle.putString("TOKEN",token);

        SettingFragment settingFragment=new SettingFragment();
        settingFragment.setArguments(bundle);

        HomeFragment homeFragment=new HomeFragment();
        homeFragment.setArguments(bundle);

        SyncFragment syncFragment=new SyncFragment();

        MyHomeAdapter adapter=new MyHomeAdapter(this,homeFragment,syncFragment,settingFragment);
        viewPager2.setAdapter(adapter);

        bottomNavigationView=findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home : viewPager2.setCurrentItem(0); break;
                    case R.id.sync : viewPager2.setCurrentItem(1); break;
                    case R.id.setting: viewPager2.setCurrentItem(2); break;
                }

                return true;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0 : bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true); break;
                    case 1 : bottomNavigationView.getMenu().findItem(R.id.sync).setChecked(true); break;
                    case 2 : bottomNavigationView.getMenu().findItem(R.id.setting).setChecked(true);; break;
                }
                super.onPageSelected(position);
            }
        });

    }



//    @SuppressLint("RestrictedApi")
//    @Override
//    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.right_menu,menu);
//
//        if(menu instanceof MenuBuilder){
//
//            MenuBuilder menuBuilder = (MenuBuilder) menu;
//            menuBuilder.setOptionalIconsVisible(true);
//        }
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.upload: pickFile(); break;
//            case R.id.refresh:
//                Log.e("AAA", "rfMAin");
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void pickFile() {
        Intent data = new Intent(Intent.ACTION_GET_CONTENT);
        data.addCategory(Intent.CATEGORY_OPENABLE);
        data.setType("*/*");
        Intent intent = Intent.createChooser(data, "Choose a file");
        ActivityResultLauncher<Intent> startActivityForResult = new ActivityResultLauncher<Intent>() {
            @Override
            public void launch(Intent input, @Nullable ActivityOptionsCompat options) {

            }

            @Override
            public void unregister() {

            }

            @NonNull
            @Override
            public ActivityResultContract<Intent, ?> getContract() {
                return null;
            }
        };
        startActivityForResult.launch(intent);
    }
//
//
//
//    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        if (result.getResultCode() == Activity.RESULT_OK) {
//            Intent data = result.getData();
////            new UploadFile().execute(data.getData());
//        }
//    });

}