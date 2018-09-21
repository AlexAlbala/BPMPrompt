package com.a2t.autobpmprompt.app;

import android.app.Application;

import com.a2t.autobpmprompt.app.lib.BuildUtils;
import com.a2t.autobpmprompt.app.lib.SharedPreferencesManager;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;
import com.splunk.mint.Mint;

public class BPMPromptApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean debug = BuildUtils.isDebugBuild();
        RealmIOHelper.getInstance().configureDatabase(this);

        if (debug) {
            SharedPreferencesManager.debug(this, false);
            RealmIOHelper.getInstance().debug(this);
        } else {
            Mint.initAndStartSession(this, "41722d1c");
        }
    }
}
