package com.demos.luck1;

import android.util.Log;
import android.view.View;

import com.demos.activity.BaseActivity;
import com.demos.databinding.ActivityLuck1Binding;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * author : JFZ
 * date : 2023/10/20 10:52
 * description :
 */
public class Luck1Activity extends BaseActivity {

    private ActivityLuck1Binding binding;

    @Override
    @NotNull
    public View getApplyWindowView() {
        binding = ActivityLuck1Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        binding.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.luckyPanel.isGameRunning()) {
                    binding.luckyPanel.startGame();
                } else {
                    int stayIndex = new Random().nextInt(8);
                    Log.e("LuckyMonkeyPanelView", "====stay===" + stayIndex);
                    binding.luckyPanel.tryToStop(stayIndex);
                }
            }
        });
    }
}
