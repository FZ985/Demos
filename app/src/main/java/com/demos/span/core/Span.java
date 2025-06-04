package com.demos.span.core;

import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/7/31 09:28
 * description : 富文本的简单使用，（背景色、文字大小、删除线、下划线、加粗、倾斜等）
 */
@SuppressWarnings("unused")
public final class Span {

    private final List<SpanBuilder> builders = new ArrayList<>();

    @ColorInt
    private final int highlightColor = Color.TRANSPARENT;

    private OnSpanClickListener onClickSpanListener;

    public Span() {
        builders.clear();
    }

    public static Span with() {
        return new Span();
    }

    public Span add(SpanBuilder builder) {
        builders.add(builder);
        return this;
    }

    public static SpanBuilder build(String string) {
        return new SpanBuilder(string);
    }

    public static SpanBuilder build(Spannable spannable) {
        return new SpanBuilder(spannable);
    }

    public void into(TextView textview) {
        if (textview != null && !builders.isEmpty()) {
            textview.setText(getSpannable());
            textview.setMovementMethod(SpanLinkMovementMethodImpl.getInstance());
            textview.setHighlightColor(highlightColor);
        }
    }

    public SpannableStringBuilder getSpannable() {
        SpannableStringBuilder string = new SpannableStringBuilder();
        if (!builders.isEmpty()) {
            for (SpanBuilder build : builders) {
                String text = build.getText();
                List<Object> spanList = build.getSpanList();
                if (!TextUtils.isEmpty(text)) {
                    SpannableString span = new SpannableString(text);
                    for (Object style : spanList) {
                        span.setSpan(style, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    if (onClickSpanListener != null) {
                        String totalString = getTotalText(builders);
                        WeakReference<SpanClick> weak = new WeakReference<>(new SpanClick(totalString, build.isUnderLine, onClickSpanListener));
                        span.setSpan(weak.get(), 0, build.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    string.append(span);
                }
            }
            return string;
        }
        return string;
    }

    public Span totalClickListener(OnSpanClickListener listener) {
        this.onClickSpanListener = listener;
        return this;
    }

    private String getTotalText(List<SpanBuilder> builders) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < builders.size(); i++) {
            if (!TextUtils.isEmpty(builders.get(i).getText())) {
                stringBuilder.append(builders.get(i).getText());
            }
        }
        return stringBuilder.toString();
    }

    private static class SpanLinkMovementMethodImpl extends LinkMovementMethod {
        private static SpanLinkMovementMethodImpl instance;

        private SpanLinkMovementMethodImpl() {
        }

        public static SpanLinkMovementMethodImpl getInstance() {
            if (instance == null) {
                instance = new SpanLinkMovementMethodImpl();
            }
            return instance;
        }

        private float x, y;

        private boolean isClick = true;

        @Override
        public boolean onTouchEvent(TextView widget, android.text.Spannable buffer, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                isClick = false;
                x = event.getX();
                y = event.getY();
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float xm = event.getX();
                float ym = event.getY();
                isClick = Math.abs(xm - x) > 15 || Math.abs(ym - y) > 15;
            }
            if (action == MotionEvent.ACTION_UP && !isClick) {
                int xUP = (int) event.getX();
                int yUP = (int) event.getY();
                xUP -= widget.getTotalPaddingLeft();
                yUP -= widget.getTotalPaddingTop();
                xUP += widget.getScrollX();
                yUP += widget.getScrollY();
                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(yUP);
                int off = layout.getOffsetForHorizontal(line, xUP);
                ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);
                if (links.length != 0) {
                    ClickableSpan link = links[0];
                    link.onClick(widget);
                } else {
                    Selection.removeSelection(buffer);
                    ViewParent parent = widget.getParent();
                    if (parent instanceof ViewGroup) {
                        // 获取被点击控件的父容器，让父容器执行点击；
                        ((ViewGroup) parent).performClick();
                    }
                }
                x = 0;
                y = 0;
                isClick = true;
            }
            return true;
        }
    }

    private static class SpanClick extends ClickableSpan {
        private final OnSpanClickListener onClickSpanListener;
        private final String text;
        private final boolean isUnderLine;

        public SpanClick(String text, boolean isUnderLine, OnSpanClickListener onClickSpanListener) {
            this.onClickSpanListener = onClickSpanListener;
            this.text = text;
            this.isUnderLine = isUnderLine;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(isUnderLine);
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (onClickSpanListener != null) {
                onClickSpanListener.onClick(widget, text);
            }
        }
    }

    public interface Spannable {

        String getText();
    }

    public interface OnSpanClickListener {
        void onClick(View widget, String text);
    }

    public final static class SpanBuilder {
        private final String text;
        private final List<Object> spanList = new ArrayList<>();
        boolean isUnderLine = false;

        public SpanBuilder(String string) {
            this.text = string;
            spanList.clear();
        }

        public SpanBuilder(@NonNull Spannable spannable) {
            this.text = spannable.getText();
            spanList.clear();
            addSpan(spannable);
        }

        public SpanBuilder backgroundColor(@ColorInt int color) {
            spanList.add(new BackgroundColorSpan(color));
            return this;
        }

        public SpanBuilder textColor(@ColorInt int color) {
            spanList.add(new ForegroundColorSpan(color));
            return this;
        }

        public SpanBuilder textSize(int dpSize) {
            spanList.add(new AbsoluteSizeSpan(dpSize, true));
            return this;
        }

        public SpanBuilder underLine() {
            isUnderLine = true;
            spanList.add(new UnderlineSpan());
            return this;
        }

        public SpanBuilder deleteLine() {
            spanList.add(new StrikethroughSpan());
            return this;
        }

        public SpanBuilder quoteLine(@ColorInt int color, int stripeWidth, int gapWidth) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spanList.add(new QuoteSpan(color, stripeWidth, gapWidth));
            }
            return this;
        }

        /**
         * 默认: Typeface.NORMAL
         * 加粗: Typeface.BOLD
         * 倾斜: Typeface.ITALIC
         * 加粗并倾斜: Typeface.BOLD_ITALIC
         *
         * @param style 文字样式
         */
        public SpanBuilder textStyle(int style) {
            spanList.add(new StyleSpan(style));
            return this;
        }

        public SpanBuilder addSpan(Object span) {
            spanList.add(span);
            return this;
        }

        public SpanBuilder click(OnSpanClickListener listener) {
            spanList.add(new SpanClick(text, isUnderLine, listener));
            return this;
        }

        String getText() {
            return text;
        }

        List<Object> getSpanList() {
            return spanList;
        }
    }
}
