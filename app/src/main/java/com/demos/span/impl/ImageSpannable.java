package com.demos.span.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.demos.span.core.Span;

import java.lang.ref.WeakReference;


/**
 * author : JFZ
 * date : 2023/8/1 11:30
 * description :
 * 图片垂直对齐方式
 * 图片宽高且保持固定比例
 * 图片水平间距
 * 图片显示文字
 * shape自适应文字
 * .9PNG自适应文字, 若图片模糊请关闭硬件加速(机型设备问题)
 * <p>
 * 默认图片垂直居中对齐文字, 使用[setAlign]可指定
 * <p>
 * 需应对更复杂的图片加载需求请使用[GlideSpannable], 例如高斯模糊,棱形等效果
 */
public class ImageSpannable extends ImageSpan implements Span.Spannable {

    private final String text;
    /**
     * 图片宽度
     */
    private int drawableWidth = 0;

    /**
     * 图片高度
     */
    private int drawableHeight = 0;

    /**
     * 图片间距
     */
    private final Rect drawableMargin = new Rect();

    /**
     * 图片内间距
     */
    private final Rect drawablePadding = new Rect();

    private WeakReference<android.graphics.drawable.Drawable> drawableRef;

    /**
     * 文字显示区域
     */
    private final Rect textDisplayRect = new Rect();

    /**
     * 图片原始间距
     */
    private final Rect drawableOriginPadding = new Rect();

    @Override
    public Drawable getDrawable() {
        if (drawableRef != null && drawableRef.get() != null) {
            return drawableRef.get();
        }
        Drawable drawable = setFixedRatioZoom(super.getDrawable());
        drawableRef = new WeakReference<>(drawable);
        return drawable;
    }

    /**
     * 设置等比例缩放图片
     */
    private Drawable setFixedRatioZoom(Drawable drawable) {
        double ratio = (double) drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
        int width;
        if (drawableWidth > 0) {
            width = drawableWidth;
        } else if (drawableWidth == -1) {
            width = textDisplayRect.width();
        } else {
            width = drawable.getIntrinsicWidth();
        }
        int height;
        if (drawableHeight > 0) {
            height = drawableHeight;
        } else if (drawableHeight == -1) {
            height = textDisplayRect.height();
        } else {
            height = drawable.getIntrinsicHeight();
        }

        if (drawableWidth != -1 && drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
            height = (int) (width / ratio);
        } else if (drawableHeight != -1 && drawable.getIntrinsicWidth() < drawable.getIntrinsicHeight()) {
            width = (int) (height * ratio);
        }

        drawable.getPadding(drawableOriginPadding);
        width += drawablePadding.left + drawablePadding.right + drawableOriginPadding.left + drawableOriginPadding.right;
        height += drawablePadding.top + drawablePadding.bottom + drawableOriginPadding.top + drawableOriginPadding.bottom;

        if (drawable instanceof NinePatchDrawable) {
            width = Math.max(width, drawable.getIntrinsicWidth());
            height = Math.max(height, drawable.getIntrinsicHeight());
        }
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    public ImageSpannable(String text, Drawable drawable) {
        super(drawable);
        this.text = text;
    }

    public ImageSpannable(Drawable drawable, String source) {
        super(drawable, source);
        this.text = source;
    }

    public ImageSpannable(Context context, String text, Uri uri) {
        super(context, uri);
        this.text = text;
    }

    public ImageSpannable(Context context, String text, int resourceId) {
        super(context, resourceId);
        this.text = text;
    }

    public ImageSpannable(Context context, String text, Bitmap bitmap) {
        super(context, bitmap);
        this.text = text;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (textSize > 0) {
            paint.setTextSize(textSize);
        }
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        if (drawableWidth == -1 || drawableHeight == -1) {
            Rect r = new Rect();
            paint.getTextBounds(text.toString(), start, end, r);
            Paint.FontMetricsInt resizeFontMetrics = paint.getFontMetricsInt();
            textDisplayRect.set(0, 0, r.width(), resizeFontMetrics.descent - resizeFontMetrics.ascent);
        }
        Rect bounds = getDrawable().getBounds();
        int imageHeight = bounds.height();
        if (fm != null) {
            if (align == Align.CENTER) {
                int fontHeight = fontMetrics.descent - fontMetrics.ascent;
                fm.ascent = fontMetrics.ascent - (imageHeight - fontHeight) / 2 - drawableMargin.top;
                fm.descent = fm.ascent + imageHeight + drawableMargin.bottom;
            } else if (align == Align.BASELINE) {
                fm.ascent = fontMetrics.descent - imageHeight - fontMetrics.descent - drawableMargin.top - drawableMargin.bottom;
                fm.descent = 0;
            } else {
                fm.ascent = fontMetrics.descent - imageHeight - drawableMargin.top - drawableMargin.bottom;
                fm.descent = 0;
            }
            fm.top = fm.ascent;
            fm.bottom = fm.descent;
        }
        return bounds.right + drawableMargin.left + drawableMargin.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.save();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            float transY;
            if (align == Align.CENTER) {
                transY = (float) (2 * y + paint.getFontMetricsInt().ascent + paint.getFontMetricsInt().descent) / 2 - (float) bounds.bottom / 2 - (float) drawableMargin.height() / 2;
            } else if (align == Align.BASELINE) {
                transY = top - drawableMargin.bottom;
            } else {
                transY = top - drawableMargin.bottom;
            }
            canvas.translate(x + drawableMargin.left, transY);
            drawable.draw(canvas);

            // draw text
            if (textVisibility) {
                canvas.translate(-drawablePadding.width() / 2F - drawableOriginPadding.right, -drawablePadding.height() / 2F + drawableOriginPadding.top);
                float textWidth = paint.measureText(text, start, end);
                Rect textDrawRect = new Rect();
                Rect textContainerRect = new Rect(bounds);
                Gravity.apply(
                        textGravity,
                        (int) textWidth,
                        (int) paint.getTextSize(),
                        textContainerRect,
                        textDrawRect
                );
                if (text instanceof Spanned) {
                    ForegroundColorSpan[] spans = ((Spanned) text).getSpans(start, end, ForegroundColorSpan.class);
                    if (spans != null && spans.length > 0) {
                        paint.setColor(spans[spans.length - 1].getForegroundColor());
                    }
                }

                canvas.drawText(
                        text, start, end,
                        (textDrawRect.left + textOffset.left - textOffset.right) + (float) (drawableOriginPadding.right + drawableOriginPadding.left) / 2,
                        (textDrawRect.bottom - (float) paint.getFontMetricsInt().descent / 2 + textOffset.top - textOffset.bottom) - (float) (drawableOriginPadding.bottom + drawableOriginPadding.top) / 2,
                        paint
                );
            }
        }
        canvas.restore();
    }

