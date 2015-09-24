package com.a2t.autobpmprompt.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.media.pdf.PDFFiles;

import java.io.File;
import java.util.List;


public class CreateActivity extends AppCompatActivity {
    private static final String TAG = "CreateActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
    }

    public void loadPDFClick(View v){
        Log.i(TAG, "Load pdf");

        List<File> pdfs = PDFFiles.findAllPDFs();
    }

    public void createPromptClick(View v){
        Log.i(TAG, "Create prompt");

        //TODO: Go to PromptActivity in Edit mode
    }
}
