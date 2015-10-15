package com.a2t.autobpmprompt.media;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

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

public class PromptManager {
    private static final String TAG = "Prompt manager";

    public static Prompt load(String name, PDFView pdf, SurfaceView floatingCanvas, Activity context, PromptEventsCallback callback) {
        Log.i(TAG, "Loading prompt " + name);
        PromptSettings settings = RealmIOHelper.getInstance().getPrompt(context, name);
        return new Prompt(pdf, floatingCanvas, settings, context, callback);
    }

    public static boolean create(Context context, PromptSettings p) {
        RealmIOHelper r = RealmIOHelper.getInstance();

        Log.i(TAG, "Create prompt " + p.toString());
        String fileName = p.getName();
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

        //TODO: Assert values !!!!
    }

    public static boolean update(Context context, Prompt p) {
        Log.i(TAG, "Update prompt" + p.toString());
        RealmIOHelper.getInstance().updatePrompt(context, p.settings);
        return true;
    }

    private static String saveFile(Context ctx, File f, String fileName) throws IOException {
        Log.i(TAG, "Save file " + f.getAbsolutePath() + " to " + fileName);
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

    public static void delete(Context ctx, Prompt prompt) {
        RealmIOHelper.getInstance().deletePrompt(ctx, prompt.settings);
    }
}
