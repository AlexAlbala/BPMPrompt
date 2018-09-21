package com.a2t.autobpmprompt.app.lib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

public class ProgressDialogFactory {

    private static final String TAG = "ProgressDialog";

    public static ProgressDialog createIndeterminated(Activity caller, int resMessage, int style, boolean show) {
        return createIndeterminated(caller, resMessage, style, show, false);
    }

    public static ProgressDialog createIndeterminated(Activity caller, int resMessage, int style, boolean show, boolean cancelable) {
        try {
            ProgressDialog progressDialog = new ProgressDialog(caller);
            progressDialog.setMessage(caller.getResources().getString(resMessage));
            progressDialog.setProgressStyle(style);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(cancelable);

            if (show)
                progressDialog.show();

            return progressDialog;

        } catch (Exception e) {
            return null;
        }
    }

    public static ProgressDialog createFixedMax(Activity caller, int resMessage, int style, boolean show) {
        return createFixedMax(caller, resMessage, style, show, false);
    }

    public static ProgressDialog createFixedMax(Activity caller, int resMessage, int style, boolean show, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(caller);
        progressDialog.setMessage(caller.getResources().getString(resMessage));
        progressDialog.setProgressStyle(style);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setCancelable(cancelable);

        if (show)
            progressDialog.show();

        return progressDialog;
    }

    public static boolean dismiss(ProgressDialog pDialog) {
        if (pDialog != null && pDialog.isShowing()) {
            try {
                pDialog.dismiss();
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error progress dialog", e);
                return false;
            }
        } else {
            return false;
        }
    }
}
