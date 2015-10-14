package com.a2t.autobpmprompt.media.audiotools;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TapTempo {
    private static final String TAG = "TAPTEMPO";
    int taps = -1;
    long timeStart = -1;
    long timeStop = -1;
    Timer t;
    int THRESHOLD_MILLIS = 3000;
    int MAX_TAPS = 5;

    private void StartTapTempo() {
        Reset();
        taps = 0;
        timeStart = System.currentTimeMillis();
        t = new Timer();
    }

    public int Tap() {
        if(timeStart == -1 && taps == -1 || taps > MAX_TAPS) {
            StartTapTempo();
            return -1;
        }

        t.cancel();
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Reset();
            }
        }, THRESHOLD_MILLIS);


        taps++;
        timeStop = System.currentTimeMillis();

        long timeDiffMillis = timeStop - timeStart;
        float timeDiffMinutes = timeDiffMillis / 60000.0f;

        return Math.round(taps / timeDiffMinutes);
    }

    public void Reset() {
        Log.i(TAG, "Reset");
        taps = -1;
        timeStart = -1;
        timeStop = -1;
    }
}
