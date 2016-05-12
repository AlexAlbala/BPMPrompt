package com.a2t.autobpmprompt.app.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.MainPagerAdapter;
import com.a2t.autobpmprompt.app.adapter.SetListAdapter;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;

import io.realm.RealmList;


public class MainActivity extends A2TActivity implements SetListDialog.SetListDialogListener, AreYouSureDialog.AreYouSureDialogListener, RenamePromptDialog.RenamePromptDialogListener {
    ViewPager viewPager;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        ldebug("onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        ldebug("onStart");
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

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            //String page = mListStringArray[group][child];
            //menu.setHeaderTitle(page);
            menu.add(group, 0, 0, R.string.rename_setlist_menu);
            menu.add(group, 1, 1, R.string.delete_setlist_menu);

        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            menu.add(group, 0, 0, R.string.rename_prompt_menu);
            menu.add(group, 1, 1, R.string.delete_prompt_menu);
        }
    }


    public boolean onContextItemSelected(MenuItem menuItem) {
        MainPagerAdapter adapter = (MainPagerAdapter) viewPager.getAdapter();
        SetListsFragment sFragment = (SetListsFragment) adapter.getRegisteredFragment(0);
        SetListAdapter s = sFragment.getSetListViewAdapter();

        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuItem.getMenuInfo();
        int groupPos, childPos;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
            PromptSettings prompt = (PromptSettings) s.getChild(groupPos, childPos);


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
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            SetList setList = (SetList) s.getGroup(groupPos);

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
    }


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
