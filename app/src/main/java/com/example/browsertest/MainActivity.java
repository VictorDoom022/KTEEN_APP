package com.example.browsertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public Uri imageUri;
    public ValueCallback<Uri[]> uploadMessage;
    WebView webView;
    SwipeRefreshLayout swipe;
    ProgressBar bar;
    FloatingActionButton floater;
    boolean doubleBackToExitPressedOnce = false;
    private ValueCallback<Uri> mUploadMessage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //WebAction();
                webView.reload();
                webView.clearCache(true);
            }
        });

        WebAction();

        //Bind JavaScript to Android code
        webView.addJavascriptInterface(new WebAppInterface(this),"Android");
        //Loading bar
        //webView.setWebChromeClient(new WebChromeClient());
        //bar = (ProgressBar) findViewById(R.id.bar);
        //floater = findViewById(R.id.floater);
        //bar.setVisibility(View.VISIBLE);


//        floater.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(webView.canGoForward()){
//                    webView.goForward();
//                }
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void WebAction(){
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setAllowFileAccess(true);
        //Optimize performance
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setEnableSmoothTransition(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        //webView.loadUrl("http://192.168.137.1/admin_system/login.php");
        //webView.loadUrl("http://192.168.0.101/github/kteen/customer_order_system/");
        webView.loadUrl("https://www.google.com/");
        swipe.setRefreshing(true);

        if (!DetectConnection.checkInternetConnection(this)){
            Snackbar snackbar = Snackbar.make(webView,"No Internet!",Snackbar.LENGTH_LONG);
            snackbar.show();
        }


        webView.setWebViewClient(new WebViewClient(){

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                webView.loadUrl("\"file:///android_assets/error.html\"");
                super.onReceivedError( webView, errorCode, description, failingUrl );
                //Toast.makeText( WebViewActivity.this, description, Toast.LENGTH_LONG );
                Snackbar snackbar = Snackbar.make(webView,description,Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            public void onPageFinished(WebView view, String url){
                //do stuff here
                swipe.setRefreshing(false);

                //backButton.setEnabled(view.canGoBack());
                //floater.setEnabled(view.canGoForward());
                //bar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }




        });
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
//        else {
//            finish();
//        }
        if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            webView.clearCache(true);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar snackbar = Snackbar.make(webView,"Press again to exit",Snackbar.LENGTH_LONG);
        snackbar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        },2000);
    }
    //Navigate web page history
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }




}

