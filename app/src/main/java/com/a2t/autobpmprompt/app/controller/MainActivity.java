package com.a2t.autobpmprompt.app.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.SetListAdapter;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;

import java.util.List;

import io.realm.RealmList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN ACTIVITY";

    ListView setListsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(getApplicationContext());
        if(setLists.size() == 0){
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
        super.onResume();
        Log.i(TAG, "onResume");
        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(getApplicationContext());

        if(setListsView == null){
            setListsView = (ListView) findViewById(R.id.main_setlists);
        }

        SetListAdapter setListAdapter = new SetListAdapter(getApplicationContext(), setLists, new SetListAdapterCallback() {
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
        });
        setListsView.setAdapter(setListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
