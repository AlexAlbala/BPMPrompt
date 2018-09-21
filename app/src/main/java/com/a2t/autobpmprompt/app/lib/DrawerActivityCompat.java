package com.a2t.autobpmprompt.app.lib;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;


public class DrawerActivityCompat extends NavigableFragmentActivityCompat {
    NavigationDrawerFragmentCompat drawer;

    public void configureDrawer(int resContainerView, int resDrawerLayout, int resContainerLayoutToInflate, int resList, ListAdapter adapter) {
        DrawerHelper.drawerLayout = resDrawerLayout;
        DrawerHelper.containerView = resContainerView;
        DrawerHelper.containerLayout = resContainerLayoutToInflate;
        DrawerHelper.drawerListItems = resList;
        DrawerHelper.setAdapter(adapter);
    }

    public void setUpDrawer() {
        drawer = (NavigationDrawerFragmentCompat) getSupportFragmentManager().findFragmentById(DrawerHelper.containerView);
        drawer.setUp();
    }

    public void setCurrentPosition(int position){
        DrawerHelper.setCurrentPosition(position);
        drawer.setCurrentPosition(position);
    }
    public DrawerLayout getDrawerView(){
        return drawer.getDrawerView();
    }

    public void registerCallbacks(NavigationDrawerFragmentCompat.NavigationDrawerCallbacks callbacks) {
        drawer.registerCallback(callbacks);
    }

    public void setDrawerIndicatorEnabled(boolean enabled) {
        drawer.setDrawerIndicatorEnabled(enabled);
    }

    public void closeDrawer(){
        if(drawer != null) {
            drawer.closeDrawer();
        }
    }

    public void setCustomIcon(int res){
        drawer.setCustomIcon(res);
    }

    public void setItemSelected(int position){
        if(drawer != null) {
            drawer.setItemChecked(position);
        }
    }

    public int getItemSelected(){
        if(drawer != null){
            return drawer.getItemChecked();
        }
        return -1;
    }

    @Override
    public void shouldDisplayHomeUp() {
        if(drawer != null) {
            //Enable Up button only  if there are entries in the back stack
            boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                setDrawerIndicatorEnabled(!canback);
                //bar.setDisplayHomeAsUpEnabled(canback);
                //bar.setHomeButtonEnabled(true);
            }
        }
    }
}
