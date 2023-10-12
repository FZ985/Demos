package com.demos.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.widget.doOnTextChanged
import com.demos.Tools
import com.demos.databinding.ActivityRoundWrapBinding


/**
 *  author : JFZ
 *  date : 2023/9/21 08:27
 *  description :
 */
class RoundWrapActivity : AppCompatActivity() {

        val tt = "哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈\n哈哈\n哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈\n哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈"
//    val tt = "哈哈哈哈哈哈哈哈哈"

    private val binding: ActivityRoundWrapBinding by lazy {
        ActivityRoundWrapBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.et.doOnTextChanged { text, _, _, _ ->
            binding.wrap.text = text
        }

        binding.et.setColor(Color.BLUE)
        binding.wrap.setColor(ColorUtils.setAlphaComponent(Tools.randomColor(),100))
        binding.wrap.text = tt

    }
}