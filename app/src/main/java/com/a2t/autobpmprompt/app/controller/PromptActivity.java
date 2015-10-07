package com.a2t.autobpmprompt.app.controller;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.MarkersAdapter;
import com.a2t.autobpmprompt.app.callback.MarkerAdapterCallback;
import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.media.Prompt;
import com.a2t.autobpmprompt.media.PromptManager;
import com.joanzapata.pdfview.PDFView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class PromptActivity extends AppCompatActivity implements MarkerDialog.MarkerDialogListener {
    static final String TAG = "PROMPTACTIVITY";
    boolean isEdit = false;
    boolean contentVisible = false;
    boolean isClick = false;

    View controlsView;
    View frameControls;
    PDFView pdfview;
    View contentsFullscreen;
    Prompt currentPrompt;
    ListView markers;
    SurfaceView floatingCanvas;

    /*********
     * TOP BAR
     *******/
    TextView currentBeat;
    TextView currentBar;
    EditText currentBpm;
    Button editTopButton;
    Button cancelTopButton;
    Button doneTopButton;

    int surfaceOffsetX;
    int surfaceOffsetY;
    int clickMarkerColor;
    Paint clickMarkerPaint;
    int clickSquareSize;
    int clickVerticalLineSize;

    int highlightMarkerFgColor;
    int highlightMarkerBgColor;

    float lastMarkerX;
    float lastMarkerY;

    int nCurrentBeat;
    int nCurrentBar;


    Runnable changeBar = new Runnable() {
        @Override
        public void run() {
            currentBar.setText(String.valueOf(nCurrentBar));
        }
    };

    Runnable changeBeat = new Runnable() {
        @Override
        public void run() {
            currentBeat.setText(String.valueOf(nCurrentBeat));
        }
    };

    PromptEventsCallback promptEventsCallback = new PromptEventsCallback() {
        @Override
        public void onBar(int bar) {
            nCurrentBar = bar;
            runOnUiThread(changeBar);
        }

        @Override
        public void onBeat(int beat) {
            nCurrentBeat = beat;
            runOnUiThread(changeBeat);
        }

        @Override
        public void onPlay() {
            hideLeftBar();
        }

        @Override
        public void onPause() {
            showLeftBar();
        }

        @Override
        public void onStop() {
            showLeftBar();
        }

        @Override
        public void onMarkerMatched(Marker match) {
            Log.i(TAG, "MARKER: " + match.getTitle());
            highlightMarker(match);
        }
    };

    private void highlightMarker(Marker marker) {
        int position;
        boolean found = false;
        for (position = 0; position < currentPrompt.settings.getMarkers().size(); position++) {
            Marker m = currentPrompt.settings.getMarkers().get(position);
            if (m.getTitle().equals(marker.getTitle())) {
                found = true;
                break;
            }
        }

        //TODO: SHOW SOMETHING

        if (found) {
            highlightMarkerAdapter(position);
        }
    }

    private void highlightMarkerAdapter(int position) {
        final View v = markers.getChildAt(position - markers.getFirstVisiblePosition());

        if (v != null) { //ELEMENT IS VISIBLE
            //TODO: HIGHLIGHT
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    v.setBackgroundColor(highlightMarkerBgColor);
                    ((TextView) v.findViewById(R.id.marker_title)).setTextColor(highlightMarkerFgColor);
                }
            });
        }
    }

    private void resetMarkerAdapter(int position) {
        final View v = markers.getChildAt(position - markers.getFirstVisiblePosition());

        if (v != null) { //ELEMENT IS VISIBLE
            //TODO: RESET APPEARANCE
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);

        /****************** LAYOUT ELEMENTS LOAD ******************************************/
        currentBar = (TextView) findViewById(R.id.prompt_current_bar);
        currentBeat = (TextView) findViewById(R.id.prompt_current_beat);
        currentBpm = (EditText) findViewById(R.id.prompt_current_bpm);
        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentsFullscreen = findViewById(R.id.contents_fullscreen);
        frameControls = findViewById(R.id.frame_content_controls);
        pdfview = (PDFView) findViewById(R.id.pdfview);
        markers = (ListView) findViewById(R.id.prompt_markers);
        floatingCanvas = (SurfaceView) findViewById(R.id.prompt_floating_canvas);

        editTopButton = (Button) findViewById(R.id.prompt_edit_top_button);
        cancelTopButton = (Button) findViewById(R.id.prompt_cancel_top_button);
        doneTopButton = (Button) findViewById(R.id.prompt_done_top_button);
        /**********************************************************************************/

        /******************* RETRIEVE VARIABLES ********************************************/
        this.isEdit = getIntent().getBooleanExtra(getString(R.string.isEditVariable), false);
        String name = getIntent().getStringExtra(getString(R.string.promptNameVariable));
        /***********************************************************************************/

        /******************* INITIALIZE DIMENSIONS && COLORS *******************************/
        surfaceOffsetX = (int) getResources().getDimension(R.dimen.activity_prompt_markers_bar);
        surfaceOffsetY = (int) getResources().getDimension(R.dimen.activity_prompt_top_bar) + getStatusBarHeight();
        clickSquareSize = (int) getResources().getDimension(R.dimen.click_square_size);
        clickVerticalLineSize = (int) getResources().getDimension(R.dimen.click_verticalline_size);
        clickMarkerColor = getResources().getColor(R.color.click_marker_color);
        clickMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickMarkerPaint.setColor(clickMarkerColor);
        highlightMarkerBgColor = getResources().getColor(R.color.highlight_marker_color_bg);
        highlightMarkerFgColor = getResources().getColor(R.color.highlight_marker_color_fg);
        /***********************************************************************************/

        /******************* PROMPT LOADING ************************************************/
        currentPrompt = PromptManager.load(name, pdfview, floatingCanvas, PromptActivity.this, promptEventsCallback);
        configure();
        currentBpm.setText(String.valueOf(currentPrompt.settings.getBpm()));
        /**********************************************************************************/
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        pdfview.recycle();
        currentPrompt.close();
        super.onDestroy();
    }

    private void configure() {
        //Clear canvas if needed
        Canvas canvas = floatingCanvas.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(clickMarkerColor, PorterDuff.Mode.CLEAR);
            floatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }

        if (isEdit) {
            frameControls.setVisibility(View.INVISIBLE);
            editTopButton.setVisibility(View.GONE);
            cancelTopButton.setVisibility(View.VISIBLE);
            doneTopButton.setVisibility(View.VISIBLE);
            currentBpm.setEnabled(true);
        } else {
            frameControls.setVisibility(View.VISIBLE);
            editTopButton.setVisibility(View.VISIBLE);
            cancelTopButton.setVisibility(View.GONE);
            doneTopButton.setVisibility(View.GONE);
            currentBpm.setEnabled(false);
        }

        //TODO: Sure is the best way to do that ?
        MarkersAdapter m = new MarkersAdapter(getApplicationContext(), currentPrompt.settings.getMarkers(), isEdit, new MarkerAdapterCallback() {
            @Override
            public void onCreateMarkerClick() {
                DialogFragment newFragment = new MarkerDialog();
                Bundle args = new Bundle();
                args.putFloat(getString(R.string.xOffsetVariable), lastMarkerX);
                args.putFloat(getString(R.string.yOffsetVariable), lastMarkerY);
                args.putInt(getString(R.string.pageVariable), currentPrompt.getPdf().getCurrentPage());

                newFragment.setArguments(args);
                newFragment.show(PromptActivity.this.getSupportFragmentManager(), "markercreate");
            }

            @Override
            public void onMarkerRemoved(Marker m) {
                //TODO: Prune database --> Remove markers without prompt parent !!
            }
        });
        markers.setAdapter(m);
    }

    public void promptPlay(View v) {
        currentPrompt.play();
    }

    public void promptPause(View v) {
        currentPrompt.pause();
    }

    public void promptStop(View v) {
        currentPrompt.stop();
    }

    public void promptSave(View v) {
        currentPrompt.stop();
        currentPrompt.settings.setBpm(Integer.parseInt(currentBpm.getText().toString()));
        currentPrompt.prepareSave();
        PromptManager.update(getApplicationContext(), currentPrompt);
        isEdit = false;
        configure();
    }

    @Override
    public void onBackPressed() {
        currentPrompt.stop();
        super.onBackPressed();
    }

    private void drawClickMarker(float x, float y) {
        if (x >= 1 && y >= 1) {
            lastMarkerX = (x - currentPrompt.getPdf().getCurrentXOffset()) / currentPrompt.getPdf().getCurrentZoom();
            lastMarkerY = (y - currentPrompt.getPdf().getCurrentYOffset()) / currentPrompt.getPdf().getCurrentZoom();

            Log.i(TAG, "Draw click in " + x + ":" + y);
            Log.i(TAG, "Real marker position: " + lastMarkerX + ":" + lastMarkerY);

            Canvas canvas = floatingCanvas.getHolder().lockCanvas();
            canvas.drawColor(255, PorterDuff.Mode.CLEAR);
            canvas.drawLine(0, y, floatingCanvas.getWidth(), y, clickMarkerPaint);

            canvas.drawLine(x, y - clickVerticalLineSize, x, y + clickVerticalLineSize, clickMarkerPaint);

            //Horizontal lines∫
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x + clickSquareSize, y - clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x - clickSquareSize, y + clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);

            //Vertical lines
            canvas.drawLine(x - clickSquareSize, y - clickSquareSize, x - clickSquareSize, y + clickSquareSize, clickMarkerPaint);
            canvas.drawLine(x + clickSquareSize, y - clickSquareSize, x + clickSquareSize, y + clickSquareSize, clickMarkerPaint);


            floatingCanvas.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawCurrentPosition() {

    }

    private int getStatusBarHeight() {
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
        if (isEdit) {
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
                        if (contentVisible) {
                            hideLeftBar();
                        } else {
                            showLeftBar();
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

    private void showLeftBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            controlsView.animate()
                    .translationX(0)
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_shortAnimTime));
        } else {
            controlsView.setVisibility(View.VISIBLE);
        }
    }

    private void hideLeftBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            controlsView.animate()
                    .translationX(controlsView.getWidth())
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_shortAnimTime));
        } else {
            controlsView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onMarkerCreated(DialogFragment dialog, String title, String note, int bar, int beat, int page, float positionX, float positionY) {
        Log.i(TAG, "Marker created");

        Marker m = new Marker();
        m.setTitle(title);
        m.setNote(note);
        m.setBar(bar);
        m.setBeat(beat);
        m.setPage(page);
        m.setOffsetX(positionX);
        m.setOffsetY(positionY);
        m.setId((int) System.currentTimeMillis());

        currentPrompt.settings.getMarkers().add(m);
        configure();
    }

    @Override
    public void onMarkerCancelled(DialogFragment dialog) {
        Log.i(TAG, "Marker canceled");
    }

    public void switchToEdit(View view) {
        isEdit = true;
        configure();
    }

    public void cancelEdit(View view) {
        //TODO: REFRESH VALUES :)
        isEdit = false;
        configure();
    }
}
