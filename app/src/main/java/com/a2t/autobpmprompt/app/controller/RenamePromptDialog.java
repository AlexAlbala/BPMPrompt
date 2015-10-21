package com.a2t.autobpmprompt.app.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.a2t.autobpmprompt.R;


public class RenamePromptDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    RenamePromptDialogListener mListener;
    EditText title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View dialogView = inflater.inflate(R.layout.dialog_rename_prompt, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setMessage(getString(R.string.rename_prompt_title))
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String mTitle = title.getText().toString();

                        mListener.onPromptRenamed(RenamePromptDialog.this, mTitle);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onPromptRenameCancelled(RenamePromptDialog.this);
                    }
                });

        title = (EditText) dialogView.findViewById(R.id.prompt_rename_title);

        String promptName = getArguments().getString("promptName");
        title.setText(promptName);

        return builder.create();
    }

    public interface RenamePromptDialogListener {
        void onPromptRenamed(DialogFragment dialog, String name);

        void onPromptRenameCancelled(DialogFragment dialog);
    }

    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetListDialogListener so we can send events to the host
            mListener = (RenamePromptDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RenamePromptDialogListener");
        }
    }
}