package com.a2t.autobpmprompt.media.audio;

public class BPMManager {
    //TODO: Manage micropohone
    //TODO: Detect autoBPM
    //TODO: Test real time performance

    Recorder rec;

    public BPMManager() {
        rec = new Recorder();

        byte[] b = rec.Record(2000);
        System.out.println("YEAH " + b.length);
    }
}
