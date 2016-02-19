package com.wondereight.airsensio.CustomView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class SnapBar extends SeekBar {

    private static final int SNAP_MIN = 0;
    private static final int SNAP_MIDDLE = 50;
    private static final int SNAP_MAX = 100;

    private static final int LOWER_HALF = (SNAP_MIN + SNAP_MIDDLE) / 2;
    private static final int UPPER_HALF = (SNAP_MIDDLE + SNAP_MAX) / 2;

    public SnapBar(Context context) {
        super(context);
        setMax(SNAP_MAX);
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public SnapBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMax(SNAP_MAX);
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public SnapBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        setMax(SNAP_MAX);
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public SnapBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setMax(SNAP_MAX);
        setOnSeekBarChangeListener(new SnapBarChangeListener());
    }

    public class SnapBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {

            int progress = seekBar.getProgress();
            if (progress >= SNAP_MIN && progress <= LOWER_HALF)
                setProgressAnimatedJdk(seekBar, progress, SNAP_MIN);
            if (progress > LOWER_HALF && progress <= UPPER_HALF)
                setProgressAnimatedJdk(seekBar, progress, SNAP_MIDDLE);
            if (progress > UPPER_HALF && progress <= SNAP_MAX) {
                setProgressAnimatedJdk(seekBar, progress, SNAP_MAX);
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
}