package com.demos.prizedraw;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ActivityProzeDrawBinding;

import java.util.Random;

/**
 * author : JFZ
 * date : 2023/7/26 09:28
 * description :
 */
public class PrizeDrawActivity extends AppCompatActivity {

    private ActivityProzeDrawBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProzeDrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAction.setOnClickListener(v -> {
            if (!binding.luckyPanel.isGameRunning()) {
                binding.luckyPanel.startGame();
            } else {
                int stayIndex = new Random().nextInt(8);
                Log.e("LuckyMonkeyPanelView", "====stay===" + stayIndex);
                binding.luckyPanel.tryToStop(stayIndex);
            }
        });
    }
}
