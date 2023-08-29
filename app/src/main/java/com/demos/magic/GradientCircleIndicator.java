package com.demos.magic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.TypedValue;
import android.view.View;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

/**
 * author : JFZ
 * date : 2023/8/29 10:26
 * description :
 */
public class GradientCircleIndicator extends View implements IPagerIndicator {
    private final RectF mOutRect = new RectF();

    private List<PositionData> mPositionDataList;

    private float dp1;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] colors = {Color.RED, Color.GREEN, Color.BLUE};

    private float maxRadius;

    private float offset;

    private float bottomOffset;

    private final Path path = new Path();

    public GradientCircleIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        dp1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        maxRadius = dp1 * 5;
        offset = dp1 * 3.5f;
        bottomOffset = offset;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        path.reset();

        float radiusY = mOutRect.bottom - maxRadius - bottomOffset;
        //中间圆
        path.addCircle(mOutRect.centerX(), radiusY, maxRadius, Path.Direction.CCW);
        //中间两侧圆
        path.addCircle(mOutRect.centerX() - offset - maxRadius - (maxRadius * 3 / 4), radiusY, (maxRadius * 3 / 4), Path.Direction.CCW);
        path.addCircle(mOutRect.centerX() + offset + maxRadius + (maxRadius * 3 / 4), radiusY, (maxRadius * 3 / 4), Path.Direction.CCW);
        //最外侧圆
        path.addCircle(mOutRect.centerX() - offset * 2 - maxRadius - (maxRadius * 3 / 4) * 2 - (maxRadius * 2 / 4)
                , radiusY, (maxRadius * 2 / 4), Path.Direction.CCW);
        path.addCircle(mOutRect.centerX() + offset * 2 + maxRadius + (maxRadius * 3 / 4) * 2 + (maxRadius * 2 / 4)
                , radiusY, (maxRadius * 2 / 4), Path.Direction.CCW);
        canvas.clipPath(path);

        RectF rectF = new RectF(mOutRect.centerX() - offset * 2 - maxRadius - (maxRadius * 3 / 4) * 2 - (maxRadius * 2 / 4) * 2,
                mOutRect.bottom - bottomOffset - maxRadius * 2,
                mOutRect.centerX() + offset * 2 + maxRadius + (maxRadius * 3 / 4) * 2 + (maxRadius * 2 / 4) * 2,
                mOutRect.bottom);
        paint.setShader(new LinearGradient(rectF.left, rectF.centerY(), rectF.right, rectF.centerY(),
                colors, null, Shader.TileMode.CLAMP));
        canvas.drawRect(rectF, paint);
//        paint.setShader(new LinearGradient(mOutRect.left, mOutRect.centerY(), mOutRect.right, mOutRect.centerY(),
//                colors, null, Shader.TileMode.CLAMP));
//        canvas.drawRect(mOutRect, paint);

        canvas.restore();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        mOutRect.left = current.mLeft + (next.mLeft - current.mLeft) * positionOffset - dp1 * 5;
        mOutRect.top = current.mTop + (next.mTop - current.mTop) * positionOffset;
        mOutRect.right = current.mRight + (next.mRight - current.mRight) * positionOffset + dp1 * 5;
        mOutRect.bottom = current.mBottom + (next.mBottom - current.mBottom) * positionOffset;

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

    public GradientCircleIndicator setColors(int... colors) {
        if (colors.length < 2) {
            this.colors = new int[2];
            this.colors[0] = colors[0];
            this.colors[1] = colors[0];
        } else this.colors = colors;
        return this;
    }

    public GradientCircleIndicator setOffset(float offsetDp) {
        this.offset = offsetDp * dp1;
        return this;
    }

    public GradientCircleIndicator setBottomOffset(float offsetDp) {
        this.bottomOffset = offsetDp * dp1;
        return this;
    }

    public GradientCircleIndicator setMaxRadius(float radiusDp) {
        this.maxRadius = radiusDp * dp1;
        return this;
    }
}
