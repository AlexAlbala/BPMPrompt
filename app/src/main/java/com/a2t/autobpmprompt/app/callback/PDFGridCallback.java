package com.a2t.autobpmprompt.app.callback;

public interface PDFGridCallback {
    void onPDFSelected(String fullPath, int position);
    void onCreatePDFClicked();
    void onPDFRemoveClicked(String fullPath, int id);
}
