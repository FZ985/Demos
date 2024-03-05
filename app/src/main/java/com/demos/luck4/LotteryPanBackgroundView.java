package com.demos.luck4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Size;
import android.view.View;

/**
 * author : JFZ
 * date : 2023/10/23 09:19
 * description :
 */
@SuppressLint("ViewConstructor")
public class LotteryPanBackgroundView extends View {

    private final int[] colors = {Color.GREEN, Color.BLUE};
    float mCurrentAngle;
    int totalNum;

    float itemAngle;

    private final Size screen;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LotteryPanBackgroundView(Context context, float currentAngle, float itemAngle, int totalNum, Size screen) {
        super(context);
        this.mCurrentAngle = currentAngle;
        this.itemAngle = itemAngle;
        this.totalNum = totalNum;
        this.screen = screen;
        mPaint.setAntiAlias(true);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(0, 0, screen.getWidth(), screen.getHeight());
        float angle = mCurrentAngle;
        for (int i = 0; i < totalNum; i++) {
            int currentColor = colors[i % colors.length];
            mPaint.setColor(currentColor);
            canvas.drawArc(rectF, angle, itemAngle, true, mPaint);
            angle = angle + itemAngle;
        }
    }
}
