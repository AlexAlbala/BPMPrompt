package com.a2t.autobpmprompt.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TempoRecord extends RealmObject {
    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getBeat() {
        return beat;
    }

    public void setBeat(int beat) {
        this.beat = beat;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }


    public int getUpper() {
        return upper;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }

    public int getLower() {
        return lower;
    }

    public void setLower(int lower) {
        this.lower = lower;
    }


    private int bar;
    private int beat;
    private int bpm;
    private int upper;
    private int lower;

    public TempoRecord() {

    }
}
