package com.demos.bottomsheet;

import android.view.View;

import com.demos.activity.BaseActivity;
import com.demos.databinding.ActivityBottomSheet1Binding;
import com.lxj.xpopup.XPopup;

import org.jetbrains.annotations.NotNull;

/**
 * by JFZ
 * 2024/6/24
 * desc：
 **/
public class BottomSheetActivity1 extends BaseActivity {

    private ActivityBottomSheet1Binding binding;

    @Override
    public @NotNull View getApplyWindowView() {
        binding = ActivityBottomSheet1Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        binding.sheet.setOnClickListener(v -> {
            new XPopup.Builder(this)
                    .isDestroyOnDismiss(true)
                    .moveUpToKeyboard(false)
                    .hasShadowBg(false)
                    .enableDrag(false)
                    .isViewMode(true)
                    .asCustom(new BottomSheetPop1(this))
                    .show();
        });
    }
}
