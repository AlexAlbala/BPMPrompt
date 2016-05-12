package com.a2t.autobpmprompt.helpers;

import android.content.Context;
import android.util.Log;

import com.a2t.a2tlib.database.RealmDriver;
import com.a2t.a2tlib.tools.BuildUtils;
import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.autobpmprompt.BuildConfig;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.SetList;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmIOHelper {
    private static RealmIOHelper INSTANCE;
    private static final String TAG = "DBHelper";

    RealmDriver<SetList> mSetLists;
    RealmDriver<PromptSettings> mPromptSettings;
    RealmDriver<Marker> mMarker;

    private RealmIOHelper() {
        mSetLists = new RealmDriver<>(SetList.class);
        mPromptSettings = new RealmDriver<>(PromptSettings.class);
        mMarker = new RealmDriver<>(Marker.class);
    }

    public static RealmIOHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RealmIOHelper();
        }

        return INSTANCE;
    }

    public void insertPrompt(Context ctx, PromptSettings settings) {
        if (settings.getSetList() != null) {
            insertPromptIntoSetList(ctx, settings, settings.getSetList());
        } else {
            LogUtils.w(TAG, "¡¡ THIS MESSAGE SHOULD NEVER BE SHOWN !!!!!");
            throw new UnsupportedOperationException();
        }
    }

    public void insertSetList(Context ctx, SetList setList) {
        mSetLists.insertOne(ctx, setList);
    }

    public void insertPromptIntoSetList(Context ctx, PromptSettings prompt, String setList) {
        SetList s = mSetLists.getOne(ctx, "title", setList);
        SetList updatedSetList = new SetList();
        CopySetList(s, updatedSetList);
        updatedSetList.getPrompts().add(prompt);
        mSetLists.updateOne(ctx, updatedSetList);
    }

    public List<SetList> getAllSetLists(Context ctx) {
        RealmResults<SetList> results = mSetLists.getAll(ctx);
        List<SetList> list = new ArrayList<>();
        for (SetList s : results) {
            list.add(s);
        }
        return list;
    }

//    public List<PromptSettings> getAllPromptSettings(Context ctx) {
//        RealmResults<PromptSettings> results = mPromptSettings.getAll(ctx);
//        List<PromptSettings> list = new ArrayList<>();
//        for (PromptSettings ps : results) {
//            list.add(ps);
//        }
//        return list;
//    }

    public void updatePrompt(Context ctx, PromptSettings updatedPrompt) {
        mPromptSettings.updateOne(ctx, updatedPrompt);
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

    private void CopySetList(SetList from, SetList to) {
        to.setPrompts(new RealmList<PromptSettings>());

        for (PromptSettings ps : from.getPrompts()) {
            PromptSettings newPrompt = new PromptSettings();
            CopyPromptSettings(ps, newPrompt);
            to.getPrompts().add(newPrompt);
        }
        to.setTitle(from.getTitle());
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
        to.setId(from.getId());
    }

    public PromptSettings getPrompt(Context ctx, long id) {
        PromptSettings ps =  mPromptSettings.getOne(ctx, "id", id);
        PromptSettings returned = new PromptSettings();
        CopyPromptSettings(ps, returned);
        return returned;
    }

    public void debug(Context ctx) {
        if(BuildUtils.isDebugBuild()) {
            RealmResults<Marker> lm = mMarker.getAll(ctx);
            LogUtils.i(TAG, "*********************************");
            LogUtils.i(TAG, "FOUND " + lm.size() + " MARKERS");
            LogUtils.i(TAG, "*********************************");
            for (Marker m : lm) {
                LogUtils.i(TAG, m.toString());
            }

            RealmResults<PromptSettings> lp = mPromptSettings.getAll(ctx);
            LogUtils.i(TAG, "*********************************");
            LogUtils.i(TAG, "FOUND " + lp.size() + " PROMPTS");
            LogUtils.i(TAG, "*********************************");
            for (PromptSettings ps : lp) {
                LogUtils.i(TAG, ps.toString());
            }

            RealmResults<SetList> ls = mSetLists.getAll(ctx);
            LogUtils.i(TAG, "*********************************");
            LogUtils.i(TAG, "FOUND " + ls.size() + " SETLISTS");
            LogUtils.i(TAG, "*********************************");

            for (SetList s : ls) {
                LogUtils.i(TAG, s.toString());
            }
        }
    }

    public void deleteSetList(Context ctx, String setList) {
        SetList set = mSetLists.getOne(ctx, "title", setList);

        for(PromptSettings ps : set.getPrompts()){
            deletePrompt(ctx, ps.getId());
        }

        mSetLists.deleteOne(ctx, "title", setList);
    }

    public void renameSetList(Context ctx, String title, String newTitle) {
        LogUtils.i(TAG, "Rename set list: " + title + " -> " + newTitle);

        SetList set = new SetList();
        CopySetList(mSetLists.getOne(ctx, "title", title), set);
        set.setTitle(newTitle);

        mSetLists.insertOne(ctx, set);
        mSetLists.deleteOne(ctx, "title", title);
    }

    public void deleteMarker(Context ctx, Marker m) {
        mMarker.deleteOne(ctx, "id", m.getId());
    }

    public void deletePrompt(Context ctx, long promptID) {
        PromptSettings p = mPromptSettings.getOne(ctx, "id", promptID);

        for(Marker m : p.getMarkers()){
            deleteMarker(ctx, m);
        }

        mPromptSettings.deleteOne(ctx, "id", promptID);
    }
}