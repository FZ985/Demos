package com.demos.luck4;

import android.view.View;

import com.demos.activity.BaseActivity;
import com.demos.databinding.ActivityLucky4Binding;

import org.jetbrains.annotations.NotNull;

/**
 * author : JFZ
 * date : 2023/10/21 09:14
 * description :
 */
public class Lucky4Activity extends BaseActivity {
    private ActivityLucky4Binding binding;

    @Override
    public @NotNull View getApplyWindowView() {
        binding = ActivityLucky4Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        binding.lucky.bindData();
        binding.start.setOnClickListener(v -> {
            binding.lucky.setScrollToPosition(0);
        });
    }

}
