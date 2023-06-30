package com.demos.magic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.Arrays;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/30 10:29
 * description : 圆弧指示器
 */
public class ArcPagerIndicator extends View implements IPagerIndicator {

    private List<PositionData> mPositionDataList;

    private Paint arcPaint;
    private final RectF arcRect = new RectF();

    private List<Integer> mColors;

    private int arcWidth, arcHeight;

    private int mYOffset;

    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();

    public ArcPagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeWidth(UIUtil.dip2px(context, 2));
        arcWidth = UIUtil.dip2px(context, 10);
        arcHeight = UIUtil.dip2px(context, 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(arcRect, 0, 180, false, arcPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        // 计算颜色
        if (mColors != null && mColors.size() > 0) {
            int currentColor = mColors.get(Math.abs(position) % mColors.size());
            int nextColor = mColors.get(Math.abs(position + 1) % mColors.size());
            int color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor);
            arcPaint.setColor(color);
        }

        float leftX = current.mLeft + ((float) ((current.width() - arcWidth) / 2));
        float nextLeftX = next.mLeft + ((float) ((next.width() - arcWidth) / 2));
        float rightX = current.mLeft + ((float) ((current.width() + arcWidth) / 2));
        float nextRightX = next.mLeft + ((float) ((next.width() + arcWidth) / 2));

        arcRect.left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
        arcRect.right = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
        arcRect.top = getHeight() - arcHeight - arcPaint.getStrokeWidth() - mYOffset;
        arcRect.bottom = getHeight() - arcPaint.getStrokeWidth() - mYOffset;
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

    public ArcPagerIndicator setColors(Integer... colors) {
        mColors = Arrays.asList(colors);
        return this;
    }

    public ArcPagerIndicator setArcWidth(int arcWidth) {
        this.arcWidth = arcWidth;
        return this;
    }

    public ArcPagerIndicator setArcHeight(int arcHeight) {
        this.arcHeight = arcHeight;
        return this;
    }

    public ArcPagerIndicator setYOffset(int mYOffset) {
        this.mYOffset = mYOffset;
        return this;
    }

    public ArcPagerIndicator setStartInterpolator(Interpolator mStartInterpolator) {
        this.mStartInterpolator = getInterpolator(mStartInterpolator);
        return this;
    }

    public ArcPagerIndicator setEndInterpolator(Interpolator mEndInterpolator) {
        this.mEndInterpolator = getInterpolator(mEndInterpolator);
        return this;
    }

    private Interpolator getInterpolator(Interpolator interpolator) {
        if (interpolator == null) {
            interpolator = new LinearInterpolator();
        }
        return interpolator;
    }
}
