package com.example.news;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.news.Network.NetworkReceiver;


public class FullNews extends AppCompatActivity implements View.OnClickListener {
    private BroadcastReceiver NetworkReceiver = null;
    SwipeRefreshLayout mySwipeRefreshLayout;
    ProgressBar progressBar;
    //control
    ImageView webBack, webShare;
    private String domain = "https://google.com";
    String websiteURL;
    private WebView webview;
    public static TextView web_internet_connection;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news);
        whiteStatus();
        NetworkReceiver = new NetworkReceiver();
        registerReceiver(NetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        domain = getIntent().getStringExtra("link");
        websiteURL = domain;
        //findViewById area
        webview = findViewById(R.id.webView);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        progressBar = findViewById(R.id.progressBar);
        webBack = findViewById(R.id.webBack);
        webShare = findViewById(R.id.webShare);
        web_internet_connection = findViewById(R.id.web_internet_connection);

        //click Listener
        webBack.setOnClickListener(this);
        webShare.setOnClickListener(this);


        webview.getSettings().setJavaScriptEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);

        //improve webView performance
        WebSettings webSettings = webview.getSettings();
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setAppCacheEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);


        //extra
        webview.setWebViewClient(new WebViewClientDemo());
        webview.setWebChromeClient(new MyChrome());

        internet();


        //Swipe to refresh functionality
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        internet();
                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.webBack) { //back functionality
            onBackPressed();
        } else if (clickId == R.id.webShare) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, domain);
                startActivity(Intent.createChooser(intent, "Share News"));
            } catch (Exception ignored) {
            }
        }
    }

    private void internet() {
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        websiteURL = webview.getUrl();
        if (webview.getUrl() == null) {
            websiteURL = domain;
        }
        webview.loadUrl(websiteURL);
        webview.setWebViewClient(new WebViewClientDemo());
        webview.setWebChromeClient(new MyChrome());

    }

    public void whiteStatus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));// set status background white
        }
    }

    //set back button functionality
    @Override
    public void onBackPressed() { //if user presses the back button do this
        if (webview.isFocused() && webview.canGoBack()) { //check if in webview and the user can go back
            webview.goBack(); //go back in webview
        } else {
            finish();
        }
    }


    //extra
    private class WebViewClientDemo extends WebViewClient {
        @Override
        //Keep webView in app when clicking links
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //phone intent
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
                //main
            } else if (url.contains("mailto:")) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else if (url.contains("intent:")) {
                Toast.makeText(FullNews.this, "Something Wrong", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(FullNews.this, "Something wrong", Toast.LENGTH_SHORT).show();
            webview.loadUrl("about:blank");
        }
    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(newProgress);
            if (newProgress > 80) {
                mySwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
            if (newProgress > 50) {
                mySwipeRefreshLayout.setRefreshing(false);
            }
            super.onProgressChanged(view, newProgress);
        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(FullNews.this, "Something wrong", Toast.LENGTH_SHORT).show();
            webview.loadUrl("about:blank");
        }


        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webview.restoreState(savedInstanceState);
    }

}

