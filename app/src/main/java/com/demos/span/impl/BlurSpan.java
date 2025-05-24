package com.demos.span.impl;


import android.graphics.BlurMaskFilter;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import com.demos.span.core.Span;

/**
 * by JFZ
 * 2025/5/24
 * desc：
 **/
public class BlurSpan extends CharacterStyle implements Span.Spannable {

    private String text;

    private final float radius;

    private BlurMaskFilter.Blur style = BlurMaskFilter.Blur.NORMAL;

    public BlurSpan(String text, float radius) {
        this.text = text;
        this.radius = radius;
    }

    public BlurSpan(String text, float radius, BlurMaskFilter.Blur style) {
        this.text = text;
        this.radius = radius;
        this.style = style;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setAntiAlias(true);
        tp.setMaskFilter(new BlurMaskFilter(radius, style));
    }

    @Override
    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }
}
