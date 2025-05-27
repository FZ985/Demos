package com.demos.blur.render;


import android.graphics.Bitmap;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.util.SizeF;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * by JFZ
 * 2025/5/27
 * desc：模糊 渲染
 **/
@RequiresApi(api = Build.VERSION_CODES.S)
public abstract class BlurRender<T extends View> {
    protected int[] sourceLocation;
    protected SizeF sourceSize = new SizeF(-1, -1);
    protected Bitmap sourceBitmap;
    protected BitmapDrawable tagDrawable;

    protected BlurConfig config = new BlurConfig();

    protected final int[] blurLocation = new int[2];

    protected View blurView;
    protected T sourceView;

    protected AppCompatActivity mActivity;

    final void init(AppCompatActivity activity, View blurView, T sourceView, BlurConfig config) {
        this.mActivity = activity;
        if (blurView != null && sourceView != null) {
            if (config != null) {
                this.config = config;
            }
            this.blurView = blurView;
            this.sourceView = sourceView;
            RenderEffect blurRenderEffect = RenderEffect.createBlurEffect(this.config.getRadius(), this.config.getRadius(),
                    Shader.TileMode.CLAMP
            );
            blurView.setRenderEffect(blurRenderEffect);
            blurView.post(() -> {
                updateSourceLocation();
                onInit();
            });
        }
    }

    public abstract void onInit();


    protected final void updateSourceLocation() {
        if (sourceView == null) return;
        if (sourceLocation == null) {
            sourceLocation = new int[2];
            sourceView.getLocationOnScreen(sourceLocation);
        }
    }


    final void release() {
        sourceLocation = null;
        if (sourceBitmap != null) {
            sourceBitmap = null;
        }
        if (tagDrawable != null) {
            tagDrawable = null;
        }
        onRelease();
    }


    public abstract void onRelease();


    protected final void log(String m) {
        Log.e("blur", m);
    }


}
