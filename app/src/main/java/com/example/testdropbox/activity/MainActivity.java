package com.example.testdropbox.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dropbox.core.android.Auth;
import com.example.testdropbox.BuildConfig;
import com.example.testdropbox.R;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.startOAuth2Authentication(getApplicationContext(),BuildConfig.API_KEY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = Auth.getOAuth2Token();
        if (token != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
            finish();
        }
    }
}