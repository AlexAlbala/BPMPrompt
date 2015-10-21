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


public class MarkerDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    MarkerDialogListener mListener;
    EditText title;
    EditText note;
    EditText bar;
    EditText beat;
    float mX;
    float mY;
    int mPage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_marker, null);

        Bundle b = getArguments();
        mX = b.getFloat("xOffset");
        mY = b.getFloat("yOffset");
        mPage = b.getInt("page");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setMessage(getString(R.string.create_marker_title))
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener.onMarkerCreated();
                        String mTitle = title.getText().toString();
                        String mNote = note.getText().toString();
                        int mBar = Integer.parseInt(bar.getText().toString());
                        int mBeat = Integer.parseInt(beat.getText().toString());
                        mListener.onMarkerCreated(MarkerDialog.this, mTitle, mNote, mBar, mBeat, mPage, mX, mY);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onMarkerCancelled(MarkerDialog.this);
                    }
                });

        title = (EditText) dialogView.findViewById(R.id.marker_title);
        note = (EditText) dialogView.findViewById(R.id.marker_note);
        bar = (EditText) dialogView.findViewById(R.id.marker_bar);
        beat = (EditText) dialogView.findViewById(R.id.marker_beat);

        return builder.create();
    }

    public interface MarkerDialogListener {
        void onMarkerCreated(DialogFragment dialog, String title, String note, int bar, int beat, int page, float positionX, float positionY);

        void onMarkerCancelled(DialogFragment dialog);
    }

    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PDFDialogResultListener so we can send events to the host
            mListener = (MarkerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MarkerDialogListener");
        }
    }
}