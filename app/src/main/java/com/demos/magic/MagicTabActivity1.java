package com.demos.magic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.demos.base.CommonFragmentStatePagerAdapter;
import com.demos.databinding.ActivityMagic1Binding;
import com.demos.viewpager.toplinkcustom.ChildFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/30 08:56
 * description :
 */
public class MagicTabActivity1 extends AppCompatActivity {
    private ActivityMagic1Binding binding;

    private final String[] t = {"哈哈", "呵呵", "嘿嘿", "嘻嘻嘻嘻"};

    private final Integer[] colors = {Color.parseColor("#ff4a42"),
            Color.parseColor("#fcde64"),
            Color.parseColor("#76b0ff"),
            Color.parseColor("#c683fe")};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMagic1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        List<Fragment> f = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            f.add(ChildFragment.instance(i));
        }
        binding.vp.setAdapter(new CommonFragmentStatePagerAdapter(getSupportFragmentManager(), f));
        binding.vp.setOffscreenPageLimit(t.length);

        initTabs(binding.magic, new ArcPagerIndicator(this)
                .setColors(colors)
                .setStartInterpolator(new AccelerateInterpolator())
                .setEndInterpolator(new DecelerateInterpolator(2f)));

        initTabs(binding.magic2, new OneHalfLinePagerIndicator(this)
                .setColors(colors)
                .setStartInterpolator(new AccelerateInterpolator())
                .setEndInterpolator(new DecelerateInterpolator(2f)));

        initTabs(binding.magic3, new GradientCircleIndicator(this)
                .setMaxRadius(3f)
                .setBottomOffset(6f)
                .setOffset(2f)
                .setColors(Color.parseColor("#ff4a42"),
                        Color.parseColor("#fcde64"),
                        Color.parseColor("#76b0ff"),
                        Color.parseColor("#c683fe")));
    }

    private void initTabs(MagicIndicator indicator, IPagerIndicator pagerIndicator) {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return t.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(t[index]);
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setOnClickListener(v -> binding.vp.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return pagerIndicator;
            }
        });
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, binding.vp);
    }

}
