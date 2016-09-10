package com.a2t.autobpmprompt.app.controller;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.a2t.autobpmprompt.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}