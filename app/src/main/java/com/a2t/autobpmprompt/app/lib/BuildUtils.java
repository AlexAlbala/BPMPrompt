package com.a2t.autobpmprompt.app.lib;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import com.a2t.autobpmprompt.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Alex on 18/4/16.
 */
public class BuildUtils {
    private static Boolean _BUILD_CACHE;

    public static Boolean isDebugBuild() {
        if (_BUILD_CACHE == null) {
            try {
                final Class<?> activityThread = Class.forName("android.app.ActivityThread");
                final Method currentPackage = activityThread.getMethod("currentPackageName");
                final String packageName = (String) currentPackage.invoke(null, (Object[]) null);
                final Class<?> buildConfig = Class.forName(packageName + ".BuildConfig");
                final Field DEBUG = buildConfig.getField("DEBUG");
                DEBUG.setAccessible(true);
                _BUILD_CACHE = DEBUG.getBoolean(null);
            } catch (final Throwable t) {
                final String message = t.getMessage();
                if (message != null && message.contains("BuildConfig")) {
                    // Proguard obfuscated build. Most likely a production build.
                    _BUILD_CACHE = false;
                } else {
                    _BUILD_CACHE = BuildConfig.DEBUG;
                }
            }
        }
        return _BUILD_CACHE;
    }

    public static int getVersionNumber(Context ctx){
        PackageInfo pInfo;
        try {
            pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
