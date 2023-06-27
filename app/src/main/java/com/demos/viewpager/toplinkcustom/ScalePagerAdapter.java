package com.demos.viewpager.toplinkcustom;


import android.view.View;

import com.demos.viewpager.CustomPagerAdapter;

/**
 * Description:
 * Author: jfz
 * Date: 2020-12-18 13:33
 */
public abstract class ScalePagerAdapter extends CustomPagerAdapter {
    public abstract int getItemCount();

    @Override
    public void init(View view, int position) {
        view.setScaleX(0.75f);
        view.setScaleY(0.75f);
        onViewCreate(view, position);
    }

    public abstract void onViewCreate(View view, int position);
}