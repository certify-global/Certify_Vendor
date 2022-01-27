package com.netronix.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class CommPref {
    static final String TAG = CommPref.class.getSimpleName();

//    enum ValType {
//        STRING,
//        INT,
//        LONG,
//        BOOL
//    }

    //abstract ValType getFieldType (String field);


    public static void prefSetString (Context context, String prefName, String key, String val) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        pref.edit().putString(key, val).commit();
    }

    public static String prefGetString (Context context, String prefName, String key, String defaultVal) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String str = pref.getString(key, defaultVal);
        return str;
    }

    public static void prefSetInt (Context context, String prefName, String key, int val) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        pref.edit().putInt(key, val).commit();
    }

    public static int prefGetInt (Context context, String prefName, String key, int defaultVal) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        int ret = pref.getInt(key, defaultVal);
        Log.i(TAG, "prefGetInt("+prefName+":"+ret+")");
        return ret;
    }

    public static void prefSetLong (Context context, String prefName, String key, long val) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        pref.edit().putLong(key, val).commit();
    }

    public static long prefGetLong (Context context, String prefName, String key, long defaultVal) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        long ret = pref.getLong(key, defaultVal);
        Log.i(TAG, "prefGetInt("+prefName+":"+ret+")");
        return ret;
    }

    public static void prefSetBool (Context context, String prefName, String key, boolean val) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        pref.edit().putBoolean(key, val).commit();
    }

    public static boolean prefGetBool (Context context, String prefName, String key, boolean defaultVal) {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        boolean ret = pref.getBoolean(key, defaultVal);
        return ret;
    }

}
