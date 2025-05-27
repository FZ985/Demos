package com.demos.blur

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import com.demos.blur.render.BlurConfig
import com.demos.blur.render.CompatBlurRender
import com.demos.databinding.ActivityBlurScrollviewBinding

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
class BlurScrollViewActivity : AppCompatActivity() {

    private val binding: ActivityBlurScrollviewBinding by lazy {
        ActivityBlurScrollviewBinding.inflate(layoutInflater)
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.view.setOnClickListener {
            binding.view.isVisible = false
        }

        binding.iv1.setOnClickListener {
            it.isVisible = false
        }

        CompatBlurRender.get().bindBlur(
            this,
            binding.top,
            binding.scroll,
            BlurConfig().apply {
                this.radius = 90f
            })

        CompatBlurRender.get().bindBlur(
            this,
            binding.center,
            binding.scroll,
            BlurConfig().apply {
                this.radius = 90f
            })

        CompatBlurRender.get().bindBlur(
            this,
            binding.bottom,
            binding.scroll,
            BlurConfig().apply {
                this.radius = 250f
                this.isDrawBlurColor = true
                this.blurColor = ColorUtils.setAlphaComponent(Color.WHITE, 150)
            })

    }

}