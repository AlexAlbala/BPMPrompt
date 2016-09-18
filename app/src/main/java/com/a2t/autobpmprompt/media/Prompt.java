package com.a2t.autobpmprompt.media;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceView;

import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.a2tlib.tools.SharedPreferencesManager;
import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.callback.PromptViewCallback;
import com.a2t.a2tlib.tools.SimpleCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.MarkerType;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.TempoRecord;
import com.a2t.autobpmprompt.media.prompt.PromptViewManager;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Prompt {
    public enum Status {
        PLAYING,
        PAUSED,
        STOPPED
    }

    private Status mStatus;

    private PromptEventsCallback mCallback;

    private final String TAG = "PROMPT";

    public PromptSettings settings;

    PromptViewManager pdf;
    Timer bpmCounterTimer;

    public int currentBar;
    public int currentBeat;

    public Prompt(PDFView pdfView, SurfaceView floatingCanvas, PromptSettings _settings, Activity context, final PromptEventsCallback callback) {
        settings = _settings;

        File f = new File(settings.getPdfFullPath());
        currentBeat = 0;
        currentBar = 1;
        pdf = new PromptViewManager(f, pdfView, floatingCanvas, context, new PromptViewCallback() {
            @Override
            public void onLoad(int i) {
                if (settings.getZoom() != 0.0f && settings.getZoom() != 1.0f) {
                    Log.i(TAG, "Applying zoom: " + settings.getZoom());
                    pdf.zoomTo(settings.getZoom(), null);
                }
                if (settings.getOffsetX() != 0.0f || settings.getOffsetY() != 0.0f) {
                    pdf.moveTo(settings.getOffsetX(), settings.getOffsetY(), null);
                }

                callback.onLoaded();
            }

            @Override
            public void onDraw(Canvas canvas, float v, float v1, int i) {

            }

            @Override
            public void onPageChanged(int i, int i1) {
                callback.onPageChanged(i, i1);
            }
        });
        bpmCounterTimer = new Timer();
        this.mCallback = callback;
        mCallback.onBeat(1);
        mCallback.onBar(1);
        mCallback.onTempoChanged(getInitialTempo());
        mStatus = Status.STOPPED;
    }

    private void updatePosition() {
        Marker m = matchMarker();
        if (m != null && m.getType() == MarkerType.MARKER) {
            notifyMarker(m);
        }

        TempoRecord tr = trackTempo();
        if (tr != null) {
            tempoChanged(tr);
        }
    }

    private int getInitialTempo() {
        return getInitialTempoRecord().getBpm();
    }

    private TempoRecord getInitialTempoRecord() {
        return settings.getTempoTrack().first();
    }

    public float getCurrentXOffset() {
        return pdf.getCurrentXOffset();
    }

    public float getCurrentYOffset() {
        return pdf.getCurrentYOffset();
    }

    public float getCurrentZoom() {
        return pdf.getCurrentZoom();
    }

    public void drawClickMarker(float x, float y) {
        pdf.drawClickMarker(x, y);
    }

    public void drawMatchedMarker(Marker marker) {
        pdf.drawMatchedMarker(marker);
    }

    //public void drawMarkers() {
    //pdf.drawMarkers();
    //}

    public int getNumberOfPages() {
        return pdf.getPageCount();
    }

    /*public void editMode(boolean edit) {
        clearCanvas();
        pdf.enableDrawMarkers(!edit);
    }*/

    public void setCurrentMarkers(List<Marker> markers) {
        pdf.setCurrentMarkers(markers);
    }

    public void setCurrentPage(int page) {
        pdf.setCurrentPage(page);
    }

    public int getCurrentPage() {
        return pdf.getCurrentPage();
    }

    public void clearCanvas() {
        pdf.clear();
    }

    public void notifyMarker(final Marker m) {
        if (m.getPage() != pdf.getCurrentPage()) {
            LogUtils.d(TAG, "from page " + pdf.getCurrentPage() + " to page " + m.getPage());
            pdf.setCurrentPage(m.getPage());
            mCallback.onMarkerMatched(m);
        } else {
            pdf.centerAt(m.getOffsetX(), m.getOffsetY(), new SimpleCallback() {
                @Override
                public void done() {
                    mCallback.onMarkerMatched(m);
                }
            });
        }
    }

    private void tempoChanged(TempoRecord tr) {
        configureTimer(tr);
        mCallback.onTempoChanged(tr.getBpm());
    }

    private Marker matchMarker() {
        for (Marker m : settings.getMarkers()) {
            if (m.getBar() == currentBar && m.getBeat() == currentBeat) {
                return m;
            }
        }
        return null;
    }

    private TempoRecord trackTempo() {
        for (TempoRecord tr : settings.getTempoTrack()) {
            if (tr.getBar() == currentBar && tr.getBeat() == currentBeat) {
                return tr;
            }
        }
        return null;
    }

    public boolean isMarkerClick(float x, float y) {
        return pdf.isMarkerClick(x, y);
    }

    public Marker getClickedMarker(float x, float y) {
        return pdf.getClickedMarker(x, y);
    }

    private void beat(TempoRecord currentTempo) {
        currentBeat++;
        if (currentBeat > currentTempo.getUpper()) {
            currentBar++;
            currentBeat = 1;
            mCallback.onBar(currentBar);
        }
        updatePosition();
        mCallback.onBeat(currentBeat);
    }

    private void configureTimer(final TempoRecord tempo) {
        int tempoBpm = tempo.getBpm();
        int tempoUpper = tempo.getUpper();
        resetTimer();
        int period;
        if (tempoUpper == 2 || tempoUpper == 3 || tempoUpper == 4) { //Simple bar
            period = 60000 / tempoBpm;
        } else { //Complex bar
            period = 20000 / tempoBpm;
        }
        LogUtils.d(TAG, "Period timer: " + period + "ms " + tempoBpm + "bpm");

        bpmCounterTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                beat(tempo);
            }
        }, 0, period);
    }

    public void play() {
        clearCanvas();
        pdf.enableDrawMarkers(false);

        mCallback.onPlay();
        configureTimer(getInitialTempoRecord());
        mStatus = Status.PLAYING;
    }

    public void pause() {
        clearCanvas();
        pdf.enableDrawMarkers(true);
        //drawMarkers();
        resetTimer();
        mCallback.onPause();
        mStatus = Status.PAUSED;
    }

    public void stop() {
        resetTimer();
        clearCanvas();
        pdf.enableDrawMarkers(true);
        //drawMarkers();
        currentBeat = 0;
        currentBar = 1;

        mCallback.onStop();
        mCallback.onBeat(1);
        mCallback.onBar(1);
        mStatus = Status.STOPPED;
    }

    private void resetTimer() {
        if (bpmCounterTimer != null) {
            bpmCounterTimer.cancel();
            bpmCounterTimer = new Timer();
        }
    }

    public void close() {
        this.stop();
        pdf.close();
    }

    public Status getStatus() {
        return mStatus;
    }

    public void prepareSave() {
        this.settings.setOffsetY(pdf.getCurrentXOffset());
        this.settings.setOffsetY(pdf.getCurrentYOffset());
        this.settings.setZoom(pdf.getCurrentZoom());
    }
}
