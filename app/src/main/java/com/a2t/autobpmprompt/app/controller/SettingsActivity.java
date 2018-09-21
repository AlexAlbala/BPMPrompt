package com.a2t.autobpmprompt.app.controller;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.lib.A2TActivity;

public class SettingsActivity extends A2TActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}