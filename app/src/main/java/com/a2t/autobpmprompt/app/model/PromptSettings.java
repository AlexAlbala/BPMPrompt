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
    private int cfgBarUpper;
    private int cfgBarLower;

    private float offsetX;
    private float offsetY;



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

    public int getCfgBarUpper() {
        return cfgBarUpper;
    }

    public void setCfgBarUpper(int cfgBarUpper) {
        this.cfgBarUpper = cfgBarUpper;
    }

    public int getCfgBarLower() {
        return cfgBarLower;
    }

    public void setCfgBarLower(int cfgBarLower) {
        this.cfgBarLower = cfgBarLower;
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

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
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
