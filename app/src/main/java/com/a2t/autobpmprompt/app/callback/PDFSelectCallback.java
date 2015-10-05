package com.a2t.autobpmprompt.app.callback;

public interface PDFSelectCallback {
    void onPDFSelected(String fullPath, int position);
    void onCreatePDFClicked();
}
