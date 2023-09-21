package com.demos.widgets.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.LinkedHashMap;

/**
 * author : JFZ
 * date : 2023/9/21 08:26
 * description :
 */
public class RoundWrapTextView extends AppCompatTextView {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path path = new Path();

    private final LinkedHashMap<Integer, RectF> map = new LinkedHashMap<>();
    private float radius;

    private float halfRadius;

    public RoundWrapTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public RoundWrapTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundWrapTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15f, getContext().getResources().getDisplayMetrics());
        halfRadius = Math.min(getPaddingTop(), Math.min(getPaddingBottom(), radius / 2f));
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (!TextUtils.isEmpty(getText())) {
            Layout layout = getLayout();
            int lineCount = layout.getLineCount();
            map.clear();
            for (int i = 0; i < lineCount; i++) {
                float left = layout.getLineLeft(i);
                float top = layout.getLineTop(i);
                float right = layout.getLineRight(i);
                float bottom = layout.getLineBottom(i);
                RectF rectF = new RectF(left, top, right, bottom);
                map.put(i, rectF);
                if (i == 0) {
                    drawFirst(canvas, rectF, lineCount == 1);
                } else {
                    RectF beforeRectF = map.get(i - 1);
                    drawOther(canvas, i, rectF, beforeRectF);
                }
                if (i != 0 && i == lineCount - 1) {
                    drawLast(canvas, i, rectF);
                }
            }
        }
        super.onDraw(canvas);
    }

    //绘制最后一行
    private void drawLast(Canvas canvas, int index, RectF rectF) {
        RectF bound = getBound(rectF);
        path.reset();
        path.moveTo(bound.left, bound.bottom - radius - halfRadius - 1f * index - 1f);
        path.lineTo(bound.left, bound.bottom - radius);
        path.quadTo(bound.left, bound.bottom, bound.left + radius, bound.bottom);
        path.lineTo(bound.right - radius, bound.bottom);
        path.quadTo(bound.right, bound.bottom, bound.right, bound.bottom - radius);
        path.lineTo(bound.right, bound.bottom - radius - halfRadius - 1f * index - 1f);
        path.close();
        canvas.drawPath(path, paint);
    }

    //绘制第一行
    private void drawFirst(Canvas canvas, RectF rectF, boolean singleLine) {
        path.reset();
        RectF bound = getBound(rectF);

        path.moveTo(bound.left, bound.bottom - radius);
        path.lineTo(bound.left, bound.top + radius);
        path.quadTo(bound.left, bound.top, bound.left + radius, bound.top);

        path.lineTo(bound.right - radius, bound.top);
        path.quadTo(bound.right, bound.top, bound.right, bound.top + radius);

        if (singleLine) {
            path.lineTo(bound.right, bound.bottom - radius);
            path.quadTo(bound.right, bound.bottom, bound.right - radius, bound.bottom);
            path.lineTo(bound.left + radius, bound.bottom);
            path.quadTo(bound.left, bound.bottom, bound.left, bound.bottom - radius);
            path.lineTo(bound.left, bound.bottom - radius);
        } else {
            path.lineTo(bound.right, bound.bottom - radius - halfRadius);
            path.lineTo(bound.left, bound.bottom - radius - halfRadius);
        }

        path.close();
        canvas.drawPath(path, paint);
    }

    //绘制其他行
    private void drawOther(Canvas canvas, int index, RectF rectF, RectF before) {
        RectF beforeRect = getBound(before);
        RectF currentRect = getBound(rectF);
        drawOtherLeft(canvas, index, currentRect, beforeRect);
        drawOtherRight(canvas, index, currentRect, beforeRect);
    }

    //绘制左边
    private void drawOtherRight(Canvas canvas, int index, RectF currentRect, RectF beforeRect) {
        path.reset();
        float bottomOffset = radius + halfRadius + 1f * index;
        path.moveTo(beforeRect.right, beforeRect.bottom - bottomOffset);

        float diff = Math.abs(beforeRect.right - currentRect.right);
        float r = radius;

        float currentBottomY = currentRect.bottom - bottomOffset;

        if (diff != 0) {
            if (diff < (radius * 2)) {
                r = diff / 2f;
                path.lineTo(beforeRect.right, beforeRect.bottom - halfRadius - r);
                if (beforeRect.right < currentRect.right) {
                    path.quadTo(beforeRect.right, beforeRect.bottom - halfRadius, beforeRect.right + r, beforeRect.bottom - halfRadius);
                    path.quadTo(currentRect.right, beforeRect.bottom - halfRadius, currentRect.right, beforeRect.bottom - halfRadius + r);
                } else if (beforeRect.right > currentRect.right) {
                    path.quadTo(beforeRect.right, beforeRect.bottom - halfRadius, beforeRect.right - r, beforeRect.bottom - halfRadius);
                    path.quadTo(beforeRect.right - diff, beforeRect.bottom - halfRadius, currentRect.right, beforeRect.bottom - halfRadius + r);
                }
            } else {
                float distance = diff - radius * 2;
                if (beforeRect.right < currentRect.right) {
                    path.quadTo(beforeRect.right, beforeRect.bottom - halfRadius, beforeRect.right + r, beforeRect.bottom - halfRadius);
                    path.lineTo(beforeRect.right + r + distance, beforeRect.bottom - halfRadius);
                    path.quadTo(currentRect.right, beforeRect.bottom - halfRadius, currentRect.right, beforeRect.bottom - halfRadius + r);
                } else {
                    path.quadTo(beforeRect.right, beforeRect.bottom - halfRadius, beforeRect.right - r, beforeRect.bottom - halfRadius);
                    path.lineTo(beforeRect.right - r - distance, beforeRect.bottom - halfRadius);
                    path.quadTo(currentRect.right, beforeRect.bottom - halfRadius, currentRect.right, beforeRect.bottom - halfRadius + r);
                }
            }
        }

        path.lineTo(currentRect.right, currentBottomY);

        int gravity = getGravity();
        if ((gravity & Gravity.LEFT) == Gravity.LEFT || (gravity & Gravity.CENTER) == Gravity.CENTER) {
            if (currentRect.right >= beforeRect.right) {
                path.lineTo(beforeRect.right, currentBottomY);
                path.lineTo(beforeRect.right, beforeRect.bottom - bottomOffset);
            } else {
                path.lineTo(currentRect.centerX() - 2f, currentBottomY);
                path.lineTo(currentRect.centerX() - 2f, beforeRect.bottom - bottomOffset);
            }
        } else {
            path.lineTo(currentRect.right - radius - 2f, currentBottomY);
            path.lineTo(currentRect.right - radius - 2f, beforeRect.bottom - bottomOffset);
        }

        path.close();
        canvas.drawPath(path, paint);
    }

    //绘制左边
    private void drawOtherLeft(Canvas canvas, int index, RectF currentRect, RectF beforeRect) {
        path.reset();
        float bottomOffset = radius + halfRadius + 1f * index;
        path.moveTo(beforeRect.left, beforeRect.bottom - bottomOffset);
        float diff = Math.abs(beforeRect.left - currentRect.left);
        float r = radius;
        float currentBottomY = currentRect.bottom - bottomOffset;

        if (diff != 0) {
            if (diff < (radius * 2)) {
                r = diff / 2f;
                path.lineTo(beforeRect.left, beforeRect.bottom - halfRadius - r);
                if (beforeRect.left < currentRect.left) {
                    path.quadTo(beforeRect.left, beforeRect.bottom - halfRadius, beforeRect.left + r, beforeRect.bottom - halfRadius);
                    path.quadTo(currentRect.left, beforeRect.bottom - halfRadius, currentRect.left, beforeRect.bottom - halfRadius + r);
                } else if (beforeRect.left > currentRect.left) {
                    path.quadTo(beforeRect.left, beforeRect.bottom - halfRadius, beforeRect.left - r, beforeRect.bottom - halfRadius);
                    path.quadTo(beforeRect.left - diff, beforeRect.bottom - halfRadius, currentRect.left, beforeRect.bottom - halfRadius + r);
                }
            } else {
                float distance = diff - radius * 2;
                if (beforeRect.left < currentRect.left) {
                    path.quadTo(beforeRect.left, beforeRect.bottom - halfRadius, beforeRect.left + r, beforeRect.bottom - halfRadius);
                    path.lineTo(beforeRect.left + r + distance, beforeRect.bottom - halfRadius);
                    path.quadTo(currentRect.left, beforeRect.bottom - halfRadius, currentRect.left, beforeRect.bottom - halfRadius + r);
                } else {
                    path.quadTo(beforeRect.left, beforeRect.bottom - halfRadius, beforeRect.left - r, beforeRect.bottom - halfRadius);
                    path.lineTo(beforeRect.left - r - distance, beforeRect.bottom - halfRadius);
                    path.quadTo(currentRect.left, beforeRect.bottom - halfRadius, currentRect.left, beforeRect.bottom - halfRadius + r);
                }
            }
        }

        path.lineTo(currentRect.left, currentBottomY);

        int gravity = getGravity();
        if ((gravity & Gravity.LEFT) == Gravity.LEFT || (gravity & Gravity.CENTER) == Gravity.CENTER) {
            if (currentRect.right >= beforeRect.right) {
                path.lineTo(beforeRect.right, currentBottomY);
                path.lineTo(beforeRect.right, beforeRect.bottom - bottomOffset);
            } else {
                path.lineTo(currentRect.centerX() + 2f, currentBottomY);
                path.lineTo(currentRect.centerX() + 2f, beforeRect.bottom - bottomOffset);
            }
        } else {
            path.lineTo(currentRect.right - radius, currentBottomY);
            path.lineTo(currentRect.right - radius, beforeRect.bottom - bottomOffset);
        }

        path.close();
        canvas.drawPath(path, paint);
    }

    private RectF getBound(RectF rectF) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        float left = rectF.left;
        if (paddingLeft > 0) {
            left = Math.max(rectF.left - paddingLeft, rectF.left);
        }
        float top = rectF.top;
        if (paddingTop > 0) {
            top = Math.max(rectF.top - paddingTop, rectF.top);
        }

        float right = rectF.right + Math.max(paddingLeft, 0);
        if (paddingRight > 0) {
            right = right + paddingRight;
        }

        float bottom = rectF.bottom + Math.max(paddingTop, 0);
        if (paddingBottom > 0) {
            bottom = bottom + paddingBottom;
        }
        return new RectF(left, top, right, bottom);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.halfRadius = Math.min(getPaddingTop(), Math.min(getPaddingBottom(), radius / 2f));
        invalidate();
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    private void log(String m) {
        Log.e("tag", m);
    }

}
