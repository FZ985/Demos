package com.demos.magic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/30 17:48
 * description :
 */
public class OneHalfLinePagerIndicator extends View implements IPagerIndicator {
    private Paint mPaint;
    private final RectF mInnerRect = new RectF();
    private final RectF mRect = new RectF();

    private List<PositionData> mPositionDataList;

    public OneHalfLinePagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRect.set(mInnerRect.centerX() - 6, mInnerRect.bottom - mInnerRect.height() * 3 / 5, mInnerRect.right + 6, mInnerRect.bottom);
        canvas.drawRoundRect(mRect, mRect.height() / 2, mRect.height() / 2, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        mInnerRect.left = current.mContentLeft + (next.mContentLeft - current.mContentLeft) * positionOffset;
        mInnerRect.top = current.mContentTop + (next.mContentTop - current.mContentTop) * positionOffset;
        mInnerRect.right = current.mContentRight + (next.mContentRight - current.mContentRight) * positionOffset;
        mInnerRect.bottom = current.mContentBottom + (next.mContentBottom - current.mContentBottom) * positionOffset;

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

    public OneHalfLinePagerIndicator setColor(int color) {
        mPaint.setColor(color);
        return this;
    }
}
