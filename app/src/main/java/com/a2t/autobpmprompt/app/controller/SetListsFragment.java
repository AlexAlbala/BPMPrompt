package com.a2t.autobpmprompt.app.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.a2t.a2tlib.content.compat.A2TFragment;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.SetListAdapter;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;

import java.util.List;

import io.realm.RealmList;

public class SetListsFragment extends A2TFragment {
    boolean editMode = false;
    ExpandableListView setListsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setlists, container, false);
        if (setListsView == null) {
            setListsView = (ExpandableListView) rootView.findViewById(R.id.main_setlists);
        }
        loadSetLists(getActivity());
        getActivity().registerForContextMenu(setListsView);
        return rootView;
    }

    public void loadSetLists(final Context ctx) {
        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(ctx);

        if (setLists.size() == 0) {
            SetList s = new SetList();
            s.setTitle(getString(R.string.first_set_list_title));
            RealmIOHelper.getInstance().insertSetList(ctx, s);

            //Set the first set list also in the local setlists list
            s.setPrompts(new RealmList<PromptSettings>());
            setLists.add(s);
        }

        SetListAdapter setListAdapter = new SetListAdapter(ctx, setLists, editMode, new SetListAdapterCallback() {
            @Override
            public void onPromptSelected(String setList, PromptSettings prompt, int position) {
                PromptManager.openPrompt(getActivity(), setList, prompt.getId());
            }

            @Override
            public void onCreatePromptClicked(String setList) {
                Intent i = new Intent(ctx, CreateActivity.class);
                i.putExtra("setListName", setList);
                startActivity(i);
            }

            @Override
            public void onSetListRenamedClicked(String setList) {
                DialogFragment newFragment = new SetListDialog();
                Bundle args = new Bundle();
                args.putString("setListName", setList);
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "renamesetlist");
            }

            @Override
            public void onRemoveSetListClicked(String setList) {
                DialogFragment newFragment = new AreYouSureDialog();
                Bundle b = new Bundle();
                b.putString("type", "setlist");
                b.putString("setListName", setList);
                newFragment.setArguments(b);
                newFragment.show(getFragmentManager(), "areyousuresetlist");
            }

            @Override
            public void onRemovePromptClicked(String setList, PromptSettings prompt, int position) {
                DialogFragment newFragment = new AreYouSureDialog();
                Bundle b = new Bundle();
                b.putString("type", "prompt");
                b.putLong("promptId", prompt.getId());
                newFragment.setArguments(b);
                newFragment.show(getFragmentManager(), "areyousureprompt");
            }
        });

        setListsView.setAdapter(setListAdapter);
        setListAdapter.notifyDataSetChanged();
    }

    public void addSetList() {
        DialogFragment newFragment = new SetListDialog();
        newFragment.show(getFragmentManager(), "createsetlist");
    }

    public SetListAdapter getSetListViewAdapter() {
        return (SetListAdapter) setListsView.getExpandableListAdapter();
    }
}
