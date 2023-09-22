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
 * date : 2023/9/21 08:26
 * description :
 */
public class WrapEditText extends AppCompatEditText {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path path = new Path();

    private final LinkedHashMap<Integer, RectF> map = new LinkedHashMap<>();
    private float radius;

    public WrapEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public WrapEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WrapEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

            RectF maxRectF = Collections.max(map.values(), (o1, o2) -> {
                float r1 = o1.width() + getPaddingLeft() + getPaddingRight();
                float r2 = o2.width() + getPaddingLeft() + getPaddingRight();
                return (int) (r1 - r2);
            });

            for (int i = 0; i < lineCount; i++) {
                if (i == 0) {
                    drawFirstLine(canvas, map.get(0), maxRectF, lineCount == 1);
                } else {
                    RectF currentRectF = map.get(i);
                    RectF up = map.get(i - 1);
                    if (up != null) {
                        drawOtherLine(canvas, currentRectF, up, maxRectF, i, lineCount);
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    @SuppressLint("RtlHardcoded")
    private void drawOtherLine(Canvas canvas, RectF rectF, RectF up, RectF maxRectF, int index, int lineCount) {
        int gravity = getGravity();
        boolean gravityLeft = (gravity & Gravity.LEFT) == Gravity.LEFT
                || (gravity & Gravity.START) == Gravity.START;
        boolean gravityRight = (gravity & Gravity.RIGHT) == Gravity.RIGHT
                || (gravity & Gravity.END) == Gravity.END;
        if (gravityLeft) {
            drawOtherLineForGravityLeft(canvas, rectF, up, maxRectF, index, lineCount);
        } else if (gravityRight) {
            maxRectF.right = getWidth();
            rectF.right = getWidth();
            drawOtherLineForGravityRight(canvas, rectF, up, maxRectF, index, lineCount);
        }
    }

    private void drawOtherLineForGravityRight(Canvas canvas, RectF current, RectF up, RectF maxRectF, int index, int lineCount) {
        float maxWidth = getPaddingLeft() + maxRectF.width() + getPaddingRight();
        float currentWidth = getPaddingLeft() + current.width() + getPaddingRight();

        float top = current.top - radius;

        path.reset();
        if (index == lineCount - 1) {
            path.moveTo(maxWidth, top);
            if (currentWidth < maxWidth) {
                float distance = maxWidth - currentWidth;
                if (distance > Math.sqrt(radius * radius + radius * radius)) {
                    path.lineTo(maxWidth - currentWidth, top);
                    path.lineTo(maxWidth - currentWidth - radius, top + radius);
                    path.quadTo(maxWidth - currentWidth, top + radius, maxWidth - currentWidth, top + radius + radius);
                    path.lineTo(maxWidth - currentWidth, current.bottom - radius);
                    path.quadTo(maxWidth - currentWidth, current.bottom, maxWidth - currentWidth + radius, current.bottom);
                } else {
                    path.lineTo(0, top);
                    path.lineTo(0, current.bottom - radius);
                    path.quadTo(0, current.bottom, radius, current.bottom);
                }
            } else {
                path.lineTo(0, top);
                path.lineTo(0, current.bottom - radius);
                path.quadTo(0, current.bottom, radius, current.bottom);
            }
            path.lineTo(maxWidth - radius, current.bottom);
            path.quadTo(maxWidth, current.bottom, maxWidth, current.bottom - radius);
            path.lineTo(maxWidth, top);
        } else {
            path.moveTo(maxRectF.left, top);
            path.lineTo(maxWidth, top);
            path.lineTo(maxWidth, current.bottom);
            path.lineTo(maxRectF.left + radius, current.bottom);
            path.quadTo(maxRectF.left, current.bottom, maxRectF.left, current.bottom - radius);
            path.lineTo(maxRectF.left, top);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawOtherLineForGravityLeft(Canvas canvas, RectF current, RectF up, RectF maxRectF, int index, int lineCount) {
        float maxWidth = getPaddingLeft() + maxRectF.width() + getPaddingRight();
        float currentWidth = getPaddingLeft() + current.width() + getPaddingRight();

        float top = current.top - radius;

        path.reset();
        path.moveTo(current.left, top);

        if (index == lineCount - 1) {
            if (currentWidth < maxWidth) {
                float distance = maxWidth - currentWidth;
                if (distance > Math.sqrt(radius * radius + radius * radius)) {
                    path.lineTo(currentWidth, top);
                    path.lineTo(currentWidth + radius, top + radius);
                    path.quadTo(currentWidth, top + radius, currentWidth, top + radius + radius);
                    path.lineTo(currentWidth, current.bottom - radius);
                    path.quadTo(currentWidth, current.bottom, currentWidth - radius, current.bottom);
                } else {
                    path.lineTo(maxWidth, top);
                    path.lineTo(maxWidth, current.bottom - radius);
                    path.quadTo(maxWidth, current.bottom, maxWidth - radius, current.bottom);
                }
            } else {
                path.lineTo(maxWidth, top);
                path.lineTo(maxWidth, current.bottom - radius);
                path.quadTo(maxWidth, current.bottom, maxWidth - radius, current.bottom);
            }

            path.lineTo(current.left + radius, current.bottom);
            path.quadTo(current.left, current.bottom, current.left, current.bottom - radius);
            path.lineTo(current.left, top);
        } else {
            path.lineTo(maxWidth, top);
            path.lineTo(maxWidth, current.bottom - radius);
            path.quadTo(maxWidth, current.bottom, maxWidth - radius, current.bottom);
            path.lineTo(current.left, current.bottom);
            path.lineTo(current.left, top);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    @SuppressLint("RtlHardcoded")
    private void drawFirstLine(Canvas canvas, RectF rectF, RectF maxRectF, boolean singleLine) {
        int gravity = getGravity();
        boolean gravityLeft = (gravity & Gravity.LEFT) == Gravity.LEFT
                || (gravity & Gravity.START) == Gravity.START;
        boolean gravityRight = (gravity & Gravity.RIGHT) == Gravity.RIGHT
                || (gravity & Gravity.END) == Gravity.END;
        if (gravityLeft) {
            drawFirstLineForGravityLeft(canvas, rectF, maxRectF, singleLine);
        } else if (gravityRight) {
            maxRectF.right = getWidth();
            rectF.right = getWidth();
            drawFirstLineForGravityRight(canvas, rectF, maxRectF, singleLine);
        }
    }

    private void drawFirstLineForGravityRight(Canvas canvas, RectF rectF, RectF maxRectF, boolean singleLine) {
        float width = getPaddingLeft() + maxRectF.width() + getPaddingRight();
        path.reset();
        path.moveTo(maxRectF.left, rectF.top + radius);
        path.quadTo(maxRectF.left, rectF.top, maxRectF.left + radius, rectF.top);
        path.lineTo(width - radius, rectF.top);
        path.quadTo(width, rectF.top, width, rectF.top + radius);
        if (singleLine) {
            path.lineTo(width, rectF.height() - radius);
            path.quadTo(width, rectF.height(), width - radius, rectF.height());
        } else {
            path.lineTo(width, rectF.height());
        }
        path.lineTo(maxRectF.left + radius, rectF.height());
        path.quadTo(maxRectF.left, rectF.height(), maxRectF.left, rectF.height() - radius);
        path.lineTo(maxRectF.left, rectF.top + radius);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawFirstLineForGravityLeft(Canvas canvas, RectF rectF, RectF maxRectF, boolean singleLine) {
        float width = getPaddingLeft() + maxRectF.width() + getPaddingRight();
        path.reset();
        path.moveTo(rectF.left, rectF.top + radius);
        path.quadTo(rectF.left, rectF.top, rectF.left + radius, rectF.top);
        path.lineTo(width - radius, rectF.top);
        path.quadTo(width, rectF.top, width, rectF.top + radius);
        if (singleLine) {
            path.lineTo(width, rectF.height() - radius);
            path.quadTo(width, rectF.height(), width - radius, rectF.height());
            path.lineTo(rectF.left + radius, rectF.height());
            path.quadTo(rectF.left, rectF.height(), rectF.left, rectF.height() - radius);
        } else {
            path.lineTo(width, rectF.height() - radius);
            path.quadTo(width, rectF.height(), width - radius, rectF.height());
            path.lineTo(rectF.left, rectF.height());
        }
        path.lineTo(rectF.left, rectF.top + radius);
        path.close();
        canvas.drawPath(path, paint);
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
