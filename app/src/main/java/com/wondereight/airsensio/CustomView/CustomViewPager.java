package com.wondereight.airsensio.CustomView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean isScrolling;

    public CustomViewPager(Context context) {
        super(context);
        this.isScrolling = true;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isScrolling = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isScrolling && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isScrolling && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return this.isScrolling && super.canScrollHorizontally(direction);
    }

    public void setPagingEnabled(boolean enabled) {
        this.isScrolling = enabled;
    }
}