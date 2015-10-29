package com.a2t.autobpmprompt.app.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.a2t.autobpmprompt.R;


public class AreYouSureDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    AreYouSureDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage(getString(R.string.areyousure_dialog_title))
                .setPositiveButton(R.string.areyousure_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onOk(AreYouSureDialog.this, getArguments());
                    }
                })
                .setNegativeButton(R.string.areyousure_dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onCancel(AreYouSureDialog.this, getArguments());
                    }
                });

        return builder.create();
    }

    public interface AreYouSureDialogListener {
        void onOk(DialogFragment dialog, Bundle args);
        void onCancel(DialogFragment dialog, Bundle args);
    }

    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetListDialogListener so we can send events to the host
            mListener = (AreYouSureDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AreYouSureDialogListener");
        }
    }
}