package com.wondereight.airsensio.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wondereight.airsensio.R;

/**
 * Created by VENUSPC on 2/2/2016.
 */
public class CustomFontUtils {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static void applyCustomFont(TextView customTextView, Context context, AttributeSet attrs) {


        try {

            TypedArray attributeArray = context.obtainStyledAttributes( attrs, R.styleable.CustomFontTextView);
            String fontName = attributeArray.getString(R.styleable.CustomFontTextView_font);

            // check if a special textStyle was used (e.g. extra bold)
            int textStyle = attributeArray.getInt(R.styleable.CustomFontTextView_textStyle, 0);

            // if nothing extra was used, fall back to regular android:textStyle parameter
            if (textStyle == 0) {
                textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
            }

            Typeface customFont = selectTypeface(context, fontName, textStyle);
            customTextView.setTypeface(customFont);
            attributeArray.recycle();
        }catch (NumberFormatException e) {
        }

    }

    private static Typeface selectTypeface(Context context, String fontName, int textStyle) {
          /*
          information about the TextView textStyle:
          http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
          */
        if (fontName.contentEquals(context.getString(R.string.font_name_opensans))) {

            switch (textStyle) {
                case Typeface.BOLD: // bold
                    return FontCache.getTypeface("font/OpenSans-Bold.ttf", context);

                case Typeface.ITALIC: // italic
                    return FontCache.getTypeface("font/OpenSans-Italic.ttf", context);

                case Typeface.BOLD_ITALIC: // bold italic
                    return FontCache.getTypeface("font/OpenSans-BoldItalic.ttf", context);

                case 5: // extra bold, equals @integer/FS_exbold
                    return FontCache.getTypeface("font/OpenSans-ExtraBold.ttf", context);

                case 7: // extra bold italic, equals @integer/FS_exbolditalic
                    return FontCache.getTypeface("font/OpenSans-ExtraBoldItalic.ttf", context);

                case 9: // semi bold, equals @integer/FS_semibold
                    return FontCache.getTypeface("font/OpenSans-Semibold.ttf", context);

                case 11: // semi bold italic, equals @integer/FS_semibolditalic
                    return FontCache.getTypeface("font/OpenSans-SemiboldItalic.ttf", context);

                case 16: // light, equals @integer/FS_light
                    return FontCache.getTypeface("font/OpenSans-Light.ttf", context);

                case 18: // light italic, equals @integer/FS_lightitalic
                    return FontCache.getTypeface("font/OpenSans-LightItalic.ttf", context);
                case Typeface.NORMAL: // regular
                default:
                    return FontCache.getTypeface("font/OpenSans-Regular.ttf", context);
            }
        }
//        else if (fontName.contentEquals(context.getString(R.string.font_name_fontawesome))) {
//            return FontCache.getTypeface("fontawesome.ttf", context);
//        }
        else {
            // no matching font found
            // return null so Android just uses the standard font (Roboto)
            return null;
        }
    }
}