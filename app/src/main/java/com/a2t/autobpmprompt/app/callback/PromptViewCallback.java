package com.a2t.autobpmprompt.app.callback;

import android.graphics.Canvas;

public interface PromptViewCallback {
    void onLoad(int i);
    void onDraw(Canvas canvas, float v, float v1, int i);
    void onPageChanged(int i, int i1);
}
