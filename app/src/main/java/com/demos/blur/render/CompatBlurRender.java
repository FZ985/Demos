package com.demos.blur.render;


import android.os.Build;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * by JFZ
 * 2025/5/27
 * desc：
 **/
public final class CompatBlurRender {

    private BlurRender render = null;

    private CompatBlurRender() {
    }

    public static CompatBlurRender get() {
        return new CompatBlurRender();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void bindBlur(AppCompatActivity owner, View blurView, View sourceView, BlurConfig config) {
        if (sourceView instanceof NestedScrollView) {
            render = new NestedScrollViewRender();
        } else if (sourceView instanceof ScrollView) {
            render = new ScrollViewRender();
        } else {
            render = new AnyViewRender();
        }

        render.init(owner, blurView, sourceView, config);
        owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (render != null) {
                    render.release();
                }
            }
        });
    }


}
