package com.a2t.autobpmprompt.app.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.a2t.autobpmprompt.R;


public class SetListDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    SetListDialogListener mListener;
    EditText title;
    String tag;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_setlist, null);
        tag = getTag();

        final boolean isRename = tag.equals("renamesetlist");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setMessage(getString(isRename ? R.string.rename_setlist_title : R.string.create_setlist_title))
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String mTitle = title.getText().toString();

                        if (isRename) {//TODO: ÑAPA ??
                            String originalName = getArguments().getString("setListName");
                            mListener.onSetListRenamed(SetListDialog.this, originalName, mTitle);
                        } else {
                            mListener.onSetListCreated(SetListDialog.this, mTitle);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onSetListCancelled(SetListDialog.this);
                    }
                });

        title = (EditText) dialogView.findViewById(R.id.setlist_title);

        if (isRename) {
            String setList = getArguments().getString("setListName");
            title.setText(setList);
        }

        return builder.create();
    }

    public interface SetListDialogListener {
        void onSetListCreated(DialogFragment dialog, String title);

        void onSetListRenamed(DialogFragment dialog, String title, String newTitle);

        void onSetListCancelled(DialogFragment dialog);
    }

    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetListDialogListener so we can send events to the host
            mListener = (SetListDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SetListDialogListener");
        }
    }
}