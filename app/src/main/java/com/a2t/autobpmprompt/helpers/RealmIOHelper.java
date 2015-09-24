package com.a2t.autobpmprompt.helpers;

import android.content.Context;
import android.util.Log;

import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.SetList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmIOHelper {
    private static RealmIOHelper INSTANCE;
    private static final String TAG = "RealmIOHelper";

    private RealmIOHelper(){

    }

    public static RealmIOHelper getInstance(){
        if(INSTANCE == null)
            INSTANCE = new RealmIOHelper();

        return INSTANCE;
    }

    private Realm getRealm(Context ctx) {
        return Realm.getInstance(ctx);
    }

    public void insertPrompt(Context ctx, PromptSettings settings) {
        Realm r = getRealm(ctx);
        r.beginTransaction();
        PromptSettings prompt = r.createObject(PromptSettings.class);
        prompt.setPdfName(settings.getPdfName());
        prompt.setBpm(settings.getBpm());
        prompt.setCfg_bar_lower(settings.getCfg_bar_lower());
        prompt.setCfg_bar_upper(settings.getCfg_bar_upper());
        prompt.setMarkers(settings.getMarkers());
        r.commitTransaction();
    }

    public void insertSetList(Context ctx, SetList setList) {
        Realm r = getRealm(ctx);
        r.beginTransaction();
        SetList s = r.createObject(SetList.class);
        s.setTitle(setList.getTitle());
        s.setPrompts(setList.getPrompts());
        r.commitTransaction();
    }

    public List<SetList> getAllSetLists(Context ctx) {
        List<SetList> list = new ArrayList<>();
        for (SetList s : getRealm(ctx).allObjects(SetList.class)) {
            list.add(s);
        }
        return list;
    }

    public List<PromptSettings> getAllPromptSettings(Context ctx) {
        List<PromptSettings> list = new ArrayList<>();
        for (PromptSettings ps : getRealm(ctx).allObjects(PromptSettings.class)) {
            list.add(ps);
        }
        return list;
    }

    public PromptSettings getPrompt(Context ctx, String name) {
        Realm r = getRealm(ctx);

        // Build the query looking at all users:
        RealmQuery<PromptSettings> query = r.where(PromptSettings.class);

        // Add query conditions:
        query.equalTo("pdfName", name);

        // Execute the query:
        PromptSettings p = query.findFirst();

        return p;
    }

    public void Debug(Context ctx){

        RealmResults<Marker> lm = getRealm(ctx).allObjects(Marker.class);
        Log.i(TAG, "FOUND " + lm.size() + " MARKERS");
        Log.i(TAG, "*********************************");
        for (Marker m : lm) {
            Log.i(TAG, m.toString());
        }

        RealmResults<PromptSettings> lp = getRealm(ctx).allObjects(PromptSettings.class);
        Log.i(TAG, "FOUND " + lp.size() + " PROMPTS");
        Log.i(TAG, "*********************************");
        for (PromptSettings ps : lp) {
            Log.i(TAG, ps.toString());
        }

        RealmResults<SetList> ls = getRealm(ctx).allObjects(SetList.class);
        Log.i(TAG, "FOUND " + ls.size() + " SETLISTS");
        Log.i(TAG, "*********************************");

        for (SetList s : getRealm(ctx).allObjects(SetList.class)) {
            Log.i(TAG, s.toString());
        }
    }
}