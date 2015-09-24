package com.a2t.autobpmprompt.app;

import android.app.Activity;
import android.util.Log;

import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.media.pdf.PDFManager;
import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Prompt {
    //Manage the audio
    //Manage the pdf
    //Manage the markers
    //Manage the tempo

    private final String TAG = "PROMPT";

    public PromptSettings settings;

    public PDFManager getPdf() {
        return pdf;
    }

    PDFManager pdf;
    Timer bpmCounterTimer;

    public int currentBar;
    public int currentBeat;

    public Prompt(PDFView pdfView, String fullPath, Activity context){
        File f = new File(fullPath);
        currentBeat = 0;
        currentBar = 1;
        pdf = new PDFManager(f, pdfView, context);
        settings = new PromptSettings();
        settings.setPdfName(f.getName());
        bpmCounterTimer = new Timer();
    }

    public void onBeat(){
        Marker m = MatchMarker();

        if(m != null){
            Log.i(TAG, "Marker " + m.getTitle());
            pdf.CenterAt(m.getOffsetX(), m.getOffsetY());
        }

        Log.i(TAG, "Bar " + currentBar + " Beat " + currentBeat);
    }

    public void onBar(){
        Log.i(TAG, "NEW BAR");
    }

    public Marker MatchMarker(){
        for(Marker m : settings.getMarkers()){
            if(m.getBar() == currentBar && m.getBeat() == currentBeat){
                return m;
            }
        }

        return null;
    }

    public void Play(){
        bpmCounterTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentBeat++;
                if(currentBeat > settings.getCfg_bar_upper()){
                    currentBar++;
                    currentBeat = 1;
                    onBar();
                }
                onBeat();
            }
        }, 0, 60000 / settings.getBpm());


    }

    public void Pause(){

    }

    public void Stop(){
        bpmCounterTimer.cancel();
        currentBeat = 0;
        currentBar = 1;

    }
}
