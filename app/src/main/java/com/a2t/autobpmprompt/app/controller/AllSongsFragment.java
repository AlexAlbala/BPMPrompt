package com.a2t.autobpmprompt.app.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.a2t.a2tlib.content.compat.A2TFragment;
import com.a2t.autobpmprompt.R;

/**
 * Created by Alex on 28/4/16.
 */
public class AllSongsFragment extends A2TFragment{
    boolean editMode = false;
    ListView setListsView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setlists, container, false);

        return rootView;

    }
}
