package com.example.testdropbox.activity;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

import com.example.testdropbox.BuildConfig;
import com.example.testdropbox.R;

import java.util.Date;


public class MainActivity extends AppCompatActivity {

    String packageName = "com.android.chrome";
    CustomTabsSession ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(@NonNull ComponentName name, CustomTabsClient client) {
                client.warmup(0);
                ss = client.newSession(new CustomTabsCallback() {
                    @Override
                    public void onNavigationEvent(int navigationEvent, @Nullable Bundle extras) {
                        super.onNavigationEvent(navigationEvent, extras);
                        switch (navigationEvent){
                            case 1 : Log.d("AAA","NAVIGATION_STARTED "); break;
                            case 2 : Log.d("AAA","NAVIGATION_FINISHED "); break;
                            case 3 : Log.d("AAA","NAVIGATION_FAILED "); break;
                            case 4 : Log.d("AAA","NAVIGATION_ABORTED "); break;
                            case 5 : Log.d("AAA","TAB_SHOWN "); break;
                            case 6 : Log.d("AAA","TAB_HIDDEN  "); break;
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

        };

        boolean bindSuccess = CustomTabsClient.bindCustomTabsService(this, packageName, connection);
        findViewById(R.id.btnLogin2).setOnClickListener(view -> {
            startActivity(new Intent(this,WebViewActivity.class));
            finish();

        });
        findViewById(R.id.btnLogin).setOnClickListener(view -> {

            int colorInt = Color.parseColor("#FFFFFF");
            CustomTabColorSchemeParams defaultColors = new CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(colorInt)
                    .build();

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(defaultColors)
                    .addMenuItem("Check", createPendingIntent())
                    .setSession(ss);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            openCustomTab(MainActivity.this, customTabsIntent, Uri.parse("" +
                    "https://www.dropbox.com/oauth2/authorize?client_id="
                    + BuildConfig.API_KEY + "&response_type=token&redirect_uri=auth://callback"));
        });

    }

    public void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {

        try {
            if (getPackageManager().getPackageInfo("com.android.chrome", 0) != null) {
                customTabsIntent.intent.setPackage(packageName);
                customTabsIntent.launchUrl(activity, uri);
            } else {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("AAA", e.toString());
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
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
                Intent intent2 = new Intent(this, HomeActivity.class);
                intent2.putExtra("TOKEN", token);
                startActivity(intent2);
            }
        } else {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private PendingIntent createPendingIntent() {
        Intent actionIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        return PendingIntent.getBroadcast(
                this.getApplicationContext(), (int) new Date().getTime(), actionIntent, FLAG_IMMUTABLE);
    }


}