package com.a2t.autobpmprompt.app.controller;

import com.a2t.a2tlib.content.ProgressDialogFactory;
import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.media.Prompt;
import com.a2t.autobpmprompt.media.PromptManager;
import com.joanzapata.pdfview.PDFView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class PromptActivity extends A2TActivity implements MarkerDialog.MarkerDialogListener, RenamePromptDialog.RenamePromptDialogListener {
    private static final int NOTIFICATION_TIME_MS = 5000;
    private static final int BLINK_LED_TIME_MS = 150;
    //boolean isEdit = false;
    boolean contentVisible = true;
    boolean isClick = false;
    boolean addingMarker = false;

    Timer blinkTimer;

    View controlsView;
    View frameControls;
    //View frameMarkers;
    PDFView pdfview;
    View contentsFullscreen;
    Prompt currentPrompt;
    //ListView markers;
    SurfaceView floatingCanvas;

    ProgressDialog progress;

    /*********
     * TOP BAR
     *******/
    TextView currentBeat;
    TextView currentBar;
    TextView currentBpm;
    //Button editTopButton;
    //Button doneTopButton;
    //Button renameTopButton;
    //Button deleteTopButton;
    ImageView ledImage;

    TextView currentMarkerTitle;
    TextView currentMarkerNote;
    TextView currentMarkerBeat;
    TextView currentMarkerBar;

    View topBarNotifiactions;

    FloatingActionButton fab;
    FloatingActionButton fab_marker;

    int surfaceOffsetX;
    int surfaceOffsetY;

    float lastMarkerX = -1;
    float lastMarkerY = -1;

    int nCurrentBeat;
    int nCurrentBar;

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
            //hideLeftBar();
            hideRightBar();
            lastMarkerX = -1;
            lastMarkerY = -1;
            contentVisible = false;
        }

        @Override
        public void onPause() {
            //showLeftBar();
            showRightBar();
            lastMarkerX = -1;
            lastMarkerY = -1;
            contentVisible = true;
        }

        @Override
        public void onStop() {
            //showLeftBar();
            showRightBar();
            lastMarkerX = -1;
            lastMarkerY = -1;
            contentVisible = true;
        }

        @Override
        public void onLoaded() {
            ProgressDialogFactory.dismiss(progress);
        }

        @Override
        public void onMarkerMatched(Marker match) {
            ldebug("MARKER: " + match.getTitle() + " " + match.getOffsetX() + ":" + match.getOffsetY());
            highlightMarker(match, true);
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);

        /****************** LAYOUT ELEMENTS LOAD ******************************************/
        currentBar = (TextView) findViewById(R.id.prompt_current_bar);
        ledImage = (ImageView) findViewById(R.id.led_top_bar);
        currentBeat = (TextView) findViewById(R.id.prompt_current_beat);
        currentBpm = (TextView) findViewById(R.id.prompt_current_bpm);

        Typeface digitalTypeface = Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");

        currentBeat.setTypeface(digitalTypeface);
        currentBpm.setTypeface(digitalTypeface);
        currentBar.setTypeface(digitalTypeface);


        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentsFullscreen = findViewById(R.id.contents_fullscreen);
        frameControls = findViewById(R.id.frame_content_controls);
        //frameMarkers = findViewById(R.id.frame_marker_list);
        pdfview = (PDFView) findViewById(R.id.pdfview);
        //markers = (ListView) findViewById(R.id.prompt_markers);
        floatingCanvas = (SurfaceView) findViewById(R.id.prompt_floating_canvas);

        //editTopButton = (Button) findViewById(R.id.prompt_edit_top_button);
        //doneTopButton = (Button) findViewById(R.id.prompt_done_top_button);
        //renameTopButton = (Button) findViewById(R.id.prompt_rename_top_button);
        //deleteTopButton = (Button) findViewById(R.id.prompt_delete_top_button);

        currentMarkerNote = (TextView) findViewById(R.id.currentMarker_note);
        currentMarkerTitle = (TextView) findViewById(R.id.currentMarker_title);
        currentMarkerBeat = (TextView) findViewById(R.id.currentMarker_beat);
        currentMarkerBar = (TextView) findViewById(R.id.currentMarker_bar);

        topBarNotifiactions = findViewById(R.id.frame_top_notification);

        fab = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab_marker = (FloatingActionButton) findViewById(R.id.fab_add_marker);
        /**********************************************************************************/

        /******************* RETRIEVE VARIABLES ********************************************/
        //this.isEdit = getIntent().getBooleanExtra("isEdit", false);
        long idPrompt = getIntent().getLongExtra("promptId", -1);
        /***********************************************************************************/

        /******************* INITIALIZE DIMENSIONS && COLORS *******************************/
        //surfaceOffsetX = (int) getResources().getDimension(R.dimen.activity_prompt_markers_bar);
        surfaceOffsetX = 0;
        surfaceOffsetY = (int) getResources().getDimension(R.dimen.activity_prompt_top_bar) + getStatusBarHeight();
        /***********************************************************************************/

        /******************* PROMPT LOADING ************************************************/
        progress = ProgressDialogFactory.createIndeterminated(this,R.string.loading,ProgressDialog.STYLE_SPINNER, true, false);
        currentPrompt = PromptManager.load(idPrompt, pdfview, floatingCanvas, PromptActivity.this, promptEventsCallback);
        currentBpm.setText(String.valueOf(currentPrompt.settings.getBpm()));
        /**********************************************************************************/

        lastMarkerX = -1;
        lastMarkerY = -1;
        blinkTimer = new Timer();

        //registerForContextMenu(markers);
        floatingCanvas.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                requestPromptLayout();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

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

    private void setMarkers() {
        List<Marker> list = new ArrayList<>();
        for (Marker m : currentPrompt.settings.getMarkers()) {
            list.add(m);
        }
        currentPrompt.setCurrentMarkers(list);
        //currentPrompt.drawMarkers();
    }

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
        currentPrompt.drawMatchedMarker(marker);

        if (f) {
            //highlightMarkerAdapter(p);
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

                    //if (f)
                    //resetMarkerAdapter(p);
                }
            }, NOTIFICATION_TIME_MS);
        }
    }

    /*private void highlightMarkerAdapter(int position) {
        final View v = markers.getChildAt(position - markers.getFirstVisiblePosition());

        if (v != null) { //ELEMENT IS VISIBLE
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //((ImageView) v.findViewById(R.id.marker_bg_image)).setImageResource(R.drawable.postitgreen);
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
                    //ImageView img = (ImageView) v.findViewById(R.id.marker_bg_image);

                    //if (img != null)
                    //    img.setImageResource(R.drawable.postityellow);
                }
            });
        }
    }*/

    //Hide top bar when the window has loaded
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            hideTopBar();
            topBarNotifiactions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        lverbose("onStart");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        lverbose("onDestroy");
        currentPrompt.close();
        super.onDestroy();
    }

    private void requestPromptLayout() {
        //Clear canvas if needed
        currentPrompt.clearCanvas();
        lastMarkerX = -1;
        lastMarkerY = -1;
        //currentPrompt.editMode(isEdit);

        /*if (isEdit) {
            hideFab();
            frameControls.setVisibility(View.INVISIBLE);
            //editTopButton.setVisibility(View.GONE);
            doneTopButton.setVisibility(View.VISIBLE);
            //renameTopButton.setVisibility(View.VISIBLE);
            //deleteTopButton.setVisibility(View.VISIBLE);

        } else {*/
        if (currentPrompt.getStatus() != Prompt.Status.PLAYING) {
            showFab();
        }
        //frameControls.setVisibility(View.VISIBLE);
        //editTopButton.setVisibility(View.VISIBLE);
        //doneTopButton.setVisibility(View.GONE);
        //renameTopButton.setVisibility(View.GONE);
        //deleteTopButton.setVisibility(View.GONE);
        //}
        setMarkers();
    }

    private void createNewMarker() {
        if (lastMarkerX >= 0 && lastMarkerY >= 0) {
            DialogFragment newFragment = new MarkerDialog();
            Bundle args = new Bundle();
            args.putFloat("xOffset", lastMarkerX);
            args.putFloat("yOffset", lastMarkerY);
            args.putInt("page", currentPrompt.getCurrentPage());

            newFragment.setArguments(args);
            newFragment.show(PromptActivity.this.getSupportFragmentManager(), "markercreate");
            addingMarker = false;
        } else {
            new AlertDialog.Builder(PromptActivity.this)
                    .setTitle(R.string.cant_create_marker)
                    .setMessage(R.string.must_click_marker)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void hideFab() {
        fab.hide();
        fab_marker.hide();
    }

    private void showFab() {
        fab.show();
        fab_marker.show();
    }

    public void promptPlay(View v) {
        hideFab();
        currentPrompt.play();
    }

    public void promptPause(View v) {
        showFab();
        currentPrompt.pause();
    }

    public void promptStop(View v) {
        showFab();
        currentPrompt.stop();
    }

    public void promptSave(View v) {
        currentPrompt.stop();
        currentPrompt.settings.setBpm(Integer.parseInt(currentBpm.getText().toString()));
        currentPrompt.prepareSave();
        PromptManager.update(getApplicationContext(), currentPrompt);
        //isEdit = false;
        requestPromptLayout();
    }

    @Override
    public void onBackPressed() {
        /*if (isEdit) {
            //TODO: Reload values !!
            isEdit = false;
            requestPromptLayout();
        } else {*/
        currentPrompt.stop();
        super.onBackPressed();
        //}
    }

    private int getStatusBarHeight() {
        boolean fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        if (fullScreen) return 0;

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        //int mwidth = markers.getWidth();
        //int mheight = markers.getHeight();
        //float mx = markers.getX();
        //float my = markers.getY();
        float aX = x - surfaceOffsetX;
        float aY = y - surfaceOffsetY;
        if (addingMarker) {
            //boolean isMarkerClick = x > mx && x < mx + mwidth && y > my && y < my + mheight;

            if (aX >= 0 && aY >= 0) {
                lastMarkerX = (aX - currentPrompt.getCurrentXOffset()) / currentPrompt.getCurrentZoom();
                lastMarkerY = (aY - currentPrompt.getCurrentYOffset()) / currentPrompt.getCurrentZoom();

                ldebug("Draw click in " + aX + ":" + aY);
                ldebug("Real marker position: " + lastMarkerX + ":" + lastMarkerY);

                createNewMarker();
                currentPrompt.drawClickMarker(aX, aY);
            }
            return super.dispatchTouchEvent(ev);
        } else {
            int cwidth = frameControls.getWidth();
            int cheight = frameControls.getHeight();
            float cx = frameControls.getX();
            float cy = frameControls.getY();

            int swidth = topBarNotifiactions.getWidth();
            int sheight = topBarNotifiactions.getHeight();
            float sx = topBarNotifiactions.getX();
            float sy = topBarNotifiactions.getY();

            boolean isControlsClick = x > cx && x < cx + cwidth && y > cy && y < cy + cheight;
            //boolean isMarkerClick = x > mx && x < mx + mwidth && y > my && y < my + mheight;
            boolean isTopClick = x > sx && x < sx + swidth && y > sy && y < sy + sheight;
            Marker mClicked = currentPrompt.getClickedMarker(aX, aY);
            boolean isMarkerClick = mClicked != null;

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClick && !isControlsClick && !isMarkerClick && !isTopClick) {
                        if (contentVisible) {
                            hideRightBar();
                        } else {
                            showRightBar();
                        }
                        contentVisible = !contentVisible;
                    } else if (isMarkerClick) {
                        ldebug("Marker clicked " + mClicked.getTitle());
                        if (currentPrompt.getStatus() != Prompt.Status.PLAYING) {
                            DialogFragment d = MarkerDialog.editMarkerDialog(mClicked);
                            d.show(getSupportFragmentManager(), "edit");
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    isClick = false;
                    break;
            }
            if (currentPrompt.getStatus() == Prompt.Status.PLAYING) {
                return !isControlsClick || super.dispatchTouchEvent(ev); //if is control click, execute super.dispatch... and return it
                //if not, java will execute return true :)
            } else {
                return super.dispatchTouchEvent(ev);
            }
        }
    }

    /*private void showLeftBar() {
        frameMarkers.animate()
                .translationX(0)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime));
    }

    private void hideLeftBar() {
        frameMarkers.animate()
                .translationX(-1 * frameMarkers.getWidth())
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime));
    }*/

    private void showRightBar() {
        controlsView.animate()
                .translationX(0)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime));
    }

    private void hideRightBar() {
        controlsView.animate()
                .translationX(controlsView.getWidth())
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime));
    }

    private void showTopBar() {
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                topBarNotifiactions.animate()
                        .translationY(0)
                        .setDuration(getResources().getInteger(
                                android.R.integer.config_shortAnimTime));
            }
        });
    }

    private void hideTopBar() {
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                topBarNotifiactions.animate()
                        .translationY(-1 * topBarNotifiactions.getHeight())
                        .setDuration(getResources().getInteger(
                                android.R.integer.config_shortAnimTime));
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onMarkerCreated(DialogFragment dialog, String title, String note, int bar, int beat, int page, float positionX, float positionY) {
        ldebug("Marker created: " + positionX + ":" + positionY);

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
        promptSave(null);
        requestPromptLayout();
    }

    @Override
    public void onMarkerEdited(DialogFragment dialog, String title, String note, int bar, int beat, int page, float positionX, float positionY) {
        ldebug("Marker edited: " + title);

        for (int i = 0; i < currentPrompt.settings.getMarkers().size(); i++) {
            Marker m = currentPrompt.settings.getMarkers().get(i);
            if (m.getTitle().equals(title)) {
                m.setNote(note);
                m.setBar(bar);
                m.setBeat(beat);
                currentPrompt.settings.getMarkers().set(i, m);
                promptSave(null);
                return;
            }
        }
    }

    @Override
    public void onMarkerCancelled(DialogFragment dialog) {
        ldebug("Marker canceled");
    }

    @Override
    public void onMarkerDeleted(DialogFragment dialog, String title) {
        ldebug("Marker deleted: " + title);

        for (int i = 0; i < currentPrompt.settings.getMarkers().size(); i++) {
            Marker m = currentPrompt.settings.getMarkers().get(i);
            if (m.getTitle().equals(title)) {
                currentPrompt.settings.getMarkers().remove(i);
                PromptManager.deleteMarkerFromPrompt(getApplicationContext(), currentPrompt, m);
                currentPrompt.setCurrentMarkers(currentPrompt.settings.getMarkers());
                currentPrompt.drawMarkers();
                return;
            }
        }
    }

    public void editPromptClicked(View view) {
        //TODO: Edit prompt dialog
        //isEdit = true;
        //showLeftBar();
        //hideRightBar();
        //requestPromptLayout();
    }

    public void deletePrompt(View view) {
        currentPrompt.stop();
        PromptManager.delete(getApplicationContext(), currentPrompt.settings.getId());
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
    public void onPromptRename(DialogFragment dialog, long prompt_id, String name) {
        PromptManager.renamePrompt(getApplicationContext(), currentPrompt, name);
        //isEdit = false;
        requestPromptLayout();
    }

    @Override
    public void onPromptRenameCancelled(DialogFragment dialog) {
        //Do Nothing :)
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.prompt_markers) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.marker_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CustomArrayAdapter<Marker> currentAdapter = (CustomArrayAdapter<Marker>) markers.getAdapter();

        switch (item.getItemId()) {
            case R.id.action_delete:
                // add stuff here
                PromptManager.deleteMarkerFromPrompt(getApplicationContext(), currentPrompt, currentAdapter.getItem(info.position));
                currentAdapter.getElements().remove(info.position);
                currentAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_edit:
                DialogFragment d = MarkerDialog.editMarkerDialog(currentAdapter.getItem(info.position));
                d.show(getSupportFragmentManager(), "edit");
                return true;
            default:
                return false;
        }
    }*/

    public void addMarker(View view) {
        hideRightBar();
        Toast.makeText(this, R.string.click_marker_please, Toast.LENGTH_LONG).show();
        addingMarker = true;
    }
}
