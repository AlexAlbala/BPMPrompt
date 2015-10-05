package com.a2t.autobpmprompt.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Marker extends RealmObject {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @PrimaryKey
    private int id;
    private int bar;
    private int beat;
    private String title;
    private String note;
    private float offsetX;
    private float offsetY;
    private int page;

    public Marker() {

    }
}
