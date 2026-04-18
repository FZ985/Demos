package com.demos.jiguanganim

import android.view.View
import com.demos.activity.BaseActivity
import com.demos.databinding.ActivityAnimBackgroundColorBinding

/**
 * by DAD FZ
 * 2026/1/29
 * desc：
 **/
class AnimBackgroundColorActivity : BaseActivity() {
    private val binding: ActivityAnimBackgroundColorBinding by lazy {
        ActivityAnimBackgroundColorBinding.inflate(
            layoutInflater
        )
    }

    override fun initView() {

    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

}