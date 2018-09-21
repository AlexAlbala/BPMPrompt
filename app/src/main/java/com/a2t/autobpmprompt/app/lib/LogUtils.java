package com.a2t.autobpmprompt.app.lib;

import android.util.Log;

/**
 * Created by Alex on 18/4/16.
 */
public class LogUtils {

    private static int DEFAULT_STACK_TRACE_STEPS = 3;

    public static void d(String TAG, String message) {
        d(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    public static void v(String TAG, String message) {
        v(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    public static void i(String TAG, String message) {
        i(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    public static void w(String TAG, String message) {
        w(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    public static void e(String TAG, String error) {
        e(DEFAULT_STACK_TRACE_STEPS, TAG, error);
    }

    public static void e(String TAG, String error, Throwable e) {
        e(DEFAULT_STACK_TRACE_STEPS, TAG, error, e);
    }

    public static void d(int stack, String TAG, String message) {
        if (BuildUtils.isDebugBuild())
            Log.d(getTag(stack, TAG), message);
    }

    public static void v(int stack, String TAG, String message) {
        if (BuildUtils.isDebugBuild())
            Log.v(getTag(stack, TAG), message);
    }

    public static void i(int stack, String TAG, String message) {
        Log.i(getTag(stack, TAG), message);
    }

    public static void w(int stack, String TAG, String message) {
        Log.w(getTag(stack, TAG), message);
    }

    public static void e(int stack, String TAG, String error) {
        Log.e(getTag(stack, TAG), error, new Exception(error));
    }

    public static void e(int stack, String TAG, String error, Throwable e) {
        Log.e(getTag(stack, TAG), error, e);
    }

    public static String getTag(int stack, String TAG) {
        if (!BuildUtils.isDebugBuild()) return TAG;

        String tag = "";
        try {
            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            for (int i = 0; i < ste.length; i++) {
                if (ste[i].getMethodName().equals("getTag")) {
                    if (i + stack < ste.length) {
                        tag = "(" + ste[i + stack].getFileName() + ":" + ste[i + stack].getLineNumber() + ")";
                    }
                }
            }

            if (tag.equals("")) {
                tag = TAG;
            }
        } catch (Exception e) {
            tag = TAG;
        }

        if(tag.length() > 23){
            if(tag.contains("Activity")){
                tag = tag.replace("Activity", "Act");
            }

            if(tag.contains("Fragment")){
                tag = tag.replace("Fragment", "Frg");
            }

            if(tag.contains("Adapter")){
                tag = tag.replace("Adapter", "Adp");
            }

            if(tag.contains("Manager")){
                tag = tag.replace("Manager", "Mgr");
            }

            if(tag.contains("Helper")){
                tag = tag.replace("Helper", "Hpr");
            }
        }

        if (tag.length() > 23) {
            tag = tag.substring(0,20) + "...";
        }

        return tag;
    }

}
