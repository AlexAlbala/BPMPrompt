package com.a2t.autobpmprompt.media.prompt;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.callback.PromptViewCallback;
import com.a2t.autobpmprompt.app.callback.SimpleCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.List;


public class PromptViewManager {
    private static final String TAG = "PROMPTVIEWMANAGER";
    //Component View
    //https://github.com/JoanZapata/android-pdfview

    //Native:
    //https://code.google.com/p/apv/

    private final float DEFAULT_ZOOM = 1.0F;
    private PDFView pdfview;
    private Activity activity;
    private SurfaceView mFloatingCanvas;
    private PromptViewCallback mCallback;

    public File getPdfFile() {
        return pdfFile;
    }

    private File pdfFile;

    private PromptViewManager() {

    }

    public PromptViewManager(File pdf, PDFView pdfview, SurfaceView floatingCanvas, Activity activity, PromptViewCallback callback) {
        mCallback = callback;
        this.loadPDF(pdf, pdfview, floatingCanvas, activity);
    }

//    /**
//     * Hack because of a bug in PDFview; It crashes when you load a second PDF
//     */
//    public static void reinitPdfView(Context ctx, PDFView view) {
//        ViewGroup group = (ViewGroup) view.getParent();
//        int index = group.indexOfChild(view);
//        group.removeView(view);
//        view = new PDFView(ctx, null);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        group.addView(view, index);
//    }

    public static boolean loadThumbnail(File pdfFile, PDFView pdfview) {
        pdfview.fromFile(pdfFile)
                .defaultPage(0)
                .pages(0)
                .showMinimap(false)
                .enableSwipe(false)
                .load();

        return true;
    }

//    @Nullable
//    public static Bitmap renderToBitmap(Context context, File pdfFile) {
//        Bitmap bi = null;
//        InputStream inStream = null;
//
//        try {
//            inStream = new FileInputStream(pdfFile);
//
//            Log.d(TAG, "Attempting to copy this file: " + pdfFile.getAbsolutePath());
//            bi = renderToBitmap(context, inStream);
//        } catch (java.io.FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                inStream.close();
//            } catch (IOException e) {
//                // do nothing because the stream has already been closed
//            }
//        }
//        return bi;
//    }
//
//
//    @Nullable
//    public static Bitmap renderToBitmap(Context context, InputStream inStream) {
//        Bitmap bi = null;
//        try {
//            byte[] decode = IOUtils.toByteArray(inStream);
//
//            ByteBuffer buf = ByteBuffer.wrap(decode);
//            PDFPage mPdfPage = new PDFFile(buf).getPage(0);
//            float width = mPdfPage.getWidth();
//            float height = mPdfPage.getHeight();
//            RectF clip = null;
//            bi = mPdfPage.getImage((int) (width), (int) (height), clip, true,
//                    true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                inStream.close();
//            } catch (IOException e) {
//                // do nothing because the stream has already been closed
//            }
//        }
//        return bi;
//    }

    public boolean loadPDF(File pdfFile, final PDFView pdfview, SurfaceView floatingCanvas, Activity mActivity) {
        pdfview.fromFile(pdfFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float v, float v1, int i) {
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
        this.pdfFile = pdfFile;
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

    public void zoomIn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdfview.zoomTo(1.5F);
            }
        });
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

    public void zoomOut() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pdfview.zoomTo(1 / 1.5F);
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


}
