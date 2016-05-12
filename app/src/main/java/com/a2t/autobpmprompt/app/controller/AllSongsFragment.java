package com.a2t.autobpmprompt.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a2t.a2tlib.content.compat.A2TFragment;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.PromptsAdapter;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;

import java.util.List;

public class AllSongsFragment extends A2TFragment {
    ListView promptsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_allsongs, container, false);

        final List<PromptSettings> promptSettings = RealmIOHelper.getInstance().getAllPrompts(getActivity());

        promptsView = (ListView) rootView.findViewById(R.id.all_songs);
        promptsView.setAdapter(new PromptsAdapter(getActivity(), promptSettings));

        promptsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PromptSettings ps = promptSettings.get(position);
                PromptManager.openPrompt(getActivity(), ps.getSetList(), ps.getId());
            }
        });

        return rootView;

    }
}
