package com.a2t.autobpmprompt.app.database;

import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.app.model.TempoRecord;

import io.realm.annotations.RealmModule;

@RealmModule(classes = {Marker.class, SetList.class, PromptSettings.class, TempoRecord.class})
public class BPMPromptModule {
    @Override
    public boolean equals(Object o) {
        return true;
    }
}
