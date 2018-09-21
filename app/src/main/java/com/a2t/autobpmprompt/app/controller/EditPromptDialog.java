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

import com.a2t.autobpmprompt.app.lib.StringUtils;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.TempoRecord;


public class EditPromptDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    PromptDialogListener mListener;
    EditText title;
    EditText upper_tempo;
    EditText lower_tempo;
    EditText bpm;

    public static EditPromptDialog editPromptDialog(PromptSettings ps) {
        EditPromptDialog md = new EditPromptDialog();
        Bundle args = new Bundle();
        args.putBoolean("edit", true);

        TempoRecord tr = ps.getTempoTrack().get(0);
        args.putString("title", ps.getName());
        args.putInt("bpm", tr.getBpm());
        args.putInt("upper_tempo", tr.getUpper());
        args.putInt("lower_tempo", tr.getLower());

        md.setArguments(args);
        return md;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_edit_prompt, null);


        final Bundle b = getArguments();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setMessage(getString(R.string.edit_marker_title))
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPromptUpdated(title.getText().toString(), Integer.parseInt(bpm.getText().toString()), Integer.parseInt(upper_tempo.getText().toString()), Integer.parseInt(lower_tempo.getText().toString()));
                    }
                }).setNegativeButton(R.string.cancel_button, null);

        title = (EditText) dialogView.findViewById(R.id.prompt_title);
        upper_tempo = (EditText) dialogView.findViewById(R.id.prompt_upper);
        lower_tempo = (EditText) dialogView.findViewById(R.id.prompt_lower);
        bpm = (EditText) dialogView.findViewById(R.id.prompt_bpm);

        if (StringUtils.isNotEmpty(b.getString("title"))) {
            title.setText(b.getString("title"));
        }

        if (b.getInt("bpm", -1) != -1) {
            bpm.setText(String.valueOf(b.getInt("bpm")));
        }

        if (b.getInt("upper_tempo", -1) != -1) {
            upper_tempo.setText(String.valueOf(b.getInt("upper_tempo")));
        }

        if (b.getInt("lower_tempo", -1) != -1) {
            lower_tempo.setText(String.valueOf(b.getInt("lower_tempo")));
        }

        return builder.create();
    }

    public interface PromptDialogListener {
        void onPromptUpdated(String title, int bpm, int upper_tempo, int lower_tempo);
    }

    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PDFDialogResultListener so we can send events to the host
            mListener = (PromptDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MarkerDialogListener");
        }
    }
}