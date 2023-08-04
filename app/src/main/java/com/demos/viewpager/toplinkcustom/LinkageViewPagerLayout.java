package com.demos.viewpager.toplinkcustom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.demos.R;
import com.demos.Tools;
import com.demos.viewpager.CustomPagerAdapter;
import com.demos.viewpager.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: jfz
 * Date: 2020-12-18 13:00
 */
@SuppressWarnings("unused")
public class LinkageViewPagerLayout extends FrameLayout {
    private NoScrollViewPager scale_viewpager;
    private ImageView scale_more_point;
    private List<View> viewList;
    private CustomPagerAdapter basePagerAdapter;
    private ScalePagerAdapter adapter;

    public LinkageViewPagerLayout(@NonNull Context context) {
        this(context, null);
    }

    public LinkageViewPagerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkageViewPagerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        removeAllViews();
        View view = inflate(getContext(), R.layout.layout_scale_viewpager, null);
        scale_viewpager = view.findViewById(R.id.scale_viewpager);
        scale_more_point = view.findViewById(R.id.scale_more_point);
        scale_viewpager.setOffscreenPageLimit(4);
        scale_viewpager.setClipChildren(false);
        basePagerAdapter = new CustomPagerAdapter() {
            @Override
            public void init(View view, int position) {
                if (adapter != null) adapter.init(view, position);
            }
        };
        scale_more_point.setAlpha(0f);
        scale_viewpager.setAdapter(basePagerAdapter);
        addView(view);
    }

    private boolean checkScroll(int position) {
        return viewList != null && viewList.size() > 2 && position == viewList.size() - 3;
    }

    private boolean checkSize() {
        return viewList != null && viewList.size() > 3;
    }

    private boolean checkSelected(int position) {
        return checkSize() && position < viewList.size() - 2;
    }

    public void setAdapter(int layoutId, final ScalePagerAdapter adapter) {
        if (adapter == null) return;
        this.adapter = adapter;
        viewList = new ArrayList<>();
        int itemCount = adapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
            viewList.add(view);
        }
        scale_viewpager.setOffscreenPageLimit(viewList.size());
        scale_viewpager.setPageMargin(-Tools.dip2px(120));
        basePagerAdapter.setViewList(viewList);
    }

    private int pos;

    public void bindViewPager(ViewPager contentViewpager) {
        contentViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int marginX = ((contentViewpager.getWidth() + contentViewpager.getPageMargin()) * position
                        + positionOffsetPixels) * (scale_viewpager.getWidth() + scale_viewpager.getPageMargin()) / (
                        contentViewpager.getWidth()
                                + contentViewpager.getPageMargin());

                if (scale_viewpager.getScrollX() != marginX) {
                    scale_viewpager.scrollTo(marginX, 0);
                }

                if (checkSize() && checkSelected(position)) {
                    if (checkScroll(position)) {
                        scale_more_point.setAlpha(1 - positionOffset);
                    } else {
                        scale_more_point.setAlpha(1f);
                    }
                }

                if (scale_viewpager.getAdapter() != null && scale_viewpager.getAdapter() instanceof CustomPagerAdapter) {
                    float factor = Math.min(positionOffset, 1f);
                    CustomPagerAdapter basePagerAdapter = (CustomPagerAdapter) scale_viewpager.getAdapter();
                    View current = basePagerAdapter.getItemView(position);
                    View beforeView = null;
                    if (LinkageViewPagerLayout.this.pos - 1 >= 0 && LinkageViewPagerLayout.this.pos - 1 < basePagerAdapter.getCount()) {
                        beforeView = basePagerAdapter.getItemView(Math.max(LinkageViewPagerLayout.this.pos - 1, 0));
                    }
                    View afterView = null;
                    if (position + 1 < basePagerAdapter.getCount()) {
                        afterView = basePagerAdapter.getItemView(position + 1);
                    }

                    if (current != null) {
                        float currentAlpha = current.getAlpha();
                        if (currentAlpha >= 1F) {
                            //当前缩小
                            current.setScaleX(Math.max(0.75f, 1F - factor));
                            current.setScaleY(Math.max(0.75f, 1F - factor));
                        } else {
                            //当前放大
                            current.setScaleX(Math.max(0.75f, factor));
                            current.setScaleY(Math.max(0.75f, factor));
                        }
                    }
                    if (beforeView != null) {
                        if (factor == 0.0F) {
                            beforeView.setScaleX(0.75f);
                            beforeView.setScaleY(0.75f);
                        }
//                      else {
//                          beforeView.setScaleX(Math.max(0.75f, f));
//                          beforeView.setScaleY(Math.max(0.75f, f));
//                      }
                    }
                    if (afterView != null) {
                        if (factor == 0.0F) {
                            afterView.setScaleX(0.75f);
                            afterView.setScaleY(0.75f);
                        } else {
                            float f = 0.75f + (factor) * (1.0f - 0.75f);
                            afterView.setScaleX(f);
                            afterView.setScaleY(f);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                LinkageViewPagerLayout.this.pos = position;
                if (checkSize() && checkSelected(position)) {
                    scale_more_point.setAlpha(1f);
                } else scale_more_point.setAlpha(0f);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    scale_viewpager.setCurrentItem(pos);
                }
            }
        });
    }

    public void setCurrentItem(int position, ViewPager contentViewPager) {
        scale_viewpager.setCurrentItem(position);
        contentViewPager.setCurrentItem(position);
    }
}