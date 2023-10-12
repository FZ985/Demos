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
import androidx.appcompat.widget.AppCompatEditText;

import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * author : JFZ
 * date : 2023/9/23 08:26
 * description :
 */
public class AppCompatWrapEditText extends AppCompatEditText {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path path = new Path();

    private final LinkedHashMap<Integer, RectF> map = new LinkedHashMap<>();
    private float radius;

    public AppCompatWrapEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public AppCompatWrapEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppCompatWrapEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10f, getContext().getResources().getDisplayMetrics());
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
    }

    @SuppressLint("DrawAllocation,RtlHardcoded")
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
                if (i == 0) {
                    RectF rectF = new RectF(left, top, right, bottom + getPaddingTop() + getPaddingBottom());
                    map.put(i, rectF);
                } else {
                    RectF up = map.get(i - 1);
                    if (up != null) {
                        RectF rectF = new RectF(left, up.bottom, right, up.bottom + (bottom - top));
                        map.put(i, rectF);
                    }
                }
            }

            int gravity = getGravity();

            boolean gravityLeft = (gravity & Gravity.LEFT) == Gravity.LEFT
                    || (gravity & Gravity.START) == Gravity.START;

            boolean gravityRight = (gravity & Gravity.RIGHT) == Gravity.RIGHT
                    || (gravity & Gravity.END) == Gravity.END;

            boolean singleLine = (lineCount == 1);
            path.reset();
            if (singleLine) {
                RectF currentRect = map.get(0);
                if (currentRect != null) {
                    drawGravitySingleLine(currentRect);
                }
            } else {
                RectF maxRectF = Collections.max(map.values(), (o1, o2) -> {
                    float r1 = o1.width() + getPaddingLeft() + getPaddingRight();
                    float r2 = o2.width() + getPaddingLeft() + getPaddingRight();
                    return (int) (r1 - r2);
                });
                if (gravityLeft) {
                    //先绘制左边
                    for (int i = 0; i < lineCount; i++) {
                        boolean isLast = (i == lineCount - 1);
                        RectF currentRect = map.get(i);
                        drawGravityLeftForLeft(currentRect, isLast, i);
                    }
                    //最后绘制右边
                    for (int i = lineCount - 1; i >= 0; i--) {
                        boolean isLast = (i == lineCount - 1);
                        RectF currentRect = map.get(i);
                        drawGravityLeftForRight(currentRect, maxRectF, isLast, i);
                    }
                } else if (gravityRight) {
                    //先绘制右边
                    for (int i = 0; i < lineCount; i++) {
                        boolean isLast = (i == lineCount - 1);
                        RectF currentRect = map.get(i);
                        drawGravityRightForRight(currentRect, maxRectF, isLast, i);
                    }
                    //最后绘制左边
                    for (int i = lineCount - 1; i >= 0; i--) {
                        boolean isLast = (i == lineCount - 1);
                        RectF currentRect = map.get(i);
                        drawGravityRightForLeft(currentRect, maxRectF, isLast, i);
                    }
                }
            }
            path.close();
            canvas.drawPath(path, paint);
        }
        super.onDraw(canvas);
    }

    private void drawGravityRightForLeft(RectF currentRect, RectF maxRectF, boolean isLast, int index) {
        float maxWidth = maxRectF.width() + getPaddingLeft() + getPaddingRight();
        float left = maxRectF.left;
        if (isLast) {
            float width = currentRect.width() + getPaddingLeft() + getPaddingRight();
            float lastLeft = getWidth() - width;
            path.lineTo(lastLeft + radius, currentRect.bottom);
            if (width < maxWidth) {
                float distance = maxWidth - width;
                if (distance > Math.sqrt(radius * radius + radius * radius)) {
                    path.quadTo(lastLeft, currentRect.bottom, lastLeft, currentRect.bottom - radius);
                    path.lineTo(lastLeft, currentRect.top + radius);
                    path.quadTo(lastLeft, currentRect.top, lastLeft - radius, currentRect.top);
                    path.lineTo(left + radius, currentRect.top);
                    path.quadTo(left, currentRect.top, left, currentRect.top - radius);
                } else {
                    path.lineTo(left + radius, currentRect.bottom);
                    path.quadTo(left, currentRect.bottom, left, currentRect.bottom - radius);
                    path.lineTo(left, currentRect.top);
                }
            } else {
                path.quadTo(left, currentRect.bottom, left, currentRect.bottom - radius);
                path.lineTo(left, currentRect.top);
            }
        } else {
            if (index == 0) {
                path.lineTo(left, currentRect.top + radius);
                path.quadTo(left, currentRect.top, left + radius, currentRect.top);
            } else {
                path.lineTo(left, currentRect.top);
            }
        }
    }

    private void drawGravityRightForRight(RectF currentRect, RectF maxRectF, boolean isLast, int index) {
        float maxWidth = getWidth();
        if (index == 0) {
            path.moveTo(maxWidth - radius, currentRect.top);
            path.quadTo(maxWidth, currentRect.top, maxWidth, currentRect.top + radius);
            path.lineTo(maxWidth, currentRect.bottom);
        } else {
            if (isLast) {
                path.lineTo(maxWidth, currentRect.bottom - radius);
                path.quadTo(maxWidth, currentRect.bottom, maxWidth - radius, currentRect.bottom);
                path.lineTo(currentRect.centerX(), currentRect.bottom);
            } else {
                path.lineTo(maxWidth, currentRect.bottom);
            }
        }
    }

    private void drawGravityLeftForRight(RectF currentRect, RectF maxRectF, boolean isLast, int index) {
        float maxWidth = maxRectF.width() + getPaddingLeft() + getPaddingRight();
        if (isLast) {
            float width = currentRect.width() + getPaddingLeft() + getPaddingRight();
            path.lineTo(width - radius, currentRect.bottom);
            if (width < maxWidth) {
                float distance = maxWidth - width;
                if (distance > Math.sqrt(radius * radius + radius * radius)) {
                    path.quadTo(width, currentRect.bottom, width, currentRect.bottom - radius);
                    path.lineTo(width, currentRect.top + radius);
                    path.quadTo(width, currentRect.top, width + radius, currentRect.top);
                    path.lineTo(maxWidth - radius, currentRect.top);
                    path.quadTo(maxWidth, currentRect.top, maxWidth, currentRect.top - radius);
                } else {
                    path.lineTo(maxWidth - radius, currentRect.bottom);
                    path.quadTo(maxWidth, currentRect.bottom, maxWidth, currentRect.bottom - radius);
                    path.lineTo(maxWidth, currentRect.top);
                }
            } else {
                path.quadTo(maxWidth, currentRect.bottom, maxWidth, currentRect.bottom - radius);
                path.lineTo(maxWidth, currentRect.top);
            }
        } else {
            if (index == 0) {
                path.lineTo(maxWidth, currentRect.top + radius);
                path.quadTo(maxWidth, currentRect.top, maxWidth - radius, currentRect.top);
            } else {
                path.lineTo(maxWidth, currentRect.top);
            }
        }
    }

    private void drawGravitySingleLine(RectF currentRect) {
        float width = currentRect.width() + getPaddingLeft() + getPaddingRight();
        path.moveTo(currentRect.centerX(), currentRect.top);
        path.lineTo(currentRect.left + radius, currentRect.top);
        path.quadTo(currentRect.left, currentRect.top, currentRect.left, currentRect.top + radius);
        path.lineTo(currentRect.left, currentRect.bottom - radius);
        path.quadTo(currentRect.left, currentRect.bottom, currentRect.left + radius, currentRect.bottom);
        path.lineTo(width - radius, currentRect.bottom);
        path.quadTo(width, currentRect.bottom, width, currentRect.bottom - radius);
        path.lineTo(width, currentRect.top + radius);
        path.quadTo(width, currentRect.top, width - radius, currentRect.top);
        path.lineTo(currentRect.centerX(), currentRect.top);
    }

    private void drawGravityLeftForLeft(RectF currentRect,
                                        boolean isLast, int index) {
        if (index == 0) {
            path.moveTo(currentRect.left + radius, currentRect.top);
            path.quadTo(currentRect.left, currentRect.top, currentRect.left, currentRect.top + radius);
            path.lineTo(currentRect.left, currentRect.bottom);
        } else {
            if (isLast) {
                path.lineTo(currentRect.left, currentRect.bottom - radius);
                path.quadTo(currentRect.left, currentRect.bottom, currentRect.left + radius, currentRect.bottom);
                path.lineTo(currentRect.centerX(), currentRect.bottom);
            } else {
                path.lineTo(currentRect.left, currentRect.bottom);
            }
        }
    }

    public void setRadius(float radius) {
        this.radius = radius;
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
