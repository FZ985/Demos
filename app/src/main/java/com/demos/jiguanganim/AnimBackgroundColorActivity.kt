package com.demos.jiguanganim

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demos.databinding.ActivityAnimBackgroundColorBinding

/**
 * by DAD FZ
 * 2026/1/29
 * desc：
 **/
class AnimBackgroundColorActivity : AppCompatActivity() {
    private val binding: ActivityAnimBackgroundColorBinding by lazy {
        ActivityAnimBackgroundColorBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}