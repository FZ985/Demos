package com.demos.span.impl;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.util.Property;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * by JFZ
 * 2024/5/16
 * desc：彩色动画字体
 **/
public class AnimatedColorSpan extends CharacterStyle implements UpdateAppearance {

    private final int[] colors;
    private Shader shader = null;
    private final Matrix matrix = new Matrix();
    private float translateXPercentage = 0;

    private final TextView view;
    private ObjectAnimator objectAnimator;

    private final boolean playAnim;

    public AnimatedColorSpan(LifecycleOwner owner, TextView textView, boolean playAnim, int... colors) {
        this.view = textView;
        this.colors = colors;
        this.playAnim = playAnim;
        owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (playAnim && objectAnimator != null) {
                    objectAnimator.cancel();
                }
            }
        });
    }

    public void setTranslateXPercentage(float percentage) {
        translateXPercentage = percentage;
    }

    public float getTranslateXPercentage() {
        return translateXPercentage;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.setStyle(Paint.Style.FILL);
        float width = paint.getTextSize() * colors.length;
        if (shader == null) {
            shader = new LinearGradient(0, 0, 0, width, colors, null,
                    Shader.TileMode.MIRROR);
        }
        matrix.reset();
        matrix.setRotate(90);
        matrix.postTranslate(width * translateXPercentage, 0);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        if (playAnim) {
            playAnim();
        }
    }

    private void playAnim() {
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(this, ANIMATED_COLOR_SPAN_FLOAT_PROPERTY, 0, 100);
            objectAnimator.setEvaluator(new FloatEvaluator());
            objectAnimator.addUpdateListener(animation -> {
                if (view != null) {
                    view.postInvalidate();
                }
            });
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(DateUtils.MINUTE_IN_MILLIS * 2);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.start();
        }

    }

    /**
     * 彩色动画 属性变化器
     */
    private static final Property<AnimatedColorSpan, Float> ANIMATED_COLOR_SPAN_FLOAT_PROPERTY
            = new Property<AnimatedColorSpan, Float>(Float.class, "ANIMATED_COLOR_SPAN_FLOAT_PROPERTY") {
        @Override
        public void set(AnimatedColorSpan span, Float value) {
            span.setTranslateXPercentage(value);
        }

        @Override
        public Float get(AnimatedColorSpan span) {
            return span.getTranslateXPercentage();
        }
    };

}
