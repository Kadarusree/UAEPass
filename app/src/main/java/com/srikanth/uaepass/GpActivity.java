package com.srikanth.uaepass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GpActivity extends AppCompatActivity {

    public static final String EXTRA_AUTH_URL = "auth_url";
    public static final String EXTRA_IS_QA = "is_qa";
    public static final String EXTRA_SUCCESS_SCHEME = "success_scheme";
    public static final String EXTRA_FAILURE_SCHEME = "failure_scheme";
    public static final String RESULT_KEY = "UAE_PASS_RESULT";

    private WebView webView;
    private boolean isQa;
    private String successScheme;
    private String failureScheme;
    private String successUrl = "";
    private String failureUrl = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);

        webView = findViewById(R.id.uaePassWebView);
        ImageView closeButton = findViewById(R.id.closeButton);

        // Get data from intent
        Intent intent = getIntent();
        String initialAuthUrl = intent.getStringExtra(EXTRA_AUTH_URL);
        isQa = intent.getBooleanExtra(EXTRA_IS_QA, false);
        successScheme = intent.getStringExtra(EXTRA_SUCCESS_SCHEME);
        failureScheme = intent.getStringExtra(EXTRA_FAILURE_SCHEME);

        // Setup WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new UaePassWebViewClient());

        // Load UAE Pass URL
        if (initialAuthUrl != null) {

            webView.loadUrl(initialAuthUrl);
        }

        closeButton.setOnClickListener(v -> {
            sendResult("Cancel");
        });
    }

    private class UaePassWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String url = uri.toString();

            if (url.startsWith("uaepass")) {
                successUrl = uri.getQueryParameter("successurl");
                failureUrl = uri.getQueryParameter("failureurl");

                String updatedUrl = url
                        .replace("uaepass://", isQa ? "uaepassstg://" : "uaepass://");

                if (successUrl != null) {
                    updatedUrl = updatedUrl.replace(successUrl, successScheme);
                }
                if (failureUrl != null) {
                    updatedUrl = updatedUrl.replace(failureUrl, failureScheme);
                }

                openExternalUrl(updatedUrl);
                return true;
            }

            return url.startsWith("https") || url.startsWith("thiqa");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getQueryParameter("code") != null) {
                sendResult(uri.getQueryParameter("code"));
            } else if ("cancelledOnApp".equals(uri.getQueryParameter("error"))) {
                sendResult("Cancel");
            }
        }
    }

    private void openExternalUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("UaePass", "No handler for URL: " + url, e);
        }
    }

    private void sendResult(String result) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY, result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
