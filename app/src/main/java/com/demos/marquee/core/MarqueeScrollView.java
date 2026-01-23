package com.demos.marquee.core;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public class MarqueeScrollView extends HorizontalScrollView implements DefaultLifecycleObserver {

    public interface OnMarqueeCompleteListener {
        void onMarqueeComplete();
    }

    public interface OnMarqueeStateListener {
        void onMarqueeNotNeeded();
    }

    private final LinearLayout container;
    private final TextView tv1;
    private final TextView tv2;
    private CharSequence originalText;

    private ObjectAnimator animator;
    private float speedPxPerSec = 150f; // 默认 150px/s
    private int repeatLimit = 1;       // -1 无限，1 单次，>1 多次

    private int spacing = 270;

    private OnMarqueeCompleteListener completeListener;
    private OnMarqueeStateListener stateListener;

    private boolean isRunning = false;

    private boolean isPause = false;

    private int singleTextWidth = 0;

    public MarqueeScrollView(Context context) {
        this(context, null);
    }

    public MarqueeScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        setHorizontalScrollBarEnabled(false);

        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);

        tv1 = new TextView(context);
        tv2 = new TextView(context);

        tv1.setSingleLine(true);
        tv2.setSingleLine(true);

        container.addView(tv1);
        container.addView(tv2);

        addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    public void setText(CharSequence text) {
        this.originalText = text;
        tv1.setText(text);
        tv2.setVisibility(GONE);
        tv2.setText(text);
    }

    public void setSpeed(float pxPerSecond) {
        if (pxPerSecond > 0) {
            this.speedPxPerSec = pxPerSecond;
        }
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setMarqueeRepeatLimit(int limit) {
        this.repeatLimit = limit;
    }

    public void setOnMarqueeCompleteListener(OnMarqueeCompleteListener l) {
        this.completeListener = l;
    }

    public void setOnMarqueeStateListener(OnMarqueeStateListener l) {
        this.stateListener = l;
    }

    public void bindLifecycle(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        isPause = true;
        stopMarquee();
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        if (isPause) {
            isPause = false;
            startMarquee();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        stopMarquee();
    }

    public void startMarquee() {
        post(() -> {
            if (originalText == null) return;

//            float w = tv1.getPaint().measureText(originalText.toString());
            float w = getTextWidth(originalText);
            singleTextWidth = (int) Math.ceil(w);
            int viewWidth = getWidth();

            stopMarquee(); // 清理旧的

            if (singleTextWidth <= viewWidth) {
                if (stateListener != null) stateListener.onMarqueeNotNeeded();
                return;
            }


            if (repeatLimit == -1) {
                // 无限循环：双份拼接
                tv1.setText(originalText);
                tv2.setText(originalText);
                tv2.setVisibility(View.VISIBLE);
                tv2.setPadding(spacing, 0, 0, 0);

                float distance = singleTextWidth;
                long duration = (long) (distance / speedPxPerSec * 1000f);

                animator = ObjectAnimator.ofFloat(container, "translationX", 0f, -distance);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(duration);
                animator.setRepeatCount(ObjectAnimator.INFINITE);
                animator.start();

                isRunning = true;
            } else if (repeatLimit == 1) {
                // 单次滚动：只滚动多出的部分
                tv1.setText(originalText);
                tv2.setVisibility(View.GONE);

                float distance = singleTextWidth - getWidth(); // 只滚动多出来的部分

                if (distance <= 0) {
                    //宽度不够滚动，直接回调
                    if (stateListener != null) stateListener.onMarqueeNotNeeded();
                    return;
                }

                long duration = (long) (distance / speedPxPerSec * 1000f);

                animator = ObjectAnimator.ofFloat(container, "translationX", 0f, -distance);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(duration);
                animator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(@NonNull android.animation.Animator animation) {
                        isRunning = false;
                        if (completeListener != null) completeListener.onMarqueeComplete();
                    }
                });
                animator.start();
                isRunning = true;
            } else {
                SpannableStringBuilder sb = new SpannableStringBuilder();
                int finalLimit = repeatLimit - 1;
                StringBuilder emptySpace = getEmptySpace(repeatLimit);
                for (int i = 0; i < finalLimit + 1; i++) {
                    sb.append(new SpannableStringBuilder(originalText));
                    sb.append(new SpannableString(emptySpace));
                }
                tv1.setText(sb);
                tv2.setVisibility(View.GONE);

                float emptySpaceWidth = tv1.getPaint().measureText(emptySpace.toString());

                float distance = (float) (singleTextWidth + emptySpaceWidth * (repeatLimit + 1)) * finalLimit;
                long duration = (long) (distance / speedPxPerSec * 1000f);

                animator = ObjectAnimator.ofFloat(container, "translationX", 0f, -distance);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(duration);
                animator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(@NonNull android.animation.Animator animation) {
                        isRunning = false;
                        if (completeListener != null) completeListener.onMarqueeComplete();
                    }
                });
                animator.start();
                isRunning = true;
            }
        });
    }

    public void stopMarquee() {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
            animator = null;
        }
        container.setTranslationX(0f);
        isRunning = false;
    }

    private StringBuilder getEmptySpace(int limit) {
        // 计算需要多少个空格
        Paint paint = tv1.getPaint();
        float spaceWidth = paint.measureText(" "); // 一个空格的像素宽
        int numSpaces = (int) Math.ceil(spacing / spaceWidth); // spacing 是你想要的间距 px

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            if (i != limit - 1) {
                for (int s = 0; s < numSpaces; s++) {
                    sb.append(" ");
                }
            }
        }
        return sb;
    }


    private float getTextWidth(CharSequence text) {
        // text 是 CharSequence，比如 SpannableStringBuilder
        TextPaint textPaint = tv1.getPaint();
        float width;

        // StaticLayout 单行测量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout layout = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, Integer.MAX_VALUE)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0, 1)
                    .setIncludePad(false)
                    .build();
            width = 0;
            for (int i = 0; i < layout.getLineCount(); i++) {
                width = Math.max(width, layout.getLineWidth(i));
            }
        } else {
            // 兼容旧版
            StaticLayout layout = new StaticLayout(text, textPaint, Integer.MAX_VALUE,
                    Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
            width = 0;
            for (int i = 0; i < layout.getLineCount(); i++) {
                width = Math.max(width, layout.getLineWidth(i));
            }
        }
        return width;
    }

    private static abstract class SimpleAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(@NonNull Animator animation) {
        }

        @Override
        public void onAnimationEnd(@NonNull Animator animation) {
        }

        @Override
        public void onAnimationCancel(@NonNull Animator animation) {
        }

        @Override
        public void onAnimationRepeat(@NonNull Animator animation) {
        }
    }
}