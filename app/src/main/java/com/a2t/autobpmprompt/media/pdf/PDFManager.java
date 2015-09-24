package com.a2t.autobpmprompt.media.pdf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class PDFManager {
    //Component View
    //https://github.com/JoanZapata/android-pdfview

    //Native:
    //https://code.google.com/p/apv/

    private final float DEFAULT_ZOOM = 1.5F;
    private String pdf;
    private PDFView pdfview;
    private Activity activity;

    public File getPdfFile() {
        return pdfFile;
    }

    private File pdfFile;

    public PDFManager() {

    }

    public PDFManager(File pdf, PDFView pdfview, Activity activity) {
        this.LoadPDF(pdf, pdfview, activity);
    }

    public boolean LoadPDF(File pdfFile, final PDFView pdfview, Activity mActivity) {
        pdfview.fromFile(pdfFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float v, float v1, int i) {
                        //System.out.println("ONDRAW");
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int i) {
                        System.out.println("ONLOAD");
                        pdfview.zoomTo(DEFAULT_ZOOM);
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int i, int i1) {
                        System.out.println("ONPAGECHANGE" + i + " - " + i1);
                    }
                }).swipeVertical(true)
                .load();

        this.pdfview = pdfview;
        this.activity = mActivity;
        this.pdf = pdfFile.getName();
        this.pdfFile = pdfFile;
        return true;
    }

    public void CenterAt(final int x, final int y) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float _x = -1 * x * pdfview.getZoom();
                float _y = -1 * y * pdfview.getZoom();

                pdfview.moveTo(_x, _y);
            }
        });
    }

    public void AdvanceStep() {
        AdvanceStep(50);
    }

    public void AdvanceStep(final int step) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float n = -1 * step * pdfview.getZoom();
                pdfview.moveRelativeTo(0f, n);
            }
        });

    }

    public void AdvanceTo(final int pos) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float y = -1 * pos * pdfview.getZoom();
                pdfview.moveTo(0f, y);
            }
        });

    }

    public void Debug() {
        Toast.makeText(activity.getApplicationContext(), "PAGE: " + pdfview.getCurrentPage() +
                "\nZOOM: " + pdfview.getZoom() +
                "\nOFFSETX: " + pdfview.getCurrentXOffset() +
                "\nOFFSETY: " + pdfview.getCurrentYOffset() +
                "\nABSX: " + pdfview.getCurrentXOffset() / pdfview.getZoom() +
                "\nABSY: " + pdfview.getCurrentYOffset() / pdfview.getZoom()
                , Toast.LENGTH_LONG).show();
    }

    public void ZoomIn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdfview.zoomTo(1.5F);
            }
        });
    }

    public void ZoomOut() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdfview.zoomTo(1 / 1.5F);
            }
        });
    }
}
