package com.demos;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

/**
 * Description: 代码实现对 控件的 描边、渐变、圆角、水波纹
 * Author: jfz
 * Date: 2021-03-01 10:08
 */
public class Ripple {
    private final Context mContext;
    private final int[] defaultColors = {Color.parseColor("#50FFFFFF"), Color.parseColor("#50FFFFFF")};
    private int[] colors;
    private int[] textColors;
    private float[] radius = {0, 0, 0, 0, 0, 0, 0, 0};
    private GradientDrawable.Orientation orientation = GradientDrawable.Orientation.LEFT_RIGHT;
    private int strokeWidth;
    private int strokeColor = -1;
    private float strokeDashWidth;
    private float strokeDashGap;
    private int rippleNormalColor = -1;
    private int ripplePressColor = -1;

    private Ripple(Context context) {
        this.mContext = context;
    }

    public static Ripple with(Context context) {
        return new Ripple(context.getApplicationContext());
    }

    public Ripple stroke(int strokeWidth, String color) {
        Ripple.this.strokeWidth = (int) dip2px(strokeWidth);
        Ripple.this.strokeColor = Color.parseColor(color);
        return this;
    }

    public Ripple stroke(int strokeWidth, @ColorRes int color) {
        Ripple.this.strokeWidth = (int) dip2px(strokeWidth);
        Ripple.this.strokeColor = ContextCompat.getColor(mContext, color);
        return this;
    }

    public Ripple strokeDash(float strokeDashWidth, float strokeDashGap) {
        Ripple.this.strokeDashWidth = dip2px(strokeDashWidth);
        Ripple.this.strokeDashGap = dip2px(strokeDashGap);
        return this;
    }

    public Ripple orientation(GradientDrawable.Orientation orientation) {
        Ripple.this.orientation = orientation;
        return this;
    }

    public Ripple topLeftRadius(float radius) {
        float ra = dip2px(radius);
        Ripple.this.radius[0] = ra;
        Ripple.this.radius[1] = ra;
        return this;
    }

    public Ripple topRightRadius(float radius) {
        float ra = dip2px(radius);
        Ripple.this.radius[2] = ra;
        Ripple.this.radius[3] = ra;
        return this;
    }

    public Ripple bottomRightRadius(float radius) {
        float ra = dip2px(radius);
        Ripple.this.radius[4] = ra;
        Ripple.this.radius[5] = ra;
        return this;
    }

    public Ripple bottomLeftRadius(float radius) {
        float ra = dip2px(radius);
        Ripple.this.radius[6] = ra;
        Ripple.this.radius[7] = ra;
        return this;
    }

    public Ripple radius(float radius) {
        float ra = dip2px(radius);
        Ripple.this.radius = new float[]{ra, ra, ra, ra, ra, ra, ra, ra};
        return this;
    }

    public Ripple textColors(String... color) {
        if (checkParams(color) && color.length > 0) {
            int[] c = new int[color.length];
            for (int i = 0; i < color.length; i++) {
                c[i] = Color.parseColor(color[i]);
            }
            textColors = getColors(c);
        }
        return this;
    }

    public Ripple textColors(@ColorRes int... color) {
        if (checkParams(color) && color.length > 0) {
            int[] c = new int[color.length];
            for (int i = 0; i < color.length; i++) {
                c[i] = ContextCompat.getColor(mContext, color[i]);
            }
            textColors = getColors(c);
        }
        return this;
    }

    public Ripple colors(String... color) {
        if (checkParams(color) && color.length > 0) {
            int[] c = new int[color.length];
            for (int i = 0; i < color.length; i++) {
                c[i] = Color.parseColor(color[i]);
            }
            colors = getColors(c);
        }
        return this;
    }

    public Ripple colors(@ColorRes int... color) {
        if (checkParams(color) && color.length > 0) {
            int[] c = new int[color.length];
            for (int i = 0; i < color.length; i++) {
                c[i] = ContextCompat.getColor(mContext, color[i]);
            }
            colors = getColors(c);
        }
        return this;
    }

    public Ripple rippleColors(String... color) {
        if (checkParams(color) && color.length > 0) {
            int[] c = new int[color.length];
            for (int i = 0; i < color.length; i++) {
                c[i] = Color.parseColor(color[i]);
            }
            int[] colors = getColors(c);
            setRippleColor(colors);
        }
        return this;
    }

    public Ripple rippleColors(@ColorRes int... color) {
        if (checkParams(color) && color.length > 0) {
            int[] c = new int[color.length];
            for (int i = 0; i < color.length; i++) {
                c[i] = ContextCompat.getColor(mContext, color[i]);
            }
            int[] colors = getColors(c);
            setRippleColor(colors);
        }
        return this;
    }

    private void setRippleColor(int[] colors) {
        if (colors.length > 1) {
            rippleNormalColor = colors[0];
            ripplePressColor = colors[1];
        } else {
            rippleNormalColor = colors[0];
            ripplePressColor = colors[0];
        }
    }

    private int[] getColors(int[] colors) {
        int len = colors.length;
        if (len < 2) {
            int[] newc = new int[2];
            newc[0] = colors[0];
            newc[1] = colors[0];
            return newc;
        }
        return colors;
    }

    public void into(View... views) {
        if (checkParams(views)) {
            for (View view : views) {
                view.setBackground(getRippleDrawable(getDrawable()));
                if (view instanceof TextView && checkParams(textColors) && textColors.length > 0) {
                    LinearGradient mLinearGradient = new LinearGradient(0, 0, ((TextView) view).getPaint().getTextSize() * ((TextView) view).getText().length(), 0, textColors, null, Shader.TileMode.CLAMP);
                    ((TextView) view).getPaint().setShader(mLinearGradient);
                    view.invalidate();
                }
            }
        }
    }

    private boolean checkParams(Object d) {
        return d != null;
    }

    private float dip2px(float dipValue) {
        if (dipValue <= 0) return 0;
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public GradientDrawable getDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setDither(true);
        if (checkParams(colors) && colors.length > 0) {
            drawable.setColors(colors);
        }
        if (checkParams(radius) && radius.length > 0) {
            drawable.setCornerRadii(radius);
        }
        if (checkParams(orientation)) {
            drawable.setOrientation(orientation);
        }
        drawable.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap);
        return drawable;
    }

    public Drawable getRippleDrawable(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && rippleNormalColor != -1 && ripplePressColor != -1) {
            if (drawable instanceof GradientDrawable && (colors == null || colors.length == 0)) {
                ((GradientDrawable) drawable).setColors(defaultColors);
            }
            int[][] stateList = new int[][]{
                    new int[]{android.R.attr.state_pressed},
                    new int[]{android.R.attr.state_focused},
                    new int[]{android.R.attr.state_activated},
                    new int[]{}
            };
            int[] stateColorList = new int[]{
                    ripplePressColor,
                    ripplePressColor,
                    ripplePressColor,
                    rippleNormalColor
            };
            ColorStateList colorStateList = new ColorStateList(stateList, stateColorList);
            return new RippleDrawable(colorStateList, drawable, null);
        } else return drawable;
    }

}