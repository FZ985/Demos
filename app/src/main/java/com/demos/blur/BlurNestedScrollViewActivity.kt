package com.demos.blur

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.demos.activity.BaseActivity
import com.demos.blur.render.BlurConfig
import com.demos.blur.render.CompatBlurRender
import com.demos.databinding.ActivityBlurNestedScrollviewBinding

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
class BlurNestedScrollViewActivity : BaseActivity() {

    private val binding: ActivityBlurNestedScrollviewBinding by lazy {
        ActivityBlurNestedScrollviewBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun initView() {
        binding.view.setOnClickListener {
            binding.view.isVisible = false
        }

        binding.iv1.setOnClickListener {
            it.isVisible = false
        }

//        CompatBlurRender.get().bindBlur(
//            this,
//            binding.top,
//            binding.scroll,
//            BlurConfig().apply {
//                this.radius = 250f
//                this.isDrawBlurColor = true
//                this.blurColor = ColorUtils.setAlphaComponent(Color.BLACK, 100)
//            })
        CompatBlurRender.get().bindBlur(
            this,
            binding.center,
            binding.scroll,
            BlurConfig().apply {
                this.radius = 90f
            })

//        CompatBlurRender.get().bindBlur(
//            this,
//            binding.bottom,
//            binding.scroll,
//            BlurConfig().apply {
//                this.radius = 250f
//                this.isDrawBlurColor = true
//                this.blurColor = ColorUtils.setAlphaComponent(Color.WHITE, 150)
//            })
    }

}