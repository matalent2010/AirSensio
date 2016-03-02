package com.wondereight.airsensio.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Miguel on 2/2/2016.
 */
public class CustomFontTextView extends TextView {

    public CustomFontTextView(Context context) {
        super(context);
        if(!isInEditMode()) {
            CustomFontUtils.applyCustomFont(this, context, null);
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            CustomFontUtils.applyCustomFont(this, context, attrs);
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode()) {
            CustomFontUtils.applyCustomFont(this, context, attrs);
        }
    }
}