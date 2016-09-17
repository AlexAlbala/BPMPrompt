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


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrintTitle() {
        return printTitle;
    }

    public void setPrintTitle(int printTitle) {
        this.printTitle = printTitle;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isShowAlways() {
        return showAlways;
    }

    public void setShowAlways(boolean showAlways) {
        this.showAlways = showAlways;
    }

    public boolean isPrintInCanvasOnMatch() {
        return printInCanvasOnMatch;
    }

    public void setPrintInCanvasOnMatch(boolean printInCanvasOnMatch) {
        this.printInCanvasOnMatch = printInCanvasOnMatch;
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
    private int type; //0 - full marker, 1 - text
    private int printTitle; //0 - no, 1 - above, 2 - below
    private int color;
    private int textSize;

    private boolean notify;
    private boolean showAlways;

    private boolean printInCanvasOnMatch;

    public Marker() {

    }
}
