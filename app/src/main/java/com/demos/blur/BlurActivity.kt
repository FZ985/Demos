package com.demos.blur

import android.content.Intent
import android.view.View
import com.demos.activity.BaseActivity
import com.demos.databinding.ActivityBlurBinding


/**
 * by JFZ
 * 2025/5/26
 * desc：
 **/
class BlurActivity : BaseActivity() {

    private val binding: ActivityBlurBinding by lazy {
        ActivityBlurBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
        binding.blurContent.setOnClickListener {
            startActivity(Intent(this, BlurLayoutActivity::class.java))
        }

        binding.blurNestedScrollView.setOnClickListener {
            startActivity(Intent(this, BlurNestedScrollViewActivity::class.java))
        }

        binding.blurScrollView.setOnClickListener {
            startActivity(Intent(this, BlurScrollViewActivity::class.java))
        }

        binding.blurWebView.setOnClickListener {
            startActivity(Intent(this, BlurWebViewActivity::class.java))
        }

        binding.blurRecycler.setOnClickListener {
            startActivity(Intent(this, BlurRecyclerViewActivity::class.java))
        }


    }

}