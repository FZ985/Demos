package com.demos.widgets.blurlayout;


import android.graphics.Bitmap;

import androidx.annotation.Nullable;

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
public interface BlurLayer {

    void showLayer();

    void hideLayer();

    void releaseLayer();

    void setRadiusAndScaleFactor(float blurRadius, float scaleFactor);

    @Nullable
    Bitmap getBlurBitmap();

}
