package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.a2t.autobpmprompt.app.controller.AllSongsFragment;
import com.a2t.autobpmprompt.app.controller.SetListsFragment;

/**
 * Created by Alex on 28/4/16.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given
        // page.
        Fragment fragment = getFragmentByPosition(position);
        registeredFragments.put(position, fragment);
        return fragment;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private Fragment getFragmentByPosition(int position) {
        switch (position) {
            case 0:
                return new SetListsFragment();
            case 1:
                return new AllSongsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
