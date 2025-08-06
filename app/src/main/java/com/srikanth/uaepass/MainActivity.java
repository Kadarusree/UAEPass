package com.srikanth.uaepass;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.srikanth.uaepass.apis.ApiService;
import com.srikanth.uaepass.apis.RetrofitClient;
import com.srikanth.uaepass.apis.TokenResponse;
import com.srikanth.uaepass.apis.UserProfileResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    Button appLogin, webLogin;
    WebView mWebView;
    TextView statusTextView, userInfoTextView;

    public boolean isWebLogin = false;
    public boolean isProd = false;


    String LOGIN_URL_STG = "https://stg-id.uaepass.ae/idshub/authorize?";
    String LOGIN_URL = "https://id.uaepass.ae/idshub/authorize?";

    String ACRN_MOBILE = "urn:digitalid:authentication:flow:mobileondevice";
    String ACRN_WEB = "urn:safelayer:tws:policies:authentication:level:low";

    String APP_REDIRECT_URL = "srikanth://redirect";
    String APP_REDIRECT_URL_PROD = "";

    String URL_SCHEME = "srikanth";
    String URL_SCHEME_PROD = "";

    String HOST_SUCCESS = "uaepasssuccess";
    String HOST_FAILURE = "uaepassfailure";

    String successUrl = "";
    String failureUrl = "";

    String UAE_PASS_CLIENT_ID_STG = "sandbox_stage";
    String UAE_PASS_CLIENT_ID = "";

    String UAE_PASS_CLIENT_SECRET_STG = "HnlHOJTkTb66Y5H";
    String UAE_PASS_CLIENT_SECRET = "";

    String SCOPE = "urn:uae:digitalid:profile:general";
    String RESPONSE_TYPE = "code";

    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusTextView = findViewById(R.id.authStatus);
        userInfoTextView = findViewById(R.id.userInfo);
        appLogin = findViewById(R.id.btnLogin);
        webLogin = findViewById(R.id.WebLogin);
        mWebView = findViewById(R.id.mWebView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        isWebLogin = false;
        
        // Initialize UI with default state
        statusTextView.setText(getString(R.string.status_ready));
        userInfoTextView.setText("No user information available");

        appLogin.setOnClickListener(view -> {
            // Handle app login button click
            statusTextView.setText(getString(R.string.status_authenticating));
            userInfoTextView.setText("Preparing mobile authentication...");
            mWebView.clearHistory();
            mWebView.clearCache(true);
            isWebLogin = false;
            mWebView.loadUrl(getURL(false));
        });

        webLogin.setOnClickListener(view -> {
            statusTextView.setText(getString(R.string.status_authenticating));
            userInfoTextView.setText("Preparing web authentication...");
            mWebView.clearHistory();
            mWebView.clearCache(true);
            isWebLogin = true;
            mWebView.loadUrl(getURL(isWebLogin));
        });



        // WebViewClient to handle URL loading and redirection
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("6010944 - shouldOverrideUrlLoading : " + url);


                if (url.startsWith(URL_SCHEME)||url.startsWith(URL_SCHEME_PROD)) {
                    Uri uri = Uri.parse(url);
                    if (uri.getQueryParameter("code") != null) {
                        statusTextView.setText(getString(R.string.status_success));
                        userInfoTextView.setText("Authentication successful! Processing user data...");
                        view.loadUrl("https://kadarisrikanth.com/");
                        getToken(uri.getQueryParameter("code"));
                    } else if (uri.getQueryParameter("error") != null) {
                        view.loadUrl("https://kadarisrikanth.com/");
                        statusTextView.setText(getString(R.string.status_error));
                        userInfoTextView.setText("Authentication failed: " + uri.getQueryParameter("error"));
                    }
                    return false;
                }

                if (url.startsWith("uaepass") && successUrl == "" && !isWebLogin) {
                    String urlScheme = isProd ? URL_SCHEME_PROD : URL_SCHEME;
                    Map<String, String> queryParams = getParamsFromURL(url);
                    successUrl = queryParams.get("successurl");
                    failureUrl = queryParams.get("failureurl");
                    url = isProd ? url : url.replace("uaepass://", "uaepassstg://");
                    url = updateURLParameter(
                            url,
                            "successurl",
                            urlScheme + "://" + HOST_SUCCESS
                    );
                    url = updateURLParameter(
                            url,
                            "failureurl",
                            urlScheme + "://" + HOST_FAILURE
                    );

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println("6010944 - onPageFinished : " + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                System.out.println("6010944 - onReceivedError : " + errorCode + " - " + description + " - " + failingUrl);
            }

        });


    }

    //
    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        System.out.println("6010944 - onNewIntent : " + data.toString());
        if (data.toString().contains(HOST_SUCCESS)) {
            mWebView.loadUrl(successUrl);
        }
        else if (data.toString().contains(HOST_FAILURE)) {
            mWebView.loadUrl(failureUrl);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Extracts query parameters from a given URL and returns them as a Map.
     *
     * @param url The URL from which to extract query parameters.
     * @return A Map containing the query parameters and their values.
     */
    public Map<String, String> getParamsFromURL(String url) {
        Uri uri = Uri.parse(url);
        Map<String, String> queryParams = new HashMap<>();
        for (String key : uri.getQueryParameterNames()) {
            queryParams.put(key, uri.getQueryParameter(key));
        }
        return queryParams;
    }

    /**
     * Updates the URL by adding or updating a parameter with the given value.
     *
     * @param url      The original URL.
     * @param param    The parameter to add or update.
     * @param paramVal The value of the parameter.
     * @return The updated URL with the specified parameter and value.
     */
    public static String updateURLParameter(String url, String param, String paramVal) {
        String baseURL;
        String additionalURL = "";
        StringBuilder newAdditionalURL = new StringBuilder();

        String[] urlParts = url.split("\\?", 2);
        baseURL = urlParts[0];

        if (urlParts.length > 1) {
            additionalURL = urlParts[1];
        }

        String[] params = additionalURL.split("&");
        boolean first = true;
//Sample Commit
        for (String p : params) {
            if (!p.isEmpty() && !p.split("=")[0].equals(param)) {
                if (!first) {
                    newAdditionalURL.append("&");
                }
                newAdditionalURL.append(p);
                first = false;
            }
        }

        if (newAdditionalURL.length() > 0) {
            newAdditionalURL.append("&");
        }
        newAdditionalURL.append(param).append("=").append(paramVal);

        return baseURL + "?" + newAdditionalURL.toString();
    }

    /**
     * Generates the URL for UAE Pass login based on the environment and whether it's for web or mobile.*/
    public String getURL(boolean isWeb) {
        String base_url = isProd ? LOGIN_URL : LOGIN_URL_STG;
        String acrn = isWeb ? ACRN_WEB : ACRN_MOBILE;
        String clientId = isProd ? UAE_PASS_CLIENT_ID : UAE_PASS_CLIENT_ID_STG;
        String clientSecret = isProd ? UAE_PASS_CLIENT_SECRET : UAE_PASS_CLIENT_SECRET_STG;
        String redirectUrl = isProd ? APP_REDIRECT_URL_PROD : APP_REDIRECT_URL;

        String url = base_url +
                "redirect_uri=" + redirectUrl +
                "&client_id=" + clientId +
                "&response_type=" + RESPONSE_TYPE +
                "&state=" + clientSecret +
                "&scope=" + SCOPE +
                "&acr_values=" + acrn +
                "&ui_locales=en";

        return url;
    }



    //Optional: Method to get the access token using the authorization code
    public void getToken(String authCode) {

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<TokenResponse> call = api.getToken(authCode);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    TokenResponse tokenResponse = response.body();
                    System.out.println("6010944 - onResponse : " + tokenResponse.getAccessToken());
                    statusTextView.setText("Getting user profile...");
                    getUserProfile(tokenResponse.getAccessToken());
                } else {
                    statusTextView.setText(getString(R.string.authentication_failed));
                    userInfoTextView.setText("Failed to get access token: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                System.out.println("6010944 - onFailure : " + t.getMessage());
                statusTextView.setText(getString(R.string.authentication_failed));
                userInfoTextView.setText("Network error during token exchange: " + t.getMessage());
            }
        });
    }


    //Optional: Method to get the user profile using the access token
    public void getUserProfile(String accessToken) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<UserProfileResponse> call = api.getUserInfo(accessToken);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    UserProfileResponse userProfile = response.body();
                    System.out.println("6010944 - User Profile: " + userProfile.getFirstnameEN());
                    statusTextView.setText(getString(R.string.authentication_successful));
                    userInfoTextView.setText(formatUserProfile(userProfile));
                } else {
                    System.out.println("6010944 - Error: " + response.message());
                    statusTextView.setText(getString(R.string.authentication_failed));
                    userInfoTextView.setText("Failed to retrieve user profile: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                System.out.println("6010944 - onFailure : " + t.getMessage());
                statusTextView.setText(getString(R.string.authentication_failed));
                userInfoTextView.setText("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Formats the user profile response into a readable format
     */
    private String formatUserProfile(UserProfileResponse userProfile) {
        if (userProfile == null) {
            return "No user profile data available";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("✓ Authentication Successful\n\n");
        
        if (userProfile.getFirstnameEN() != null) {
            formatted.append("Name: ").append(userProfile.getFirstnameEN()).append("\n");
        }
        
        // Add other profile fields as needed
        formatted.append("\n--- Raw Profile Data ---\n");
        formatted.append(userProfile.toString());
        
        return formatted.toString();
    }

    // Optional: Method to open the UAE Pass app if installed
    public void launchOnActivty(){
        UaePassDialogFragment fragment = UaePassDialogFragment.newInstance(true /* or true if QA */, new UaePassDialogFragment.UaePassCallback() {
            @Override
            public void onResult(String codeOrCancel) {
                if ("Cancel".equals(codeOrCancel)) {
                    // Handle user cancellation
                } else {
                    // Handle success, codeOrCancel is the auth code
                }
            }
        });

        fragment.show(getSupportFragmentManager(), "UaePassDialog");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            String result = data.getStringExtra(GpActivity.RESULT_KEY);
            // Handle success or "Cancel"
        }
    }

}

//Fetch Test
//Fetch 1
