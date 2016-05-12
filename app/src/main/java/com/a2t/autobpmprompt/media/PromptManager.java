package com.a2t.autobpmprompt.media;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;
import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.realm.Realm;
import io.realm.RealmList;

public class PromptManager {
    private static final String TAG = "Prompt manager";

    public static Prompt load(long id, PDFView pdf, SurfaceView floatingCanvas, Activity context, PromptEventsCallback callback) {
        LogUtils.i(TAG, "Loading prompt " + id);
        PromptSettings settings = RealmIOHelper.getInstance().getPrompt(context, id);
        return new Prompt(pdf, floatingCanvas, settings, context, callback);
    }

    public static boolean create(Context context, PromptSettings p) {
        RealmIOHelper r = RealmIOHelper.getInstance();
        p.setId(System.currentTimeMillis());
        p.setMarkers(new RealmList<Marker>());
        String fileName = p.getName() + p.getId();
        File f = new File(p.getPdfFullPath());
        String saved;
        try {
            saved = saveFile(context, f, fileName);
            p.setPdfFullPath(saved);
            r.insertPrompt(context, p);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(Context context, Prompt p) {
        LogUtils.i(TAG, "Update prompt" + p.toString());
        RealmIOHelper.getInstance().updatePrompt(context, p.settings);
        return true;
    }

    private static String saveFile(Context ctx, File f, String fileName) throws IOException {
        LogUtils.i(TAG, "Save file " + f.getAbsolutePath() + " to " + fileName);
        InputStream is = new FileInputStream(f);
        OutputStream os = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] buff = new byte[1024];
        int len;
        while ((len = is.read(buff)) > 0) {
            os.write(buff, 0, len);
        }
        is.close();
        os.close();

        return ctx.getFilesDir() + "/" + fileName;
    }

    public static void deleteMarkerFromPrompt(Context ctx, Prompt prompt, Marker m) {
        update(ctx, prompt);
        RealmIOHelper.getInstance().deleteMarker(ctx, m);
    }

    public static void renamePrompt(Context ctx, Prompt prompt, String newName){
        prompt.settings.setName(newName);
        update(ctx, prompt);
    }

    public static void renamePrompt(Context ctx, long prompt_id, String newName){
        PromptSettings prompt = RealmIOHelper.getInstance().getPrompt(ctx, prompt_id);
        prompt.setName(newName);
        RealmIOHelper.getInstance().updatePrompt(ctx, prompt);
    }

    public static void delete(Context ctx, long promptId) {
        //Remove saved file
        PromptSettings ps = RealmIOHelper.getInstance().getPrompt(ctx, promptId);
        File f = new File(ps.getPdfFullPath());
        f.delete();
        RealmIOHelper.getInstance().deletePrompt(ctx, promptId);
    }
}
