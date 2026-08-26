package com.general.files;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.multixpro.user.R;
import com.utils.Utils;
import com.view.anim.loader.AVLoadingIndicatorView;

public class SafetyDialog extends AppCompatActivity {
    private Context mContext;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_safety_measure);

        ImageView backArrowImgView = (ImageView) findViewById(R.id.backArrowImgView);

        backArrowImgView.setOnClickListener(v -> finish());
        WebView mWebView = (WebView) findViewById(R.id.webView);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        mWebView.setWebViewClient(new myWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 90) {
                    loaderView.setVisibility(View.GONE);
                }
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(MyApp.isJSEnabled);

        String url = getIntent().getStringExtra("URL");
        mWebView.loadUrl(url + "&fromapp=yes");
        loaderView.setVisibility(View.VISIBLE);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    public void open(String url, String imageURL) {
        final Dialog addAddressDailog = new Dialog(mContext);
        View contentView = View.inflate(mContext, R.layout.dailog_safety_measure, null);

        addAddressDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(ViewGroup.LayoutParams.MATCH_PARENT, mContext)));
        addAddressDailog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addAddressDailog.setCancelable(true);

        int screenHeight = ((int) Utils.getScreenPixelHeight(mContext));
        int peekHeight = 0;
        int bottomMarginForLoader = Utils.dpToPx(50, mContext);


        ImageView backArrowImgView = (ImageView) addAddressDailog.findViewById(R.id.backArrowImgView);

        backArrowImgView.setOnClickListener(v -> addAddressDailog.dismiss());
        RelativeLayout container = (RelativeLayout) addAddressDailog.findViewById(R.id.container);
        WebView mWebView = (WebView) addAddressDailog.findViewById(R.id.webView);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) addAddressDailog.findViewById(R.id.loaderView);

        RelativeLayout.LayoutParams loaderView_ly_params = (RelativeLayout.LayoutParams) loaderView.getLayoutParams();
        loaderView_ly_params.bottomMargin = screenHeight - peekHeight + bottomMarginForLoader;
        loaderView.setLayoutParams(loaderView_ly_params);

//        mWebView.setWebViewClient(new myWebClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 90) {
                    loaderView.setVisibility(View.GONE);
                }
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(MyApp.isJSEnabled);


        mWebView.loadUrl(url + "&fromapp=yes");

        loaderView.setVisibility(View.VISIBLE);

        addAddressDailog.show();
    }

    public class myWebClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }
}
