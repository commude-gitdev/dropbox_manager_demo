package com.hoangt3k56.dropbox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.api.ApiService;
import com.hoangt3k56.dropbox.model.Enty;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
//                Intent intent = new Intent(LoginActivity.this, WebVActivity.class);
//                startActivity(intent);

//                String token = "Bearer sl.BRx5weF0KD9_qzXKdZzzhttGUDKZFzqNx7XvofWCPtTyz2Yf-PijY2xn21VzLAyp8I_ms6mxo13gG5qMjUdCLcLhiNS2f2mJ2QdEqbsMDg5XcRNPjUb2QxUGLMo2GBOMQCKaq6BUgTbg";
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra("TOKEN",token);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = Auth.getOAuth2Token();
        Log.d("hoangdev", "tocken  " + token);
        if(token!=null)
        {
            Intent intent=new Intent(this, MainActivity.class);
            intent.putExtra("TOKEN","Bearer " + token);
            startActivity(intent);
        }
    }
}