package com.a2t.autobpmprompt.app.controller;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.a2tlib.tools.StringUtils;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.TempoRecord;
import com.a2t.autobpmprompt.media.PromptManager;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.media.audiotools.TapTempo;
import com.a2t.autobpmprompt.media.prompt.PromptViewManager;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileNotFoundException;

import io.realm.RealmList;


public class CreateActivity extends A2TActivity implements PDFDialog.PDFDialogResultListener {
    EditText name;
    EditText bpm;
    EditText upper;
    EditText lower;
    //PDFView pdfPreview;
    ImageView pdfPreviewImage;
    File pdfFile;
    String setList;
    int setListPosition;
    TapTempo t;
    private int currentLower;
    private int currentUpper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        name = (EditText) findViewById(R.id.create_name);
        bpm = (EditText) findViewById(R.id.create_bpm);
        upper = (EditText) findViewById(R.id.create_bar_upper);
        lower = (EditText) findViewById(R.id.create_bar_lower);
        //pdfPreview = (PDFView) findViewById(R.id.create_pdfpreview);
        pdfPreviewImage = (ImageView) findViewById(R.id.create_pdfpreview_image);

        setList = getIntent().getStringExtra("setListName");
        setListPosition = getIntent().getIntExtra("setListPosition", -1);

        t = new TapTempo();

        currentLower = currentUpper = 4;

        RadioButton rbTempo44 = (RadioButton) findViewById(R.id.create_tempo_44);
        RadioButton rbTempo34 = (RadioButton) findViewById(R.id.create_tempo_34);
        RadioButton rbTempo68 = (RadioButton) findViewById(R.id.create_tempo_68);
        RadioButton rbTempoOther = (RadioButton) findViewById(R.id.create_tempo_other);

        final View inputTempoOtherLayout = findViewById(R.id.create_tempo_other_inputs);
        inputTempoOtherLayout.setVisibility(View.GONE);

        View.OnClickListener radioTempoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.create_tempo_34:
                        currentUpper = 3;
                        currentLower = 4;
                        inputTempoOtherLayout.setVisibility(View.GONE);
                        break;
                    case R.id.create_tempo_44:
                        currentUpper = 4;
                        currentLower = 4;
                        inputTempoOtherLayout.setVisibility(View.GONE);
                        break;
                    case R.id.create_tempo_68:
                        currentUpper = 6;
                        currentLower = 8;
                        inputTempoOtherLayout.setVisibility(View.GONE);
                        break;
                    case R.id.create_tempo_other:
                        currentUpper = -1;
                        currentLower = -1;
                        inputTempoOtherLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };

        rbTempo44.setOnClickListener(radioTempoListener);
        rbTempo34.setOnClickListener(radioTempoListener);
        rbTempo68.setOnClickListener(radioTempoListener);
        rbTempoOther.setOnClickListener(radioTempoListener);
    }

    public void loadPDFClick(View v) {
        linfo("Load pdfs");
        DialogFragment newFragment = new PDFDialog();
        newFragment.show(getFragmentManager(), "pdfselect");
    }

    public void onTapClick(View v) {
        int cBpm = t.Tap();
        if (cBpm > 0) {
            bpm.setText(String.valueOf(cBpm));
        }
    }

    public void createPromptClick(View v) {
        if (StringUtils.isNotEmpty(name.getText().toString()) && StringUtils.isNotEmpty(bpm.getText().toString())) {
            linfo("Create prompt");
            linfo("PDF: " + pdfFile.getAbsolutePath());

            PromptSettings promptSettings = new PromptSettings();
            promptSettings.setName(name.getText().toString());
            promptSettings.setPdfFullPath(pdfFile.getAbsolutePath());
            RealmList<TempoRecord> tempoTrack = new RealmList<>();
            TempoRecord tr = new TempoRecord();
            tr.setId(System.currentTimeMillis());
            tr.setBar(1);
            tr.setBeat(1);
            tr.setBpm(Integer.parseInt(bpm.getText().toString()));
            if (currentLower == -1 || currentUpper == -1) {
                tr.setUpper(Integer.parseInt(upper.getText().toString()));
                tr.setLower(Integer.parseInt(lower.getText().toString()));
            } else {
                tr.setUpper(currentUpper);
                tr.setLower(currentLower);
            }
            ((RealmList) tempoTrack).add(tr);
            promptSettings.setTempoTrack(tempoTrack);
            promptSettings.setSetList(setList);
            promptSettings.setSetListPosition(setListPosition);

            PromptManager.create(this, promptSettings);
            PromptManager.reorderPromptInSetList(this, setList, -1, -1);

            //LAUNCH PROMPT EDIT ACTIVITY
            Intent i = new Intent(this, PromptActivity.class);
            i.putExtra("promptId", promptSettings.getId());
            i.putExtra("setListName", setList);
            i.putExtra("isEdit", true);

            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPDFSelected(DialogFragment dialog, String fullPath) {
        //pdfFile = new File(fullPath);

        String pdfName = null;
        try {
            pdfName = PromptViewManager.loadThumbnailImage(this, fullPath, pdfPreviewImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
        if (StringUtils.isEmpty(name.getText().toString()) && StringUtils.isNotEmpty(pdfName)) {
            name.setText(pdfName.replace(".pdf", ""));
        }

        findViewById(R.id.create_submit).setEnabled(true);
    }

}
