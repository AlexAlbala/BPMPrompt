package com.a2t.autobpmprompt.app.database;

import android.content.Context;
import android.util.Log;

import com.a2t.a2tlib.database.RealmDriver;
import com.a2t.a2tlib.database.RealmFactory;
import com.a2t.a2tlib.tools.BuildUtils;
import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.app.model.TempoRecord;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmIOHelper {
    private static RealmIOHelper INSTANCE;
    private static final String TAG = "DBHelper";

    RealmDriver<SetList> mSetLists;
    RealmDriver<PromptSettings> mPromptSettings;
    RealmDriver<Marker> mMarker;
    RealmDriver<TempoRecord> mTempo;

    private RealmIOHelper() {
        mSetLists = new RealmDriver<>(SetList.class);
        mPromptSettings = new RealmDriver<>(PromptSettings.class);
        mMarker = new RealmDriver<>(Marker.class);
        mTempo = new RealmDriver<>(TempoRecord.class);
    }

    public static RealmIOHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RealmIOHelper();
        }

        return INSTANCE;
    }

    public void configureDatabase(Context ctx) {
        LogUtils.i(TAG, "INIT DB");
        RealmConfiguration rc = new RealmConfiguration.Builder(ctx)
                .schemaVersion(1)
                .setModules(new BPMPromptModule())
                .migration(new BPMPromptMigration())
                .build();
        RealmFactory.setRealmConfiguration(rc);
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

    public List<PromptSettings> getAllPrompts(Context ctx) {
        /*List<PromptSettings> allPrompts = new ArrayList<>();
        List<SetList> setLists = getAllSetLists(ctx);
        for (SetList s : setLists) {
            for (PromptSettings p : s.getPrompts()) {
                p.setSetList(s.getTitle());
                allPrompts.add(p);
            }
        }
        return allPrompts;*/

        return mPromptSettings.getAll(ctx);
    }

    public PromptSettings getPromptBySetListAndPosition(Context ctx, String setList, int position, boolean enabled) {
        PromptSettings prompt = mPromptSettings.getOneByQuery(ctx, mPromptSettings.getRealm(ctx).where(PromptSettings.class).equalTo("setList", setList).equalTo("setListPosition", position).equalTo("enabled", enabled));

        /*List<PromptSettings> allPrompts = new ArrayList<>();
        List<SetList> setLists = getAllSetLists(ctx);
        for (SetList s : setLists) {
            for (PromptSettings p : s.getPrompts()) {
                p.setSetList(s.getTitle());
                allPrompts.add(p);
            }
        }
        return allPrompts;*/

        return prompt;
    }

    public SetList getSetListByTitle(Context ctx, String title) {
        SetList s = new SetList();
        SetList orig = mSetLists.getOne(ctx, "title", title);
        CopySetList(orig, s);
        return s;
    }

    public void movePromptInSetList(Context ctx, String setList) {

    }

    public List<PromptSettings> getAllPromptsFromSetList(Context ctx, String setList) {
        SetList s = getSetListByTitle(ctx, setList);

//        List<PromptSettings> prompts = mPromptSettings.getByQuery(ctx, mPromptSettings.getRealm(ctx).where(PromptSettings.class).equalTo("setList", setList), null, false);
//        Collections.sort(prompts, new Comparator<PromptSettings>() {
//            @Override
//            public int compare(PromptSettings lhs, PromptSettings rhs) {
//                if (lhs.getSetListPosition() == rhs.getSetListPosition()) {
//                    return 0;
//                } else {
//                    if (lhs.getSetListPosition() > rhs.getSetListPosition()) {
//                        return 1;
//                    } else {
//                        return -1;
//                    }
//                }
//            }
//        });
        return s.getPrompts();
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
        /*for (Marker m : updatedPrompt.getMarkers()) {
            mMarker.updateOne(ctx, m);
        }*/
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
        to.setPrintTitle(from.getPrintTitle());
        to.setType(from.getType());
        to.setColor(from.getColor());
        to.setTextSize(from.getTextSize());
        to.setPrintInCanvasOnMatch(from.isPrintInCanvasOnMatch());
        to.setNotify(from.isNotify());
        to.setShowAlways(from.isShowAlways());
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

    public void CopyPromptSettings(PromptSettings from, PromptSettings to) {
        to.setMarkers(new RealmList<Marker>());
        to.setTempoTrack(new RealmList<TempoRecord>());

        for (Marker m : from.getMarkers()) {
            Marker newMarker = new Marker();
            CopyMarker(m, newMarker);
            to.getMarkers().add(newMarker);
        }

        for (TempoRecord tr : from.getTempoTrack()) {
            TempoRecord newTr = new TempoRecord();
            CopyTempoRecord(tr, newTr);
            to.getTempoTrack().add(newTr);
        }

        //to.setMarkers(from.getMarkers());
        to.setPdfFullPath(from.getPdfFullPath());
        to.setSetList(from.getSetList());
        to.setName(from.getName());
        to.setOffsetX(from.getOffsetX());
        to.setOffsetY(from.getOffsetY());
        to.setZoom(from.getZoom());
        to.setId(from.getId());
        to.setSetListPosition(from.getSetListPosition());
        to.setEnabled(from.isEnabled());
    }

    private void CopyTempoRecord(TempoRecord from, TempoRecord to) {
        to.setId(from.getId());
        to.setBar(from.getBar());
        to.setBeat(from.getBeat());
        to.setBpm(from.getBpm());
        to.setUpper(from.getUpper());
        to.setLower(from.getLower());
    }


    public PromptSettings getPrompt(Context ctx, long id) {
        PromptSettings ps = mPromptSettings.getOne(ctx, "id", id);
        PromptSettings returned = new PromptSettings();
        CopyPromptSettings(ps, returned);
        return returned;
    }

    public void debug(Context ctx) {
        if (BuildUtils.isDebugBuild()) {
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

            RealmResults<TempoRecord> ltr = mTempo.getAll(ctx);
            LogUtils.i(TAG, "*********************************");
            LogUtils.i(TAG, "FOUND " + ltr.size() + " TEMPORECORDS");
            LogUtils.i(TAG, "*********************************");
            for (TempoRecord tr : ltr) {
                LogUtils.i(TAG, tr.toString());
            }
        }
    }

    public void deleteSetList(Context ctx, String setList) {
        SetList set = mSetLists.getOne(ctx, "title", setList);

        for (PromptSettings ps : set.getPrompts()) {
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

        for (Marker m : p.getMarkers()) {
            deleteMarker(ctx, m);
        }

        mPromptSettings.deleteOne(ctx, "id", promptID);
    }
}