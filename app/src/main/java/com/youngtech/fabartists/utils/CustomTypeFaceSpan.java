package com.youngtech.fabartists.utils;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Created by VARUN on 01/01/19.
 */
@SuppressLint("ParcelCreator")
public class CustomTypeFaceSpan extends TypefaceSpan
{
    private final Typeface type;
    /**
     * @param family The font family for this typeface.  Examples include
     *               "monospace", "serif", and "sans-serif".
     */
    public CustomTypeFaceSpan(String family, Typeface typeface) {
        super(family);
        this.type = typeface;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        applyCustomFont(ds,type);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        super.updateMeasureState(paint);
        applyCustomFont(paint,type);
    }
    /*public CustomTypeFaceSpan(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomTypeFaceSpan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public CustomTypeFaceSpan(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }*/
    private void applyCustomFont(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null)
        {
            oldStyle = 0;
        }
        else
        {
            oldStyle = old.getStyle();
        }
        int fake = oldStyle & ~tf.getStyle();
        if((fake & Typeface.BOLD)!=0)
        {
            paint.setFakeBoldText(true);
        }
        {
            paint.setFakeBoldText(true);
        }
        if((fake & Typeface.ITALIC)!=0)
        {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(tf);
    }
}
