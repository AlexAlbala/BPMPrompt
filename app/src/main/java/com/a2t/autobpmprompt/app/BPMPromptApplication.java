package com.a2t.autobpmprompt.app;

import android.app.Application;

import com.a2t.a2tlib.tools.BuildUtils;
import com.a2t.a2tlib.tools.SharedPreferencesManager;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;
import com.splunk.mint.Mint;

/**
 * Created by Alex on 21/9/16.
 */
public class BPMPromptApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean debug = BuildUtils.isDebugBuild();
        RealmIOHelper.getInstance().configureDatabase(this);

        if (debug) {
            SharedPreferencesManager.debug(this, false);
            RealmIOHelper.getInstance().debug(this);
        }

        if (!debug) {
            Mint.initAndStartSession(this, "41722d1c");
        }
    }
}
