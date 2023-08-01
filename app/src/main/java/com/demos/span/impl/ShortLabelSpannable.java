package com.demos.span.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.demos.span.core.Span;

/**
 * author : JFZ
 * date : 2023/7/31 11:38
 * description : 短标签样式
 */
public class ShortLabelSpannable extends ReplacementSpan implements Span.Spannable {
    private final Context mContext;
    private final int mBgColorResId; //Icon背景颜色
    private final String mText;  //Icon内文字
    private float mBgHeight;  //Icon背景高度
    private float mBgWidth;  //Icon背景宽度
    private float mRadius;  //Icon圆角半径
    private float mRightMargin; //右边距
    private float mLeftMargin;//左边距

    private float horizontalPadding;//内部边距
    private float mTextSize; //文字大小
    private int textColor = Color.WHITE;

    public ShortLabelSpannable(Context context, @ColorRes int bgColorResId, String text) {
        this.mContext = context.getApplicationContext();
        this.mBgHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17f, mContext.getResources().getDisplayMetrics());
        this.mRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());
        this.mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        this.mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, mContext.getResources().getDisplayMetrics());
        this.mBgColorResId = bgColorResId;
        this.mText = text;
    }

    public ShortLabelSpannable radius(float radius) {
        this.mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, mContext.getResources().getDisplayMetrics());
        return build();
    }

    public ShortLabelSpannable textSize(float textSize) {
        this.mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, mContext.getResources().getDisplayMetrics());
        return build();
    }

    public ShortLabelSpannable textColor(int color) {
        this.textColor = color;
        return build();
    }

    public ShortLabelSpannable horizontalPadding(float horizontalPadding) {
        this.horizontalPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, horizontalPadding, mContext.getResources().getDisplayMetrics());
        return build();
    }

    /**
     * 设置右边距
     */
    public ShortLabelSpannable rightMargin(float rightMargin) {
        this.mRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightMargin, mContext.getResources().getDisplayMetrics());
        return build();
    }

    public ShortLabelSpannable leftMargin(float leftMargin) {
        this.mLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftMargin, mContext.getResources().getDisplayMetrics());
        return build();
    }

    public ShortLabelSpannable bgHeight(float height) {
        this.mBgHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, mContext.getResources().getDisplayMetrics());
        return build();
    }

    private ShortLabelSpannable build() {
        //计算背景的宽度
        this.mBgWidth = calculateBgWidth(mText);
        return this;
    }

    /**
     * 计算icon背景宽度
     *
     * @param text icon内文字
     */
    private float calculateBgWidth(String text) {
        if (text.length() > 1) {
            //多字，宽度=文字宽度+padding
            Rect textRect = new Rect();
            Paint paint = new Paint();
            paint.setTextSize(mTextSize);
            paint.getTextBounds(text, 0, text.length(), textRect);
            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            return textRect.width() + padding * 2;
        } else {
            //单字，宽高一致为正方形
            return mBgHeight;
        }
    }

    /**
     * 设置宽度，宽度=背景宽度+右边距
     */
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) (mBgWidth + mRightMargin + mLeftMargin + horizontalPadding * 2);
    }

    /**
     * draw
     *
     * @param text   完整文本
     * @param start  setSpan里设置的start
     * @param end    setSpan里设置的start
     * @param x      x
     * @param top    当前span所在行的上方y
     * @param y      y其实就是metric里baseline的位置
     * @param bottom 当前span所在行的下方y(包含了行间距)，会和下一行的top重合
     * @param paint  使用此span的画笔
     */
    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //画背景
        Paint bgPaint = new Paint();
        bgPaint.setColor(ContextCompat.getColor(mContext, mBgColorResId));
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        Paint.FontMetrics metrics = paint.getFontMetrics();

        float textHeight = metrics.descent - metrics.ascent;
        //算出背景开始画的y坐标
        float bgStartY = y + (textHeight - mBgHeight) / 2 + metrics.ascent;

        //画背景
        float rLeft = x + mLeftMargin;
        float rTop = bgStartY;
        float rRight = rLeft + mBgWidth + horizontalPadding * 2;
        float rBottom = rTop + mBgHeight;
        RectF bgRect = new RectF(rLeft, rTop, rRight, rBottom);
        canvas.drawRoundRect(bgRect, mRadius, mRadius, bgPaint);

        //把字画在背景中间
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(mTextSize);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);  //这个只针对x有效
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textRectHeight = fontMetrics.bottom - fontMetrics.top;

//        float textX = x + mBgWidth / 2 + mLeftMargin;
        float textX = x + mBgWidth / 2 + mLeftMargin + horizontalPadding;
        float textY = bgStartY + (mBgHeight - textRectHeight) / 2 - fontMetrics.top;
        canvas.drawText(mText, textX, textY, textPaint);
    }

    @Override
    public String getText() {
        return mText;
    }
}
