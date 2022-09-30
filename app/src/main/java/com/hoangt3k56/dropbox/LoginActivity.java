package com.hoangt3k56.dropbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    public static final String KEY="sv2abu0lwbaoed6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.startOAuth2Authentication(getApplicationContext(),KEY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = Auth.getOAuth2Token();
        if(token!=null)
        {
            Intent intent=new Intent(this,MainActivity.class);
            intent.putExtra("TOKEN",token);
            startActivity(intent);
        }
    }
}