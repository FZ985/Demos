package com.demos.span.impl;

import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

import com.demos.span.core.Span;

/**
 * by JFZ
 * 2024/5/16
 * desc：渐变效果
 **/
public class RainbowSpan extends CharacterStyle implements UpdateAppearance, Span.Spannable {
    private final int[] colors;

    private String text;

    final Matrix matrix = new Matrix();

    private Shader shader;

    public RainbowSpan(String text, int... colors) {
        this.text = text;
        this.colors = colors;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.setStyle(Paint.Style.FILL);
        if (shader == null) {
            shader = new LinearGradient(0, 0, 0, paint.getTextSize() * colors.length, colors, null,
                    Shader.TileMode.MIRROR);
        }
        matrix.reset();
        matrix.setRotate(90);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
    }


    @Override
    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }
}