    public enum Align {
        BASELINE,
        CENTER,
        BOTTOM
    }

    private Align align = Align.CENTER;

    /**
     * 设置图片垂直对其方式
     * 图片默认垂直居中对齐文字: [Align.CENTER]
     */
    public ImageSpannable setAlign(Align align) {
        this.align = align;
        return this;
    }

    /**
     * 设置图片宽高
     * 如果指定大于零值则会基于图片宽高中最大值然后根据宽高比例固定缩放图片
     *
     * @param width  指定图片宽度, -1 使用文字宽度, 0 使用图片原始宽度
     * @param height 指定图片高度, -1 使用文字高度, 0 使用图片原始高度
     */
    public ImageSpannable setDrawableSize(int width, int height) {
        this.drawableWidth = width;
        this.drawableHeight = height;
        if (drawableRef != null) {
            drawableRef.clear();
        }
        return this;
    }

    /**
     * 设置图片水平间距
     */
    public ImageSpannable setMarginHorizontal(int left, int right) {
        drawableMargin.left = left;
        drawableMargin.right = right;
        return this;
    }

    /**
     * 设置图片水平间距
     */
    public ImageSpannable setMarginVertical(int top, int bottom) {
        drawableMargin.top = top;
        drawableMargin.bottom = bottom;
        return this;
    }

    /**
     * 设置图片水平内间距
     */
    public ImageSpannable setPaddingHorizontal(int left, int right) {
        drawablePadding.left = left;
        drawablePadding.right = right;
        if (drawableRef != null) {
            drawableRef.clear();
        }
        return this;
    }

    /**
     * 设置图片垂直内间距
     */
    public ImageSpannable setPaddingVertical(int top, int bottom) {
        drawablePadding.top = top;
        drawablePadding.bottom = bottom;
        if (drawableRef != null) {
            drawableRef.clear();
        }
        return this;
    }

    private Rect textOffset = new Rect();
    private int textGravity = Gravity.CENTER;
    private boolean textVisibility = false;
    private int textSize = 0;

    /**
     * 当前为背景图片, 这会导致显示文字内容, 但图片不会根据文字内容自动调整
     *
     * @param visibility 是否显示文字
     */
    public ImageSpannable setTextVisibility(boolean visibility) {
        this.textVisibility = visibility;
        return this;
    }

    /**
     * 文字偏移值
     */
    public ImageSpannable setTextOffset(int left, int top, int right, int bottom) {
        textOffset.set(left, top, right, bottom);
        return this;
    }

    /**
     * 文字对齐方式(基于图片), 默认对齐方式[Gravity.CENTER]
     *
     * @param gravity 值等效于[TextView.setGravity], 例如[Gravity.BOTTOM], 使用[or]组合多个值
     */
    public ImageSpannable setTextGravity(int gravity) {
        textGravity = gravity;
        return this;
    }

    /**
     * 配合[AbsoluteSizeSpan]设置字体大小则图片/文字会基线对齐, 而使用本方法则图片/文字会居中对齐
     *
     * @param size 文字大小, 单位px
     *             see setTextVisibility
     */
    public ImageSpannable setTextSize(int size) {
        textSize = size;
        return this;
    }

    @Override
    public String getText() {
        return text;
    }
}
