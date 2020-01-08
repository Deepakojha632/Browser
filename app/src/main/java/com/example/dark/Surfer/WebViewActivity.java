package com.example.dark.Surfer;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.view.View.FOCUS_DOWN;
import static android.view.View.FOCUS_UP;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.makeText;

/**
 * Created by DARK on 01-01-2002.
 */
public class WebViewActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    static WebView wv;
    static EditText urlAdd, key;
    boolean close = false;
    View decoder;
    Bundle bm;
    RelativeLayout rl;
    SQLiteDatabase db;
    CheckBox deskMode, incognito, fullscrn;
    Animation up, down;
    Button back, forward, slideMenu, home, exit, searchUrl, searchKey, shareUrl, stopLoad, bookmark, closeTab, history;
    SwipeRefreshLayout swipe;
    ProgressBar pBar;
    boolean loading = false;
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.childtab);

        checkPermissions();

        wv = (WebView) findViewById(R.id.wv);
        rl = (RelativeLayout) findViewById(R.id.slider);
        deskMode = (CheckBox) findViewById(R.id.desktop);
        closeTab = (Button) findViewById(R.id.closeTab);
        bookmark = (Button) findViewById(R.id.bookmark);
        fullscrn = (CheckBox) findViewById(R.id.fullscrn);
        history = (Button) findViewById(R.id.history);
        incognito = (CheckBox) findViewById(R.id.inconito);
        shareUrl = (Button) findViewById(R.id.shareUrl);
        swipe = (SwipeRefreshLayout) findViewById(R.id.srl);
        pBar = (ProgressBar) findViewById(R.id.hpBar);
        urlAdd = (EditText) findViewById(R.id.urlAddr);
        key = (EditText) findViewById(R.id.search);
        back = (Button) findViewById(R.id.back);
        forward = (Button) findViewById(R.id.forward);
        slideMenu = (Button) findViewById(R.id.menu);
        home = (Button) findViewById(R.id.home);
        exit = (Button) findViewById(R.id.exit);
        searchUrl = (Button) findViewById(R.id.go);
        searchKey = (Button) findViewById(R.id.searchBtn);
        stopLoad = (Button) findViewById(R.id.stopLoading);

        shareUrl.setOnClickListener(this);
        closeTab.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        home.setOnClickListener(this);
        exit.setOnClickListener(this);
        history.setOnClickListener(this);
        searchUrl.setOnClickListener(this);
        searchKey.setOnClickListener(this);
        stopLoad.setOnClickListener(this);
        swipe.setOnRefreshListener(this);

        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.requestFocusFromTouch();
        wv.setFocusable(true);
        wv.setFocusableInTouchMode(true);
        wv.setSaveEnabled(true);
        wv.setNetworkAvailable(true);
        wv.requestFocus(FOCUS_DOWN | FOCUS_UP);

        wv.getSettings().setSupportMultipleWindows(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        //   to make webview loads faster....
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        //wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //wv.getSettings().getCacheMode();
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setTextSize(WebSettings.TextSize.SMALLER);

        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        wv.getSettings().supportMultipleWindows();
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setLightTouchEnabled(true);

        /*wv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (rl.getVisibility() == VISIBLE) {
                            rl.setAnimation(down);
                            rl.setVisibility(GONE);
                        }
                }
                return true;
            }
        });*/

        //saving form data,password

        wv.getSettings().setEnableSmoothTransition(true);
        //zoom setting
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setGeolocationEnabled(true);
        up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        wv.setWebChromeClient(new WebChromeClient() {
                                  @Override
                                  public void onProgressChanged(WebView view, int progress) {
                                      if (progress < 100 && pBar.getVisibility() == GONE) {
                                          loading = true;
                                          pBar.setVisibility(VISIBLE);
                                      }
                                      pBar.setProgress(progress);
                                      if (progress == 100) {
                                          pBar.setVisibility(GONE);
                                      } else
                                          pBar.setVisibility(VISIBLE);
                                  }

                                  public void onCloseWindow(WebView view) {
                                      super.onCloseWindow(view);
                                      Log.d(TAG, "Window close");
                                  }
                              }
        );

        if (Build.VERSION.SDK_INT >= 16) {
            wv.getSettings().setAllowFileAccessFromFileURLs(true);
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }

        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String ur, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ur));
                String extension = MimeTypeMap.getFileExtensionFromUrl(ur);
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                mimeType = mime.getMimeTypeFromExtension(extension);
                request.setMimeType(mimeType);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(ur);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading...");
                request.setTitle(URLUtil.guessFileName(ur, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setVisibleInDownloadsUi(true);
                String fileName = URLUtil.guessFileName(ur, contentDisposition, mimeType);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                makeText(WebViewActivity.this, "Downloading File..", Toast.LENGTH_SHORT).show();
                dm.enqueue(request);
            }
        });

        wv.setWebViewClient(new MyWebviewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                try {
                    if (!isConnected()) {
                        if (loading == true)
                            wv.loadUrl("file:///android_asset/Web/index.html");
                        //urlAdd.setText(wv.getUrl());
                        Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
                    } else if (isConnected()) {
                        Toast.makeText(getApplicationContext(), "Network available", Toast.LENGTH_SHORT).show();
                        wv.loadUrl("javascript:history.go(-1)");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipe.setRefreshing(true);
                if (rl.getVisibility() == VISIBLE) {
                    rl.setAnimation(down);
                    rl.setVisibility(GONE);
                    urlAdd.setText(view.getUrl());
                    //urlAdd.setText(wv.getUrl());
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipe.setRefreshing(false);

            }
        });

        String url = "http://www.google.com";
        wv.loadUrl(url);
        urlAdd.setText(wv.getUrl());
        incognito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (incognito.isChecked()) {

                    if (rl.getVisibility() == VISIBLE) {
                        rl.setAnimation(down);
                        rl.setVisibility(GONE);
                        //urlAdd.setText(wv.getUrl());
                    }
                    Toast.makeText(getApplicationContext(), "Incognito mode on", Toast.LENGTH_SHORT).show();

                    CookieManager.getInstance().setAcceptCookie(false);

                    //Make sure no caching is done
                    wv.getSettings().setCacheMode(wv.getSettings().LOAD_NO_CACHE);
                    wv.getSettings().setAppCacheEnabled(false);
                    wv.clearHistory();
                    wv.clearCache(true);

                    //Make sure no autofill for Forms/ user-name password happens for the app
                    wv.clearFormData();
                    wv.getSettings().setSavePassword(false);
                    wv.getSettings().setSaveFormData(false);

                } else {

                    if (rl.getVisibility() == VISIBLE) {
                        rl.setAnimation(down);
                        rl.setVisibility(GONE);
                        //urlAdd.setText(wv.getUrl());
                    }
                    CookieManager.getInstance().setAcceptCookie(true);

                    //Make sure no caching is done
                    wv.getSettings().setCacheMode(wv.getSettings().LOAD_CACHE_ELSE_NETWORK);
                    wv.getSettings().setAppCacheEnabled(true);
                    wv.clearCache(false);

                    //Make sure no autofill for Forms/ user-name password happens for the app
                    //wv.clearFormData();
                    wv.getSettings().setSavePassword(true);
                    wv.getSettings().setSaveFormData(true);
                }
            }
        });

        //menu button litening the click event
        slideMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rl.setVisibility(rl.getVisibility() == VISIBLE ? GONE : VISIBLE);
                if (rl.getVisibility() == VISIBLE) rl.setAnimation(up);
                else {
                    rl.setAnimation(down);
                }
            }
        });

        fullscrn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (fullscrn.isChecked()) {
                    hideSystemUi();
                    if (rl.getVisibility() == VISIBLE) {
                        rl.setAnimation(down);
                        rl.setVisibility(GONE);
                        //urlAdd.setText(wv.getUrl());
                    }

                } else {
                    showSystemUI();
                    if (rl.getVisibility() == VISIBLE) {
                        rl.setAnimation(down);
                        rl.setVisibility(GONE);
                        //urlAdd.setText(wv.getUrl());
                    }
                }
            }
        });

        deskMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (deskMode.isChecked()) {
                    if (rl.getVisibility() == VISIBLE) {
                        rl.setAnimation(down);
                        rl.setVisibility(GONE);
                        //urlAdd.setText(wv.getUrl());
                    }
                    makeText(getApplicationContext(), "Switched to desktop mode", Toast.LENGTH_SHORT).show();
                    wv.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/600.7.12 (KHTML, like Gecko) Version/8.0.7 Safari/600.7.12");
                    makeText(getApplicationContext(), "Reloading current page into desktop mode", Toast.LENGTH_SHORT).show();
                    wv.reload();
                    wv.getSettings().setUseWideViewPort(true);
                    wv.getSettings().setLoadWithOverviewMode(true);
                    wv.getSettings().setSupportZoom(true);
                    //urlAdd.setText(wv.getUrl());
                } else {
                    if (rl.getVisibility() == VISIBLE) {
                        rl.setAnimation(down);
                        rl.setVisibility(GONE);
                        //urlAdd.setText(wv.getUrl());
                    }
                    wv.clearCache(true);
                    makeText(getApplicationContext(), "Switched to mobile mode", Toast.LENGTH_SHORT).show();
                    wv.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36");
                    makeText(getApplicationContext(), "Reloading current page into mobile mode", Toast.LENGTH_SHORT).show();
                    wv.reload();
                    urlAdd.setText(wv.getUrl());
                }
            }
        });
    }

    private void showSystemUI() {
        decoder = getWindow().getDecorView();
        decoder.setSystemUiVisibility(
                SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void hideSystemUi() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decoder = getWindow().getDecorView();
        decoder.setSystemUiVisibility(
                SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public boolean isConnected() throws Exception {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    @Override
    public void onBackPressed() {
        //rl.setVisibility(rl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (wv.canGoBack()) {
            wv.goBack();
            //urlAdd.setText(wv.getUrl());
            if (rl.getVisibility() == VISIBLE) {
                rl.setAnimation(down);
                rl.setVisibility(GONE);
            }
        } else {

            if (close) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                close = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        close = false;
                    }
                }, 3 * 1000);
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onRefresh() {
        wv.reload();
        if (rl.getVisibility() == VISIBLE) {
            rl.setAnimation(down);
            rl.setVisibility(GONE);
            //urlAdd.setText(wv.getUrl());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            if (wv.canGoBack()) {
                wv.goBack();
                if (rl.getVisibility() == VISIBLE) {
                    rl.setAnimation(down);
                    rl.setVisibility(GONE);
                    //urlAdd.setText(wv.getUrl());
                }
            }
        }
        if (view == forward) {
            if (wv.canGoForward()) {
                wv.goForward();
                if (rl.getVisibility() == VISIBLE) {
                    rl.setAnimation(down);
                    rl.setVisibility(GONE);
                    //urlAdd.setText(wv.getUrl());
                }
            }
        }
        if (view == searchUrl) {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (rl.getVisibility() == VISIBLE) {
                    rl.setAnimation(down);
                    rl.setVisibility(GONE);
                    //urlAdd.setText(wv.getUrl());
                }
            } catch (Exception e) {

            }
            if (urlAdd.getText() != null) {
                //wv.loadUrl(urlAdd.getText().toString());
                getCorrectUrl(urlAdd.getText().toString());
                //Toast.makeText(getApplicationContext(), urlAdd.getText().toString(), Toast.LENGTH_LONG).show();
                //urlAdd.setText(wv.getUrl());
            }
        }
        if (view == searchKey) {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (rl.getVisibility() == VISIBLE) {
                    rl.setAnimation(down);
                    rl.setVisibility(GONE);
                    //urlAdd.setText(wv.getUrl());
                }
            } catch (Exception e) {

            }
            if (key.getText() != null) {
                String keyword = "http://www.google.com/search?hl=us&q=" + key.getText().toString();
                wv.loadUrl(keyword);
                //urlAdd.setText(wv.getUrl());
                key.setText(null);
            }
        }
        if (view == stopLoad) {
            wv.stopLoading();
            if (rl.getVisibility() == VISIBLE) {
                rl.setAnimation(down);
                rl.setVisibility(GONE);
                //urlAdd.setText(wv.getUrl());
            }
        }

        if (view == bookmark) {
            bm = new Bundle();
            bm.putString("TITLE", wv.getTitle());
            bm.putString("URL", wv.getUrl());
            Intent i = new Intent(this, AddBookmark.class);
            i.putExtras(bm);
            startActivity(i);
            if (rl.getVisibility() == VISIBLE) {
                rl.setAnimation(down);
                rl.setVisibility(GONE);
                //urlAdd.setText(wv.getUrl());
            }
        }
        if (view == home) {
            String url = "http://www.google.com";
            wv.loadUrl(url);
            if (rl.getVisibility() == VISIBLE) {
                rl.setAnimation(down);
                rl.setVisibility(GONE);
                //urlAdd.setText(wv.getUrl());
            }
            //urlAdd.setText(wv.getUrl());
        }

        //closing current tab
        if (view == closeTab) {
            this.finish();
            //wv.clearView();
        }
        //to close the application
        if (view == exit) {
            this.finish();
        }
        if (view == history) {
            startActivity(new Intent(this, History.class));
            if (rl.getVisibility() == VISIBLE) {
                rl.setAnimation(down);
                rl.setVisibility(GONE);
                //urlAdd.setText(wv.getUrl());
            }
        }

        if (view == shareUrl) {
            if (rl.getVisibility() == VISIBLE) {
                rl.setAnimation(down);
                rl.setVisibility(GONE);
                //urlAdd.setText(wv.getUrl());
            }
            Toast.makeText(getApplicationContext(), "preparing to send", Toast.LENGTH_SHORT).show();
            Intent shareLink = new Intent(Intent.ACTION_SEND);
            shareLink.setType("text/plain");
            shareLink.putExtra(Intent.EXTRA_SUBJECT, "Link");
            shareLink.putExtra(Intent.EXTRA_TEXT, wv.getUrl());
            startActivity(Intent.createChooser(shareLink, "Share via"));
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

    //correcting entered url
    void getCorrectUrl(String textUrl) {
        String newUrl = null;
        if (textUrl.contains("http://www.")) {
            makeText(getApplicationContext(), "1" + textUrl.substring(11, textUrl.length() - 1), Toast.LENGTH_SHORT).show();
            newUrl = "http://" + textUrl.substring(11, textUrl.length() - 1);
        } else {
            if (textUrl.contains("https://www.")) {
                makeText(getApplicationContext(), "3" + textUrl.substring(12, textUrl.length() - 1), Toast.LENGTH_SHORT).show();
                newUrl = "http://" + textUrl.substring(12, textUrl.length() - 1);
            } else if (textUrl.contains("www.")) {
                makeText(getApplicationContext(), "2" + textUrl.substring(4, textUrl.length() - 1), Toast.LENGTH_SHORT).show();
                newUrl = "http://" + textUrl.substring(4, textUrl.length() - 1);
            } else if (textUrl.contains("http://")) {
                makeText(getApplicationContext(), "4" + textUrl.substring(7, textUrl.length() - 1), Toast.LENGTH_SHORT).show();
                newUrl = "http://" + textUrl.substring(7, textUrl.length() - 1);
            } else if (textUrl.contains("https://")) {
                makeText(getApplicationContext(), "5" + textUrl.substring(8, textUrl.length() - 1), Toast.LENGTH_SHORT).show();
                newUrl = "http://" + textUrl.substring(8, textUrl.length() - 1);
            } else {
                makeText(getApplicationContext(), "6" + "URL correct", Toast.LENGTH_SHORT).show();
                //wv.loadUrl("http://"+urlAdd.getText().toString());
                newUrl = "http://" + textUrl;
            }
        }
        try {
            wv.loadUrl(newUrl);
            urlAdd.setText(wv.getUrl());
        } catch (Exception e) {
            Log.v("add Url", "" + e.toString());
        }
    }


    private class MyWebviewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Date d = new Date();
            if (url.startsWith("http") | url.startsWith("https")) {
                try {
                    if (!incognito.isChecked() && !url.isEmpty() && !view.getTitle().isEmpty()) {
                        //creating database for storing history
                        db = openOrCreateDatabase("Web.db", Context.MODE_PRIVATE, null);
                        db.execSQL("CREATE TABLE IF NOT EXISTS HISTORY(title varchar ,url varchar,time VARCHAR,PRIMARY KEY(url,time));");
                        //Toast.makeText(getApplicationContext(), "Updating history", Toast.LENGTH_SHORT).show();
                        db.execSQL("INSERT INTO HISTORY VALUES('" + view.getTitle() + "','" + view.getUrl() + "','" + d.getHours() + d.getMinutes() + d.getSeconds() + "');");
                        //Toast.makeText(getApplicationContext(), "History updated", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                urlAdd.setText(url);
                return false;
            }
            Uri parsedUri = Uri.parse(url);
            PackageManager packageManager = getPackageManager();
            Intent browseIntent = new Intent(Intent.ACTION_VIEW).setData(parsedUri);
            if (browseIntent.resolveActivity(packageManager) != null) {
                startActivity(browseIntent);
                return true;
            }
            //if not activity found, try to parse intent://
            if (url.startsWith("intent:")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent);
                        return true;
                    }
                    //try to find fallback url
                    String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                    if (fallbackUrl != null) {
                        view.loadUrl(fallbackUrl);
                        return true;
                    }
                    //invite to install
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + intent.getPackage()));
                    if (marketIntent.resolveActivity(packageManager) != null) {
                        startActivity(marketIntent);
                        return true;
                    }
                } catch (URISyntaxException e) {
                    //not an intent uri
                    e.printStackTrace();
                }
            }
            return true;//do nothing in other cases
        }

    }
}