package com.br.barayefrokht;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_CHOOSER_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSION_CODE = 2;

    String websiteURL = "https://barayefrokht.vercel.app/"; // sets web url
    private WebView webview;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!CheckNetwork.isInternetAvailable(this)) {
            // If there is no internet connection
            showNoInternetDialog();
        } else {
            // Webview stuff
            setupWebView();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    private void setupWebView() {
        webview = findViewById(R.id.webView);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Allow mixed content
        webSettings.setAllowFileAccess(true); // Enable file access
        webSettings.setAllowContentAccess(true); // Enable content access
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Use the default cache mode

        // Enable cookies
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webview, true);
            cookieManager.setAcceptCookie(true);
        }

        // Bind JavaScript interface
        webview.addJavascriptInterface(new WebAppInterface(), "Android");

        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.setWebViewClient(new WebViewClientDemo());
        webview.setWebChromeClient(new WebChromeClientDemo());
        webview.loadUrl(websiteURL);
    }

    private class WebViewClientDemo extends WebViewClient {
        @Override
        // Keep webview in app when clicking links
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("whatsapp://")) {
                // If the URL starts with "whatsapp://", open it using an Intent
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true; // Return true to indicate that the URL has been handled
            } else {
                // For other URLs, load them in the WebView
                view.loadUrl(url);
                return true;
            }
        }
    }

    private class WebChromeClientDemo extends WebChromeClient {
        // For Android 5.0+
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
            takePictureIntent.setType("*/*");
            startActivityForResult(takePictureIntent, FILE_CHOOSER_REQUEST_CODE);

            return true;
        }
    }

    // Function to check internet connection
    static class CheckNetwork {

        private static final String TAG = CheckNetwork.class.getSimpleName();

        public static boolean isInternetAvailable(Context context) {
            NetworkInfo info = ((ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

            if (info == null || !info.isConnected()) {
                Log.d(TAG, "No internet connection");
                return false;
            } else {
                Log.d(TAG, "Internet connection available...");
                return true;
            }
        }
    }

    // Function to show alert dialog for no internet connection
    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No internet connection available")
                .setMessage("Please check your Mobile data or Wi-Fi network.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    //set back button functionality
    @Override
    public void onBackPressed() { //if user presses the back button do this
        if (webview.isFocused() && webview.canGoBack()) { //check if in webview and the user can go back
            webview.goBack(); //go back in webview
        } else { //do this if the webview cannot go back any further

            new AlertDialog.Builder(this) //alert the person knowing they are about to close
                    .setTitle("EXIT")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (mFilePathCallback == null) return;
            Uri[] results = null;
            // Check if response is positive
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
    }

    public class WebAppInterface {
        @android.webkit.JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
