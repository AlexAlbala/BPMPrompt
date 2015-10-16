package com.a2t.autobpmprompt.media.prompt;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PDFFiles {
    private static final String TAG = "PDFFiles";

    public static List<PromptPDFFile> findAllPDFs() {
        return findAllPDFs(Environment.getExternalStorageDirectory().toString());
    }

    private static List<PromptPDFFile> findAllPDFs(String path) {
        //TODO: Progress bar :)
        Log.i(TAG, "Searching pdf files in " + path);

        List<PromptPDFFile> files = new ArrayList<>();

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            Log.i(TAG, "Empty files in " + path);
            return files;
        }

        for (File f : list) {
            if (f.isDirectory()) {
                files.addAll(findAllPDFs(f.getAbsolutePath()));
            } else {
                if (f.getName().endsWith(".pdf") || f.getName().endsWith(".PDF")) {
                    PromptPDFFile pdf = new PromptPDFFile();
                    pdf.file = f;
                    pdf.displayName = f.getName();
                    files.add(pdf);
                    Log.i(TAG, "FOUND FILE:" + f.getAbsoluteFile());
                }
            }
        }

        return files;
    }
}
