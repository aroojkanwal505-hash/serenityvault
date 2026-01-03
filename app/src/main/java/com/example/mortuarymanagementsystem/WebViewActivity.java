package com.example.mortuarymanagementsystem;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends BaseActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webView);

        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Set WebViewClient to open links in app
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(WebViewActivity.this, "Page loaded", Toast.LENGTH_SHORT).show();
            }
        });

        // Load a sample URL (related to mortuary/forensic medicine)
        webView.loadUrl("https://www.who.int/health-topics/forensic-medicine");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}