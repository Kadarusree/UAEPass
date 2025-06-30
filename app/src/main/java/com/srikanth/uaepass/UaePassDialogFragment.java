package com.srikanth.uaepass;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class UaePassDialogFragment extends DialogFragment {

    public interface UaePassCallback {
        void onResult(String codeOrCancel);
    }

    private WebView webView;
    private ImageView btnClose;

    private boolean loadSuccessUrl = false;
    private String successUrl = "";
    private String failureUrl = "";

    private boolean isUaePassQa = true; // pass this via newInstance if needed

    private UaePassCallback callback;

    // Constants (these should ideally be passed or loaded from config)
    private static final String URL_SCHEME = "srikanth";

    private static final String HOST_SUCCESS = "uaepasssuccess";
    private static final String HOST_FAILURE = "uaepassfailure";

   // private static final String HOST_SUCCESS = "digitalid-users-ids/signatures/success";
  //  private static final String HOST_FAILURE = "digitalid-users-ids/signatures/failure";

    // For simplicity, minimal set - replace with actual values from your config
    private static final String UAE_PASS_PACKAGE_ID = "ae.uaepass.app";
    private static final String UAE_PASS_QA_PACKAGE_ID = "ae.uaepass.qa.app";

    String REDIRECT_URL = "srikanth://redirect";

  //  private static final String REDIRECT_URL = "";
    private static final String SCOPE = "urn:uae:digitalid:profile:general";
    private static final String RESPONSE_TYPE = "code";
    private static final String UAE_PASS_CLIENT_ID = "sandbox_stage";
    private static final String UAE_PASS_CLIENT_SECRET = "HnlHOJTkTb66Y5H";
    private static final String UAE_PASS_CLIENT_ID_STG = "sandbox_stage";
    private static final String UAE_PASS_CLIENT_SECRET_STG = "HnlHOJTkTb66Y5H";
    private static final String BASE_URL = "https://stg-id.uaepass.ae";

    private static final String ACR_VALUES_MOBILE = "urn:digitalid:authentication:flow:mobileondevice";
    private static final String ACR_VALUES_WEB = "urn:safelayer:tws:policies:authentication:level:low";

    public static UaePassDialogFragment newInstance(boolean isQa, UaePassCallback callback) {
        UaePassDialogFragment fragment = new UaePassDialogFragment();
        fragment.isUaePassQa = isQa;
        fragment.callback = callback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_view_activity, container, false);
        webView = view.findViewById(R.id.uaePassWebView);
        btnClose = view.findViewById(R.id.closeButton);

        btnClose.setOnClickListener(v -> {
            if (callback != null) callback.onResult("Cancel");
            dismiss();
        });

        setupWebView();

        checkUaePassAppInstalledAndLoadUrl();

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearCache(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleUrlLoading(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrlLoading(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    private boolean handleUrlLoading(String url) {
        if (url.startsWith("uaepass") && TextUtils.isEmpty(successUrl)) {
            Map<String, String> params = getParamsFromUrl(url);
            successUrl = decode(params.get("successurl"));
            failureUrl = decode(params.get("failureurl"));

            if (isUaePassQa) {
                url = url.replace("uaepass://", "uaepassstg://");
            }

            url = updateURLParameter(url, "successurl", URL_SCHEME + "://" + HOST_SUCCESS);
            url = updateURLParameter(url, "failureurl", URL_SCHEME + "://" + HOST_FAILURE);

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true; // Prevent WebView from loading this URL
        }

        if (url.startsWith("https") || url.startsWith("thiqa")) {
            return false; // WebView loads this url
        }
        return true;
    }

    private void checkUaePassAppInstalledAndLoadUrl() {
        boolean isInstalled = false;
        String packageId = isUaePassQa ? UAE_PASS_QA_PACKAGE_ID : UAE_PASS_PACKAGE_ID;
        try {
            Context ctx = requireContext();
            ctx.getPackageManager().getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        String authUrl = getAuthUrl(isInstalled);
        webView.loadUrl("https://stg-id.uaepass.ae/idshub/authorize?redirect_uri=srikanth://redirect&client_id=sandbox_stage&response_type=code&state=HnlHOJTkTb66Y5H&scope=urn:uae:digitalid:profile:general&acr_values=urn:digitalid:authentication:flow:mobileondevice&ui_locales=en");
    }

    private String getAuthUrl(boolean isInstalled) {
        String clientId = isUaePassQa ? UAE_PASS_CLIENT_ID_STG : UAE_PASS_CLIENT_ID;
        String clientSecret = isUaePassQa ? UAE_PASS_CLIENT_SECRET_STG : UAE_PASS_CLIENT_SECRET;

      //  String acrValues = isInstalled ? ACR_VALUES_MOBILE : ACR_VALUES_WEB;

        String acrValues =  ACR_VALUES_MOBILE ;

        return BASE_URL + "/idshub/authorize"
                + "?redirect_uri=" + REDIRECT_URL
                + "&client_id=" + clientId
                + "&response_type=" + RESPONSE_TYPE
                + "&state=" + clientSecret
                + "&scope=" + SCOPE
                + "&acr_values=" + acrValues
                + "&ui_locales=en";
    }

    @Override
    public void onResume() {
        super.onResume();
        // Handle incoming deep links
        Intent intent = getActivity().getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().startsWith(URL_SCHEME + "://")) {
            handleDeepLink(data.toString());
        }
    }

    private void handleDeepLink(String url) {
        if (url.contains(HOST_SUCCESS)) {
            loadSuccessUrl = true;
            webView.loadUrl(successUrl);
        } else if (url.contains(HOST_FAILURE)) {
            loadSuccessUrl = false;
            dismiss();
            if (callback != null) callback.onResult("Cancel");
        } else {
            Map<String, String> params = getParamsFromUrl(url);
            if (params.containsKey("code")) {
                if (callback != null) callback.onResult(params.get("code"));
                dismiss();
            } else if (params.containsKey("error")) {
                if ("cancelledOnApp".equals(params.get("error"))) {
                    if (callback != null) callback.onResult("Cancel");
                    dismiss();
                } else {
                    dismiss();
                }
            }
        }
    }

    private Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> params = new HashMap<>();
        Uri uri = Uri.parse(url);
        for (String key : uri.getQueryParameterNames()) {
            params.put(key, uri.getQueryParameter(key));
        }
        return params;
    }

    private String decode(String s) {
        try {
            return s == null ? "" : URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private String updateURLParameter(String url, String param, String paramVal) {
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();

        // Remove existing param
        builder.clearQuery();
        for (String key : uri.getQueryParameterNames()) {
            if (!key.equals(param)) {
                builder.appendQueryParameter(key, uri.getQueryParameter(key));
            }
        }
        // Add updated param
        builder.appendQueryParameter(param, paramVal);

        return builder.build().toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog full screen
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
