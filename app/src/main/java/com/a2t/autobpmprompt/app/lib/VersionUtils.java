package com.a2t.autobpmprompt.app.lib;

import android.os.Build;

/**
 * Created by Alex on 15/7/16.
 */
public class VersionUtils {

    public static boolean isOverVersion(int version){
        return (Build.VERSION.SDK_INT >= version);
    }

    public static boolean isMarshmallowAndOver(){
        return isOverVersion(Build.VERSION_CODES.M);
    }
}
