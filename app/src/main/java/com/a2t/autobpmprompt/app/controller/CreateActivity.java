package com.a2t.autobpmprompt.app.controller;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.media.PromptManager;
import com.a2t.autobpmprompt.app.model.PromptSettings;

import java.io.File;


public class CreateActivity extends AppCompatActivity implements PDFDialog.PDFDialogResultListener {
    private static final String TAG = "CreateActivity";

    EditText name;
    EditText bpm;
    EditText upper;
    EditText lower;

    File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        name = (EditText)findViewById(R.id.create_name);
        bpm = (EditText)findViewById(R.id.create_bpm);
        upper = (EditText)findViewById(R.id.create_bar_upper);
        lower = (EditText)findViewById(R.id.create_bar_lower);
    }

    public void loadPDFClick(View v){
        Log.i(TAG, "Load pdfs");
        DialogFragment newFragment = new PDFDialog();
        newFragment.show(getSupportFragmentManager(), "pdfselect");
    }

    public void createPromptClick(View v){
        Log.i(TAG, "Create prompt");
        Log.i(TAG, "PDF: " + pdfFile.getAbsolutePath());

        PromptSettings promptSettings = new PromptSettings();
        promptSettings.setName(name.getText().toString());
        promptSettings.setPdfFullPath(pdfFile.getAbsolutePath());
        promptSettings.setBpm(Integer.parseInt(bpm.getText().toString()));
        promptSettings.setCfg_bar_upper(Integer.parseInt(upper.getText().toString()));
        promptSettings.setCfg_bar_lower(Integer.parseInt(lower.getText().toString()));

        PromptManager.create(getApplicationContext(), promptSettings);

        //TODO: Launch PromptActivity in Edit mode
    }

    @Override
    public void onPDFSelected(DialogFragment dialog, String fullPath) {
        Log.i(TAG, "CLICK BACK " + fullPath);
        pdfFile = new File(fullPath);
        dialog.dismiss();
    }

}
