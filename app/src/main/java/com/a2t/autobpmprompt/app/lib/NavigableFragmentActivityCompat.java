package com.a2t.autobpmprompt.app.lib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.a2t.autobpmprompt.app.lib.DoneCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NavigableFragmentActivityCompat extends A2TActivity implements FragmentManager.OnBackStackChangedListener {
    int cAnimIn;
    int cAnimOut;
    List<DoneCallback> onResumeFragmentCallbacks;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearAnimation();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();
        onResumeFragmentCallbacks = new ArrayList<>();
    }

    public void setCustomAnimation(int cAnimIn, int cAnimOut) {
        this.cAnimIn = cAnimIn;
        this.cAnimOut = cAnimOut;
    }

    public void clearAnimation() {
        this.cAnimIn = 0;
        this.cAnimOut = 0;
    }

    private void animate(FragmentTransaction transaction) {
        if (cAnimIn != 0 && cAnimOut != 0) {
            transaction.setCustomAnimations(cAnimIn, cAnimOut, cAnimIn, cAnimOut);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        } catch (IllegalStateException e) {
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error going back", e);
        }
    }

    public void clearBackStack() {
        try {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(canback);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        try {
            getSupportFragmentManager().popBackStack();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error going back", e);
            return false;
        }
    }

    public void replaceFragment(int container, Fragment fragment, boolean stack) {
        if(!isFinishing() && !isDestroyed()) {
            try {
                // update the main content by replacing fragments
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                animate(transaction);
                transaction.replace(container, fragment);

                if (stack) {
                    transaction.addToBackStack(null);
                }
                transaction.commitAllowingStateLoss();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void showHideFragment(int container, Fragment old, Fragment fragment, boolean stack) {
        if(!isFinishing() && !isDestroyed()) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            animate(transaction);
            transaction.hide(old);
            transaction.add(container, fragment);

            if (stack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
        }
    }


    public void addFragment(int container, Fragment fragment, boolean stack) {
        if(!isFinishing() && !isDestroyed()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            animate(transaction);
            transaction.add(container, fragment);
            if (stack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void removeFragment(Fragment fragment, boolean stack) {
        if(!isFinishing() && !isDestroyed()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            animate(transaction);
            transaction.remove(fragment);
            if (stack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void back() {
        try {
            getSupportFragmentManager().popBackStackImmediate();
        } catch (Exception e) {
            Log.e(TAG, "Error back", e);
        }
    }

    public void addOnResumeFragmentCallbacks(DoneCallback cb) {
        if (!onResumeFragmentCallbacks.contains(cb)) {
            onResumeFragmentCallbacks.add(cb);
        }
    }

    public void removeOnResumeFragmentCallbacks(DoneCallback cb) {
        if (onResumeFragmentCallbacks.contains(cb)) {
            onResumeFragmentCallbacks.remove(cb);
        }
    }

    public void clearOnResumeFragmentCallbacks() {
        onResumeFragmentCallbacks = new ArrayList<>();
    }

    @Override
    protected void onResumeFragments() {
        for (DoneCallback cb : onResumeFragmentCallbacks) {
            cb.done();
        }
    }
}
