package com.a2t.autobpmprompt.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.a2tlib.tools.BuildUtils;
import com.a2t.a2tlib.tools.SharedPreferencesManager;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.MainPagerAdapter;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;
import com.a2t.autobpmprompt.media.audio.Recorder;
import com.splunk.mint.Mint;

import io.realm.RealmList;


public class MainActivity extends A2TActivity implements SetListDialog.SetListDialogListener, AreYouSureDialog.AreYouSureDialogListener, RenamePromptDialog.RenamePromptDialogListener {
    ViewPager viewPager;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean debug = BuildUtils.isDebugBuild();
        if(!debug) {
            Mint.initAndStartSession(this, "41722d1c");
        }


        RealmIOHelper.getInstance().debug(getApplicationContext());
        fab = (FloatingActionButton) findViewById(R.id.fab_add);


        //***** CONFIGURE TOOLBAR ********//
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.tab_setlists);
        tabLayout.getTabAt(1).setText(R.string.tab_all_songs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferencesManager.debug(this, false);

        Recorder r = new Recorder();
        r.findAudioRecord();
    }

    public void fabButton(View view) {
        if (viewPager.getCurrentItem() == 0) {
            ((SetListsFragment) ((MainPagerAdapter) viewPager.getAdapter()).getRegisteredFragment(0)).addSetList();
        }
    }

    public void reloadData() {
        ((MainPagerAdapter) viewPager.getAdapter()).updateData(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
