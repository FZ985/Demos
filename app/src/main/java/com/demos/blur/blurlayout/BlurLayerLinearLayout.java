package com.demos.blur.blurlayout;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
public class BlurLayerLinearLayout extends LinearLayout implements BlurLayer {

    private final BlurLayer helper = new BlurLayerHelper();


    public BlurLayerLinearLayout(Context context) {
        super(context);
        ((BlurLayerHelper) helper).setView(this);
    }

    public BlurLayerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ((BlurLayerHelper) helper).setView(this);
    }

    public BlurLayerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((BlurLayerHelper) helper).setView(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean showLayer = ((BlurLayerHelper) helper).isShowLayer();
        if (showLayer) return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        ((BlurLayerHelper) helper).dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ((BlurLayerHelper) helper).onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void showBlurLayer() {
        helper.showBlurLayer();
    }

    @Override
    public void hideBlurLayer() {
        helper.hideBlurLayer();
    }

    @Override
    public void releaseBlurLayer() {
        helper.releaseBlurLayer();
    }

    @Override
    public void setRadiusAndScaleFactor(float blurRadius, float scaleFactor) {
        helper.setRadiusAndScaleFactor(blurRadius, scaleFactor);
    }

    @Nullable
    @Override
    public Bitmap getBlurBitmap() {
        return helper.getBlurBitmap();
    }

    @Override
    protected void onDetachedFromWindow() {
        helper.releaseBlurLayer();
        super.onDetachedFromWindow();
        ((BlurLayerHelper) helper).setView(null);
    }
}
