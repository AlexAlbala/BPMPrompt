package com.a2t.autobpmprompt.app.lib;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.a2t.autobpmprompt.BuildConfig;
import com.a2t.autobpmprompt.app.lib.BuildUtils;
import com.a2t.autobpmprompt.app.lib.LogUtils;
import com.a2t.autobpmprompt.app.lib.StringUtils;

public abstract class A2TFragment extends Fragment {
    private static int DEFAULT_STACK_TRACE_STEPS = 4;
    protected boolean DEBUG = BuildConfig.DEBUG;
    protected final String TAG = this.getClass().getSimpleName();

    public A2TFragment() {
        DEBUG = BuildUtils.isDebugBuild();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    protected void ldebug(String message) {
        LogUtils.d(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    protected void lverbose(String message) {
        LogUtils.v(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    protected void linfo(String message) {
        LogUtils.i(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    protected void lwarn(String message) {
        LogUtils.w(DEFAULT_STACK_TRACE_STEPS, TAG, message);
    }

    protected void lerror(String error) {
        LogUtils.e(DEFAULT_STACK_TRACE_STEPS, TAG, error, new Exception(error));
    }

    protected void lerror(String error, Throwable e) {
        LogUtils.e(DEFAULT_STACK_TRACE_STEPS, TAG, error, e);
    }

    protected void alert(@StringRes Integer title, @StringRes Integer message) {
        if (title != 0) {
            alert(getResources().getString(title), getResources().getString(message));
        } else {
            alert("", getResources().getString(message));
        }
    }

    protected void alert(@StringRes Integer message) {
        alert(0, message);
    }

    protected void alert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (StringUtils.isNotEmpty(title)) {
            builder.setTitle(title);
        }

        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();
    }

    protected void alert(String message) {
        alert("", message);
    }
}