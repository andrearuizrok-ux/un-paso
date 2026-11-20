package com.unpaso.alavez;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/** Offline-only shell around the existing v4 app. Backup flows use Android document picker. */
public final class MainActivity extends Activity {
    private static final String HOME = "https://appassets.androidplatform.net/index.html";
    private static final String HOST = "appassets.androidplatform.net";
    private static final int REQUEST_SAVE = 9001;
    private static final int REQUEST_OPEN = 9002;
    private static final int MAX_JSON_BYTES = 10 * 1024 * 1024;
    private WebView webView;
    private String waitingExport;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(36,59,57));
        getWindow().setNavigationBarColor(Color.rgb(36,59,57));
        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(247,247,241));
        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        webView.setWebViewClient(new LocalAssetsClient());
        webView.addJavascriptInterface(new BackupBridge(), "AndroidDiary");
        setContentView(webView);
        webView.loadUrl(HOME);
    }

    private static boolean isLocal(Uri uri) {
        return uri != null && "https".equalsIgnoreCase(uri.getScheme())
            && HOST.equalsIgnoreCase(uri.getHost());
    }

    private final class LocalAssetsClient extends WebViewClient {
        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            if (isLocal(uri)) return false;
            if (request.isForMainFrame() && request.hasGesture()
                && ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))) {
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (ActivityNotFoundException ignored) { }
            }
            return true; // no external pages or untrusted frames inside our privileged WebView
        }
        @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            if (!isLocal(uri)) return blocked();
            final String path = uri.getPath();
            final String asset;
            final String mime;
            if (path == null || "/".equals(path) || "/index.html".equals(path)) {
                asset = "index.html"; mime = "text/html";
            } else if ("/icons/icon-192.png".equals(path)) {
                asset = "icons/icon-192.png"; mime = "image/png";
            } else if ("/icons/icon-512.png".equals(path)) {
                asset = "icons/icon-512.png"; mime = "image/png";
            } else return blocked(); // strict allowlist prevents path traversal
            try {
                return new WebResourceResponse(mime, "UTF-8", 200, "OK",
                    Collections.singletonMap("Cache-Control", "no-store"), getAssets().open(asset));
            } catch (IOException e) { return blocked(); }
        }
        private WebResourceResponse blocked() {
            return new WebResourceResponse("text/plain", "UTF-8", 404, "Not Found",
                Collections.emptyMap(), new ByteArrayInputStream(new byte[0]));
        }
    }

    /** Never expose arbitrary filesystem or network capabilities to the JavaScript. */
    public final class BackupBridge {
        @JavascriptInterface public void exportJson(final String payload, final String proposedName) {
            if (payload == null || payload.getBytes(StandardCharsets.UTF_8).length > MAX_JSON_BYTES) return;
            runOnUiThread(() -> {
                waitingExport = payload;
                Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                i.setType("application/json");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String safeName = proposedName == null ? "un-paso-a-la-vez.json"
                    : proposedName.replaceAll("[^a-zA-Z0-9_.-]", "_");
                i.putExtra(Intent.EXTRA_TITLE, safeName);
                try { startActivityForResult(i, REQUEST_SAVE); }
                catch (ActivityNotFoundException e) { waitingExport = null; }
            });
        }
        @JavascriptInterface public void chooseBackup() {
            runOnUiThread(() -> {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("application/json");
                try { startActivityForResult(i, REQUEST_OPEN); }
                catch (ActivityNotFoundException ignored) { }
            });
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SAVE) {
            final String content = waitingExport;
            waitingExport = null;
            if (resultCode == RESULT_OK && data != null && data.getData() != null && content != null) {
                try (OutputStream out = getContentResolver().openOutputStream(data.getData())) {
                    if (out != null) out.write(content.getBytes(StandardCharsets.UTF_8));
                } catch (IOException ignored) { /* Android document picker owns error UI */ }
            }
        } else if (requestCode == REQUEST_OPEN && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try (InputStream in = getContentResolver().openInputStream(data.getData());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                if (in == null) return;
                byte[] chunk = new byte[8192]; int count; int read = 0;
                while ((count = in.read(chunk)) >= 0) {
                    read += count;
                    if (read > MAX_JSON_BYTES) return;
                    out.write(chunk, 0, count);
                }
                String text = new String(out.toByteArray(), StandardCharsets.UTF_8);
                webView.evaluateJavascript("window.receiveAndroidBackup(" + JSONObject.quote(text) + ")", null);
            } catch (Exception ignored) { /* malformed backups are rejected by JS validation */ }
        }
    }

    @Override public void onBackPressed() {
        if (webView == null) { super.onBackPressed(); return; }
        webView.evaluateJavascript("(window.nativeBack && window.nativeBack()) === true", value -> {
            if (!"true".equals(value)) MainActivity.super.onBackPressed();
        });
    }

    @Override protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }
    @Override protected void onDestroy() {
        if (webView != null) { webView.removeJavascriptInterface("AndroidDiary"); webView.destroy(); webView = null; }
        super.onDestroy();
    }
}
