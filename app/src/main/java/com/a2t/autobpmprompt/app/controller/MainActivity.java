package com.a2t.autobpmprompt.app.controller;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
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
import com.a2t.autobpmprompt.media.PromptManager;

import java.util.List;

import io.realm.RealmList;


public class MainActivity extends Activity implements SetListDialog.SetListDialogListener, AreYouSureDialog.AreYouSureDialogListener {
    private static final String TAG = "MAIN ACTIVITY";

    boolean editMode = false;
    ListView setListsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSetLists();
        RealmIOHelper.getInstance().debug(getApplicationContext());
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

    }

    private void loadSetLists() {
        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(getApplicationContext());

        if (setLists.size() == 0) {
            SetList s = new SetList();
            s.setTitle(getString(R.string.first_set_list_title));
            RealmIOHelper.getInstance().insertSetList(getApplicationContext(), s);

            //Set the first set list also in the local setlists list
            s.setPrompts(new RealmList<PromptSettings>());
            setLists.add(s);
        }

        if (setListsView == null) {
            setListsView = (ListView) findViewById(R.id.main_setlists);
        }

        SetListAdapter setListAdapter = new SetListAdapter(getApplicationContext(), setLists, editMode, new SetListAdapterCallback() {
            @Override
            public void onPromptSelected(String setList, PromptSettings prompt, int position) {
                Intent i = new Intent(getApplicationContext(), PromptActivity.class);
                i.putExtra("setListName", setList);
                i.putExtra("promptId", prompt.getId());
                i.putExtra("isEdit", false);
                startActivity(i);
            }

            @Override
            public void onCreatePromptClicked(String setList) {
                Intent i = new Intent(getApplicationContext(), CreateActivity.class);
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
        newFragment.show(getFragmentManager(), "createsetlist");
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

    @Override
    public void onOk(DialogFragment dialog, Bundle args) {
        String type = args.getString("type");
        if (type != null) {
            if (type.equals("setlist")) {
                RealmIOHelper.getInstance().deleteSetList(getApplicationContext(), args.getString("setListName"));
            } else if (type.equals("prompt")) {
                PromptManager.delete(getApplicationContext(), args.getLong("promptId"));
            }
        }
        loadSetLists();
        dialog.dismiss();
    }

    @Override
    public void onCancel(DialogFragment dialog, Bundle args) {
        dialog.dismiss();
    }
}
