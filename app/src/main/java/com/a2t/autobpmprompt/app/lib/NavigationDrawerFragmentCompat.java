package com.a2t.autobpmprompt.app.lib;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.a2t.autobpmprompt.app.lib.DrawerHelper;

public class NavigationDrawerFragmentCompat extends Fragment {

    private static final String TAG = "NAVDRAWERFRAGMENT";
    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    public NavigationDrawerFragmentCompat() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Select either the default item (0) or the last selected item.
        selectItem(DrawerHelper.getCurrentPosition());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(
                DrawerHelper.containerLayout, container);
        mDrawerListView = (ListView) v.findViewById(DrawerHelper.drawerListItems);

        mDrawerListView.setAdapter(DrawerHelper.getAdapter());
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setItemChecked(DrawerHelper.getCurrentPosition(), true);
        DrawerHelper.setCurrentPosition(-1);
        return v;
    }

    public ActionBar mGetActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void registerCallback(NavigationDrawerCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void setUp() {
        mFragmentContainerView = getActivity().findViewById(DrawerHelper.containerView);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(DrawerHelper.drawerLayout);

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = mGetActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                /*R.drawable.ic_drawer,  */           /* nav drawer image to replace 'Up' caret */
                android.R.string.ok,  /* "open drawer" description for accessibility */
                android.R.string.cancel  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (mCallbacks != null) {
                    mCallbacks.onNavigationDrawerOpened();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerListView.setItemChecked(DrawerHelper.getCurrentPosition(), true);
    }

    public void setCurrentPosition(int position){
        if(position == -1){
            mDrawerListView.clearChoices();
        } else {
            mDrawerListView.setSelection(position);
        }

        mDrawerListView.requestLayout();
    }

    public DrawerLayout getDrawerView() {
        return mDrawerLayout;
    }

    public void setDrawerIndicatorEnabled(boolean enabled) {
        mDrawerToggle.setDrawerIndicatorEnabled(enabled);
    }

    public void setCustomIcon(int res) {
        mDrawerToggle.setHomeAsUpIndicator(res);
    }

    private void selectItem(int position) {
        Log.i(TAG, "selected " + position);

        if (position != DrawerHelper.getCurrentPosition()) {
            if (mDrawerListView != null) {
                mDrawerListView.setItemChecked(position, true);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
                int oldPosition = DrawerHelper.getCurrentPosition();
                DrawerHelper.setCurrentPosition(position);
                if (mCallbacks != null) {
                    mCallbacks.onNavigationDrawerItemSelected(oldPosition, position, mDrawerListView.getAdapter().getItem(position));
                }
            }
        }


    }

    public void closeDrawer() {
        if (mDrawerLayout != null && mFragmentContainerView != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public void setItemChecked(int position) {
        if (position == -1) {
            mDrawerListView.clearChoices();
            mDrawerListView.requestLayout();
        }
        DrawerHelper.setCurrentPosition(position);
        mDrawerListView.setItemChecked(position, true);
    }

    public int getItemChecked() {
        return DrawerHelper.getCurrentPosition();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        //if (mDrawerLayout != null && isDrawerOpen()) {
        //    inflater.inflate(R.menu.contacts, menu);
        //    showGlobalContextActionBar();
        //}
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    /*private void showGlobalContextActionBar() {
        ActionBar actionBar = mGetActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }*/

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int oldPosition, int position, Object item);

        void onNavigationDrawerOpened();
    }
}
