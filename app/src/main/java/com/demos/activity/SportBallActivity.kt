package com.demos.activity

import android.view.View
import com.demos.databinding.ActivitySportBallBinding


/**
 * by JFZ
 * 2025/5/22
 * desc：
 **/
class SportBallActivity : BaseActivity() {

    private val binding: ActivitySportBallBinding by lazy {
        ActivitySportBallBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
        binding.ball.post {
            binding.ball.setCollidableViews(binding.v1, binding.v2)
        }
    }
}