package com.a2t.autobpmprompt.media;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.helpers.RealmIOHelper;
import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PromptManager {
    private static final String TAG = "Prompt manager";

    public static Prompt load(String name, PDFView pdf, SurfaceView floatingCanvas, Activity context) {
        Log.i(TAG, "Loading prompt " + name);
        PromptSettings settings = RealmIOHelper.getInstance().getPrompt(context, name);

        //Prompt p = new Prompt(pdf, settings.getPdfFullPath(), context);
        //p.settings = settings;

        return new Prompt(pdf, floatingCanvas, settings, context);
    }

    /*public static Prompt create(String name, PDFView pdf, Activity context) {
        return new Prompt(pdf, name, context);
    }*/

    public static boolean create(Context context, PromptSettings p) {
        RealmIOHelper r = RealmIOHelper.getInstance();
        //if (r.getPrompt(context, p.getName()) != null) {
        //    Log.i(TAG, "Cannot create prompt " + p.getName() + ". Already exists");
        //    return false;
        //} else {

            Log.i(TAG, "Create prompt " + p.toString());
            String fileName = p.getName();
            File f = new File(p.getPdfFullPath());
            String saved = null;
            try {
                saved = saveFile(context, f, fileName);
                p.setPdfFullPath(saved);
                r.insertPrompt(context, p);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        //}

        //TODO: Assert values !!!!
    }

    //TODO: Save and update
    public static boolean update(Context context, Prompt p) {
        Log.i(TAG, "Update prompt" + p.toString());
        RealmIOHelper.getInstance().insertPrompt(context, p.settings);
        try {
            saveFile(context, p.getPdf().getPdfFile(), p.getPdf().getPdfFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: Assert values !!!!
        return true;
    }

    public static List<PromptSettings> loadAll(Context context) {
        return RealmIOHelper.getInstance().getAllPromptSettings(context);
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
}
