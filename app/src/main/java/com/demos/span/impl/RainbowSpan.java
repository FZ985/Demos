package com.demos.span.impl;

import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

/**
 * by JFZ
 * 2024/5/16
 * desc：渐变效果
 **/
public class RainbowSpan extends CharacterStyle implements UpdateAppearance {
    private final int[] colors;

    final Matrix matrix = new Matrix();

    private Shader shader;

    public RainbowSpan(int... colors) {
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


}