package com.a2t.autobpmprompt.media;

import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;

import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.media.prompt.PromptViewManager;
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

    public PromptViewManager getPdf() {
        return pdf;
    }

    PromptViewManager pdf;
    Timer bpmCounterTimer;

    public int currentBar;
    public int currentBeat;

    /*public Prompt(PDFView pdfView, String fullPath, Activity context){
        File f = new File(fullPath);
        currentBeat = 0;
        currentBar = 1;
        pdf = new PromptViewManager(f, pdfView, context);
        settings = new PromptSettings();
        settings.setPdfFullPath(fullPath);
        bpmCounterTimer = new Timer();

        pdf.CenterAt(settings.getOffset_x(), settings.getOffset_y());
    }*/

    public Prompt(PDFView pdfView, SurfaceView floatingCanvas, PromptSettings _settings, Activity context){
        settings = _settings;

        File f = new File(settings.getPdfFullPath());
        currentBeat = 0;
        currentBar = 1;
        pdf = new PromptViewManager(f, pdfView, floatingCanvas, context);
        bpmCounterTimer = new Timer();
        pdf.CenterAt(settings.getOffset_x(), settings.getOffset_y());
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
        ResetTimer();
    }

    public void Stop(){
        ResetTimer();
        currentBeat = 0;
        currentBar = 1;
    }

    private void ResetTimer(){
        bpmCounterTimer.cancel();
        bpmCounterTimer = new Timer();
    }
}
