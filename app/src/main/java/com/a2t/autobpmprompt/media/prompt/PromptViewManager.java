package com.a2t.autobpmprompt.media.prompt;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.a2tlib.tools.SharedPreferencesManager;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PromptViewCallback;
import com.a2t.a2tlib.tools.SimpleCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.MarkerType;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.List;
import java.util.Timer;


public class PromptViewManager {
    private static final String TAG = "PROMPTVIEWMANAGER";
    //Component View
    //https://github.com/JoanZapata/android-pdfview

    //Native:
    //https://code.google.com/p/apv/

    private List<Marker> currentMarkers;

    private PDFView pdfview;
    private Activity activity;
    private SurfaceView mFloatingCanvas;
    private PromptViewCallback mCallback;
    private int clickMarkerColor;
    private Paint clickMarkerPaint;
    private int clickSquareSize;

    private Timer markerRefreshTimer;
    private final int REFRESH_THRESHOLD_MS = 30;
    private final int MARKER_FRAMES_SKIP = 0;

    private Marker lastMarkerPainted;
    private float lastXClick;
    private float lastYClick;
    private int marker_frame_count = 0;
    private boolean drawMarkers = true;
    private boolean hideMarkers = false;

    //private final int MOVEMENT_PX = 1;

    public PromptViewManager(File pdf, final PDFView pdfview, SurfaceView floatingCanvas, Activity activity, PromptViewCallback callback) {
        mCallback = callback;
        clickSquareSize = (int) activity.getResources().getDimension(R.dimen.click_square_size);
        clickMarkerColor = activity.getResources().getColor(R.color.click_marker_color);
        clickMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickMarkerPaint.setColor(clickMarkerColor);
        clickMarkerPaint.setStrokeWidth(activity.getResources().getDimension(R.dimen.click_stroke_size));
        markerRefreshTimer = new Timer();
        this.loadPDF(pdf, pdfview, floatingCanvas, activity);
    }

    public boolean isMarkerClick(float x, float y) {
        return getClickedMarker(x, y) != null;
    }

    public Marker getClickedMarker(float x, float y) {
        for (Marker m : currentMarkers) {
            float pos_x = m.getOffsetX() * getCurrentZoom() + getCurrentXOffset();
            float pos_y = m.getOffsetY() * getCurrentZoom() + getCurrentYOffset();

            if (x < pos_x + clickSquareSize && x > pos_x - clickSquareSize && y > pos_y - clickSquareSize && y < pos_y + clickSquareSize) {
                return m;
            }
        }
        return null;
    }

    public static boolean loadThumbnail(File pdfFile, PDFView pdfview) {
        pdfview.fromFile(pdfFile)
                .defaultPage(0)
                .pages(0)
                .enableSwipe(false)
                .load();

        pdfview.useBestQuality(false);

        return true;
    }

