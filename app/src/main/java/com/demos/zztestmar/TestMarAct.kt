package com.demos.zztestmar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.demos.Tools
import com.demos.databinding.ZzzMarActBinding
import com.demos.span.core.Span


/**
 * by JFZ
 * 2024/9/10
 * desc：
 **/
class TestMarAct : AppCompatActivity() {

    private val binding: ZzzMarActBinding by lazy {
        ZzzMarActBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
        binding.marquee.bindLifecycle(getLifecycle()) // 绑定生命周期
        binding.marquee.startMarquee()
    }
}