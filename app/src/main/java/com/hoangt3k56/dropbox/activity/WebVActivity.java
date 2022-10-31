package com.hoangt3k56.dropbox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hoangt3k56.dropbox.R;

public class WebVActivity extends AppCompatActivity {

    WebView webView;

    private final String mURL = "https://www.dropbox.com/oauth2/authorize?client_id=sv2abu0lwbaoed6&response_type=token&redirect_uri=https://dropbox.demo.local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_vactivity);
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(mURL);

        // Cria problemas de XSS na aplicação. Usar com cuidado
        webView.getSettings().setJavaScriptEnabled(true);
    }
}