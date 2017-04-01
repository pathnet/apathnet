package com.pathnet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    public final static String SP_NAME = "BusinessControl";
    private static SharedPreferences sp;
    private static SPUtil share = null;
    private SharedPreferences.Editor editor;

    public SPUtil(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        sp.edit().putBoolean(key, value).commit();
    }

    public static void setInt(Context context, String key, int value) {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        sp.edit().putInt(key, value).commit();
    }

    public static void setString(Context context, String key, String value) {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        sp.edit().putString(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getInt(key, defValue);

    }

    public static int getInt(Context context, String key) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getInt(key, -1);

    }

    public static String getString(Context context, String key, String defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getString(key, defValue);
    }

    public static String getString(Context context, String key) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getString(key, "");
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getBoolean(key, defValue);
    }

    public static boolean getBoolean(Context context, String key) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getBoolean(key, false);
    }

    public static SPUtil getInstance(Context context) {
        if (share == null) {
            share = new SPUtil(context);
        }
        return share;
    }

    public void setSharePreferenceVlaue(String modle, String vlaue) {
        editor.putString(modle, vlaue);
        editor.commit();
    }

    public void setSharePreferenceVlaue(String modle, long vlaue) {
        editor.putLong(modle, vlaue);
        editor.commit();
    }

    public void setSharePreferenceVlaue(String key, int vlaue) {
        editor.putInt(key, vlaue);
        editor.commit();
    }

    public void setSharePreferenceVlaue(String vlaue, boolean bool) {
        editor.putBoolean(vlaue, bool);
        editor.commit();
    }

    /*清除所有数据*/
    public void clearAllData() {
        if (editor != null) {
            editor.clear().commit();
        }
        if (sp != null) {
            sp.edit().clear().commit();
        }
    }

    public String getSharePreferenceVlaue(String modle) {
        return sp.getString(modle, "");
    }

    public int getSharePreferenceInt(String modle) {
        return sp.getInt(modle, 0);
    }

    public int getSharePreferenceInt(String modle, int def) {
        return sp.getInt(modle, def);
    }

    public boolean getSharePreferenceBoolean(String modle) {
        return sp.getBoolean(modle, false);
    }

    public long getSharePreferenceLong(String modle) {
        return sp.getLong(modle, 0l);
    }


}
