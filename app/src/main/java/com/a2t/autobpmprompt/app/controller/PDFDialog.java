package com.a2t.autobpmprompt.app.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.PDFGridAdapter;
import com.a2t.autobpmprompt.app.callback.PDFGridCallback;
import com.a2t.autobpmprompt.media.prompt.PromptPDFFile;
import com.a2t.autobpmprompt.media.prompt.PDFFiles;

import java.util.List;


public class PDFDialog extends DialogFragment implements PDFGridCallback {
    // Use this instance of the interface to deliver action events
    PDFDialogResultListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_pdfs, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setMessage(getString(R.string.select_pdf_title));

        AlertDialog dialog = builder.create();

        List<PromptPDFFile> pdfs = PDFFiles.findAllPDFs();
        GridView gridview = (GridView) dialogView.findViewById(R.id.dialog_gridpdf);
        gridview.setAdapter(new PDFGridAdapter(getActivity(), pdfs, false, false, this));
        return dialog;
    }

    @Override
    public void onPDFSelected(String fullPath, int position) {
        Toast.makeText(getActivity(), "Selected file " + fullPath, Toast.LENGTH_SHORT).show();
        mListener.onPDFSelected(PDFDialog.this, fullPath);
    }

    @Override
    public void onCreatePDFClicked() {
        //DO NOTHING :)
    }

    @Override
    public void onPDFRemoveClicked(String fullPath, int id) {
        //Do nothing :)
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PDFDialogResultListener {
        void onPDFSelected(DialogFragment dialog, String fullPath);
    }


    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PDFDialogResultListener so we can send events to the host
            mListener = (PDFDialogResultListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PDFDialogResultListener");
        }
    }
}