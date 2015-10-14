package com.a2t.autobpmprompt.app.controller;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.SetListAdapter;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;


public class MainActivity extends AppCompatActivity implements SetListDialog.SetListDialogListener {
    private static final String TAG = "MAIN ACTIVITY";

    boolean editMode = false;
    ListView setListsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(getApplicationContext());
        if (setLists.size() == 0) {
            SetList s = new SetList();
            s.setTitle(getString(R.string.first_set_list_title));
            RealmIOHelper.getInstance().insertSetList(getApplicationContext(), s);

            //Set the first set list also in the local setlists list
            s.setPrompts(new RealmList<PromptSettings>());
            setLists.add(s);
        }

        setListsView = (ListView) findViewById(R.id.main_setlists);
        RealmIOHelper.getInstance().Debug(getApplicationContext());
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

    }

    private void loadSetLists() {
        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(getApplicationContext());

        if (setListsView == null) {
            setListsView = (ListView) findViewById(R.id.main_setlists);
        }

        SetListAdapter setListAdapter = new SetListAdapter(getApplicationContext(), setLists, editMode, new SetListAdapterCallback() {
            @Override
            public void onPromptSelected(String setList, PromptSettings prompt, int position) {
                //Toast.makeText(getApplicationContext(), "SELECTED PROMPT " + prompt.toString(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), PromptActivity.class);
                i.putExtra(getString(R.string.promptNameVariable), prompt.getName());
                i.putExtra(getString(R.string.setListNameVariable), setList);
                i.putExtra(getString(R.string.isEditVariable), false);
                startActivity(i);
            }

            @Override
            public void onCreatePromptClicked(String setList) {
                Intent i = new Intent(getApplicationContext(), CreateActivity.class);
                i.putExtra(getString(R.string.setListNameVariable), setList);
                startActivity(i);
            }

            @Override
            public void onSetListRenamedClicked(String setList) {
                DialogFragment newFragment = new SetListDialog();
                Bundle args = new Bundle();
                args.putString(getString(R.string.setListNameVariable), setList);
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "renamesetlist");
            }

            @Override
            public void onSetListRemovedClicked(String setList) {
                //TODO: Are you sure ? check :)
                RealmIOHelper.getInstance().deleteSetList(getApplicationContext(), setList);
                loadSetLists();
            }
        });
        setListsView.setAdapter(setListAdapter);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        loadSetLists();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void createSetList(View view) {
        DialogFragment newFragment = new SetListDialog();
        newFragment.show(getSupportFragmentManager(), "createsetlist");
    }

    public void editMode(View view) {
        //TODO: Change icon :)
        editMode = !editMode;
        loadSetLists();
    }

    @Override
    public void onSetListCreated(DialogFragment dialog, String title) {
        SetList s = new SetList();
        s.setTitle(title);
        s.setPrompts(new RealmList<PromptSettings>());
        RealmIOHelper.getInstance().insertSetList(getApplicationContext(), s);
        loadSetLists();
    }

    @Override
    public void onSetListRenamed(DialogFragment dialog, String title, String newTitle) {
        RealmIOHelper.getInstance().renameSetList(getApplicationContext(), title, newTitle);
        loadSetLists();
    }

    @Override
    public void onSetListCancelled(DialogFragment dialog) {

    }
}
