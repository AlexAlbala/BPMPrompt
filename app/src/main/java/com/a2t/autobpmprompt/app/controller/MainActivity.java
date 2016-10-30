package com.a2t.autobpmprompt.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.a2t.a2tlib.content.DrawerHelper;
import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.a2tlib.content.compat.DrawerActivityCompat;
import com.a2t.a2tlib.content.compat.NavigableFragmentActivityCompat;
import com.a2t.a2tlib.content.compat.NavigationDrawerFragmentCompat;
import com.a2t.a2tlib.tools.BuildUtils;
import com.a2t.a2tlib.tools.SharedPreferencesManager;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.DrawerAdapter;
import com.a2t.autobpmprompt.app.adapter.MainPagerAdapter;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;
import com.a2t.autobpmprompt.media.audio.Recorder;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.realm.RealmList;


public class MainActivity extends DrawerActivityCompat implements SetListDialog.SetListDialogListener, AreYouSureDialog.AreYouSureDialogListener, RenamePromptDialog.RenamePromptDialogListener {
    FloatingActionButton fab;
    SetListsFragment contentFragment;
    List<SetList> setlists;
    DrawerAdapter mAdapter;

    @Override
    public void setUpDrawer() {
        super.setUpDrawer();

        final DrawerLayout view = getDrawerView();

        findViewById(R.id.drawer_all_songs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentFragment.showAll();
                closeDrawer();
                DrawerHelper.setCurrentPosition(-1);
            }
        });

        findViewById(R.id.drawer_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                closeDrawer();
                DrawerHelper.setCurrentPosition(-1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setlists = new ArrayList<>();
        mAdapter = new DrawerAdapter(this, setlists);
        configureDrawer(R.id.drawer_fragment, R.id.drawer_layout, R.layout.drawer_layout, R.id.bpm_drawer_list, mAdapter);
        setContentView(R.layout.activity_main);
        setUpDrawer();

        fab = (FloatingActionButton) findViewById(R.id.fab_add);

        registerCallbacks(new NavigationDrawerFragmentCompat.NavigationDrawerCallbacks() {
            @Override
            public void onNavigationDrawerItemSelected(int oldPosition, int position, Object item) {
                SetList setList = (SetList) item;
                contentFragment.showSetList(setList);
            }

            @Override
            public void onNavigationDrawerOpened() {

            }
        });

        contentFragment = new SetListsFragment();
        replaceFragment(R.id.content_frame, contentFragment, false);
        // Set the list's click listener

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        SharedPreferencesManager.debug(this, false);

        Recorder r = new Recorder();
        r.findAudioRecord();

        loadSetLists();
    }

    private void loadSetLists() {
        List<SetList> tmpSetLists = RealmIOHelper.getInstance().getAllSetLists(this);
        setlists.clear();
        setlists.addAll(tmpSetLists);
        mAdapter.notifyDataSetChanged();
        contentFragment.showSetList(contentFragment.currentSetList);
    }

    public void fabButton(View view) {
        contentFragment.addSetList();
    }


    public void reloadData() {
        if (contentFragment.currentSetList != null) {
            contentFragment.currentSetList = RealmIOHelper.getInstance().getSetListByTitle(MainActivity.this, contentFragment.currentSetList.getTitle());
        }
        loadSetLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadData();
        //contentFragment.showAll();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOk(DialogFragment dialog, Bundle args) {
        String type = args.getString("type");
        if (type != null) {
            if (type.equals("setlist")) {
                RealmIOHelper.getInstance().deleteSetList(this, args.getString("setListName"));
                contentFragment.currentSetList = null;
                DrawerHelper.setCurrentPosition(-1);

            } else if (type.equals("prompt")) {
                PromptManager.delete(this, args.getLong("promptId"));
            }
        }
        reloadData();
        dialog.dismiss();
    }

    @Override
    public void onCancel(DialogFragment dialog, Bundle args) {
        dialog.dismiss();
    }

    @Override
    public void onSetListCreated(DialogFragment dialog, String title) {
        SetList s = new SetList();
        s.setTitle(title);
        s.setPrompts(new RealmList<PromptSettings>());
        RealmIOHelper.getInstance().insertSetList(this, s);
        reloadData();
    }

    @Override
    public void onSetListRenamed(DialogFragment dialog, String title, String newTitle) {
        RealmIOHelper.getInstance().renameSetList(this, title, newTitle);
        contentFragment.currentSetList = RealmIOHelper.getInstance().getSetListByTitle(this, newTitle);
        reloadData();
    }

    @Override
    public void onSetListCancelled(DialogFragment dialog) {

    }

    /*public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        linfo(v.toString());
        linfo("aaaa" + (v.getTag() == null));
        MenuTag m = (MenuTag)v.getTag();
        String type = m.type;
        int group = m.groupPosition;
        int child = m.childPosition;

        if (type.equals("setlist")) {
            //String page = mListStringArray[group][child];
            //menu.setHeaderTitle(page);
            menu.add(group, child, 0, R.string.rename_setlist_menu);
            menu.add(group, child, 1, R.string.delete_setlist_menu);

        } else if (type.equals("prompt")) {
            menu.add(group, child, 0, R.string.rename_prompt_menu);
            menu.add(group, child, 1, R.string.delete_prompt_menu);
        }
    }


    public boolean onContextItemSelected(MenuItem menuItem) {
        int groupPos = menuItem.getGroupId();
        int childPos = menuItem.getItemId();

        String type = childPos == -1 ? "setlist" : "prompt";
        if (type.equals("prompt")) {

            //PromptSettings prompt = (PromptSettings) s.getChild(groupPos, childPos);
            PromptSettings prompt = new PromptSettings();

            Bundle b = new Bundle();
            b.putString("type", "prompt");
            b.putLong("promptId", prompt.getId());
            b.putString("promptName", prompt.getName());

            if (menuItem.getItemId() == 0) {
                //Rename prompt
                DialogFragment d = new RenamePromptDialog();
                d.setArguments(b);
                d.show(getSupportFragmentManager(), "renameprompt");
            } else if (menuItem.getItemId() == 1) {
                //Delete prompt
                DialogFragment d = new AreYouSureDialog();
                d.setArguments(b);
                d.show(getSupportFragmentManager(), "deleteprompt");

            }
        } else if (type.equals("setlist")) {
            //SetList setList = (SetList) s.getGroup(groupPos);
            SetList setList = new SetList();
            Bundle b = new Bundle();
            b.putString("type", "setlist");
            b.putString("setListName", setList.getTitle());
            if (menuItem.getItemId() == 0) {
                //Rename setlist
                DialogFragment d = new SetListDialog();
                d.setArguments(b);
                d.show(getSupportFragmentManager(), "renamesetlist");
            } else if (menuItem.getItemId() == 1) {
                //Delete setlist
                DialogFragment d = new AreYouSureDialog();
                d.setArguments(b);
                d.show(getSupportFragmentManager(), "deletesetlist");
            }
        }

        return super.onContextItemSelected(menuItem);
    }*/


    @Override
    public void onPromptRename(DialogFragment dialog, long prompt_id, String name) {
        PromptManager.renamePrompt(this, prompt_id, name);
        reloadData();
    }

    @Override
    public void onPromptRenameCancelled(DialogFragment dialog) {
        //DO Nothing :)
    }
}
