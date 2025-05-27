package com.demos.blur.render;


import android.graphics.Color;

/**
 * by JFZ
 * 2025/5/27
 * desc：
 **/
public final class BlurConfig {
    private float radius = 50f;

    private boolean drawBlurColor = false;

    private int blurColor = 0x7FFFFFFF;

    public boolean isValidColor() {
        int alpha = Color.alpha(blurColor); // 0-255
        int red = Color.red(blurColor);     // 0-255
        int green = Color.green(blurColor); // 0-255
        int blue = Color.blue(blurColor);   // 0-255

        return drawBlurColor &&
                (alpha >= 0 && alpha <= 255) &&
                (red >= 0 && red <= 255) &&
                (green >= 0 && green <= 255) &&
                (blue >= 0 && blue <= 255);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isDrawBlurColor() {
        return drawBlurColor;
    }

    public void setDrawBlurColor(boolean drawBlurColor) {
        this.drawBlurColor = drawBlurColor;
    }

    public int getBlurColor() {
        return blurColor;
    }

    public void setBlurColor(int blurColor) {
        this.blurColor = blurColor;
    }
}
