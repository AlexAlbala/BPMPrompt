package com.a2t.autobpmprompt.app.controller;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a2t.a2tlib.content.compat.A2TFragment;
import com.a2t.a2tlib.tools.DisplayUtils;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.PromptListAdapter;
import com.a2t.autobpmprompt.app.adapter.PromptsAdapter;
import com.a2t.autobpmprompt.app.callback.PromptCardCallbacks;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends A2TFragment implements PromptCardCallbacks {
    RecyclerView recyclerView;
    PromptListAdapter mAdapter;
    List<PromptSettings> promptSettingsesList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_allsongs, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.all_songs_recycler_view);

        promptSettingsesList = new ArrayList<>();
        mAdapter = new PromptListAdapter(promptSettingsesList, this, getActivity());
        RecyclerView.LayoutManager mLayoutManager;

        if (DisplayUtils.isLargeScreen(getActivity())) {
            // on a large screen device ...
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        reloadData();

        return rootView;
    }

    public void reloadData() {
        final List<PromptSettings> promptSettings = RealmIOHelper.getInstance().getAllPrompts(getActivity());
        promptSettingsesList.clear();
        for (PromptSettings ps : promptSettings) {
            promptSettingsesList.add(ps);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPromptSelected(PromptSettings prompt) {

    }

    @Override
    public void onRemovePromptClicked(PromptSettings prompt) {
        reloadData();
    }

    @Override
    public void onActivePromptChanged(PromptSettings prompt) {
        reloadData();
    }
}
