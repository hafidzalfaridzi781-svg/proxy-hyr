package com.zyecitedz.proxyhyr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_NOTIF = 100;
    private static final int REQ_OVERLAY = 101;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Start foreground service (background permission)
        PermissionHelper.startBackgroundService(this);

        // 2. Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
            }
        }

        // 3. Request battery optimization exemption (background)
        PermissionHelper.requestBatteryExemption(this);

        // 4. Request overlay permission
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_OVERLAY);
        }

        // 5. Setup WebView
        setupWebView();

        // 6. Request Shizuku permission
        ShizukuHelper.requestPermission(this, granted -> runOnUiThread(() -> {
            String msg = granted ? "Shizuku: GRANTED" : "Shizuku: DENIED / TIDAK ADA";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            if (webView != null) {
                webView.evaluateJavascript(
                        "window.onShizukuStatus && window.onShizukuStatus(" + granted + ");", null);
            }
        }));
    }

    private void setupWebView() {
        webView = findViewById(R.id.webview);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JsBridge(), "Android");

        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIF && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Notifikasi diaktifkan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /** Bridge dari WebView ke Android */
    public class JsBridge {
        @JavascriptInterface
        public String getDeveloper() {
            return "ZyeCitedz";
        }

        @JavascriptInterface
        public String getChannelUrl() {
            return "https://whatsapp.com/channel/0029VbCdJuNDZ4LT4arD1H3v";
        }

        @JavascriptInterface
        public boolean isShizukuAvailable() {
            return ShizukuHelper.isAvailable();
        }

        @JavascriptInterface
        public boolean isShizukuGranted() {
            return ShizukuHelper.isGranted();
        }

        @JavascriptInterface
        public void requestShizuku() {
            runOnUiThread(() -> ShizukuHelper.requestPermission(MainActivity.this, granted ->
                    runOnUiThread(() -> {
                        if (webView != null) {
                            webView.evaluateJavascript(
                                    "window.onShizukuStatus && window.onShizukuStatus("
                                            + granted + ");", null);
                        }
                    })));
        }

        @JavascriptInterface
        public void openChannel() {
            runOnUiThread(() -> {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://whatsapp.com/channel/0029VbCdJuNDZ4LT4arD1H3v"));
                startActivity(i);
            });
        }

        @JavascriptInterface
        public void toast(String msg) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, msg,
                    Toast.LENGTH_SHORT).show());
        }
    }
}
