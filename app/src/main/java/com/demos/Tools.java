package com.demos;

import android.graphics.Color;

import java.util.Random;

/**
 * author : JFZ
 * date : 2023/6/27 09:33
 * description :
 */
public class Tools {

    public static int randomColor(){
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static int dip2px(float dpValue) {
        final float scale = BaseApp.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int randomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    public static int getScreenWidth() {
        return BaseApp.getInstance().getResources().getDisplayMetrics().widthPixels;
    }

}
