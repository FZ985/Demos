package com.demos.blur.render;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.SizeF;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;

/**
 * by JFZ
 * 2025/5/27
 * desc：ScrollView 实时渲染模糊
 **/
@RequiresApi(api = Build.VERSION_CODES.S)
final class ScrollViewRender extends BlurRender<ScrollView> {

    final View.OnLayoutChangeListener onLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
        if (blurView != null && sourceView != null && blurView.getWidth() > 0 && blurView.getHeight() > 0) {
            updateBlur();
        }
    };


    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = this::updateBlur;

    @Override
    public void onInit() {
        sourceView.removeOnLayoutChangeListener(onLayoutChangeListener);
        sourceView.addOnLayoutChangeListener(onLayoutChangeListener);

        sourceView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
        sourceView.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
        updateBlur();
    }

    private void updateBlur() {
        if (blurView == null || sourceView == null) return;
        if (blurView.getVisibility() != View.VISIBLE) return;
        updateSourceLocation();
        updateSourceSize();
        if (sourceSize.getHeight() <= 0) return;
        if (sourceBitmap == null
                || sourceBitmap.isRecycled()
                || sourceBitmap.getWidth() != sourceSize.getWidth()
                || sourceBitmap.getHeight() != sourceSize.getHeight()) {
            sourceBitmap = Bitmap.createBitmap((int) sourceSize.getWidth(), (int) sourceSize.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(sourceBitmap);
            sourceView.draw(canvas);
            if (config.isValidColor()) {
                canvas.drawColor(config.getBlurColor());
            }
        }

        blurView.getLocationOnScreen(blurLocation);
        int x = blurLocation[0];
        int y = blurLocation[1];
        int width = blurView.getWidth();
        int height = blurView.getHeight();

        int safeY = sourceView.getScrollY() + y - sourceLocation[1];

        if (safeY + height > sourceBitmap.getHeight()) {
            safeY = sourceBitmap.getHeight() - height;
        }

        if (safeY < 0) {
            safeY = 0;
        }

        // 裁剪重叠部分
        Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, x, safeY, width, height);
        Bitmap clipped = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, true);
        if (tagDrawable == null) {
            tagDrawable = new BitmapDrawable(blurView.getResources(), clipped);
            blurView.setBackground(tagDrawable);
        } else {
            tagDrawable.setBitmap(clipped);
        }
    }

    private void updateSourceSize() {
        if (sourceView.getChildCount() == 0) return;
        int w = sourceView.getChildAt(0).getMeasuredWidth();
        int h = sourceView.getChildAt(0).getMeasuredHeight();
        if (sourceSize.getWidth() != w || sourceSize.getHeight() != h) {
            sourceSize = new SizeF(w, h);
        }
    }

    @Override
    public void onRelease() {
        if (sourceView != null) {
            sourceView.removeOnLayoutChangeListener(onLayoutChangeListener);
            sourceView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
        }
    }
}
