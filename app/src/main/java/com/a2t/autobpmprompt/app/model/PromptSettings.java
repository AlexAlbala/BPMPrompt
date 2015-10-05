package com.a2t.autobpmprompt.app.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;


public class PromptSettings extends RealmObject {
    @PrimaryKey
    private String name;

    private String pdfFullPath;

    //CONFIG
    private int cfg_bar_upper;
    private int cfg_bar_lower;

    private int offset_x;
    private int offset_y;



    private float zoom;

    //STATE
    private int bpm;


    //ONLY FOR INNER TRACKING... I'M NOT SURE OF THAT....
    @Ignore
    private String setList;

    public String getSetList() {
        return setList;
    }

    public void setSetList(String setList) {
        this.setList = setList;
    }

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

    public int getOffset_y() {
        return offset_y;
    }

    public void setOffset_y(int offset_y) {
        this.offset_y = offset_y;
    }

    public int getOffset_x() {
        return offset_x;
    }

    public void setOffset_x(int offset_x) {
        this.offset_x = offset_x;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public PromptSettings() {

    }
}
