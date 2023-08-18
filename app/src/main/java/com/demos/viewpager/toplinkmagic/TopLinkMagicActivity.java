package com.demos.viewpager.toplinkmagic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.demos.R;
import com.demos.base.CommonFragmentStatePagerAdapter;
import com.demos.databinding.ActivityToplinkMagicBinding;
import com.demos.viewpager.toplinkcustom.ChildFragment;
import com.google.android.material.imageview.ShapeableImageView;

import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * Author: jfz
 * Date: 2020-12-21 9:53
 */
public class TopLinkMagicActivity extends AppCompatActivity {
    private ActivityToplinkMagicBinding binding;
    private final String[] CHANNELS = {"CUPCAKE", "DONUT", "ECLAIR", "GINGERBREAD", "HONEYCOMB", "ICE_CREAM_SANDWICH", "JELLY_BEAN", "KITKAT", "LOLLIPOP", "M", "NOUGAT"};
    private List<String> mDataList = Arrays.asList(CHANNELS);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToplinkMagicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        binding.click.setOnClickListener(v -> {
//            mDataList.remove(0);
//            commonFragmentAdapter.remove(0);
//            commonFragmentAdapter.notifyDataSetChanged();

//            FragmentContainerHelper helper = new FragmentContainerHelper(binding.magicindicator);
//            helper.handlePageSelected(3);

//            binding.magicVp.setCurrentItem(3);
        });
    }

    private void init() {
        initAdapter();
        initTop();
    }

    private CommonFragmentStatePagerAdapter commonFragmentAdapter;

    private void initAdapter() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            fragments.add(ChildFragment.instance(i));
        }
        binding.magicVp.setAdapter(commonFragmentAdapter = new CommonFragmentStatePagerAdapter(getSupportFragmentManager(), fragments));
    }

    private void initTop() {
        LinkageNavigator commonNavigator = new LinkageNavigator(this);
        commonNavigator.setSkimOver(true);
        commonNavigator.setOnScroll(true);
        int padding = UIUtil.dip2px(this, 180) / 2;
        commonNavigator.setRightPadding(padding);
        commonNavigator.setLeftPadding(padding);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);

                // load custom layout
                View customLayout = LayoutInflater.from(context).inflate(R.layout.layout_item_magic, null);
                final ShapeableImageView titleImg = customLayout.findViewById(R.id.item_vp_iv);
                final TextView titleText = customLayout.findViewById(R.id.item_vp_tv);
                titleText.setText(String.valueOf(index));
                commonPagerTitleView.setContentView(customLayout);
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(Color.RED);
                        binding.magicPoint.setAlpha(checkPointIndex(index) ? 1f : 0f);
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(Color.BLUE);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        titleImg.setScaleX(1.0f + (0.65f - 1.0f) * leavePercent);
                        titleImg.setScaleY(1.0f + (0.65f - 1.0f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        titleImg.setScaleX(0.65f + (1.0f - 0.65f) * enterPercent);
                        titleImg.setScaleY(0.65f + (1.0f - 0.65f) * enterPercent);
                    }
                });

                commonPagerTitleView.setOnClickListener(v -> binding.magicVp.setCurrentItem(index));

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        binding.magicindicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(binding.magicindicator, binding.magicVp);
    }

    public boolean checkPointIndex(int position) {
        return (mDataList != null && mDataList.size() > 2 && position < mDataList.size() - 2);
    }
}