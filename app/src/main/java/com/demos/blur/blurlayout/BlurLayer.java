package com.demos.blur.blurlayout;


import android.graphics.Bitmap;

import androidx.annotation.Nullable;

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
public interface BlurLayer {

    void showBlurLayer();

    void hideBlurLayer();

    void releaseBlurLayer();

    void setRadiusAndScaleFactor(float blurRadius, float scaleFactor);

    @Nullable
    Bitmap getBlurBitmap();

}
