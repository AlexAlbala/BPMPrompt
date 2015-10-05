package com.a2t.autobpmprompt.media.prompt;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceView;
import android.widget.Toast;

import com.a2t.autobpmprompt.app.model.Marker;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.List;


public class PromptViewManager {
    //Component View
    //https://github.com/JoanZapata/android-pdfview

    //Native:
    //https://code.google.com/p/apv/

    private final float DEFAULT_ZOOM = 1.0F;
    private String pdf;
    private PDFView pdfview;
    private Activity activity;
    private SurfaceView floatingCanvas;

    public File getPdfFile() {
        return pdfFile;
    }

    private File pdfFile;

    private PromptViewManager() {

    }

    public PromptViewManager(File pdf, PDFView pdfview, SurfaceView floatingCanvas, Activity activity) {
        this.LoadPDF(pdf, pdfview, floatingCanvas, activity);
    }

    public static boolean LoadThumbnail(File pdfFile, PDFView pdfview){
        pdfview.fromFile(pdfFile)
                .defaultPage(0)
                .pages(0)
                .showMinimap(false)
                .enableSwipe(false)
                .load();

        return true;
    }

    public boolean LoadPDF(File pdfFile, final PDFView pdfview, SurfaceView floatingCanvas, Activity mActivity) {
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
                        //System.out.println("ONLOAD");
                        pdfview.zoomTo(DEFAULT_ZOOM);
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int i, int i1) {
                        //System.out.println("ONPAGECHANGE" + i + " - " + i1);
                    }
                }).swipeVertical(true)
                .load();

        floatingCanvas.setZOrderOnTop(true);
        floatingCanvas.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.pdfview = pdfview;
        this.activity = mActivity;
        this.pdf = pdfFile.getName();
        this.pdfFile = pdfFile;
        this.floatingCanvas = floatingCanvas;
        return true;
    }

    public void PrintMarkers(List<Marker> markers){

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

    public float getCurrentXOffset(){
        return pdfview.getCurrentXOffset();
    }

    public float getCurrentYOffset(){
        return pdfview.getCurrentYOffset();
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
