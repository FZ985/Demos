package com.demos.zztestmar

import android.graphics.Color
import android.util.Log
import android.view.View
import com.demos.Tools
import com.demos.activity.BaseActivity
import com.demos.databinding.ZzzMarActBinding
import com.demos.span.core.Span


/**
 * by JFZ
 * 2024/9/10
 * desc：
 **/
class TestMarAct : BaseActivity() {

    private val binding: ZzzMarActBinding by lazy {
        ZzzMarActBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
        binding.click.setOnClickListener {
            binding.mar.setText(
                Tools.randomNumber(20, 1999)
                    .toString() + "哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈",
                Color.BLUE
            )
        }

        binding.marquee.setText(
            Span.with()
                .add(
                    Span.build("这是一个带生命周期和暂停/恢复功能的跑马灯！哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈很好")
                        .textColor(
                            Color.BLUE
                        )
                )
                .spannable
        )
        binding.marquee.setMarqueeRepeatLimit(2)
        binding.marquee.setSpeed(150f)
        binding.marquee.setOnMarqueeCompleteListener({
            Log.d("Marquee", "滚动完成！")
        })
        binding.marquee.setOnMarqueeStateListener {
            Log.d("Marquee", "不需要滚动！")
        }
        binding.marquee.bindLifecycle(lifecycle) // 绑定生命周期
        binding.marquee.startMarquee()
    }
}