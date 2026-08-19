package com.conductix.app;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final String APP_URL = "https://yoandarz.github.io/conductix/";
    private static final String HOST = "yoandarz.github.io";
    private static final int FILE_CHOOSER_REQUEST = 4101;
    private static final int STATUS_COLOR = Color.rgb(36, 20, 61);
    private static final int NAV_COLOR = Color.rgb(36, 20, 61);
    private WebView webView;
    private FrameLayout root;
    private View statusBarScrim;
    private ValueCallback<Uri[]> filePathCallback;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(NAV_COLOR);

        root = new FrameLayout(this);
        root.setBackgroundColor(NAV_COLOR);

        webView = new WebView(this);
        FrameLayout.LayoutParams webParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        root.addView(webView, webParams);

        statusBarScrim = new View(this);
        statusBarScrim.setBackgroundColor(STATUS_COLOR);
        FrameLayout.LayoutParams scrimParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            Gravity.TOP
        );
        root.addView(statusBarScrim, scrimParams);

        setContentView(root);
        configureSystemBars();
        if (Build.VERSION.SDK_INT <= 28 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4102);
        }
        configure();
        webView.loadUrl(APP_URL);
    }

    private void configureSystemBars() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), root);
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );

            FrameLayout.LayoutParams webLp = (FrameLayout.LayoutParams) webView.getLayoutParams();
            webLp.topMargin = bars.top;
            webLp.bottomMargin = bars.bottom;
            webLp.leftMargin = bars.left;
            webLp.rightMargin = bars.right;
            webView.setLayoutParams(webLp);

            FrameLayout.LayoutParams scrimLp = (FrameLayout.LayoutParams) statusBarScrim.getLayoutParams();
            scrimLp.height = bars.top;
            statusBarScrim.setLayoutParams(scrimLp);

            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void configure() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        webView.addJavascriptInterface(new AndroidBridge(), "ConductixNativeAndroid");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                try { startActivityForResult(intent, FILE_CHOOSER_REQUEST); return true; }
                catch (Exception ex) { filePathCallback = null; return false; }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) { return route(r.getUrl()); }
            @Override public boolean shouldOverrideUrlLoading(WebView v, String url) { return route(Uri.parse(url)); }
            private boolean route(Uri u) {
                if ("https".equalsIgnoreCase(u.getScheme()) && HOST.equalsIgnoreCase(u.getHost())) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, u));
                return true;
            }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FILE_CHOOSER_REQUEST || filePathCallback == null) return;
        Uri[] result = null;
        if (resultCode == RESULT_OK && data != null && data.getData() != null) result = new Uri[]{data.getData()};
        filePathCallback.onReceiveValue(result);
        filePathCallback = null;
    }

    public class AndroidBridge {
        @JavascriptInterface public void saveFile(String fileName, String mimeType, String base64Data) {
            new Thread(() -> {
                try {
                    byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
                    String safeName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
                    if (Build.VERSION.SDK_INT >= 29) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Downloads.DISPLAY_NAME, safeName);
                        values.put(MediaStore.Downloads.MIME_TYPE, mimeType == null || mimeType.isEmpty() ? "application/octet-stream" : mimeType);
                        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Conductix");
                        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                        if (uri == null) throw new IllegalStateException("No se pudo crear el archivo");
                        try (OutputStream out = getContentResolver().openOutputStream(uri)) { if (out == null) throw new IllegalStateException("No se pudo abrir el archivo"); out.write(bytes); }
                    } else {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) throw new SecurityException("Permiso de almacenamiento no concedido");
                        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Conductix");
                        if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("No se pudo crear la carpeta Conductix");
                        try (FileOutputStream out = new FileOutputStream(new File(dir, safeName))) { out.write(bytes); }
                    }
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Guardado en Descargas/Conductix", Toast.LENGTH_LONG).show());
                } catch (Exception ex) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "No se pudo guardar: " + ex.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        }
    }

    @Override public void onBackPressed() { if (webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
}
