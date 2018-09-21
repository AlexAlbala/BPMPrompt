package com.a2t.autobpmprompt.app.database;

import com.a2t.autobpmprompt.app.lib.LogUtils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by Alex on 21/9/16.
 */
public class BPMPromptMigration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        LogUtils.v("REALMMIGRATION", "migrating from " + oldVersion + " to " + newVersion);
        RealmSchema schema = realm.getSchema();
        if(oldVersion == 1) {
            /*RealmObjectSchema s = schema.get("SetList");
            if(s.isPrimaryKey("title") && !s.isRequired("title")){

            }*/
            LogUtils.v("REALMMIGRATION", "migration done");
        }
    }
}
