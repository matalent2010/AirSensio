package com.wondereight.sensioair.CustomView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Miguel on 02/2/2016.
 */

public class SnapBar extends SeekBar {

    private final int SNAP_MAX = 100;
    private final int SNAP_STEP = 11;    // in this case, step is 0 ~ 10

    private int[] STEP_HALF = new int[SNAP_STEP+1];
    private int[] STEP_POINT = new int[SNAP_STEP];
    private boolean isScrollingSmooth = false;  //if true, be animated when scroll the thumb.

    public SnapBar(Context context) {
        super(context);
        setMax(SNAP_MAX);
        setStepValues();
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public SnapBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMax(SNAP_MAX);
        setStepValues();
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public SnapBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        setMax(SNAP_MAX);
        setStepValues();
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public SnapBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setMax(SNAP_MAX);
        setStepValues();
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public class SnapBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if( !isScrollingSmooth) {
                seekBar.setProgress(STEP_POINT[getSteps()]);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
            if( isScrollingSmooth ) {
                int progress = seekBar.getProgress();
                setProgressAnimatedJdk(seekBar, progress, STEP_POINT[getSteps()]);
            }
        }
    }

    private static void setProgressAnimatedJdk(final SeekBar seekBar, int from, int to) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                seekBar.setProgress(animProgress);
            }
        });
        anim.start();
    }

    private void setStepValues(){
        for( int i=0; i<SNAP_STEP; i++){
            STEP_HALF[i] = i==0 ? 0: SNAP_MAX/(SNAP_STEP-1) * i - SNAP_MAX/(SNAP_STEP-1)/2;
            STEP_POINT[i] = SNAP_MAX/(SNAP_STEP-1) * i;
        }
        STEP_HALF[SNAP_STEP] = SNAP_MAX;
    }

    public int getSteps(){
        int nValue = getProgress();
        if(nValue == 0) return 0;
        for( int i=0; i<SNAP_STEP; i++){
            if(STEP_HALF[i] < nValue && nValue <= STEP_HALF[i+1] )
                return i;
        }
        return 0;
    }

    public void setSteps( int steps ){

        int nValue = (int)(steps * ((float)SNAP_MAX / (SNAP_STEP-1))); //getProgress();

        setProgress(nValue);
    }

    public void setMovingSmooth(boolean isSmooth){
        isScrollingSmooth = isSmooth;
    }
}