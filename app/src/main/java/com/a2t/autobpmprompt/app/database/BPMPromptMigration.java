package com.a2t.autobpmprompt.app.database;

import com.a2t.a2tlib.tools.LogUtils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Alex on 21/9/16.
 */
public class BPMPromptMigration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        LogUtils.v("REALMMIGRATION", "migrating from " + oldVersion + " to " + newVersion);
        RealmSchema schema = realm.getSchema();

        /*if (oldVersion == 0) {
            schema.get("Record")
                    .addField("text", String.class);
            oldVersion++;
        }

        if (oldVersion == 1) {
            schema.create("ContactID")
                    .addField("lookup_contact_id", String.class)
                    .setNullable("lookup_contact_id", false)
                    .addIndex("lookup_contact_id")
                    .addPrimaryKey("lookup_contact_id");

            oldVersion++;
        }*/
        LogUtils.v("REALMMIGRATION", "migration done");
    }
}
