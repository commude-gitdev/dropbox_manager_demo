package com.example.testdropbox.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

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
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                openCustomTab(MainActivity.this, customTabsIntent, Uri.parse("" +
                        "https://www.dropbox.com/oauth2/authorize?client_id="
                        + BuildConfig.API_KEY + "&response_type=token&redirect_uri=auth://callback"));
            }
        });

    }

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        String packageName = "com.android.chrome";
        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        } else {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("AAA","onNewIntent"+intent.toString());
        super.onNewIntent(intent);
        this.setIntent(intent);
        Uri appLinkData = intent.getData();
        if (appLinkData == null) return;
        String url = appLinkData.toString();
        if (url.contains("#access_token=")) {
            String[] split1 = url.split("#access_token=", 2);
            String[] split2 = split1[split1.length - 1].split("&");
            String token = split2[0];
            if (token != null) {
                Intent intent2=new Intent(this,HomeActivity.class);
                intent2.putExtra("TOKEN",token);
                startActivity(intent2);
            }
        } else {
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }




}