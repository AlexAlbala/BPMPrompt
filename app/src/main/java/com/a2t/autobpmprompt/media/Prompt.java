package com.a2t.autobpmprompt.media;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.SurfaceView;

import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.callback.PromptViewCallback;
import com.a2t.autobpmprompt.app.callback.SimpleCallback;
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

    private PromptEventsCallback mCallback;

    private final String TAG = "PROMPT";

    public PromptSettings settings;

    public PromptViewManager getPdf() {
        return pdf;
    }

    PromptViewManager pdf;
    Timer bpmCounterTimer;

    public int currentBar;
    public int currentBeat;

    public Prompt(PDFView pdfView, SurfaceView floatingCanvas, PromptSettings _settings, Activity context, PromptEventsCallback callback) {
        settings = _settings;

        File f = new File(settings.getPdfFullPath());
        currentBeat = 0;
        currentBar = 1;
        pdf = new PromptViewManager(f, pdfView, floatingCanvas, context, new PromptViewCallback() {
            @Override
            public void onLoad(int i) {
                if (settings.getZoom() != 0.0f && settings.getZoom() != 1.0f) {
                    pdf.zoomTo(settings.getZoom(), null);
                }
                if (settings.getOffsetX() != 0.0f || settings.getOffsetY() != 0.0f) {
                    pdf.moveTo(settings.getOffsetX(), settings.getOffsetY(), null);
                }
            }

            @Override
            public void onDraw(Canvas canvas, float v, float v1, int i) {

            }

            @Override
            public void onPageChanged(int i, int i1) {

            }
        });

        bpmCounterTimer = new Timer();

        this.mCallback = callback;

        mCallback.onBeat(1);
        mCallback.onBar(1);
    }

    public void updatePosition() {
        final Marker m = matchMarker();

        if (m != null) {
            if (m.getPage() != pdf.getCurrentPage()) {
                pdf.setCurrentPage(m.getPage());
            }

            pdf.centerAt(m.getOffsetX(), m.getOffsetY(), new SimpleCallback() {
                @Override
                public void done() {
                    mCallback.onMarkerMatched(m);
                }
            });
        }
    }

    public Marker matchMarker() {
        for (Marker m : settings.getMarkers()) {
            if (m.getBar() == currentBar && m.getBeat() == currentBeat) {
                return m;
            }
        }
        return null;
    }

    public void play() {
        int period;
        int upper = settings.getCfgBarUpper();
        if (upper == 2 || upper == 3 || upper == 4) { //Simple bar
            period = 60000 / settings.getBpm();
        } else { //Complex bar
            period = 20000 / settings.getBpm();
        }

        mCallback.onPlay();
        bpmCounterTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentBeat++;
                if (currentBeat > settings.getCfgBarUpper()) {
                    currentBar++;
                    currentBeat = 1;
                    mCallback.onBar(currentBar);
                }
                updatePosition();
                mCallback.onBeat(currentBeat);
            }
        }, 0, period);
    }

    public void pause() {
        resetTimer();
        mCallback.onPause();
    }

    public void stop() {
        resetTimer();
        currentBeat = 0;
        currentBar = 1;

        mCallback.onStop();
        mCallback.onBeat(1);
        mCallback.onBar(1);
    }

    private void resetTimer() {
        bpmCounterTimer.cancel();
        bpmCounterTimer = new Timer();
    }

    public void close() {
        pdf.close();
    }

    public void prepareSave() {
        this.settings.setOffsetY((int) pdf.getCurrentXOffset());
        this.settings.setOffsetY((int) pdf.getCurrentYOffset());
        this.settings.setZoom(pdf.getCurrentZoom());
    }
}
