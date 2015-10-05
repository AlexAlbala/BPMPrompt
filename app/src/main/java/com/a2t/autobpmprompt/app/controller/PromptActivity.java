package com.a2t.autobpmprompt.app.controller;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.MarkersAdapter;
import com.a2t.autobpmprompt.app.callback.MarkerAdapterCallback;
import com.a2t.autobpmprompt.media.Prompt;
import com.a2t.autobpmprompt.media.PromptManager;
import com.joanzapata.pdfview.PDFView;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;


public class PromptActivity extends AppCompatActivity implements MarkerDialog.MarkerDialogListener{
    private static final String TAG = "PROMPTACTIVITY";
    private boolean isEdit = false;
    private boolean contentVisible = false;
    private boolean isClick = false;

    View controlsView;
    View frameControls;
    PDFView pdfview;
    View contentsFullscreen;
    Prompt currentPrompt;
    ListView markers;
    SurfaceView floatingCanvas;

    int surfaceOffsetX;
    int surfaceOffsetY;
    int clickMarkerColor;
    Paint clickMarkerPaint;
    int clickSquareSize;
    int clickVerticalLineSize;

    int lastMarkerX;
    int lastMarkerY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
    }

    public void onResume(){
        super.onResume();

        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentsFullscreen = findViewById(R.id.contents_fullscreen);
        frameControls = findViewById(R.id.frame_content_controls);
        pdfview = (PDFView) findViewById(R.id.pdfview);
        markers = (ListView) findViewById(R.id.prompt_markers);

        /******************* RETRIEVE VARIABLES ********************************************/
        this.isEdit = getIntent().getBooleanExtra(getString(R.string.isEditVariable), false);
        String name = getIntent().getStringExtra(getString(R.string.promptNameVariable));

        //Load prompt
        //Load the floating canvas
        floatingCanvas = (SurfaceView)findViewById(R.id.prompt_floating_canvas);
        currentPrompt = PromptManager.load(name, pdfview, floatingCanvas, PromptActivity.this);
        /**********************************************************************************/

        /******************* INITIALIZE DIMENSIONS ********************************************/
        surfaceOffsetX = (int)getResources().getDimension(R.dimen.activity_prompt_markers_bar);
        surfaceOffsetY = (int)getResources().getDimension(R.dimen.activity_prompt_top_bar) + getStatusBarHeight();
        clickSquareSize = (int)getResources().getDimension(R.dimen.click_square_size);
        clickVerticalLineSize = (int)getResources().getDimension(R.dimen.click_verticalline_size);
        clickMarkerColor = getResources().getColor(R.color.click_marker_color);
        clickMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickMarkerPaint.setColor(clickMarkerColor);
        /**********************************************************************************/

        Button saveButton =(Button)findViewById(R.id.button_prompt_save);
        if(isEdit){
            saveButton.setVisibility(View.VISIBLE);
        } else{
            saveButton.setVisibility(View.INVISIBLE);
        }

        MarkersAdapter m = new MarkersAdapter(getApplicationContext(), currentPrompt.settings.getMarkers(), isEdit, new MarkerAdapterCallback() {
            @Override
            public void onCreateMarkerClick() {
                DialogFragment newFragment = new MarkerDialog();
                Bundle args = new Bundle();
                args.putInt("x", lastMarkerX);
                args.putInt("y", lastMarkerY);
                newFragment.setArguments(args);
                newFragment.show(PromptActivity.this.getSupportFragmentManager(), "markercreate");
            }
        });
        markers.setAdapter(m);
    }

    public void promptPlay(View v){
        currentPrompt.Play();
    }

    public void promptPause(View v){
        currentPrompt.Pause();
    }

    public void promptStop(View v){
        currentPrompt.Stop();
    }

    public void promptSave(View v){
        currentPrompt.Stop();
    }

    @Override
    public void onBackPressed() {
        currentPrompt.Stop();
        super.onBackPressed();
    }

    private void drawClickMarker(float x, float y){
        if(x >= 1 && y >= 1) {
            lastMarkerX = (int)x;
            lastMarkerY = (int)y;
            Canvas canvas = floatingCanvas.getHolder().lockCanvas();
            canvas.drawColor(255, PorterDuff.Mode.CLEAR);
            canvas.drawLine(0, y, floatingCanvas.getWidth(), y, clickMarkerPaint);

            canvas.drawLine(x, y - clickVerticalLineSize, x, y + clickVerticalLineSize, clickMarkerPaint);

            //Horizontal lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            //Vertical lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);


            floatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawCurrentPosition(){

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        if(isEdit){
            drawClickMarker(x - surfaceOffsetX, y - surfaceOffsetY);
            return super.dispatchTouchEvent(ev);
        } else {
            float px = frameControls.getX();
            float py = frameControls.getY();
            int pwidth = frameControls.getWidth();
            int pheight = frameControls.getHeight();

            boolean isFragmentClick = x > px && x < px + pwidth && y > py && y < py + pheight;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClick && !isFragmentClick) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            controlsView.animate()
                                    .translationX(contentVisible ? 0 : controlsView.getWidth())
                                    .setDuration(getResources().getInteger(
                                            android.R.integer.config_shortAnimTime));
                        } else {
                            controlsView.setVisibility(contentVisible ? View.VISIBLE : View.GONE);
                        }
                        contentVisible = !contentVisible;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    isClick = false;
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onMarkerCreated(DialogFragment dialog, String title, String note, int bar, int beat, int positionX, int positionY) {
        Log.i(TAG, "Marker created");
    }

    @Override
    public void onCancel(DialogFragment dialog) {
        Log.i(TAG, "Marker canceled");
    }
}
