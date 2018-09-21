package com.a2t.autobpmprompt.app.lib;

import android.widget.ListAdapter;

public class DrawerHelper {
    public static int currentPosition;
    public static int drawerListItems;
    public static int drawerLayout;
    public static int containerView;
    public static int containerLayout;
    private static ListAdapter mAdapter;

    public static int getCurrentPosition() {
        return currentPosition;
    }

    public static void setCurrentPosition(int position){
        currentPosition = position;
    }

    public static void setAdapter(ListAdapter adapter){
        mAdapter = adapter;
    }

    public static ListAdapter getAdapter(){
        return mAdapter;
    }


}
