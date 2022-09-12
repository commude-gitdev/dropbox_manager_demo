package com.example.testdropbox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.android.Auth;
import com.example.testdropbox.R;

public class MainActivity extends AppCompatActivity {

    public static final String KEY = "ipjcwa57av127b2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.startOAuth2Authentication(getApplicationContext(), KEY);
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