package com.demos.luck4;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ActivityLucky4Binding;

/**
 * author : JFZ
 * date : 2023/10/21 09:14
 * description :
 */
public class Lucky4Activity extends AppCompatActivity {
    private ActivityLucky4Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLucky4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.lucky.bindData();
        binding.start.setOnClickListener(v -> {
            binding.lucky.setScrollToPosition(0);
        });
    }

}
