package com.demos.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.demos.Tools
import com.demos.databinding.ActivityRoundWrapBinding


/**
 *  author : JFZ
 *  date : 2023/9/21 08:27
 *  description :
 */
class RoundWrapActivity : AppCompatActivity() {

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
        binding.wrap.setColor(Tools.randomColor())

    }
}