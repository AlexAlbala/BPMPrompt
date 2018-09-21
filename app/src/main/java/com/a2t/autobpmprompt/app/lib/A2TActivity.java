package com.a2t.autobpmprompt.app.lib;

import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.a2t.autobpmprompt.BuildConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class A2TActivity extends AppCompatActivity {
    private static int DEFAULT_STACK_TRACE_STEPS = 4;
    protected boolean DEBUG = BuildConfig.DEBUG;
    protected final String TAG = this.getClass().getSimpleName();
    private List<BackCallback> backButtonListeners = new ArrayList<>();

    public A2TActivity() {
        DEBUG = BuildUtils.isDebugBuild();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void disableShowHideAnimation(ActionBar actionBar) {
        try {
            actionBar.setShowHideAnimationEnabled(false);
        } catch (Exception exception) {
            lerror("SupporActionBar", exception);
        }
    }

    public void addOnBackButtonListener(BackCallback listener) {
        if (!backButtonListeners.contains(listener)) {
            backButtonListeners.add(listener);
        }
    }

    @Override
    public void onBackPressed() {
        if (backButtonListeners != null) {
            if (backButtonListeners.size() > 0) {
                boolean result = false;
                for (BackCallback cb : backButtonListeners) {
                    result = cb.onBack();
                    if(result){
                        break;
                    }
                }
                if(!result){
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}