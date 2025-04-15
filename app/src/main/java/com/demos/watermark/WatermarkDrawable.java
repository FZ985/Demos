package com.demos.watermark;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * by JFZ
 * 2025/3/13
 * desc：
 **/
public class WatermarkDrawable extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final String watermarkText;
    private int backgroundColor = Color.TRANSPARENT;
    private int xOffset = 100;
    private int yOffset = 100;


    public WatermarkDrawable(String text) {
        this.watermarkText = text;
        paint.setColor(Color.parseColor("#999999"));
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAlpha(100); // 设置透明度，0-255
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        int textWidth = (int) paint.measureText(watermarkText);
        int textHeight = (int) (paint.descent() - paint.ascent());
        canvas.save();
        canvas.drawColor(backgroundColor);
        canvas.rotate(-30, width / 2f, height / 2f); // 旋转 -30°，你可以调整角度

        for (int y = -height; y < height * 2; y += textHeight + yOffset) { // 控制行间距
            for (int x = -width; x < width * 2; x += textWidth + xOffset) { // 控制列间距
                canvas.drawText(watermarkText, x, y, paint);
            }
        }

        canvas.restore();
    }


    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setTextSize(int textSize) {
        paint.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        paint.setColor(textColor);
    }
}


