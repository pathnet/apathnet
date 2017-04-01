package com.pathnet.weiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pathnet.activity.LoginActivity;
import com.pathnet.network.api.IBcsApi;


/**
 * @Copyright © 2016 上海安捷力信息系统有限公司. All rights reserved.
 * @Title: WebViewInterface.java
 * @Prject: ShopSales1017
 * @Package: com.asc.businesscontrol.appwidget.webview
 * @Description: WebView 交互
 * @author:孙波
 * @date: 2016-11-11 下午1:57:20
 * @version: V2.1.0
 */
public class WebViewInterface {
    public static final String mUserAgent = "d2store/2.2.0 (API-Version 2.2.0)";
    private Context mContext;
    private NumberProgressBar mNumberProgressbar;

    public WebViewInterface(Context context) {
        this.mContext = context;
    }

    /**
     * @param clazz
     * @param orgId
     * @Title: startUi
     * @Description: 页面跳转
     * @return: void
     */
    public void startUi(Class<?> clazz, String orgId) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("ProductsDetailsActivityId", orgId);
        intent.putExtra("ActionDetailsInforActivityId", orgId);
        intent.putExtra("orgId", orgId);
        mContext.startActivity(intent);
    }

    /**
     * @param webView
     * @param urlString
     * @Title: setWebView
     * @Description: 初始化webview
     * @return: void
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView(WebView webView, String urlString) {
        // 设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置是否支持缩放
        webView.getSettings().setBuiltInZoomControls(false);
        // 设置缓冲的模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebViewChromeClient());
        webView.loadUrl(urlString);
        webView.setWebViewClient(new MyWebViewClient());
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView(WebView webView, String urlString, boolean tag) {
        // 设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置是否支持缩放
        webView.getSettings().setBuiltInZoomControls(false);
        // 设置缓冲的模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (tag) {
            webView.setWebChromeClient(new WebViewChromeClient());
        }
        webView.loadUrl(urlString);
        webView.setWebViewClient(new MyWebViewClient());
    }

    /**
     * @param webView           设置的webview
     * @param numberProgressBar 进度条控件
     * @param url               跳转的url
     * @Title: synCookies
     * @Description:
     * @return: void
     */
    public void synCookies(WebView webView, NumberProgressBar numberProgressBar, String url) {
        if (!webView.getSettings().getUserAgentString().contains(WebViewInterface.mUserAgent)) {
            webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString().trim() + " " + WebViewInterface.mUserAgent);
        }
        CookieSyncManager.createInstance(mContext);
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        String oldCookie = cookieManager.getCookie(url);
        if (oldCookie != null) {
            Log.d("webView.syncCookie!!!", oldCookie);
        }
//		String sessionId = SharePreferenceUtil.getInstance(mContext).getSharePreferenceVlaue(RequestApi.SESSIONId);
//		String path = SharePreferenceUtil.getInstance(mContext).getSharePreferenceVlaue("path");
//		String domain = SharePreferenceUtil.getInstance(mContext).getSharePreferenceVlaue("domain");
//		StringBuilder sbCookie = new StringBuilder();
//		sbCookie.append(String.format("sid=%s", sessionId));
//		sbCookie.append(String.format(";domain=%s", domain));
//		sbCookie.append(String.format(";path=%s", path));
//		cookieManager.setCookie(url, sbCookie.toString());
        CookieSyncManager.getInstance().sync();
        // 初始化进度条
        mNumberProgressbar = numberProgressBar;
        numberProgressBar.setPrefix("");
        numberProgressBar.setSuffix("%");
        setWebView(webView, url);
    }

    public void synCookies(WebView webView, String url) {
        if (!webView.getSettings().getUserAgentString().contains(WebViewInterface.mUserAgent)) {
            webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString().trim() + " " + WebViewInterface.mUserAgent);
        }
        CookieSyncManager.createInstance(mContext);
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        String oldCookie = cookieManager.getCookie(url);
        if (oldCookie != null) {
            Log.d("webView.syncCookie!!!", oldCookie);
        }
//		String sessionId = SharePreferenceUtil.getInstance(mContext).getSharePreferenceVlaue(RequestApi.SESSIONId);
//		String path = SharePreferenceUtil.getInstance(mContext).getSharePreferenceVlaue("path");
//		String domain = SharePreferenceUtil.getInstance(mContext).getSharePreferenceVlaue("domain");
//		StringBuilder sbCookie = new StringBuilder();
//		sbCookie.append(String.format("sid=%s", sessionId));
//		sbCookie.append(String.format(";domain=%s", domain));
//		sbCookie.append(String.format(";path=%s", path));
//		cookieManager.setCookie(url, sbCookie.toString());
        CookieSyncManager.getInstance().sync();
        setWebView(webView, url, false);
    }

    /**
     * @Copyright © 2016 上海安捷力信息系统有限公司. All rights reserved.
     * @Title: WebViewInterface.java
     * @Prject: ShopSales1017
     * @Package: com.asc.businesscontrol.appwidget.webview
     * @Description: webview 视图
     * @author:孙波
     * @date: 2016-11-11 下午2:48:50
     * @version: V2.1.0
     */
    public class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            String url1 = view.getUrl();
            if (TextUtils.isEmpty(url1)) {
                url1 = "";
            }
            if (url1.startsWith(IBcsApi.BASE_URL + "/login")) {
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }
            return true;
        }
    }

    /**
     * @Copyright © 2016 上海安捷力信息系统有限公司. All rights reserved.
     * @Title: WebViewInterface.java
     * @Prject: ShopSales1017
     * @Package: com.asc.businesscontrol.appwidget.webview
     * @Description: webview 加载进度
     * @author:孙波
     * @date: 2016-11-11 下午2:49:15
     * @version: V2.1.0
     */
    public class WebViewChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mNumberProgressbar.setVisibility(View.GONE);
            } else {
                if (View.GONE == mNumberProgressbar.getVisibility()) {
                    mNumberProgressbar.setVisibility(View.VISIBLE);
                }
                mNumberProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
