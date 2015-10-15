package com.a2t.autobpmprompt.helpers;

import android.content.Context;
import android.util.Log;

import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.media.Prompt;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmIOHelper {
    private static RealmIOHelper INSTANCE;
    private static final String TAG = "RealmIOHelper";

    private RealmIOHelper() {

    }

    public static RealmIOHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RealmIOHelper();
        }

        return INSTANCE;
    }

    private Realm getRealm(Context ctx) {
        return Realm.getInstance(ctx);
    }

    public void insertPrompt(Context ctx, PromptSettings settings) {
        if (settings.getSetList() != null) {
            insertPromptIntoSetList(ctx, settings, settings.getSetList());
        } else {
            Log.w(TAG, "¡¡ THIS MESSAGE SHOULD NEVER BE SHOWN !!!!!");

            throw new UnsupportedOperationException();
        }
    }

    public void insertSetList(Context ctx, SetList setList) {
        Realm r = getRealm(ctx);
        r.beginTransaction();
        SetList s = r.createObject(SetList.class);
        s.setTitle(setList.getTitle());
        s.setPrompts(setList.getPrompts());
        r.commitTransaction();
    }

    public void insertPromptIntoSetList(Context ctx, PromptSettings prompt, String setList) {
        Realm r = getRealm(ctx);
        r.beginTransaction();

        SetList updatedSetList = r.where(SetList.class).equalTo("title", setList).findFirst();
        updatedSetList.getPrompts().add(prompt);
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

    public void updatePrompt(Context ctx, PromptSettings prompt) {
        Realm r = getRealm(ctx);
        r.beginTransaction();
        PromptSettings updatedPrompt = r.where(PromptSettings.class).equalTo("name", prompt.getName()).findFirst();
        CopyPromptSettings(prompt, updatedPrompt);
        r.commitTransaction();

    }

    private void CopyMarker(Marker from, Marker to) {
        to.setId(from.getId());
        to.setOffsetX(from.getOffsetX());
        to.setOffsetY(from.getOffsetY());
        to.setTitle(from.getTitle());
        to.setNote(from.getNote());
        to.setBar(from.getBar());
        to.setBeat(from.getBeat());
        to.setPage(from.getPage());
    }

    private void CopyPromptSettings(PromptSettings from, PromptSettings to) {
        to.setMarkers(new RealmList<Marker>());
        for (Marker m : from.getMarkers()) {
            Marker newMarker = new Marker();
            CopyMarker(m, newMarker);
            to.getMarkers().add(newMarker);
        }

        //to.setMarkers(from.getMarkers());
        to.setPdfFullPath(from.getPdfFullPath());
        to.setBpm(from.getBpm());
        to.setSetList(from.getSetList());
        to.setCfgBarUpper(from.getCfgBarUpper());
        to.setCfgBarLower(from.getCfgBarLower());
        to.setName(from.getName());
        to.setOffsetX(from.getOffsetX());
        to.setOffsetY(from.getOffsetY());
        to.setZoom(from.getZoom());
    }

    public PromptSettings getPrompt(Context ctx, String name) {
        Realm r = getRealm(ctx);

        // Build the query looking at all users:
        RealmQuery<PromptSettings> query = r.where(PromptSettings.class);

        // Add query conditions:
        query.equalTo("name", name);

        // Execute the query:
        PromptSettings fromDb = query.findFirst();

        PromptSettings returned = new PromptSettings();
        CopyPromptSettings(fromDb, returned);
        return returned;
    }

    public void Debug(Context ctx) {

        RealmResults<Marker> lm = getRealm(ctx).allObjects(Marker.class);
        Log.i(TAG, "*********************************");
        Log.i(TAG, "FOUND " + lm.size() + " MARKERS");
        Log.i(TAG, "*********************************");
        for (Marker m : lm) {
            Log.i(TAG, m.toString());
        }

        RealmResults<PromptSettings> lp = getRealm(ctx).allObjects(PromptSettings.class);
        Log.i(TAG, "*********************************");
        Log.i(TAG, "FOUND " + lp.size() + " PROMPTS");
        Log.i(TAG, "*********************************");
        for (PromptSettings ps : lp) {
            Log.i(TAG, ps.toString());
        }

        RealmResults<SetList> ls = getRealm(ctx).allObjects(SetList.class);
        Log.i(TAG, "*********************************");
        Log.i(TAG, "FOUND " + ls.size() + " SETLISTS");
        Log.i(TAG, "*********************************");

        for (SetList s : getRealm(ctx).allObjects(SetList.class)) {
            Log.i(TAG, s.toString());
        }
    }

    public void deleteSetList(Context ctx, String setList) {
        //TODO: Delete prompts if there are

        Realm r = getRealm(ctx);
        SetList set = r.where(SetList.class).equalTo("title", setList).findFirst();

        // All changes to data must happen in a transaction
        r.beginTransaction();
        set.removeFromRealm();
        r.commitTransaction();

        for(PromptSettings ps : set.getPrompts()){
            deletePrompt(ctx, ps);
        }
    }

    public void renameSetList(Context ctx, String title, String newTitle) {
        Log.i(TAG, "Rename set list: " + title + " -> " + newTitle);

        Realm r = getRealm(ctx);
        SetList set = r.where(SetList.class).equalTo("title", title).findFirst();

        //Create new set list
        r.beginTransaction();
        SetList s = r.createObject(SetList.class);
        s.setTitle(newTitle);
        s.setPrompts(set.getPrompts());
        r.commitTransaction();

        // Remove old set list
        r.beginTransaction();
        set.removeFromRealm();
        r.commitTransaction();
    }

    public void deleteMarker(Context ctx, Marker m) {
        Realm r = getRealm(ctx);
        Marker marker = r.where(Marker.class).equalTo("id", m.getId()).findFirst();

        // Remove marker
        r.beginTransaction();
        marker.removeFromRealm();
        r.commitTransaction();
    }

    public void deletePrompt(Context ctx, PromptSettings prompt) {
        Realm r = getRealm(ctx);
        PromptSettings p = r.where(PromptSettings.class).equalTo("name", prompt.getName()).findFirst();

        // Remove prompt
        r.beginTransaction();
        p.removeFromRealm();
        r.commitTransaction();

        for(Marker m : p.getMarkers()){
            deleteMarker(ctx, m);
        }
    }
}