package com.demos.zztestmar

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demos.Tools
import com.demos.databinding.ZzzMarActBinding


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
    }
}