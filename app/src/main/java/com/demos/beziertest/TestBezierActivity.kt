package com.demos.beziertest

import android.view.View
import com.demos.activity.BaseActivity
import com.demos.click.PerfectClickListener
import com.demos.databinding.ZzTestBezierBinding


/**
 *  author : JFZ
 *  date : 2023/8/21 09:54
 *  description :
 */
class TestBezierActivity : BaseActivity() {

    private val binding: ZzTestBezierBinding by lazy {
        ZzTestBezierBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
        binding.click.setOnClickListener(object : PerfectClickListener() {
            override fun onViewClick(v: View) {
                binding.bv.start()
            }
        })
    }
}