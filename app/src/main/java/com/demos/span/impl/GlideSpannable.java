package com.demos.span.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.demos.span.core.Span;

import java.util.concurrent.atomic.AtomicReference;


/**
 * author : JFZ
 * date : 2023/8/1 08:41
 * description :
 * 使用Glide加载图片资源
 * <p>
 * 图片垂直对齐方式
 * 图片宽高且保持固定比例, 如果存在占位图会优先使用占位图宽高比
 * 图片水平间距
 * 图片显示文字
 * shape自适应文字
 * 播放GIF动画
 * <p>
 * 默认图片垂直居中对齐文字, 使用[setAlign]可指定
 */
public class GlideSpannable extends ReplacementSpan implements Span.Spannable {
    /**
     * gif循环次数
     */
    private int loopCount = GifDrawable.LOOP_FOREVER;

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

    private RequestOptions requestOption = new RequestOptions();

    private final AtomicReference<Drawable> drawableRef = new AtomicReference<>();

    /**
     * 文字显示区域
     */
    private final Rect textDisplayRect = new Rect();

    /**
     * 图片原始间距
     */
    private final Rect drawableOriginPadding = new Rect();

    /**
     * 初始固定图片显示区域, 优先级: 自定义尺寸 > 占位图尺寸 > 文字尺寸
     */
    private Rect fixDrawableBounds = new Rect();

    private Request request = null;

    private final Rect textOffset = new Rect();
    private int textGravity = Gravity.CENTER;
    private boolean textVisibility = false;
    private int textSize = 0;

    public enum Align {
        BASELINE,
        CENTER,
        BOTTOM
    }

    private Align align = Align.CENTER;

    /**
     * 占位图
     */
    private Drawable getPlaceHolder() {
        Drawable drawable = null;
        try {
            if (requestOption.getPlaceholderDrawable() != null) {
                drawable = requestOption.getPlaceholderDrawable();
            } else {
                drawable = ContextCompat.getDrawable(view.getContext(), requestOption.getPlaceholderId());
            }
        } catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
        if (drawable != null){
            drawable = setFixedRatioZoom(drawable);
        }
        return drawable;
    }

    private final TextView view;
    private final Object url;

