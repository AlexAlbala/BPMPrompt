package com.a2t.autobpmprompt.app.controller;

import com.a2t.a2tlib.content.ProgressDialogFactory;
import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.adapter.SimplePromptListAdapter;
import com.a2t.autobpmprompt.app.callback.PromptEventsCallback;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.TempoRecord;
import com.a2t.autobpmprompt.media.Prompt;
import com.a2t.autobpmprompt.media.PromptManager;
import com.github.barteksc.pdfviewer.PDFView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.Image;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.RealmList;


public class PromptActivity extends A2TActivity implements EditPromptDialog.PromptDialogListener, MarkerDialog.MarkerDialogListener, RenamePromptDialog.RenamePromptDialogListener {
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

    ListView setListPrompts;

    /********
     * PAGINATION
     ********/

    ImageView pagerRight;
    ImageView pagerLeft;
    /*********
     * TOP BAR
     *******/
    TextView currentBeat;
    TextView currentBar;
    TextView currentBpm;
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

    List<PromptSettings> promptsFromCurrentSetList;

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
            highlightMarker(match);
        }

        @Override
        public void onTempoChanged(final int bpm) {
            runOnUiThread(new TimerTask() {
                @Override
                public void run() {
                    currentBpm.setText(String.valueOf(bpm));
                }
            });
        }

        @Override
        public void onPageChanged(int page, int pageCount) {
            ldebug("page changed " + page);

            if (page == 0) {
                pagerLeft.setVisibility(View.GONE);
                if (pageCount > 1) {
                    pagerRight.setVisibility(View.VISIBLE);
                } else {
                    pagerRight.setVisibility(View.GONE);
                }
            } else if (page > 0) {
                pagerLeft.setVisibility(View.VISIBLE);
                if (pageCount > (page + 1)) {
                    pagerRight.setVisibility(View.VISIBLE);
                } else {
                    pagerRight.setVisibility(View.GONE);
                }
            }
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        pdfview = (PDFView) findViewById(R.id.pdfview);
        floatingCanvas = (SurfaceView) findViewById(R.id.prompt_floating_canvas);

        currentMarkerNote = (TextView) findViewById(R.id.currentMarker_note);
        currentMarkerTitle = (TextView) findViewById(R.id.currentMarker_title);
        currentMarkerBeat = (TextView) findViewById(R.id.currentMarker_beat);
        currentMarkerBar = (TextView) findViewById(R.id.currentMarker_bar);

        topBarNotifiactions = findViewById(R.id.frame_top_notification);

        fab = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab_marker = (FloatingActionButton) findViewById(R.id.fab_add_marker);

        setListPrompts = (ListView) findViewById(R.id.prompt_set_list_prompts);
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
        progress = ProgressDialogFactory.createIndeterminated(this, R.string.loading, ProgressDialog.STYLE_SPINNER, true, false);
        currentPrompt = PromptManager.load(idPrompt, pdfview, floatingCanvas, PromptActivity.this, promptEventsCallback);
        /**********************************************************************************/

        pagerRight = (ImageView) findViewById(R.id.prompt_next_page);
        pagerLeft = (ImageView) findViewById(R.id.prompt_previous_page);


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

        final String fromSetList = currentPrompt.settings.getSetList();
        promptsFromCurrentSetList = PromptManager.getAllPtomptsFromSetList(this, fromSetList);
//
//        ArrayList<Map<String, Object>> items = new ArrayList<>();
//        for (int i = 0; i < s.size(); ++i) {
//            HashMap<String, Object> item = new HashMap<>();
//            item.put("name", s.get(i).getName());
//            item.put("position", i);
//
//            items.add(item);
//        }
//        DragNDropSimpleAdapter adapter = new DragNDropSimpleAdapter(this,
//                items,
//                R.layout.row_simple_prompt,
//                new String[]{"name"},
//                new int[]{R.id.row_simple_prompt_title},
//                R.id.row_simple_prompt_title);
//
        setListPrompts.setAdapter(new SimplePromptListAdapter(this, promptsFromCurrentSetList));
        setListPrompts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PromptManager.openPrompt(PromptActivity.this, fromSetList, promptsFromCurrentSetList.get(position).getId());
                finish();
            }
        });

        findViewById(R.id.prompt_set_list_prompts_container).setVisibility(View.GONE);

        findViewById(R.id.prompt_toggle_setlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSetlist(v);
            }
        });

        ImageView setlist_next = (ImageView) findViewById(R.id.prompt_setlist_next);
        ImageView setlist_previous = (ImageView) findViewById(R.id.prompt_setlist_previous);

        final int currentPropmtPosition = currentPrompt.settings.getSetListPosition();
        if (currentPropmtPosition < (promptsFromCurrentSetList.size() - 1)) {
            setlist_next.setVisibility(View.VISIBLE);
            setlist_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PromptManager.openPrompt(PromptActivity.this, fromSetList, promptsFromCurrentSetList.get(currentPropmtPosition + 1).getId());
                    finish();
                }
            });
        } else {
            setlist_next.setVisibility(View.INVISIBLE);
        }

        if (currentPropmtPosition > 0) {
            setlist_previous.setVisibility(View.VISIBLE);
            setlist_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PromptManager.openPrompt(PromptActivity.this, fromSetList, promptsFromCurrentSetList.get(currentPropmtPosition - 1).getId());
                    finish();
                }
            });
        } else {
            setlist_previous.setVisibility(View.INVISIBLE);
        }


        pagerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ldebug("next pager clicked " + currentPrompt.getCurrentPage());
                currentPrompt.setCurrentPage(currentPrompt.getCurrentPage() + 1);
            }
        });

        pagerLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ldebug("previous pager clicked");
                currentPrompt.setCurrentPage(currentPrompt.getCurrentPage() - 1);
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

    private void highlightMarker(final Marker marker) {
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
        if (marker.isPrintInCanvasOnMatch()) {
            currentPrompt.drawMatchedMarker(marker);
        }

        if (f) {
            //highlightMarkerAdapter(p);
        }

        if (marker.isNotify()) {
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


        if (currentPrompt.getStatus() != Prompt.Status.PLAYING) {
            showFab();
        }

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
        //currentPrompt.settings.setBpm(Integer.parseInt(currentBpm.getText().toString()));
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

            int ppwidth = pagerLeft.getWidth();
            int ppheight = pagerLeft.getHeight();
            float ppx = pagerLeft.getX();
            float ppy = pagerLeft.getY();

            int npwidth = pagerRight.getWidth();
            int npheight = pagerRight.getHeight();
            float npx = pagerRight.getX();
            float npy = pagerRight.getY();

            boolean isControlsClick = x > cx && x < cx + cwidth && y > cy && y < cy + cheight;
            //boolean isMarkerClick = x > mx && x < mx + mwidth && y > my && y < my + mheight;
            boolean isTopClick = x > sx && x < sx + swidth && y > sy && y < sy + sheight;
            Marker mClicked = currentPrompt.getClickedMarker(aX, aY);
            boolean isMarkerClick = mClicked != null;
            boolean isPreviousPageClick = x > ppx && x < ppx + ppwidth && y > ppy && y < ppy + ppheight;
            boolean isNextPageClick = x > npx && x < npx + npwidth && y > npy && y < npy + npheight;

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClick && !isControlsClick && !isMarkerClick && !isTopClick && !isPreviousPageClick && !isNextPageClick) {
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
                if (isControlsClick || isNextPageClick || isPreviousPageClick) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    return true;
                }
            } else {
                return super.dispatchTouchEvent(ev);
            }
        }
    }

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
    public void onMarkerCreated(DialogFragment dialog, Marker m) {
        ldebug("Marker created: " + m.getOffsetX() + ":" + m.getOffsetY());
        m.setId((int) System.currentTimeMillis());

        currentPrompt.settings.getMarkers().add(m);
        promptSave(null);
        requestPromptLayout();
    }

    @Override
    public void onMarkerEdited(DialogFragment dialog, Marker em) {
        ldebug("Marker edited: " + em.getTitle());

        for (int i = 0; i < currentPrompt.settings.getMarkers().size(); i++) {
            Marker m = currentPrompt.settings.getMarkers().get(i);
            if (m.getId() == em.getId()) {
                currentPrompt.settings.getMarkers().set(i, em);
                PromptManager.update(this, currentPrompt);
                currentPrompt.setCurrentMarkers(currentPrompt.settings.getMarkers());
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
                //currentPrompt.drawMarkers();
                return;
            }
        }
    }

    public void editPromptClicked(View view) {
        DialogFragment d = EditPromptDialog.editPromptDialog(currentPrompt.settings);
        d.show(getSupportFragmentManager(), "edit_prompt");
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

    public void addMarker(View view) {
        hideRightBar();
        Toast.makeText(this, R.string.click_marker_please, Toast.LENGTH_LONG).show();
        addingMarker = true;
        hideFab();
    }

    @Override
    public void onPromptUpdated(String title, int bpm, int upper_tempo, int lower_tempo) {
        currentPrompt.settings.setName(title);
        //RealmList<TempoRecord> t = new RealmList<>();
        //currentPrompt.settings.setTempoTrack();
        //currentPrompt.settings.setBpm(bpm);
        //currentPrompt.settings.setCfgBarUpper(upper_tempo);
        //currentPrompt.settings.setCfgBarLower(lower_tempo);
        PromptManager.update(this, currentPrompt);
        requestPromptLayout();
    }

    public void toggleSetlist(View view) {
        View sc = findViewById(R.id.prompt_set_list_prompts_container);
        if (sc.getVisibility() == View.GONE) {
            sc.setVisibility(View.VISIBLE);
        } else {
            sc.setVisibility(View.GONE);
        }
    }
}
