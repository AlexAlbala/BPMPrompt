package com.a2t.autobpmprompt.app.lib;

import android.util.Log;

public abstract class SimpleCallback {
    private static final String TAG = "SmpCb";

    public abstract void done();

    public void error(String message, Throwable error){
        Log.e(TAG, message, error);
    }

}