    /**
     * GIF动画触发刷新文字的回调
     */
    private final Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            view.postInvalidate();
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

        }

    };

    public GlideSpannable(TextView view, Object url) {
        this.view = view;
        this.url = url;
    }

    private Drawable getDrawable() {
        Request request = this.request;
        if (drawableRef.get() == null && (request == null || request.isComplete())) {
            Rect drawableSize = getDrawableSize();
            this.request = Glide.with(view)
                    .load(url)
                    .apply(requestOption)
                    .into(new CustomTarget<Drawable>(drawableSize.width(), drawableSize.height()) {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (resource instanceof GifDrawable) {
                                resource.setCallback(drawableCallback);
                                ((GifDrawable) resource).setLoopCount(loopCount);
                                ((GifDrawable) resource).start();
                            }
                            if (fixDrawableBounds.isEmpty()) {
                                fixDrawableBounds = getDrawableSize();
                            }
                            resource.setBounds(fixDrawableBounds);
                            drawableRef.set(resource);
                            view.postInvalidate();
                        }

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            if (placeholder != null) {
                                Drawable drawable = setFixedRatioZoom(placeholder);
                                if (drawable != null) {
                                    drawableRef.set(drawable);
                                }
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (errorDrawable != null && errorDrawable != drawableRef.get()) {
                                Drawable drawable = setFixedRatioZoom(errorDrawable);
                                if (drawable != null) {
                                    drawableRef.set(errorDrawable);
                                    view.postInvalidate();
                                }
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    }).getRequest();
        }
        return drawableRef.get();
    }

    /**
     * 设置等比例缩放图片
     */
    private Drawable setFixedRatioZoom(Drawable drawable) {
        if (drawable != null) {
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
            drawable.getPadding(drawableOriginPadding);
            width += drawablePadding.left + drawablePadding.right + drawableOriginPadding.left + drawableOriginPadding.right;
            height += drawablePadding.top + drawablePadding.bottom + drawableOriginPadding.top + drawableOriginPadding.bottom;

            if (drawable instanceof NinePatchDrawable) {
                width = Math.max(width, drawable.getIntrinsicWidth());
                height = Math.max(height, drawable.getIntrinsicHeight());
            }
            drawable.setBounds(new Rect(0, 0, width, height));
            return drawable;
        }
        return null;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (textSize > 0) {
            paint.setTextSize(textSize);
        }
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        if (drawableWidth <= 0 || drawableHeight <= 0) {
            Rect r = new Rect();
            paint.getTextBounds(text.toString(), start, end, r);
            Paint.FontMetricsInt resizeFontMetrics = paint.getFontMetricsInt();
            textDisplayRect.set(0, 0, r.width(), resizeFontMetrics.descent - resizeFontMetrics.ascent);
        }
        Drawable drawable = getDrawable();
        Rect bounds = drawable != null ? drawable.getBounds() : getDrawableSize();
        fixDrawableBounds = bounds;
        int imageHeight = bounds.height();
        if (fm != null) {
            switch (align) {
                case CENTER: {
                    int fontHeight = fontMetrics.descent - fontMetrics.ascent;
                    fm.ascent = fontMetrics.ascent - (imageHeight - fontHeight) / 2 - drawableMargin.top;
                    fm.descent = fm.ascent + imageHeight + drawableMargin.bottom;
                    break;
                }
                case BASELINE: {
                    fm.ascent = fontMetrics.bottom - imageHeight - fontMetrics.descent - drawableMargin.top - drawableMargin.bottom;
                    fm.descent = 0;
                    break;
                }
                case BOTTOM: {
                    fm.ascent = fontMetrics.descent - imageHeight - drawableMargin.top - drawableMargin.bottom;
                    fm.descent = 0;
                    break;
                }
            }
            fm.top = fm.ascent;
            fm.bottom = fm.descent;
        }
        return bounds.right + drawableMargin.left + drawableMargin.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        Rect bounds = drawable != null ? drawable.getBounds() : getDrawableSize();
        float transY;
        if (align == Align.CENTER) {
            transY = (float) (2 * y + paint.getFontMetricsInt().ascent + paint.getFontMetricsInt().descent) / 2 - (float) bounds.bottom / 2 - (float) drawableMargin.height() / 2;
        } else if (align == Align.BASELINE) {
            transY = (float) (bottom - bounds.bottom - paint.getFontMetricsInt().descent - drawableMargin.bottom);
        } else {
            transY = (float) bottom - bounds.bottom - drawableMargin.bottom;
        }
        canvas.translate(x + drawableMargin.left, transY);
        if (drawable != null) {
            drawable.draw(canvas);
        }

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
                // draw text color
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
        canvas.restore();
    }

    /**
     * 默认显示区域
     * 优先使用自定义尺寸, 如果没用配置则使用文字显示区域
     */
    private Rect getDrawableSize() {
        Drawable placeHolder = getPlaceHolder();
        int width;
        if (drawableWidth > 0) {
            width = drawableWidth;
        } else if (drawableWidth == -1) {
            width = textDisplayRect.width();
        } else if (placeHolder != null) {
            width = placeHolder.getIntrinsicWidth();
        } else {
            width = textDisplayRect.width();
        }
        int height;
        if (drawableHeight > 0) {
            height = drawableHeight;
        } else if (drawableHeight == -1) {
            height = textDisplayRect.height();
        } else if (placeHolder != null) {
            height = placeHolder.getIntrinsicHeight();
        } else {
            height = textDisplayRect.height();
        }
        if (placeHolder != null) {
            if (width != placeHolder.getIntrinsicWidth()) {
                width += drawablePadding.left + drawablePadding.right + drawableOriginPadding.left + drawableOriginPadding.right;
            }
            if (height != placeHolder.getIntrinsicHeight()) {
                height += drawablePadding.top + drawablePadding.bottom + drawableOriginPadding.top + drawableOriginPadding.bottom;
            }
        }
        return new Rect(0, 0, width, height);
    }

    @Override
    public String getText() {
        return "[Glide]";
    }

    /**
     * 设置图片垂直对其方式
     * 图片默认垂直居中对齐文字: [Align.CENTER]
     */
    public GlideSpannable setAlign(Align align) {
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
    public GlideSpannable setDrawableSize(int width, int height) {
        this.drawableWidth = width;
        this.drawableHeight = height;
        drawableRef.set(null);
        return this;
    }

    /**
     * 设置图片水平间距
     */
    public GlideSpannable setMarginHorizontal(int left, int right) {
        drawableMargin.left = left;
        drawableMargin.right = right;
        return this;
    }

    /**
     * 设置图片水平间距
     */
    public GlideSpannable setMarginVertical(int top, int bottom) {
        drawableMargin.top = top;
        drawableMargin.bottom = bottom;
        return this;
    }

    /**
     * 设置图片水平内间距
     */
    public GlideSpannable setPaddingHorizontal(int left, int right) {
        drawablePadding.left = left;
        drawablePadding.right = right;
        drawableRef.set(null);
        return this;
    }

    /**
     * 设置图片垂直内间距
     */
    public GlideSpannable setPaddingVertical(int top, int bottom) {
        drawablePadding.top = top;
        drawablePadding.bottom = bottom;
        drawableRef.set(null);
        return this;
    }

    /**
     * 配置Glide请求选项, 例如占位图、加载失败图等
     * 如果使用[RequestOptions.placeholder]占位图会导致默认使用占位图宽高, 除非你使用[setDrawableSize]覆盖默认值
     * <p>
     * 默认会使用[RequestOptions.fitCenterTransform]图片会拉伸显示, 当然你可以覆盖该配置, 比如使用[RequestOptions.centerCropTransform]裁剪显示
     */
    public GlideSpannable setRequestOption(RequestOptions requestOption) {
        this.requestOption = requestOption;
        return this;
    }

    /**
     * GIF动画播放循环次数, 默认无限循环
     */
    public GlideSpannable setLoopCount(int loopCount) {
        this.loopCount = loopCount;
        return this;
    }

    /**
     * 当前为背景图片, 这会导致显示文字内容, 但图片不会根据文字内容自动调整
     *
     * @param visibility 是否显示文字
     */
    public GlideSpannable setTextVisibility(boolean visibility) {
        textVisibility = visibility;
        return this;
    }

    /**
     * 文字偏移值
     */
    public GlideSpannable setTextOffset(int left, int top, int right, int bottom) {
        textOffset.set(left, top, right, bottom);
        return this;
    }

    /**
     * 文字对齐方式(基于图片), 默认对齐方式[Gravity.CENTER]
     *
     * @param gravity 值等效于[TextView.setGravity], 例如[Gravity.BOTTOM], 使用[or]组合多个值
     */
    public GlideSpannable setTextGravity(int gravity) {
        this.textGravity = gravity;
        return this;
    }

    /**
     * 配合[AbsoluteSizeSpan]设置字体大小则图片/文字会基线对齐, 而使用本方法则图片/文字会居中对齐
     *
     * @param size 文字大小, 单位px
     * setTextVisibility()
     */
    public GlideSpannable setTextSize(int size) {
        textSize = size;
        return this;
    }
}
