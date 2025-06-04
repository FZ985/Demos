package com.demos.span.impl;


import android.graphics.BlurMaskFilter;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

/**
 * by JFZ
 * 2025/5/24
 * desc：
 **/
public class BlurSpan extends CharacterStyle {


    private final float radius;

    private BlurMaskFilter.Blur style = BlurMaskFilter.Blur.NORMAL;

    public BlurSpan(float radius) {
        this.radius = radius;
    }

    public BlurSpan(float radius, BlurMaskFilter.Blur style) {
        this.radius = radius;
        this.style = style;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setAntiAlias(true);
        tp.setMaskFilter(new BlurMaskFilter(radius, style));
    }

}
