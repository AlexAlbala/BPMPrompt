package com.a2t.autobpmprompt.app;

import android.app.Activity;
import android.content.Context;

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
    public static Prompt load(String name, PDFView pdf, Activity context) {
        PromptSettings settings = RealmIOHelper.getInstance().getPrompt(context, name);

        Prompt p = new Prompt(pdf, name, context);
        p.settings = settings;

        return p;
    }

    public static Prompt create(String name, PDFView pdf, Activity context) {
        return new Prompt(pdf, name, context);
    }

    public static boolean save(Context context, Prompt p) {
        RealmIOHelper.getInstance().insertPrompt(context, p.settings);
        try {
            saveFile(context, p.getPdf().getPdfFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: Assert values !!!!
        return true;
    }

    public static List<PromptSettings> loadAll(Context context) {
        return RealmIOHelper.getInstance().getAllPromptSettings(context);
    }

    private static void saveFile(Context ctx, File f) throws IOException {
        InputStream is = new FileInputStream(f);
        OutputStream os = ctx.openFileOutput(f.getName(), Context.MODE_PRIVATE);
        byte[] buff = new byte[1024];
        int len;
        while ((len = is.read(buff)) > 0) {
            os.write(buff, 0, len);
        }
        is.close();
        os.close();
    }
}
