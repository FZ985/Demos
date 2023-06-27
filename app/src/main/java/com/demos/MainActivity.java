package com.demos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ActivityMainBinding;
import com.demos.viewpager.toplinkcustom.TopLinkCustomActivity;
import com.demos.viewpager.toplinkmagic.TopLinkMaigcAcivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.viewpager1.setOnClickListener(v -> {
            startActivity(new Intent(this, TopLinkCustomActivity.class));
        });
        binding.viewpager2.setOnClickListener(v -> {
            startActivity(new Intent(this, TopLinkMaigcAcivity.class));
        });
    }
}