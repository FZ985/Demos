package com.demos.bottomsheet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ActivityBottomSheet1Binding;
import com.lxj.xpopup.XPopup;

/**
 * by JFZ
 * 2024/6/24
 * desc：
 **/
public class BottomSheetActivity1 extends AppCompatActivity {

    private ActivityBottomSheet1Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomSheet1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
