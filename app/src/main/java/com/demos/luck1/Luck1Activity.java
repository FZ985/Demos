package com.demos.luck1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ActivityLuck1Binding;

import java.util.Random;

/**
 * author : JFZ
 * date : 2023/10/20 10:52
 * description :
 */
public class Luck1Activity extends AppCompatActivity {

    private ActivityLuck1Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLuck1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
