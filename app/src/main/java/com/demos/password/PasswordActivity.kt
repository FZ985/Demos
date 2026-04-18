package com.demos.password

import android.view.View
import com.demos.activity.BaseActivity
import com.demos.databinding.ActivityPasswordBinding


/**
 *  author : JFZ
 *  date : 2023/9/11 10:10
 *  description :
 */
class PasswordActivity : BaseActivity() {

    private val binding: ActivityPasswordBinding by lazy {
        ActivityPasswordBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
    }
}