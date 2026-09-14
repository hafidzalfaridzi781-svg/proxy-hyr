package com.zyecitedz.proxyhyr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionHelper.startBackgroundService(this);
        PermissionHelper.requestBatteryExemption(this);

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(i);
        }

        setupWebView();
        requestShizukuOnStart();
    }

    private void setupWebView() {
        webView = findViewById(R.id.webview);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new RealBridge(), "HYR");
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void requestShizukuOnStart() {
        try {
            if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() != 0) {
                Shizuku.requestPermission(2001);
            }
        } catch (Throwable ignored) {}
    }

    private void runJs(String fn, String arg) {
        runOnUiThread(() -> {
            if (webView != null)
                webView.evaluateJavascript("window." + fn + "(" + arg + ");", null);
        });
    }

    // ============================================================
    // JAVASCRIPT BRIDGE — semua method di sini FUNGSIONAL
    // ============================================================
    public class RealBridge {

        @JavascriptInterface
        public String developer() { return "ZyeCitedz"; }

        @JavascriptInterface
        public String channel() {
            return "https://whatsapp.com/channel/0029VbCdJuNDZ4LT4arD1H3v";
        }

        @JavascriptInterface
        public void openChannel() {
            runOnUiThread(() -> startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(channel()))));
        }

        @JavascriptInterface
        public boolean shizukuReady() {
            try { return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == 0; }
            catch (Throwable t) { return false; }
        }

        @JavascriptInterface
        public void requestShizuku() {
            runOnUiThread(() -> {
                try {
                    if (!Shizuku.pingBinder()) {
                        Toast.makeText(MainActivity.this,
                                "Shizuku tidak berjalan", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Shizuku.requestPermission(2001);
                } catch (Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ==== TEST PROXY (REAL) ====
        @JavascriptInterface
        public void testProxy(String host, int port) {
            new Thread(() -> {
                long t0 = System.currentTimeMillis();
                String result;
                try {
                    Proxy proxy = new Proxy(Proxy.Type.HTTP,
                            new InetSocketAddress(host, port));
                    HttpURLConnection c = (HttpURLConnection)
                            new URL("https://api.ipify.org").openConnection(proxy);
                    c.setConnectTimeout(8000);
                    c.setReadTimeout(8000);
                    int code = c.getResponseCode();
                    String body = "";
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(c.getInputStream()))) {
                        body = br.readLine();
                    }
                    long ms = System.currentTimeMillis() - t0;
                    result = "{\"ok\":" + (code == 200) + ",\"code\":" + code +
                            ",\"latency\":" + ms + ",\"ip\":\"" + body + "\"}";
                } catch (Exception e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onProxyResult", result);
            }).start();
        }

        // ==== IP PUBLIK (REAL) ====
        @JavascriptInterface
        public void checkIP() {
            new Thread(() -> {
                String result;
                try {
                    HttpURLConnection c = (HttpURLConnection)
                            new URL("https://api.ipify.org").openConnection();
                    c.setConnectTimeout(8000);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(c.getInputStream()));
                    String ip = br.readLine();
                    br.close();
                    result = "{\"ok\":true,\"ip\":\"" + ip + "\"}";
                } catch (Exception e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onIPResult", result);
            }).start();
        }

        // ==== PING (REAL) ====
        @JavascriptInterface
        public void ping(String host) {
            new Thread(() -> {
                String result;
                try {
                    long t0 = System.currentTimeMillis();
                    InetAddress addr = InetAddress.getByName(host);
                    boolean reachable = addr.isReachable(5000);
                    long ms = System.currentTimeMillis() - t0;
                    result = "{\"ok\":" + reachable + ",\"ip\":\"" +
                            addr.getHostAddress() + "\",\"latency\":" + ms + "}";
                } catch (Exception e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onPingResult", result);
            }).start();
        }

        // ==== DNS LOOKUP (REAL) ====
        @JavascriptInterface
        public void dnsLookup(String host) {
            new Thread(() -> {
                String result;
                try {
                    InetAddress[] addrs = InetAddress.getAllByName(host);
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < addrs.length; i++) {
                        if (i > 0) sb.append(",");
                        sb.append("\"").append(addrs[i].getHostAddress()).append("\"");
                    }
                    sb.append("]");
                    result = "{\"ok\":true,\"ips\":" + sb + "}";
                } catch (Exception e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onDnsResult", result);
            }).start();
        }

        // ==== SET SYSTEM PROXY via Shizuku (REAL) ====
        @JavascriptInterface
        public void setSystemProxy(String host, int port) {
            new Thread(() -> {
                String result;
                try {
                    String cmd = "settings put global http_proxy " + host + ":" + port;
                    Process p = Shizuku.newProcess(new String[]{"sh", "-c", cmd}, null, null);
                    int exit = p.waitFor();
                    result = "{\"ok\":" + (exit == 0) + ",\"exit\":" + exit + "}";
                } catch (Throwable e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onSystemProxyResult", result);
            }).start();
        }

        // ==== CLEAR SYSTEM PROXY (REAL) ====
        @JavascriptInterface
        public void clearSystemProxy() {
            new Thread(() -> {
                String result;
                try {
                    Process p = Shizuku.newProcess(new String[]{
                            "sh", "-c", "settings put global http_proxy :0"}, null, null);
                    int exit = p.waitFor();
                    result = "{\"ok\":" + (exit == 0) + "}";
                } catch (Throwable e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onSystemProxyResult", result);
            }).start();
        }

        // ==== GET SYSTEM PROXY (REAL) ====
        @JavascriptInterface
        public void getSystemProxy() {
            new Thread(() -> {
                String result;
                try {
                    Process p = Shizuku.newProcess(new String[]{
                            "sh", "-c", "settings get global http_proxy"}, null, null);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
                    String val = br.readLine();
                    p.waitFor();
                    result = "{\"ok\":true,\"proxy\":\"" +
                            (val == null ? "null" : val.replace("\"", "'")) + "\"}";
                } catch (Throwable e) {
                    result = "{\"ok\":false,\"error\":\"" +
                            e.getMessage().replace("\"", "'") + "\"}";
                }
                runJs("onSystemProxyResult", result);
            }).start();
        }

        // ==== DEVICE INFO (REAL) ====
        @JavascriptInterface
        public String deviceInfo() {
            Runtime r = Runtime.getRuntime();
            long totalMb = r.totalMemory() / 1024 / 1024;
            long maxMb   = r.maxMemory()   / 1024 / 1024;
            return "{\"model\":\"" + Build.MODEL + "\"," +
                    "\"brand\":\"" + Build.BRAND + "\"," +
                    "\"android\":" + Build.VERSION.SDK_INT + "," +
                    "\"abi\":\"" + Build.SUPPORTED_ABIS[0] + "\"," +
                    "\"heap_total_mb\":" + totalMb + "," +
                    "\"heap_max_mb\":" + maxMb + "}";
        }

        // ==== BATTERY INFO (REAL) ====
        @JavascriptInterface
        public String batteryInfo() {
            android.os.BatteryManager bm = (android.os.BatteryManager)
                    getSystemService(Context.BATTERY_SERVICE);
            int level = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
            int status = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_STATUS);
            return "{\"level\":" + level + ",\"status\":" + status + "}";
        }
    }
}
