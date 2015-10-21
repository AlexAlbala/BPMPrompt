package com.a2t.autobpmprompt.media.prompt;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PromptViewCallback;
import com.a2t.autobpmprompt.app.callback.SimpleCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;


public class PromptViewManager {
    private static final String TAG = "PROMPTVIEWMANAGER";
    //Component View
    //https://github.com/JoanZapata/android-pdfview

    //Native:
    //https://code.google.com/p/apv/

    private PDFView pdfview;
    private Activity activity;
    private SurfaceView mFloatingCanvas;
    private PromptViewCallback mCallback;
    int clickMarkerColor;
    Paint clickMarkerPaint;
    int clickSquareSize;
    int clickVerticalLineSize;

    private Marker lastMarkerPrinted;

    public PromptViewManager(File pdf, PDFView pdfview, SurfaceView floatingCanvas, Activity activity, PromptViewCallback callback) {
        mCallback = callback;
        clickSquareSize = (int) activity.getResources().getDimension(R.dimen.click_square_size);
        clickVerticalLineSize = (int) activity.getResources().getDimension(R.dimen.click_verticalline_size);
        clickMarkerColor = activity.getResources().getColor(R.color.click_marker_color);
        clickMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickMarkerPaint.setColor(clickMarkerColor);
        this.loadPDF(pdf, pdfview, floatingCanvas, activity);
    }

    public static boolean loadThumbnail(File pdfFile, PDFView pdfview) {
        pdfview.fromFile(pdfFile)
                .defaultPage(0)
                .pages(0)
                .showMinimap(false)
                .enableSwipe(false)
                .load();

        return true;
    }

    public boolean loadPDF(File pdfFile, final PDFView pdfview, SurfaceView floatingCanvas, Activity mActivity) {
        pdfview.fromFile(pdfFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float v, float v1, int i) {
                        if(lastMarkerPrinted != null){
                            drawMarkerMatched(lastMarkerPrinted);
                        } else{
                            clear();
                        }
                        mCallback.onDraw(canvas, v, v1, i);
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int i) {
                        mCallback.onLoad(i);
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int i, int i1) {
                        mCallback.onPageChanged(i, i1);
                    }
                }).swipeVertical(true)
                .load();

        floatingCanvas.setZOrderOnTop(true);
        floatingCanvas.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.pdfview = pdfview;
        this.activity = mActivity;
        this.mFloatingCanvas = floatingCanvas;
        return true;
    }

    public void centerAt(final float x, final float y, SimpleCallback callback) {
        Log.i(TAG, "Center at " + x + ":" + y);
        float moveX = x - (pdfview.getWidth() / (2 * pdfview.getZoom()));
        float moveY = y - (pdfview.getHeight() / (2 * pdfview.getZoom()));

        moveTo(moveX, moveY, callback);
    }

    public void moveTo(final float x, final float y, final SimpleCallback callback) {
        if (x >= 0 || y >= 0) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float _x = -1 * x * pdfview.getZoom();
                    float _y = -1 * y * pdfview.getZoom();

                    if (_x > 0) _x = 0;
                    if (_y > 0) _y = 0;

                    Log.i(TAG, "Going to move to " + x + ":" + y + " -> " + _x + ":" + _y);
                    pdfview.moveTo(_x, _y);
                    if (callback != null)
                        callback.done();
                }
            });
        } else {
            Log.i(TAG, "Discard movement to " + x + ":" + y);
            if (callback != null)
                callback.done();
        }
    }

    public void advanceStep(final int step) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float n = -1 * step * pdfview.getZoom();
                pdfview.moveRelativeTo(0f, n);
            }
        });

    }

    public void debug() {
        Toast.makeText(activity.getApplicationContext(), "PAGE: " + pdfview.getCurrentPage() +
                "\nZOOM: " + pdfview.getZoom() +
                "\nOFFSETX: " + pdfview.getCurrentXOffset() +
                "\nOFFSETY: " + pdfview.getCurrentYOffset() +
                "\nABSX: " + pdfview.getCurrentXOffset() / pdfview.getZoom() +
                "\nABSY: " + pdfview.getCurrentYOffset() / pdfview.getZoom()
                , Toast.LENGTH_LONG).show();
    }

    public float getCurrentXOffset() {
        return pdfview.getCurrentXOffset();
    }

    public float getCurrentYOffset() {
        return pdfview.getCurrentYOffset();
    }

    public int getCurrentPage() {
        return pdfview.getCurrentPage();
    }

    public void zoomTo(final float zoom, final SimpleCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdfview.zoomTo(zoom);
                if (callback != null) {
                    callback.done();
                }
            }
        });
    }

    public void setCurrentPage(final int page) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdfview.jumpTo(page);
            }
        });
    }

    public void close() {
        pdfview.recycle();
    }

    public float getCurrentZoom() {
        return pdfview.getZoom();
    }

    public void drawClickMarker(float x, float y) {
        if (x >= 1 && y >= 1) {

            Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
            canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
            canvas.drawLine(0, y, mFloatingCanvas.getWidth(), y, clickMarkerPaint);

            canvas.drawLine(x, y - clickVerticalLineSize, x, y + clickVerticalLineSize, clickMarkerPaint);

            //Horizontal lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            //Vertical lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void clear(){
        Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
            mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
        lastMarkerPrinted = null;
    }

    public void drawMarkerMatched(Marker marker) {
        lastMarkerPrinted = marker;
        Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
        canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
        float x = marker.getOffsetX() * getCurrentZoom() + getCurrentXOffset();
        float y = marker.getOffsetY() * getCurrentZoom() + getCurrentYOffset();
        Log.i(TAG, "Draw marker " + x + ":" + y);
        if (x >= 1 && y >= 1) {

            //Horizontal lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            //Vertical lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            canvas.drawText(marker.getTitle(), x + clickSquareSize * 2, y, clickMarkerPaint);
            //canvas.drawText(marker.getNote(), x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

        }

        mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
    }
}
