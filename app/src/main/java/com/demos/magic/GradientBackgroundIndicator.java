package com.demos.magic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

/**
 * author : JFZ
 * date : 2023/10/26 08:36
 * description :
 */
public class GradientBackgroundIndicator extends View implements IPagerIndicator {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mInnerRect = new RectF();
    private final RectF mRect = new RectF();

    private int[] colors;

    private List<PositionData> mPositionDataList;

    private float horOffset, verOffset;

    private float dp1;

    private float radius;

    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();

    public GradientBackgroundIndicator(Context context) {
        super(context);
        dp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        horOffset = dp1 * 5;
        verOffset = dp1 * 2;
        radius = dp1 * 10;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (colors != null && colors.length > 1) {
            this.mPaint.setShader(new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(), colors, null, Shader.TileMode.CLAMP));
        }
        canvas.drawRoundRect(mRect, radius, radius, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        mInnerRect.left = current.mContentLeft + (next.mContentLeft - current.mContentLeft) * mStartInterpolator.getInterpolation(positionOffset);
        mInnerRect.top = current.mContentTop + (next.mContentTop - current.mContentTop) * positionOffset;
        mInnerRect.right = current.mContentRight + (next.mContentRight - current.mContentRight) * mEndInterpolator.getInterpolation(positionOffset);
        mInnerRect.bottom = current.mContentBottom + (next.mContentBottom - current.mContentBottom) * positionOffset;

        mRect.set(mInnerRect.left - horOffset,
                mInnerRect.top - verOffset,
                mInnerRect.right + horOffset,
                mInnerRect.bottom + verOffset);

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }

    public GradientBackgroundIndicator setStartInterpolator(Interpolator mStartInterpolator) {
        this.mStartInterpolator = mStartInterpolator;
        return this;
    }

    public GradientBackgroundIndicator setEndInterpolator(Interpolator mEndInterpolator) {
        this.mEndInterpolator = mEndInterpolator;
        return this;
    }

    public GradientBackgroundIndicator setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public GradientBackgroundIndicator setColors(int... colors) {
        if (colors.length < 2) {
            this.colors = new int[2];
            this.colors[0] = colors[0];
            this.colors[1] = colors[0];
        } else this.colors = colors;
        return this;
    }

    public GradientBackgroundIndicator setHorOffset(float horOffset) {
        this.horOffset = horOffset * dp1;
        return this;
    }

    public GradientBackgroundIndicator setVerOffset(float verOffset) {
        this.verOffset = verOffset * dp1;
        return this;
    }
}