package com.a2t.autobpmprompt.app.controller;

import android.content.res.Configuration;

import com.a2t.autobpmprompt.app.lib.NavigationDrawerFragmentCompat;
import com.a2t.autobpmprompt.app.lib.DisplayUtils;

/**
 * Created by Alex on 29/10/16.
 */

public class BPMNavigationDrawerFragment extends NavigationDrawerFragmentCompat {

    @Override
    public void setUp() {
        if (DisplayUtils.isNormalScreen(getActivity()) || DisplayUtils.isSmallScreen(getActivity())) {
            // on a large screen device ...
            super.setUp();
        }
    }
}
