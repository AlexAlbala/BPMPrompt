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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class PromptActivity extends AppCompatActivity implements MarkerDialog.MarkerDialogListener, RenamePromptDialog.RenamePromptDialogListener {
    static final String TAG = "PROMPTACTIVITY";
    private static final int NOTIFICATION_TIME = 5000;
    private static final int BLINK_LED_TIME_MS = 150;
    boolean isEdit = false;
    boolean contentVisible = false;
    boolean isClick = false;

    Timer blinkTimer;

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
    Button doneTopButton;
    Button renameTopButton;
    Button deleteTopButton;
    ImageView ledImage;

    TextView currentMarkerTitle;
    TextView currentMarkerNote;
    TextView currentMarkerBeat;
    TextView currentMarkerBar;

    View topBarNotifiactions;

    int surfaceOffsetX;
    int surfaceOffsetY;

    float lastMarkerX;
    float lastMarkerY;

    int nCurrentBeat;
    int nCurrentBar;

    private void blinkLed() {
        if (blinkTimer != null) {
            ledImage.setImageResource(R.drawable.ledon);
            blinkTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ledImage.setImageResource(R.drawable.ledoff);
                        }
                    });
                }
            }, BLINK_LED_TIME_MS);
        }
    }

    Runnable changeBar = new Runnable() {
        @Override
        public void run() {
            currentBar.setText(String.valueOf(nCurrentBar));
            blinkLed();
        }
    };

    Runnable changeBeat = new Runnable() {
        @Override
        public void run() {
            currentBeat.setText(String.valueOf(nCurrentBeat));
            blinkLed();
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
            Log.i(TAG, "MARKER: " + match.getTitle() + " " + match.getOffsetX() + ":" + match.getOffsetY());
            highlightMarker(match, true);
        }
    };

    private void highlightMarker(final Marker marker, boolean isNotification) {
        int position;
        boolean found = false;
        for (position = 0; position < currentPrompt.settings.getMarkers().size(); position++) {
            Marker m = currentPrompt.settings.getMarkers().get(position);
            if (m.getTitle().equals(marker.getTitle())) {
                found = true;
                break;
            }
        }

        final int p = position;
        final boolean f = found;
        currentPrompt.getPdf().drawMarkerMatched(marker);

        if (f) {
            highlightMarkerAdapter(p);
        }

        if (isNotification) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentMarkerTitle.setText(marker.getTitle() + ":");
                    currentMarkerNote.setText(marker.getNote());
                    currentMarkerBeat.setText("Beat: " + marker.getBeat());
                    currentMarkerBar.setText("Bar: " + marker.getBar());
                }
            });

            showTopBar();

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    hideTopBar();

                    if (f)
                        resetMarkerAdapter(p);
                }
            }, NOTIFICATION_TIME);
        }
    }

    private void highlightMarkerAdapter(int position) {
        final View v = markers.getChildAt(position - markers.getFirstVisiblePosition());

        if (v != null) { //ELEMENT IS VISIBLE
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) v.findViewById(R.id.marker_bg_image)).setImageResource(R.drawable.postitgreen);
                }
            });
        }
    }

    private void resetMarkerAdapter(int position) {
        final View v = markers.getChildAt(position - markers.getFirstVisiblePosition());

        if (v != null) { //ELEMENT IS VISIBLE
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) v.findViewById(R.id.marker_bg_image)).setImageResource(R.drawable.postityellow);
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
        ledImage = (ImageView) findViewById(R.id.led_top_bar);
        currentBeat = (TextView) findViewById(R.id.prompt_current_beat);
        currentBpm = (EditText) findViewById(R.id.prompt_current_bpm);
        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentsFullscreen = findViewById(R.id.contents_fullscreen);
        frameControls = findViewById(R.id.frame_content_controls);
        pdfview = (PDFView) findViewById(R.id.pdfview);
        markers = (ListView) findViewById(R.id.prompt_markers);
        floatingCanvas = (SurfaceView) findViewById(R.id.prompt_floating_canvas);

        editTopButton = (Button) findViewById(R.id.prompt_edit_top_button);
        doneTopButton = (Button) findViewById(R.id.prompt_done_top_button);
        renameTopButton = (Button) findViewById(R.id.prompt_rename_top_button);
        deleteTopButton = (Button) findViewById(R.id.prompt_delete_top_button);

        currentMarkerNote = (TextView) findViewById(R.id.currentMarker_note);
        currentMarkerTitle = (TextView) findViewById(R.id.currentMarker_title);
        currentMarkerBeat = (TextView) findViewById(R.id.currentMarker_beat);
        currentMarkerBar = (TextView) findViewById(R.id.currentMarker_bar);

        topBarNotifiactions = findViewById(R.id.frame_top_notification);
        /**********************************************************************************/

        /******************* RETRIEVE VARIABLES ********************************************/
        this.isEdit = getIntent().getBooleanExtra("isEdit", false);
        long idPrompt = getIntent().getLongExtra("promptId", -1);
        /***********************************************************************************/

        /******************* INITIALIZE DIMENSIONS && COLORS *******************************/
        surfaceOffsetX = (int) getResources().getDimension(R.dimen.activity_prompt_markers_bar);
        surfaceOffsetY = (int) getResources().getDimension(R.dimen.activity_prompt_top_bar) + getStatusBarHeight();
        /***********************************************************************************/

        /******************* PROMPT LOADING ************************************************/
        currentPrompt = PromptManager.load(idPrompt, pdfview, floatingCanvas, PromptActivity.this, promptEventsCallback);
        configure();
        currentBpm.setText(String.valueOf(currentPrompt.settings.getBpm()));
        /**********************************************************************************/

        blinkTimer = new Timer();
    }

    //Hide top bar when the window has loaded
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            hideTopBar();
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        currentPrompt.close();
        super.onDestroy();
    }

    private void configure() {
        //Clear canvas if needed
        currentPrompt.getPdf().clear();

        if (isEdit) {
            frameControls.setVisibility(View.INVISIBLE);
            editTopButton.setVisibility(View.GONE);
            doneTopButton.setVisibility(View.VISIBLE);
            renameTopButton.setVisibility(View.VISIBLE);
            deleteTopButton.setVisibility(View.VISIBLE);
            currentBpm.setEnabled(true);
        } else {
            frameControls.setVisibility(View.VISIBLE);
            editTopButton.setVisibility(View.VISIBLE);
            doneTopButton.setVisibility(View.GONE);
            renameTopButton.setVisibility(View.GONE);
            deleteTopButton.setVisibility(View.GONE);
            currentBpm.setEnabled(false);
        }

        //TODO: Sure is the best way to do that ?
        MarkersAdapter m = new MarkersAdapter(getApplicationContext(), currentPrompt.settings.getMarkers(), isEdit, new MarkerAdapterCallback() {
            @Override
            public void onCreateMarkerClick() {
                DialogFragment newFragment = new MarkerDialog();
                Bundle args = new Bundle();
                args.putFloat("xOffset", lastMarkerX);
                args.putFloat("yOffset", lastMarkerY);
                args.putInt("page", currentPrompt.getPdf().getCurrentPage());

                newFragment.setArguments(args);
                newFragment.show(PromptActivity.this.getSupportFragmentManager(), "markercreate");
            }

            @Override
            public void onMarkerRemoved(Marker m) {
                PromptManager.deleteMarkerFromPrompt(getApplicationContext(), currentPrompt, m);
            }

            @Override
            public void onMarkerClicked(Marker m) {
                currentPrompt.notifyMarker(m);
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
        if (isEdit) {
            //TODO: Reload values !!
            isEdit = false;
            configure();
        } else {
            currentPrompt.stop();
            super.onBackPressed();
        }
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
            float aX = x - surfaceOffsetX;
            float aY = y - surfaceOffsetY;

            if (aX >= 0 && aY >= 0) {
                lastMarkerX = (aX - currentPrompt.getPdf().getCurrentXOffset()) / currentPrompt.getPdf().getCurrentZoom();
                lastMarkerY = (aY - currentPrompt.getPdf().getCurrentYOffset()) / currentPrompt.getPdf().getCurrentZoom();

                Log.i(TAG, "Draw click in " + aX + ":" + aY);
                Log.i(TAG, "Real marker position: " + lastMarkerX + ":" + lastMarkerY);

                currentPrompt.getPdf().drawClickMarker(aX, aY);
            }
            return super.dispatchTouchEvent(ev);
        } else {
            int cwidth = frameControls.getWidth();
            int cheight = frameControls.getHeight();
            float cx = frameControls.getX();
            float cy = frameControls.getY();

            int mwidth = markers.getWidth();
            int mheight = markers.getHeight();
            float mx = markers.getX();
            float my = markers.getY();

            int swidth = topBarNotifiactions.getWidth();
            int sheight = topBarNotifiactions.getHeight();
            float sx = topBarNotifiactions.getX();
            float sy = topBarNotifiactions.getY();

            boolean isControlsClick = x > cx && x < cx + cwidth && y > cy && y < cy + cheight;
            boolean isMarkerClick = x > mx && x < mx + mwidth && y > my && y < my + mheight;
            boolean isTopClick = x > sx && x < sx + swidth && y > sy && y < sy + sheight;

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClick && !isControlsClick && !isMarkerClick && !isTopClick) {
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

    private void showTopBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            topBarNotifiactions.animate()
                    .translationY(0)
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_shortAnimTime));
        } else {
            topBarNotifiactions.setVisibility(View.VISIBLE);
        }
    }

    private void hideTopBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            topBarNotifiactions.animate()
                    .translationY(-1 * topBarNotifiactions.getHeight())
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_shortAnimTime));
        } else {
            topBarNotifiactions.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onMarkerCreated(DialogFragment dialog, String title, String note, int bar, int beat, int page, float positionX, float positionY) {
        Log.i(TAG, "Marker created: " + positionX + ":" + positionY);

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

    public void deletePrompt(View view) {
        currentPrompt.stop();
        PromptManager.delete(getApplicationContext(), currentPrompt);
        finish();
    }

    public void renamePrompt(View view) {
        DialogFragment newFragment = new RenamePromptDialog();
        Bundle args = new Bundle();
        args.putString("promptName", currentPrompt.settings.getName());
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "renamesetlist");
    }

    @Override
    public void onPromptRenamed(DialogFragment dialog, String name) {
        PromptManager.renamePrompt(getApplicationContext(), currentPrompt, name);
        isEdit = false;
        configure();
    }

    @Override
    public void onPromptRenameCancelled(DialogFragment dialog) {

    }
}
