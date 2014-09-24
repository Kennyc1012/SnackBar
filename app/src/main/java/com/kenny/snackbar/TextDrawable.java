package com.kenny.snackbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class TextDrawable extends Drawable {
    private String mText;

    private Paint mPaint;

    private int mIntrinsicWidth;

    private int mIntrinsicHeight;

    /**
     * Creates a TextDrawable object
     *
     * @param text     The text to display
     * @param textSize The text size
     * @param color    The color of the text
     * @param tf       The font to display the text in (Optional)
     */
    public TextDrawable(String text, float textSize, int color, @Nullable Typeface tf) {
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException("Text Drawable must be supplied with a String");
        }

        mText = text;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        if (tf != null) {
            mPaint.setTypeface(tf);
        }

        mIntrinsicWidth = (int) (mPaint.measureText(mText, 0, mText.length()) + .5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawText(mText, 0, mText.length(), bounds.centerX(), bounds.centerY() * 2, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }
}
