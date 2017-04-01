package com.pathnet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.pathnet.interfaces.IBcsConstants;
import com.pathnet.weiget.NumberProgressBar;
import com.pathnet.weiget.WebViewInterface;

import java.util.List;

/**
 * Created by root on 16-6-24.
 */
public class UiUtils {
    public static int responseCode = 200;
    public static int responseUpdateCode = 203;
    private static Context mContext;
    private static NumberProgressBar mNumberProgressbar;
    private static String LET_GO_OF_UPDATE = "放开以刷新";
    private static String NO_MORE_DATA = "暂无更多数据";
    private static String DOWN_UPDATE = "下拉刷新";
    private static String UPDATE = "正在刷新";
    private static String LOADING = "正在加载...";
    private static String UP_LOADING = "上拉加载";

    @SuppressLint("SetJavaScriptEnabled")
    public static void setWebView(WebView webView, NumberProgressBar numberProgressBar, String urlString) {
        if (!webView.getSettings().getUserAgentString().contains(WebViewInterface.mUserAgent)) {
            webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString().trim() + " " + WebViewInterface.mUserAgent);
        }
        // synCookies(mContext, urlString, "");
        mNumberProgressbar = numberProgressBar;
        numberProgressBar.setPrefix("");
        numberProgressBar.setSuffix("%");
        // 设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置是否支持缩放
        webView.getSettings().setBuiltInZoomControls(false);
        // 设置缓冲的模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new MyChromeClient());
        webView.loadUrl(urlString);
        webView.setWebViewClient(new MyWebViewClient());
    }

    /**
     * 在浏览器中内部登录 同步一下cookie
     */
    public static void synCookies(Context context, String url, String cookies) {
        CookieSyncManager.createInstance(context);
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        String oldCookie = cookieManager.getCookie(url);
        if (oldCookie != null) {
            Log.d("webView.syncCookie", oldCookie);
        }
        String sessionId = SPUtil.getInstance(context).getSharePreferenceVlaue(IBcsConstants.SESSIONId);
        String path = SPUtil.getInstance(context).getSharePreferenceVlaue("path");
        String domain = SPUtil.getInstance(context).getSharePreferenceVlaue("domain");
        StringBuilder sbCookie = new StringBuilder();
        sbCookie.append(String.format("sid=%s", sessionId));
        sbCookie.append(String.format(";domain=%s", domain));
        sbCookie.append(String.format(";path=%s", path));
        String cookieValue = sbCookie.toString();
        cookieManager.setCookie(url, cookieValue);
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 去除null
     *
     * @param value
     * @return
     */
    public static String getText(String value) {
        return TextUtils.isEmpty(value) ? "" : value.trim();
    }

    /**
     * 获取EditText的值
     *
     * @param editText
     * @return
     */
    public static String getText(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim()) ? "" : editText.getText().toString().trim();
    }

    /**
     * 获取textView的值
     *
     * @param editText
     * @return
     */
    public static String getText(TextView editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim()) ? "" : editText.getText().toString().trim();
    }

    /**
     * 获取Button的值
     *
     * @param editText
     * @return
     */
    public static String getText(Button editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim()) ? "" : editText.getText().toString().trim();
    }

    public static boolean isEmpty(String... str) {
        boolean flag = false;
        if (null == str) {
            return false;
        }
        for (int i = 0; i < str.length; i++) {
            if (!TextUtils.isEmpty(str[i])) {
                return true;
            }
            flag = false;
        }
        return flag;
    }

    public static boolean isEmptyAll(String... str) {
        if (null == str) {
            return true;
        }
        for (int i = 0; i < str.length; i++) {
            if (TextUtils.isEmpty(str[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算出该TextView中文字的长度(像素)
     *
     * @param textView
     * @param text
     * @return
     */
    public static float getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        float textLength = paint.measureText(text);
        return textLength;
    }

    /**
     * @param view 刷新控件
     * @Title: updateUI
     * @Description: 下拉刷新上拉加载
     * @return: void
     */
    public static void refresh(PullToRefreshBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getRefreshableView().setVerticalScrollBarEnabled(false);
        view.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(DOWN_UPDATE);
        view.getLoadingLayoutProxy(true, false).setPullLabel("");
        view.getLoadingLayoutProxy(true, false).setRefreshingLabel(UPDATE);
        view.getLoadingLayoutProxy(true, false).setReleaseLabel(LET_GO_OF_UPDATE);
        view.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(UP_LOADING);
        view.getLoadingLayoutProxy(false, true).setPullLabel("");
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(LOADING);
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(LET_GO_OF_UPDATE);
    }

    public static <T> void update(PullToRefreshBase<?> view, List<T> updataLists, List<T> downLists, boolean flag, Handler handler) {
        Message msg = new Message();
        msg.obj = updataLists;
        if (flag) {
            if (updataLists != null && updataLists.size() > 0) {
                view.getLoadingLayoutProxy().setReleaseLabel(LET_GO_OF_UPDATE);
            } else {
                view.getLoadingLayoutProxy(false, true).setReleaseLabel(NO_MORE_DATA);
            }
            msg.what = responseUpdateCode;
        } else {
            if (downLists != null && downLists.size() > 0) {
                view.getLoadingLayoutProxy().setReleaseLabel(LET_GO_OF_UPDATE);
            } else {
                view.getLoadingLayoutProxy().setReleaseLabel(NO_MORE_DATA);
            }
            msg.what = responseCode;
        }
        handler.sendMessage(msg);
    }

    /**
     * @param view 刷新控件
     * @Title: updateUI
     * @Description: 下拉刷新
     * @return: void
     */
    public static void refreshDown(PullToRefreshBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        view.getRefreshableView().setVerticalScrollBarEnabled(false);
        view.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(DOWN_UPDATE);
        view.getLoadingLayoutProxy(true, false).setPullLabel("");
        view.getLoadingLayoutProxy(true, false).setRefreshingLabel(UPDATE);
        view.getLoadingLayoutProxy(true, false).setReleaseLabel(LET_GO_OF_UPDATE);
    }

    /**
     * @param view
     * @Title: updateUI
     * @Description: 设置下拉刷新(屏蔽上拉加载)
     * @return: void
     */
    public static void refreshUp(PullToRefreshBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        view.getRefreshableView().setVerticalScrollBarEnabled(false);
        view.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(UP_LOADING);
        view.getLoadingLayoutProxy(false, true).setPullLabel("");
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(UPDATE);
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(LET_GO_OF_UPDATE);
    }

    /**
     * @param view
     * @Title: updateUI
     * @Description: 禁止所有刷新
     * @return: void
     */
    public static void refreshNo(PullToRefreshBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.DISABLED);
        view.getRefreshableView().setVerticalScrollBarEnabled(false);
        view.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(UP_LOADING);
        view.getLoadingLayoutProxy(false, true).setPullLabel("");
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(UPDATE);
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(LET_GO_OF_UPDATE);
    }

    /**
     * 设置textView 的值
     *
     * @param textView 控件
     * @param value    值
     */
    public void setTextViewValue(TextView textView, String value) {
        textView.setText(TextUtils.isEmpty(value) ? "" : value);
    }

    /**
     * 设置textView 的值
     *
     * @param textView 控件
     * @param value    值
     */
    public void setTextViewValue(TextView textView, String title, String value) {
        textView.setText(title + (TextUtils.isEmpty(value) ? "" : value));
    }

    /**
     * 设置Button 的值
     *
     * @param text  控件
     * @param value 值
     */
    public void setButtonValue(Button text, String value) {
        text.setText(TextUtils.isEmpty(value) ? "" : value);
    }

    /**
     * 设置Button 的值
     *
     * @param text  控件
     * @param value 值
     */
    public void setButtonValue(Button text, String title, String value) {
        text.setText(title + (TextUtils.isEmpty(value) ? "" : value));
    }

    /**
     * 设置CheckBox 的值
     *
     * @param text  控件
     * @param value 值
     */
    public void setCheckBoxValue(CheckBox text, String value) {
        text.setText(TextUtils.isEmpty(value) ? "" : value);
    }

    /**
     * 设置CheckBox 的值
     *
     * @param text  控件
     * @param value 值
     */
    public void setCheckBoxValue(CheckBox text, String title, String value) {
        text.setText(title + (TextUtils.isEmpty(value) ? "" : value));
    }

    // Web视图
    private static class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private static class MyChromeClient extends WebChromeClient {
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

    public class JsInterface {
        public JsInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void f1Time(String content) {
            // estimatedTime=content;
        }
    }
}
