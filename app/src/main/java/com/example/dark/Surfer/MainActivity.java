package com.example.dark.Surfer;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    WebView wv;
    SwipeRefreshLayout swipe;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        wv = findViewById(R.id.wv);
        swipe = findViewById(R.id.srl);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.requestFocusFromTouch();
        wv.setNetworkAvailable(true);
        wv.getSettings().setSupportMultipleWindows(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        //   to make webview loads faster....
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wv.getSettings().getCacheMode();
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        wv.getSettings().setSaveFormData(true);
        wv.getSettings().setSavePassword(true);
        wv.getSettings().setEnableSmoothTransition(true);
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.loadUrl("http://www.google.com/");
        url = wv.getOriginalUrl();


        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String ur, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ur));
                String extension = MimeTypeMap.getFileExtensionFromUrl(ur);
                request.setMimeType(mimeType);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(ur);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(ur, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(ur, contentDisposition, extension));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue((request));
                Toast.makeText(getApplicationContext(), "Downloading File..", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.addCategory(Intent.ACTION_OPEN_DOCUMENT);
                i.setType("*/*");
                startActivity(i);
            }
        });

        wv.setWebViewClient(new MyWebviewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipe.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipe.setRefreshing(false);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history) {
            Intent i = new Intent(MainActivity.this, History.class);
            startActivity(i);
        }
        if (item.getItemId() == R.id.back) {
            if (wv.canGoBack()) {
                wv.goBack();
            }
        }
        if (item.getItemId() == R.id.forward) {
            if (wv.canGoForward()) {
                wv.goForward();
            }
        }
        if (item.getItemId() == R.id.newTab) {
            Intent i = new Intent(MainActivity.this, TabActivity.class);
            startActivity(i);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (wv != null) {
            wv.loadUrl("about:blank"); // Clear the content
            wv.onPause();
            wv.removeAllViews();
            wv.destroyDrawingCache();
            // For older Android versions, you might need to remove it from its parent first
            // if (wv.getParent() != null) {
            //     ((android.view.ViewGroup) wv.getParent()).removeView(wv);
            // }
            wv.destroy();
            wv = null; // Nullify the reference
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        wv.reload();
    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private class MyWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("market//") || url.startsWith("vnd:youtube") || url.startsWith("tel:") || url.startsWith("mailto:")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                view.loadUrl(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}

