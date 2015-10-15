package com.a2t.autobpmprompt.app.callback;

import android.graphics.Canvas;

import com.a2t.autobpmprompt.app.model.Marker;

public interface PromptEventsCallback {
    void onBar(int bar);
    void onBeat(int beat);
    void onPlay();
    void onPause();
    void onStop();
    void onMarkerMatched(Marker match);
}
