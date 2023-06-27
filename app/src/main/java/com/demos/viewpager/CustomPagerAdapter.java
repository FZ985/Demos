package com.demos.viewpager;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Description:
 * Author: jfz
 * Date: 2020-12-18 10:30
 */
public abstract class CustomPagerAdapter extends PagerAdapter {
    private List<View> viewList;

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return viewList == null ? 0 : viewList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = viewList.get(position);
        container.addView(view);
        init(view, position);
        return view;
    }

    public abstract void init(View view, int position);

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public View getItemView(int position) {
        if (viewList != null && position < viewList.size()) return viewList.get(position);
        return null;
    }
}