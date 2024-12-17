package com.demos.widgets.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * by JFZ
 * 2024/12/17
 * desc：
 **/
@SuppressLint("DrawAllocation")
public class LinearGradientTextView extends AppCompatTextView {

    private final Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LinearGradientTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public LinearGradientTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearGradientTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int[] colors = new int[]{Color.parseColor("#F64Dff"), Color.parseColor("#7733FF")};

    private void init() {
        pathPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (TextUtils.isEmpty(getText())) {
            return;
        }
        int lineCount = getLineCount();
        Layout layout = getLayout();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();

        for (int i = 0; i < lineCount; i++) {
            float left = paddingLeft + layout.getLineLeft(i);
            float right = layout.getLineRight(i) + left;
//            float top = paddingTop + layout.getLineTop(i);
//            float bottom = top + layout.getLineBaseline(i) + layout.getLineDescent(i);
//            canvas.drawRect(left, top, right, bottom, paint);

            Path textPath = new Path();
            getPaint().getTextPath(getText().toString(),
                    layout.getLineStart(i),
                    layout.getLineEnd(i),
                    left,
                    layout.getLineBaseline(i) + paddingTop,
                    textPath);
            LinearGradient gradient = new LinearGradient(
                    0, 0, right, 0,
                    colors,
                    null,
                    Shader.TileMode.CLAMP
            );
            pathPaint.setTextSize(getTextSize());
            pathPaint.setStrokeWidth(getPaint().getStrokeWidth());
            pathPaint.setShader(gradient);
            canvas.drawPath(textPath, pathPaint);
        }
    }


    public void setColors(int[] colors) {
        if (colors != null && colors.length > 0) {
            if (colors.length > 1) {
                this.colors = colors;
            } else {
                this.colors = new int[2];
                this.colors[0] = colors[0];
                this.colors[1] = colors[0];
            }
            invalidate();
        }
    }
}
