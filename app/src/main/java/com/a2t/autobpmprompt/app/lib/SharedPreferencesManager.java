package com.a2t.autobpmprompt.app.lib;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesManager {

    public static void set(Context ctx, String key, String value) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static void set(Context ctx, String key, boolean value) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public static void set(Context ctx, String key, int value) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public static void set(Context ctx, String key, double value) {
        set(ctx, key, (float) value);
    }

    public static void set(Context ctx, String key, float value) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    public static String getString(Context ctx, String key) {
        return getString(ctx, key, null);
    }

    public static String getString(Context ctx, String key, String def) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            return sp.getString(key, def);
        }
        return null;
    }

    public static int getInt(Context ctx, String key) {
        return getInt(ctx, key, 0);
    }

    public static int getInt(Context ctx, String key, Integer defaultValue) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            return sp.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public static boolean getBoolean(Context ctx, String key) {
        return getBoolean(ctx, key, false);
    }

    public static boolean getBoolean(Context ctx, String key, boolean defaultValue) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            return sp.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public static float getFloat(Context ctx, String key, float defaultValue) {
        if (ctx != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            return sp.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    public static void debug(Context context, boolean alert) {
        debug(context, PreferenceManager.getDefaultSharedPreferences(context), alert);
    }

    private static void debug(Context context, SharedPreferences sp, boolean alert) {
        if (BuildUtils.isDebugBuild()) {

            Map<String, ?> prefs = sp.getAll();
            String printVal = "";
            for (String key : prefs.keySet()) {
                Object pref = prefs.get(key);
                if (pref instanceof Boolean) {
                    printVal += key + " : " + (Boolean) pref + "\n";
                }
                if (pref instanceof Float) {
                    printVal += key + " : " + (Float) pref + "\n";
                }
                if (pref instanceof Integer) {
                    printVal += key + " : " + (Integer) pref + "\n";
                }
                if (pref instanceof Long) {
                    printVal += key + " : " + (Long) pref + "\n";
                }
                if (pref instanceof String) {
                    printVal += key + " : " + (String) pref + "\n";
                }
                if (pref instanceof Set<?>) {
                    printVal += key + " : " + (Set<String>) pref + "\n";
                }
            }

            if (alert) {
                new AlertDialog.Builder(context)
                        .setMessage(printVal)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                LogUtils.d("SPMANAGER", printVal);
            }
        }
    }

    public static void debug(Context context, String prefs_name, boolean alert) {
        if (BuildUtils.isDebugBuild()) {
            debug(context, context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE), alert);
        }
    }

    public static boolean has(Context ctx, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.contains(key);
    }

    public static void remove(Context ctx, String key) {
        if (ctx != null) {
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.remove(key);
            editor.apply();
        }
    }
}