    public boolean loadPDF(File pdfFile, final PDFView pdfview, SurfaceView floatingCanvas, Activity mActivity) {
        boolean vertical = SharedPreferencesManager.getString(mActivity, "pref_orientation").equals(mActivity.getResources().getString(R.string.prf_sheet_orientation_vertical));
        pdfview.fromFile(pdfFile)
                .enableSwipe(true)
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float v, float v1, int i) {
                        if (!hideMarkers && ((marker_frame_count++) == MARKER_FRAMES_SKIP) && currentMarkers != null) {
                            drawMarkers();
                            marker_frame_count = 0;
                        }

                        /*markerRefreshTimer.cancel();
                        markerRefreshTimer = new Timer();
                        markerRefreshTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(currentMarkers != null) {
                                    try {
                                    } catch(Exception e){
                                        LogUtils.v(TAG, "Marker exception " + e.getMessage());
                                    }
                                }
                            }
                        }, REFRESH_THRESHOLD_MS);*/

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
                    public void onPageChanged(int page, int pageCount) {
                        mCallback.onPageChanged(page, pageCount);
                    }
                }).swipeHorizontal(!vertical)
                .load();

        pdfview.useBestQuality(true);
        floatingCanvas.setZOrderOnTop(true);
        floatingCanvas.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.pdfview = pdfview;
        this.activity = mActivity;
        this.mFloatingCanvas = floatingCanvas;
        return true;
    }

    public int getPageCount() {
        return pdfview.getPageCount();
    }

    public void enableDrawMarkers(boolean enable) {
        drawMarkers = enable;
        drawMarkers();
    }

    public void hideMarkers(boolean hide) {
        hideMarkers = hide;
        clear();
        if (!hide) {
            drawMarkers();
        }
    }

    public void centerAt(final float x, final float y, SimpleCallback callback) {
        LogUtils.i(TAG, "Center at " + x + ":" + y);
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

                    LogUtils.d(TAG, "Going to move to " + x + ":" + y + " -> " + _x + ":" + _y);

                    /*float tx, ty;
                    for (tx = pdfview.getCurrentXOffset(), ty = pdfview.getCurrentYOffset(); tx > _x || ty > _y; tx -= MOVEMENT_PX, ty -= MOVEMENT_PX) {
                        if (tx < _x) tx = _x;
                        if (ty < _y) ty = _y;
                        Log.i(TAG, "tx " + tx + " ty " + ty);

                        pdfview.moveTo(tx, ty);
                    }*/

                    pdfview.moveTo(_x, _y);

                    if (callback != null) {
                        callback.done();
                    }
                }
            });
        } else {
            Log.i(TAG, "Discard movement to " + x + ":" + y);
            if (callback != null)
                callback.done();
        }
    }

    public void debug() {
        Log.i(TAG, "PAGE: " + pdfview.getCurrentPage() +
                "\nZOOM: " + pdfview.getZoom() +
                "\nOFFSETX: " + pdfview.getCurrentXOffset() +
                "\nOFFSETY: " + pdfview.getCurrentYOffset() +
                "\nABSX: " + pdfview.getCurrentXOffset() / pdfview.getZoom() +
                "\nABSY: " + pdfview.getCurrentYOffset() / pdfview.getZoom());
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
        lastXClick = x;
        lastYClick = y;
        if (x >= 1 && y >= 1) {

            Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
            drawClickOnCanvas(canvas, -1, x, y);
            mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawClickOnCanvas(Canvas canvas, int color, float x, float y) {
        //canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
//        canvas.drawLine(0, y, mFloatingCanvas.getWidth(), y, clickMarkerPaint);

        //HORIZONTAL SEPARATOR
        //canvas.drawLine(x, y - clickVerticalLineSize, x, y + clickVerticalLineSize, clickMarkerPaint);

        //SQUARE
        //Horizontal lines
        //canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);
        //canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);

        Drawable d;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            d = activity.getResources().getDrawable(R.drawable.bubble, activity.getTheme());
            if (d != null && color > -1) {
                d.setTint(color);
                d.setColorFilter(color, PorterDuff.Mode.ADD);
            }
        } else {
            d = activity.getResources().getDrawable(R.drawable.bubble);
        }


        if (d != null) {
            d.setBounds((int) (x - clickSquareSize * getCurrentZoom()), (int) (y - clickSquareSize * getCurrentZoom()), (int) (x + clickSquareSize * getCurrentZoom()), (int) (y + clickSquareSize * getCurrentZoom()));
            d.draw(canvas);
        }

        //Vertical lines
        //canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
//        canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);
    }

    public void clear() {
        Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
            mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
        lastMarkerPainted = null;
    }

    public synchronized void drawMarkers() {
        Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
            for (Marker m : currentMarkers) {
                if (m.isShowAlways() || drawMarkers || m.equals(lastMarkerPainted)) {
                    if (m.getType() == MarkerType.MARKER) {
                        paintMarker(canvas, m, false);
                    } else if (m.getType() == MarkerType.TEXT_ONLY) {
                        paintTextMarker(canvas, m, false);
                    }
                }
            }
            mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void setCurrentMarkers(List<Marker> currentMarkers) {
        this.currentMarkers = currentMarkers;
        drawMarkers();
    }

    private void paintMarker(Canvas canvas, Marker marker, boolean highlighted) {
        //canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
        float x = marker.getOffsetX() * getCurrentZoom() + getCurrentXOffset();
        float y = marker.getOffsetY() * getCurrentZoom() + getCurrentYOffset();
        LogUtils.v(TAG, "Draw marker " + x + ":" + y);
        if (x >= 1 && y >= 1) {

            drawClickOnCanvas(canvas, marker.getColor(), x, y);

            Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markerPaint.setColor(marker.getColor());
            float pixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    marker.getTextSize() * getCurrentZoom(),
                    Resources.getSystem().getDisplayMetrics()
            );
            markerPaint.setTextSize(pixels);
            //clickMarkerPaint.setStrokeWidth(activity.getResources().getDimension(R.dimen.click_stroke_size));

            if (marker.getPrintTitle() > 0) {
                float textMeasuredSize = markerPaint.measureText(marker.getTitle());
                if (marker.getPrintTitle() == 1) { //above
                    canvas.drawText(marker.getTitle(), x - (textMeasuredSize / 2), y - clickSquareSize, markerPaint);
                } else {//below
                    canvas.drawText(marker.getTitle(), x - (textMeasuredSize / 2), y + clickSquareSize, markerPaint);
                }
            }

            /*
            //Horizontal lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            //Vertical lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            */

            //canvas.drawText(marker.getTitle(), x + clickSquareSize * 2, y, clickMarkerPaint);
        }
    }

    private void paintTextMarker(Canvas canvas, Marker marker, boolean highlighted) {
        //canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
        float x = marker.getOffsetX() * getCurrentZoom() + getCurrentXOffset();
        float y = marker.getOffsetY() * getCurrentZoom() + getCurrentYOffset();
        LogUtils.v(TAG, "Draw text marker " + x + ":" + y);
        if (x >= 1 && y >= 1) {

            Paint markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markerPaint.setColor(marker.getColor());
            float pixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    marker.getTextSize() * getCurrentZoom(),
                    Resources.getSystem().getDisplayMetrics()
            );
            markerPaint.setTextSize(pixels);
            //clickMarkerPaint.setStrokeWidth(activity.getResources().getDimension(R.dimen.click_stroke_size));

            float textMeasuredSize = markerPaint.measureText(marker.getTitle());
            canvas.drawText(marker.getTitle(), x - (textMeasuredSize / 2), y, markerPaint);

            /*
            //Horizontal lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            //Vertical lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            */

            //canvas.drawText(marker.getTitle(), x + clickSquareSize * 2, y, clickMarkerPaint);
        }
    }

    public synchronized void drawMatchedMarker(Marker marker) {
        lastMarkerPainted = marker;
        Canvas canvas = mFloatingCanvas.getHolder().lockCanvas();
        canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
        paintMarker(canvas, marker, true);
        mFloatingCanvas.getHolder().unlockCanvasAndPost(canvas);
    }
}
