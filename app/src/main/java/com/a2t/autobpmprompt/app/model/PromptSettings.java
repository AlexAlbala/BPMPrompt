package com.a2t.autobpmprompt.app.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class PromptSettings extends RealmObject {
    @PrimaryKey
    private String name;

    private String pdfFullPath;

    //CONFIG
    private int cfg_bar_upper;
    private int cfg_bar_lower;

    //STATE
    private int bpm;

    //DATA
    private RealmList<Marker> markers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPdfFullPath() {
        return pdfFullPath;
    }

    public void setPdfFullPath(String pdfFullPath) {
        this.pdfFullPath = pdfFullPath;
    }

    public int getCfg_bar_upper() {
        return cfg_bar_upper;
    }

    public void setCfg_bar_upper(int cfg_bar_upper) {
        this.cfg_bar_upper = cfg_bar_upper;
    }

    public int getCfg_bar_lower() {
        return cfg_bar_lower;
    }

    public void setCfg_bar_lower(int cfg_bar_lower) {
        this.cfg_bar_lower = cfg_bar_lower;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public RealmList<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(RealmList<Marker> markers) {
        this.markers = markers;
    }

    public PromptSettings() {

    }
}
