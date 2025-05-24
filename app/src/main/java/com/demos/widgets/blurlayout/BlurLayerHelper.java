package com.demos.widgets.blurlayout;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.demos.utils.BlurUtil;

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
public class BlurLayerHelper implements BlurLayer {
    private Bitmap bitmap;
    private View view;
    private float blurRadius = 50f;
    private float scaleFactor = 0.8f;
    private boolean showLayer = false;

    public void setView(View view) {
        this.view = view;
    }

    public final void dispatchDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            bitmap.recycle();
        }
    }

    public final void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (showLayer) {
            releaseLayer();
            bitmap = view2Bitmap();
        }
    }

    @Override
    public synchronized final void showLayer() {
        releaseLayer();
        if (view == null) return;
        if (view.getWidth() == 0 && view.getMeasuredWidth() == 0) {
            view.post(() -> {
                showLayer = true;
                bitmap = view2Bitmap();
                view.invalidate();
            });
        } else {
            showLayer = true;
            bitmap = view2Bitmap();
            view.invalidate();
        }
    }

    @Override
    public final void hideLayer() {
        releaseLayer();
        if (view == null) return;
        view.invalidate();
    }

    @Override
    public void releaseLayer() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        showLayer = false;
    }

    public boolean isShowLayer() {
        return showLayer;
    }

    private Bitmap view2Bitmap() {
        if (view == null) {
            return null;
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bmp = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas c = new Canvas(bmp);
        view.draw(c);
        return BlurUtil.blurBitmap(view.getContext(), bmp, blurRadius, scaleFactor, true);
    }

    @Override
    public void setRadiusAndScaleFactor(float blurRadius, float scaleFactor) {
        this.blurRadius = blurRadius;
        this.scaleFactor = scaleFactor;
        if (showLayer) {
            showLayer();
        }
    }

    @Nullable
    @Override
    public Bitmap getBlurBitmap() {
        return bitmap;
    }

    @Deprecated
    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        // Reset the drawing cache background colors to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Folder", "failed getViewBitmap(" + v + ")",
                    new RuntimeException());
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }
}
