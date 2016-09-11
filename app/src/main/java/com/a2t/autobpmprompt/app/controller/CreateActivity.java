package com.a2t.autobpmprompt.app.controller;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.TempoRecord;
import com.a2t.autobpmprompt.media.PromptManager;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.media.audiotools.TapTempo;
import com.a2t.autobpmprompt.media.prompt.PromptViewManager;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;


public class CreateActivity extends A2TActivity implements PDFDialog.PDFDialogResultListener {
    EditText name;
    EditText bpm;
    EditText upper;
    EditText lower;
    PDFView pdfPreview;
    File pdfFile;
    String setList;
    int setListPosition;
    TapTempo t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        name = (EditText)findViewById(R.id.create_name);
        bpm = (EditText)findViewById(R.id.create_bpm);
        upper = (EditText)findViewById(R.id.create_bar_upper);
        lower = (EditText)findViewById(R.id.create_bar_lower);
        pdfPreview = (PDFView)findViewById(R.id.create_pdfpreview);

        setList = getIntent().getStringExtra("setListName");
        setListPosition = getIntent().getIntExtra("setListPosition", -1);

        t = new TapTempo();
    }

    public void loadPDFClick(View v){
        Log.i(TAG, "Load pdfs");
        DialogFragment newFragment = new PDFDialog();
        newFragment.show(getFragmentManager(), "pdfselect");
    }

    public void onTapClick(View v) {
        int cBpm = t.Tap();
        if(cBpm > 0) {
            bpm.setText(String.valueOf(cBpm));
        }
    }

    public void createPromptClick(View v){
        Log.i(TAG, "Create prompt");
        Log.i(TAG, "PDF: " + pdfFile.getAbsolutePath());

        PromptSettings promptSettings = new PromptSettings();
        promptSettings.setName(name.getText().toString());
        promptSettings.setPdfFullPath(pdfFile.getAbsolutePath());
        RealmList<TempoRecord> tempoTrack = new RealmList<>();
        TempoRecord tr = new TempoRecord();
        tr.setBar(1);
        tr.setBeat(1);
        tr.setBpm(Integer.parseInt(bpm.getText().toString()));
        tr.setUpper(Integer.parseInt(upper.getText().toString()));
        tr.setLower(Integer.parseInt(lower.getText().toString()));
        tempoTrack.add(tr);
        promptSettings.setTempoTrack(tempoTrack);
        promptSettings.setSetList(setList);
        promptSettings.setSetListPosition(setListPosition);

        PromptManager.create(getApplicationContext(), promptSettings);

        //LAUNCH PROMPT EDIT ACTIVITY
        Intent i = new Intent(getApplicationContext(), PromptActivity.class);
        i.putExtra("promptId", promptSettings.getId());
        i.putExtra("setListName", setList);
        i.putExtra("isEdit", true);

        startActivity(i);
        finish();
    }

    @Override
    public void onPDFSelected(DialogFragment dialog, String fullPath) {
        Log.i(TAG, "CLICK BACK " + fullPath);
        pdfFile = new File(fullPath);

        PromptViewManager.loadThumbnail(pdfFile, pdfPreview);
        dialog.dismiss();
    }

}
