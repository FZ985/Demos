package com.demos.other

import android.view.View
import com.demos.activity.BaseActivity
import com.demos.databinding.ActivityAppbarLayoutBinding


/**
 *  author : JFZ
 *  date : 2023/8/18 09:56
 *  description :
 */
class AppbarLayoutActivity : BaseActivity() {

    private val binding: ActivityAppbarLayoutBinding by lazy {
        ActivityAppbarLayoutBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
    }
}