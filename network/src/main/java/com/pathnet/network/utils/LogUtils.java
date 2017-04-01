package com.pathnet.network.utils;

import android.util.Log;

public class LogUtils {
    public static final String mTag = "asc_";
    public static final String RESPONSE_ERROR = "--【responseError】--";
    public static final String CODE = "--【code】--";
    public static final String URL = "--【url】--";
    public static final String MSG = "--【msg】--";
    private static final boolean ENABLE = true;

    /**
     * 打印一个debug等级的log
     */
    public static void d(String tag, String msg) {
        if (ENABLE) {
            Log.d(mTag + tag, msg);
        }
    }

    /**
     * 打印一个debug等级的log
     */
    public static void d(Class cls, String msg) {
        if (ENABLE) {
            Log.d(mTag + cls.getSimpleName(), msg);
        }
    }

    /**
     * 打印一个 error 等级的log
     */
    public static void e(String tag, String msg) {
        if (ENABLE) {
            Log.e(mTag + tag, msg);
        }
    }

    /**
     * 打印一个 error 等级的log
     */
    public static void e(Class cls, String msg) {
        if (ENABLE) {
            Log.e(mTag + cls.getSimpleName(), msg);
        }
    }
}
