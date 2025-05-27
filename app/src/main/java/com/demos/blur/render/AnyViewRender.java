package com.demos.blur.render;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.annotation.RequiresApi;

/**
 * by JFZ
 * 2025/5/27
 * desc：任何组件 实时渲染模糊
 **/
@RequiresApi(api = Build.VERSION_CODES.S)
final class AnyViewRender extends BlurRender<View> {

    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = this::updateBlur;

    @Override
    public void onInit() {

        sourceView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
        sourceView.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);

        updateBlur();
    }

    private void updateBlur() {
        if (mActivity == null) return;
        if (blurView == null || sourceView == null) return;
        if (blurView.getVisibility() != View.VISIBLE) return;
        Window window = mActivity.getWindow();
        View view = window.getDecorView();
        if (view.getWidth() == 0 || view.getHeight() == 0) return;
        sourceBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request(
                window,
                sourceBitmap,
                copyResult -> {
                    if (copyResult == PixelCopy.SUCCESS) {
                        blurView.getLocationInWindow(blurLocation);
                        int x = blurLocation[0];
                        int y = blurLocation[1];
                        int width = blurView.getWidth();
                        int height = blurView.getHeight();
                        int safeY = (int) (y + 1);

                        if (safeY < 0) {
                            safeY = 0;
                        }

                        if (safeY > sourceBitmap.getHeight()) {
                            safeY = sourceBitmap.getHeight() - height - 1;
                        }

                        if (config.isValidColor()) {
                            sourceBitmap = applyColorOverlay(sourceBitmap);
                        }

                        // 裁剪重叠部分
                        Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, x, safeY, width, height);
                        if (tagDrawable == null) {
                            tagDrawable = new BitmapDrawable(blurView.getResources(), bitmap);
                            blurView.setBackground(tagDrawable);
                        } else {
                            tagDrawable.setBitmap(bitmap);
                        }
                    }
                },
                new Handler(Looper.getMainLooper())
        );

    }

    Bitmap applyColorOverlay(Bitmap original) {
        Bitmap result = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(original, 0f, 0f, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(config.getBlurColor());
        canvas.drawRect(0f, 0f, original.getWidth(), original.getHeight(), paint);
        return result;
    }


    @Override
    public void onRelease() {
        if (sourceView != null) {
            sourceView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
        }
    }
}
