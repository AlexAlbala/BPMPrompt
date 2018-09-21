package com.a2t.autobpmprompt.app.helpers;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDropped(int fromPosition, int toPosition);
}