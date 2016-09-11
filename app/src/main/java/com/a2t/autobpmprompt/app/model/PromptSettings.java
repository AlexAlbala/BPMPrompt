package com.a2t.autobpmprompt.app.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;


public class PromptSettings extends RealmObject {
    @PrimaryKey
    private long id;

    private String name;

    private String pdfFullPath;

    private float offsetX;
    private float offsetY;
    private float zoom;


    private String setList;
    private int setListPosition;

    private RealmList<TempoRecord> tempoTrack;


    public RealmList<TempoRecord> getTempoTrack() {
        return tempoTrack;
    }

    public void setTempoTrack(RealmList<TempoRecord> tempoTrack) {
        this.tempoTrack = tempoTrack;
    }

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public PromptSettings() {

    }

    public int getSetListPosition() {
        return setListPosition;
    }

    public void setSetListPosition(int setListPosition) {
        this.setListPosition = setListPosition;
    }
}
