// RedirectActivity.java
package com.srikanth.uaepass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class RedirectActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle the deep link
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            // Process the URI as needed
            // Example: extract query parameters
            String code = data.getQueryParameter("code");
            // Pass data to MainActivity or handle as needed
        }

        finish(); // Close activity if no UI is needed
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }

    public void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            // Process the URI as needed
            String code = data.getQueryParameter("code");
            // Pass data to MainActivity or handle as needed
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra("code", code);
            startActivity(mainIntent);
        }
    }
}