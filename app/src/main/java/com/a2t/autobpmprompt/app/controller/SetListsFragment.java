package com.a2t.autobpmprompt.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2t.a2tlib.content.compat.A2TFragment;
import com.a2t.a2tlib.tools.DisplayUtils;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.PromptListAdapter;
import com.a2t.autobpmprompt.app.callback.PromptCardCallbacks;
import com.a2t.autobpmprompt.app.callback.SimpleItemTouchHelperCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;

import java.util.ArrayList;
import java.util.List;

public class SetListsFragment extends A2TFragment {
    RecyclerView recyclerView;
    PromptListAdapter mAdapter;
    List<PromptSettings> promptSettingsesList;
    FloatingActionButton fab_create;
    SetList currentSetList;
    TextView setListTitle;
    ImageView editSetList;
    ImageView deleteSetList;
    View setListContainer;

    SimpleItemTouchHelperCallback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setlists, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.setlist_song_recyclerview);
        setListTitle = (TextView) rootView.findViewById(R.id.setlist_name);
        editSetList = (ImageView) rootView.findViewById(R.id.setlist_edit);
        deleteSetList = (ImageView) rootView.findViewById(R.id.setlist_delete);

        setListContainer = rootView.findViewById(R.id.setlist_title_container);


        promptSettingsesList = new ArrayList<>();
        mAdapter = new PromptListAdapter(promptSettingsesList, new PromptCardCallbacks() {
            @Override
            public void onPromptSelected(PromptSettings prompt) {

            }

            @Override
            public void onRemovePromptClicked(PromptSettings prompt) {
                showSetList(RealmIOHelper.getInstance().getSetListByTitle(getActivity(), prompt.getSetList()));
            }

            @Override
            public void onActivePromptChanged(PromptSettings prompt) {
                showSetList(RealmIOHelper.getInstance().getSetListByTitle(getActivity(), prompt.getSetList()));
            }
        }, getActivity());
        RecyclerView.LayoutManager mLayoutManager;

        // if (DisplayUtils.isExtraLargeScreen(getActivity())) {
        // on a large screen device ...
        //     mLayoutManager = new GridLayoutManager(getActivity(), 3);
        //} else {
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        //}

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setItemViewCacheSize(0);
        fab_create = (FloatingActionButton) rootView.findViewById(R.id.fab_add_prompt);
        fab_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.recyclePdfs();
                Intent i = new Intent(getActivity(), CreateActivity.class);
                i.putExtra("setListName", currentSetList.getTitle());
                i.putExtra("setListPosition", currentSetList.getPrompts().size());
                startActivity(i);
            }
        });

        fab_create.hide();
        setListContainer.setVisibility(View.GONE);

        editSetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameSetList();
            }
        });

        deleteSetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSetList();
            }
        });

        callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }

    public void showSetList(SetList setList) {
        if (setList != null) {
            callback.setDragEnabled(true);
            setListContainer.setVisibility(View.VISIBLE);
            currentSetList = setList;
            fab_create.show();
            promptSettingsesList.clear();
            promptSettingsesList.addAll(setList.getPrompts());
            mAdapter.notifyDataSetChanged();
            setListTitle.setText(currentSetList.getTitle());
        } else {
            if (setListContainer != null && promptSettingsesList != null && mAdapter != null) {
                setListContainer.setVisibility(View.GONE);
                promptSettingsesList.clear();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void showAll() {
        callback.setDragEnabled(false);
        fab_create.hide();
        final List<PromptSettings> promptSettings = RealmIOHelper.getInstance().getAllPrompts(getActivity());
        promptSettingsesList.clear();
        for (PromptSettings ps : promptSettings) {
            promptSettingsesList.add(ps);
        }
        mAdapter.notifyDataSetChanged();
        setListContainer.setVisibility(View.GONE);
    }

    public void addSetList() {
        DialogFragment newFragment = new SetListDialog();
        newFragment.show(getFragmentManager(), "createsetlist");
    }

    public void renameSetList() {
        DialogFragment newFragment = new SetListDialog();
        Bundle b = new Bundle();
        b.putString("type", "setlist");
        b.putString("setListName", currentSetList.getTitle());
        newFragment.setArguments(b);
        newFragment.show(getFragmentManager(), "renamesetlist");
    }

    public void deleteSetList() {
        DialogFragment d = new AreYouSureDialog();
        Bundle b = new Bundle();
        b.putString("type", "setlist");
        b.putString("setListName", currentSetList.getTitle());
        d.setArguments(b);
        d.show(getFragmentManager(), "deletesetlist");
    }
}
