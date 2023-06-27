package com.demos.viewpager.toplinkcustom;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.demos.R;
import com.demos.base.CommonFragmentStatePagerAdapter;
import com.demos.databinding.ActivityViewpageLinkSuctomBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/27 09:46
 * description :
 */
public class TopLinkCustomActivity extends AppCompatActivity {

    private ActivityViewpageLinkSuctomBinding binding;

    private final int count = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewpageLinkSuctomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
    }

    private void initData() {
        binding.link.setAdapter(R.layout.item_vp, new ScalePagerAdapter() {
            @Override
            public int getItemCount() {
                return count;
            }

            @Override
            public void onViewCreate(View view, int position) {
                TextView item_vp_tv = view.findViewById(R.id.item_vp_tv);
                item_vp_tv.setText(String.valueOf(position));
            }
        });

        List<Fragment> frags = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            frags.add(ChildFragment.instance(i));
        }
        binding.vp.setAdapter(new CommonFragmentStatePagerAdapter(getSupportFragmentManager(), frags));
        binding.vp.setOffscreenPageLimit(count);
        binding.link.bindViewPager(binding.vp);

    }

}
