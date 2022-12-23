package com.example.testdropbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testdropbox.BuildConfig;
import com.example.testdropbox.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView wv = findViewById(R.id.webView);
        String url = "" +
                "https://www.dropbox.com/oauth2/authorize?client_id="
                + BuildConfig.API_KEY + "&response_type=token&redirect_uri=auth://callback";
        Log.d("AAA", url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d("AAA", url.toString());
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().contains("#access_token=")) {
                    String[] split1 = request.getUrl().toString().split("#access_token=", 2);
                    String[] split2 = split1[split1.length - 1].split("&");
                    String token = split2[0];
                    if (token != null) {
                        Intent intent2 = new Intent(WebViewActivity.this, HomeActivity.class);
                        intent2.putExtra("TOKEN", token);
                        startActivity(intent2);
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("AAA", url.toString());
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d("AAA", error.toString());
                super.onReceivedError(view, request, error);
            }

        });
    }
}