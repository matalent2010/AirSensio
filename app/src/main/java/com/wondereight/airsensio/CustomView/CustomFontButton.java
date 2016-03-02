package com.wondereight.airsensio.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Miguel on 2/8/2016.
 */
public class CustomFontButton extends Button {

    public CustomFontButton(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}
