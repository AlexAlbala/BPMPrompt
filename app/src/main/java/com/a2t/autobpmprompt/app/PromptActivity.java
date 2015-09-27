package com.a2t.autobpmprompt.app;

import com.a2t.autobpmprompt.R;
import com.joanzapata.pdfview.PDFView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import java.util.List;


public class PromptActivity extends Activity {
    private boolean isEdit = false;
    private boolean contentVisible = false;
    private boolean isClick = false;

    View controlsView;
    View frameControls;
    PDFView pdfview;
    View contentsFullscreen;
    Prompt currentPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_play);

        //TODO: ONLY KITKAT AND ABOVE !!
        //TODO: For lower versions, fullscreen and margins !

        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentsFullscreen = findViewById(R.id.contents_fullscreen);
        frameControls = findViewById(R.id.frame_content_controls);

        pdfview = (PDFView) findViewById(R.id.pdfview);

        /******************* RETRIEVE VARIABLES ********************************************/
        this.isEdit = getIntent().getBooleanExtra(getString(R.string.isEditVariable), false);
        String name = getIntent().getStringExtra(getString(R.string.promptNameVariable));

        //Load prompt
        currentPrompt = PromptManager.load(name, pdfview, PromptActivity.this);
        /**********************************************************************************/

        currentPrompt.Play();
    }

    @Override
    public void onBackPressed() {
        currentPrompt.Stop();
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isEdit){
            return super.dispatchTouchEvent(ev);
        } else {
            float x = ev.getX();
            float y = ev.getY();

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
}
